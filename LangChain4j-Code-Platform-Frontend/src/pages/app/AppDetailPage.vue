<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import {
  deleteApp,
  deleteMyApp,
  deployApp,
  getApp,
  getAppByAdmin,
} from '@/api/appController'
import AppPreviewFrame from '@/components/app/AppPreviewFrame.vue'
import { useLoginUserStore } from '@/stores/loginUser'
import {
  formatDateTime,
  getAppPreviewUrl,
  getCodeGenTypeLabel,
  getIdString,
  isAdmin,
  isAppOwner,
} from '@/utils/app'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const app = ref<API.AppVo>()
const loading = ref(false)
const previewSeed = ref(Date.now())

const appId = computed(() => getIdString(route.params.id))
const canEdit = computed(() => isAdmin(loginUserStore.loginUser) || isAppOwner(app.value, loginUserStore.loginUser))
const canDelete = computed(() => canEdit.value)
const previewUrl = computed(() => {
  if (!app.value) {
    return ''
  }
  return `${getAppPreviewUrl(app.value)}?t=${previewSeed.value}`
})

const loadApp = async () => {
  loading.value = true
  try {
    if (!appId.value) {
      message.error('应用 ID 无效')
      return
    }
    const commonRes = await getApp({ id: appId.value })
    if (commonRes.data.code === 200 && commonRes.data.data) {
      app.value = commonRes.data.data
      return
    }
    if (isAdmin(loginUserStore.loginUser)) {
      const adminRes = await getAppByAdmin({ id: appId.value })
      if (adminRes.data.code === 200 && adminRes.data.data) {
        app.value = adminRes.data.data
        return
      }
      message.error(adminRes.data.message || '获取应用详情失败')
      return
    }
    message.error(commonRes.data.message || '获取应用详情失败')
  } finally {
    loading.value = false
  }
}

const goEdit = () => {
  const targetId = getIdString(app.value?.id)
  if (!targetId) {
    return
  }
  if (isAdmin(loginUserStore.loginUser)) {
    router.push(`/app/admin/edit/${targetId}`)
    return
  }
  router.push(`/app/my/edit/${targetId}`)
}

const goChat = () => {
  const targetId = getIdString(app.value?.id)
  if (targetId) {
    router.push(`/app/chat/${targetId}`)
  }
}

const doDelete = async () => {
  const targetId = getIdString(app.value?.id)
  if (!targetId) {
    return
  }
  const api = isAdmin(loginUserStore.loginUser) ? deleteApp : deleteMyApp
  const res = await api({ id: targetId })
  if (res.data.code === 200) {
    message.success('删除成功')
    router.push(isAdmin(loginUserStore.loginUser) ? '/admin/appManage' : '/')
  } else {
    message.error(res.data.message || '删除失败')
  }
}

const doDeploy = async () => {
  const targetId = getIdString(app.value?.id)
  if (!targetId) {
    return
  }
  const res = await deployApp({ appId: targetId })
  if (res.data.code === 200 && res.data.data) {
    message.success(`部署成功：${res.data.data}`)
    previewSeed.value = Date.now()
    loadApp()
  } else {
    message.error(res.data.message || '部署失败')
  }
}

onMounted(() => {
  loadApp()
})
</script>

<template>
  <a-spin :spinning="loading">
    <div class="detail-page">
      <section class="summary-panel">
        <div class="summary-head">
          <div>
            <h1>{{ app?.appName || '应用详情' }}</h1>
            <p>创建者：{{ app?.user?.userName || '-' }}</p>
          </div>
          <div class="summary-actions">
            <a-button type="primary" @click="goChat">继续生成</a-button>
            <a-button v-if="canEdit" @click="goEdit">编辑</a-button>
            <a-button v-if="canEdit" @click="doDeploy">部署</a-button>
            <a-popconfirm
              v-if="canDelete"
              title="确认删除这个应用？"
              ok-text="删除"
              cancel-text="取消"
              @confirm="doDelete"
            >
              <a-button danger>删除</a-button>
            </a-popconfirm>
          </div>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span>生成类型</span>
            <strong>{{ getCodeGenTypeLabel(app?.codeGenType) }}</strong>
          </div>
          <div class="info-item">
            <span>优先级</span>
            <strong>{{ app?.priority ?? '-' }}</strong>
          </div>
          <div class="info-item">
            <span>创建时间</span>
            <strong>{{ formatDateTime(app?.createTime) }}</strong>
          </div>
          <div class="info-item">
            <span>更新时间</span>
            <strong>{{ formatDateTime(app?.updateTime) }}</strong>
          </div>
          <div class="info-item">
            <span>部署地址</span>
            <strong>{{ app?.deployKey ? `http://localhost/${app.deployKey}/` : '未部署' }}</strong>
          </div>
          <div class="info-item">
            <span>部署时间</span>
            <strong>{{ formatDateTime(app?.deployedTime) }}</strong>
          </div>
        </div>
        <div class="prompt-box">
          <h3>初始提示词</h3>
          <p>{{ app?.initPrompt || '暂无' }}</p>
        </div>
      </section>

      <section class="preview-section">
        <AppPreviewFrame :src="previewUrl" />
      </section>
    </div>
  </a-spin>
</template>

<style scoped>
.detail-page {
  display: grid;
  grid-template-columns: minmax(360px, 430px) minmax(0, 1fr);
  gap: 20px;
}

.summary-panel {
  padding: 28px;
  border-radius: 28px;
  background: white;
  box-shadow: 0 16px 40px rgba(40, 65, 95, 0.08);
}

.summary-head {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.summary-head h1 {
  margin: 0;
  color: #11263a;
}

.summary-head p {
  margin: 8px 0 0;
  color: #6d8096;
}

.summary-actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
  margin-top: 24px;
}

.info-item {
  padding: 16px;
  border-radius: 18px;
  background: #f5f8fb;
  border: 1px solid rgba(24, 53, 83, 0.06);
}

.info-item span {
  display: block;
  color: #73849a;
  font-size: 13px;
}

.info-item strong {
  display: block;
  margin-top: 8px;
  color: #152b40;
  line-height: 1.6;
}

.prompt-box {
  margin-top: 24px;
  padding: 18px 20px;
  border-radius: 20px;
  background: linear-gradient(180deg, #f7fbff 0%, #eef6ff 100%);
}

.prompt-box h3 {
  margin: 0 0 10px;
  color: #11263a;
}

.prompt-box p {
  margin: 0;
  color: #5f7187;
  white-space: pre-wrap;
  line-height: 1.8;
}

@media (max-width: 1100px) {
  .detail-page {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .info-grid {
    grid-template-columns: 1fr;
  }
}
</style>
