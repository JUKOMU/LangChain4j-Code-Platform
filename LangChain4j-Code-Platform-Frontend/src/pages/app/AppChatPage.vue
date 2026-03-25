<script setup lang="ts">
import { computed, nextTick, onMounted, reactive, ref } from 'vue'
import { useRoute } from 'vue-router'
import { CopyOutlined, DeploymentUnitOutlined, SendOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { deployApp, getApp } from '@/api/appController'
import ChatMarkdownBubble from '@/components/app/ChatMarkdownBubble.vue'
import AppPreviewFrame from '@/components/app/AppPreviewFrame.vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { formatDateTime, getAppPreviewUrl, getIdString, isAppOwner } from '@/utils/app'
import { streamAppChat } from '@/utils/sse'

type ChatMessage = {
  id: string
  role: 'user' | 'assistant'
  content: string
  time: string
}

const route = useRoute()
const loginUserStore = useLoginUserStore()
const app = ref<API.AppVo>()
const loading = ref(false)
const deploying = ref(false)
const deployModalVisible = ref(false)
const deployUrl = ref('')
const previewSeed = ref(Date.now())
const assistantMessageId = ref('')
const messageListRef = ref<HTMLDivElement>()

const inputForm = reactive({
  content: '',
})

const messages = ref<ChatMessage[]>([])

const appId = computed(() => getIdString(route.params.id))
const isViewMode = computed(() => route.query.view === '1')
const canChat = computed(() => isAppOwner(app.value, loginUserStore.loginUser))
const inputDisabledReason = computed(() =>
  canChat.value ? '' : '无法在别人的作品下对话哦~',
)
const previewUrl = computed(() => {
  if (!app.value) {
    return ''
  }
  return `${getAppPreviewUrl(app.value)}?t=${previewSeed.value}`
})

const scrollToBottom = async () => {
  await nextTick()
  const element = messageListRef.value
  if (element) {
    element.scrollTop = element.scrollHeight
  }
}

const appendMessage = (role: ChatMessage['role'], content: string) => {
  const item = {
    id: `${role}-${Date.now()}-${Math.random().toString(36).slice(2, 8)}`,
    role,
    content,
    time: new Date().toISOString(),
  }
  messages.value.push(item)
  void scrollToBottom()
  return item.id
}

const updateAssistantMessage = (content: string) => {
  const target = messages.value.find((item) => item.id === assistantMessageId.value)
  if (target) {
    target.content = content
  }
  void scrollToBottom()
}

const loadApp = async () => {
  if (!appId.value) {
    message.error('应用 ID 无效')
    return
  }
  const res = await getApp({ id: appId.value })
  if (res.data.code === 200 && res.data.data) {
    app.value = res.data.data
    return
  }
  message.error(res.data.message || '获取应用详情失败')
}

const sendMessage = async (rawMessage?: string) => {
  if (!canChat.value) {
    message.warning(inputDisabledReason.value)
    return
  }
  const userMessage = (rawMessage ?? inputForm.content).trim()
  if (!userMessage || loading.value) {
    return
  }
  appendMessage('user', userMessage)
  inputForm.content = ''
  loading.value = true
  let aiContent = ''
  assistantMessageId.value = appendMessage('assistant', '')
  try {
    await streamAppChat(appId.value, userMessage, {
      onMessage: (chunk) => {
        aiContent += chunk
        updateAssistantMessage(aiContent)
      },
      onDone: async () => {
        previewSeed.value = Date.now()
        await loadApp()
      },
    })
  } catch (error) {
    const errorMessage = error instanceof Error ? error.message : '生成失败'
    updateAssistantMessage(errorMessage)
    message.error(errorMessage)
  } finally {
    loading.value = false
  }
}

const doDeploy = async () => {
  if (!app.value?.id || !canChat.value) {
    if (!canChat.value) {
      message.warning(inputDisabledReason.value)
    }
    return
  }
  deploying.value = true
  try {
    const res = await deployApp({ appId: getIdString(app.value.id) })
    if (res.data.code === 200 && res.data.data) {
      deployUrl.value = res.data.data
      deployModalVisible.value = true
      await loadApp()
      return
    }
    message.error(res.data.message || '部署失败')
  } finally {
    deploying.value = false
  }
}

const copyDeployUrl = async () => {
  if (!deployUrl.value) {
    return
  }
  await navigator.clipboard.writeText(deployUrl.value)
  message.success('部署地址已复制')
}

onMounted(async () => {
  if (!loginUserStore.loginUser.id) {
    await loginUserStore.fetchLoginUser()
  }
  await loadApp()
  const autoPrompt = route.query.autoPrompt
  if (isViewMode.value) {
    return
  }
  if (typeof autoPrompt === 'string' && autoPrompt.trim()) {
    void sendMessage(autoPrompt)
  }
})
</script>

<template>
  <div class="app-chat-page">
    <div class="top-bar">
      <div class="title-side">
        <h1>{{ app?.appName || '应用生成对话' }}</h1>
        <p v-if="app">最近更新时间：{{ formatDateTime(app.updateTime || app.createTime) }}</p>
      </div>
      <a-button type="primary" size="large" :loading="deploying" :disabled="!canChat" @click="doDeploy">
        <template #icon><DeploymentUnitOutlined /></template>
        部署应用
      </a-button>
    </div>

    <div class="chat-layout">
      <section class="chat-panel">
        <div ref="messageListRef" class="message-list">
          <div
            v-for="item in messages"
            :key="item.id"
            class="message-row"
            :class="item.role === 'user' ? 'is-user' : 'is-ai'"
          >
            <div class="message-bubble">
              <div class="message-role">{{ item.role === 'user' ? '你' : 'AI' }}</div>
              <div v-if="item.role === 'user'" class="message-content message-content-plain">
                {{ item.content }}
              </div>
              <ChatMarkdownBubble
                v-else-if="item.content"
                class="message-content"
                :content="item.content"
              />
              <div v-else class="message-content message-content-placeholder">
                {{ loading ? '正在生成中...' : '' }}
              </div>
            </div>
          </div>
          <a-empty
            v-if="messages.length === 0"
            description="输入你的修改需求，AI 会持续生成网站并在右侧实时预览。"
          />
        </div>
        <div class="chat-input" :class="{ 'is-disabled': !canChat }">
          <a-tooltip :title="inputDisabledReason" :open="!canChat ? undefined : false">
            <div>
              <a-textarea
                v-model:value="inputForm.content"
                :auto-size="{ minRows: 4, maxRows: 8 }"
                :disabled="!canChat"
                placeholder="例如：导航栏再精简一点，增加文章详情页封面区域，并把整体色调调整成浅蓝色。"
              />
            </div>
          </a-tooltip>
          <div class="chat-toolbar">
            <span>
              {{
                canChat
                  ? '支持连续多轮修改，生成完成后右侧会自动刷新'
                  : '当前是查看模式，只能浏览别人的作品'
              }}
            </span>
            <a-button type="primary" :loading="loading" :disabled="!canChat" @click="sendMessage()">
              <template #icon><SendOutlined /></template>
              发送
            </a-button>
          </div>
        </div>
      </section>

      <section class="preview-panel">
        <AppPreviewFrame :src="previewUrl" :loading="loading" />
      </section>
    </div>

    <a-modal v-model:open="deployModalVisible" title="部署成功" :footer="null">
      <p class="deploy-desc">应用已经部署完成，可以直接访问以下地址：</p>
      <a-input :value="deployUrl" readonly />
      <div class="deploy-actions">
        <a-button @click="copyDeployUrl">
          <template #icon><CopyOutlined /></template>
          复制地址
        </a-button>
        <a-button type="primary" :href="deployUrl" target="_blank">打开应用</a-button>
      </div>
    </a-modal>
  </div>
</template>

<style scoped>
.app-chat-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.top-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 22px 26px;
  border-radius: 28px;
  background: white;
  box-shadow: 0 16px 36px rgba(40, 65, 95, 0.08);
}

.title-side h1 {
  margin: 0;
  font-size: 28px;
  color: #11263a;
}

.title-side p {
  margin: 8px 0 0;
  color: #708196;
}

.chat-layout {
  display: grid;
  grid-template-columns: minmax(360px, 460px) minmax(0, 1fr);
  gap: 20px;
}

.chat-panel,
.preview-panel {
  min-width: 0;
}

.chat-panel {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.message-list {
  height: 680px;
  overflow-y: auto;
  padding: 20px;
  border-radius: 28px;
  background: white;
  box-shadow: 0 16px 40px rgba(40, 65, 95, 0.08);
}

.message-row {
  display: flex;
  margin-bottom: 16px;
}

.message-row.is-user {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 88%;
  padding: 16px 18px;
  border: 1px solid rgba(23, 54, 82, 0.08);
  border-radius: 22px;
  background: linear-gradient(180deg, #fbfdff 0%, #f4f8fb 100%);
  box-shadow: 0 8px 24px rgba(34, 63, 94, 0.05);
}

.is-user .message-bubble {
  background: linear-gradient(180deg, #10283d 0%, #173852 100%);
  color: white;
}

.message-role {
  margin-bottom: 8px;
  font-size: 12px;
  opacity: 0.7;
}

.message-content {
  line-height: 1.7;
}

.message-content-plain {
  white-space: pre-wrap;
}

.message-content-placeholder {
  color: #7b8ea5;
}

.chat-input {
  padding: 20px;
  border-radius: 28px;
  background: white;
  box-shadow: 0 16px 36px rgba(40, 65, 95, 0.08);
}

.chat-input.is-disabled {
  background: #f7f9fc;
}

.chat-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 14px;
  color: #72849a;
  font-size: 13px;
}

.deploy-desc {
  color: #5f7187;
}

.deploy-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 16px;
}

@media (max-width: 1100px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }

  .message-list {
    height: 420px;
  }
}

@media (max-width: 768px) {
  .top-bar {
    flex-direction: column;
    align-items: flex-start;
  }

  .chat-toolbar {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
