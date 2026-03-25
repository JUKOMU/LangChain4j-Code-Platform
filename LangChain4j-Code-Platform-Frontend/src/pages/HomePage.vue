<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import AppCard from '@/components/app/AppCard.vue'
import { addApp, deleteMyApp, getFeaturedApps, getMyApps } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import ACCESS_ENUM from '@/access/accessEnum'
import {
  APP_CODE_GEN_TYPES,
  buildNewAppName,
  DEFAULT_APP_CODE_GEN_TYPE,
  getAppDeployUrl,
  getIdString,
} from '@/utils/app'

const router = useRouter()
const loginUserStore = useLoginUserStore()

const createForm = reactive({
  prompt: '',
  codeGenType: DEFAULT_APP_CODE_GEN_TYPE,
})

const mySearch = reactive<API.AppQueryDto>({
  pageNum: 1,
  pageSize: 6,
  appName: '',
})

const featuredSearch = reactive<API.AppQueryDto>({
  pageNum: 1,
  pageSize: 6,
  appName: '',
})

const myApps = ref<API.AppVo[]>([])
const featuredApps = ref<API.AppVo[]>([])
const myTotal = ref(0)
const featuredTotal = ref(0)
const creating = ref(false)
const myLoading = ref(false)
const featuredLoading = ref(false)

const isLoggedIn = computed(() => loginUserStore.loginUser.userRole !== ACCESS_ENUM.NOT_LOGIN)

const loadMyApps = async () => {
  if (!isLoggedIn.value) {
    myApps.value = []
    myTotal.value = 0
    return
  }
  myLoading.value = true
  try {
    const res = await getMyApps({ ...mySearch })
    if (res.data.code === 200 && res.data.data) {
      myApps.value = res.data.data.records ?? []
      myTotal.value = res.data.data.totalRow ?? 0
      return
    }
    message.error(res.data.message || '获取我的应用失败')
  } finally {
    myLoading.value = false
  }
}

const loadFeaturedApps = async () => {
  if (!isLoggedIn.value) {
    featuredApps.value = []
    featuredTotal.value = 0
    return
  }
  featuredLoading.value = true
  try {
    const res = await getFeaturedApps({ ...featuredSearch })
    if (res.data.code === 200 && res.data.data) {
      featuredApps.value = res.data.data.records ?? []
      featuredTotal.value = res.data.data.totalRow ?? 0
      return
    }
    message.error(res.data.message || '获取精选应用失败')
  } finally {
    featuredLoading.value = false
  }
}

const createAppFromPrompt = async () => {
  if (!createForm.prompt.trim()) {
    message.warning('请输入你想生成的网站描述')
    return
  }
  if (!isLoggedIn.value) {
    await router.push(`/user/login?redirect=${encodeURIComponent(window.location.href)}`)
    return
  }
  creating.value = true
  try {
    const initPrompt = createForm.prompt.trim()
    const res = await addApp({
      initPrompt,
      codeGenType: createForm.codeGenType,
      appName: buildNewAppName(initPrompt),
    })
    const appId = getIdString(res.data.data)
    if (res.data.code === 200 && appId) {
      await router.push({
        path: `/app/chat/${appId}`,
        query: {
          autoPrompt: initPrompt,
        },
      })
      return
    }
    message.error(res.data.message || '创建应用失败')
  } finally {
    creating.value = false
  }
}

const handleDelete = async (app: API.AppVo) => {
  const appId = getIdString(app.id)
  if (!appId) {
    return
  }
  const res = await deleteMyApp({ id: appId })
  if (res.data.code === 200) {
    message.success('删除成功')
    loadMyApps()
    loadFeaturedApps()
  } else {
    message.error(res.data.message || '删除失败')
  }
}

const goEdit = (app: API.AppVo) => {
  const appId = getIdString(app.id)
  if (!appId) {
    return
  }
  router.push(`/app/my/edit/${appId}`)
}

const goChat = (app: API.AppVo) => {
  const appId = getIdString(app.id)
  if (!appId) {
    return
  }
  router.push({
    path: `/app/chat/${appId}`,
    query: {
      view: '1',
    },
  })
}

const viewWork = (app: API.AppVo) => {
  const deployUrl = getAppDeployUrl(app)
  if (!deployUrl) {
    return
  }
  window.open(deployUrl, '_blank', 'noopener,noreferrer')
}

onMounted(async () => {
  if (!loginUserStore.loginUser.id) {
    await loginUserStore.fetchLoginUser()
  }
  loadMyApps()
  loadFeaturedApps()
})
</script>

<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-inner">
        <div class="hero-badge">AI Code Platform</div>
        <h1>一句话，生成完整网站应用</h1>
        <p>从提示词到页面预览、持续对话、部署上线，都在一个工作流里完成。</p>
        <div class="prompt-card">
          <a-textarea
            v-model:value="createForm.prompt"
            :auto-size="{ minRows: 5, maxRows: 7 }"
            placeholder="例如：帮我生成一个个人博客，包含首页、文章列表、文章详情和关于我页面，风格简洁现代。"
          />
          <div class="prompt-toolbar">
            <a-radio-group v-model:value="createForm.codeGenType" button-style="solid">
              <a-radio-button
                v-for="item in APP_CODE_GEN_TYPES"
                :key="item.value"
                :value="item.value"
              >
                {{ item.label }}
              </a-radio-button>
            </a-radio-group>
            <a-button type="primary" size="large" :loading="creating" @click="createAppFromPrompt">
              开始生成
            </a-button>
          </div>
        </div>
      </div>
    </section>

    <section class="content-section">
      <div class="section-header">
        <div>
          <h2>我的应用</h2>
          <p>支持按名称搜索，继续生成、编辑和删除自己的应用。</p>
        </div>
        <a-form layout="inline" @finish="loadMyApps">
          <a-form-item>
            <a-input
              v-model:value="mySearch.appName"
              allow-clear
              placeholder="搜索我的应用"
              @pressEnter="loadMyApps"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="loadMyApps">搜索</a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-empty
        v-if="!isLoggedIn"
        description="登录后可查看自己的应用和精选应用，并开始创建网站。"
      />
      <a-spin v-else :spinning="myLoading">
        <div class="card-grid">
          <AppCard
            v-for="item in myApps"
            :key="item.id"
            :app="item"
            editable
            @delete="handleDelete"
            @edit="goEdit"
            @chat="goChat"
            @view-work="viewWork"
          />
        </div>
        <a-empty v-if="!myLoading && myApps.length === 0" description="你还没有创建应用" />
        <div class="pagination-bar">
          <a-pagination
            v-model:current="mySearch.pageNum"
            v-model:page-size="mySearch.pageSize"
            :page-size-options="['6', '12', '18', '20']"
            :total="myTotal"
            show-size-changer
            @change="loadMyApps"
            @showSizeChange="loadMyApps"
          />
        </div>
      </a-spin>
    </section>

    <section class="content-section featured-section">
      <div class="section-header">
        <div>
          <h2>精选应用</h2>
          <p>管理员精选内容会优先展示，适合快速了解平台生成效果。</p>
        </div>
        <a-form layout="inline" @finish="loadFeaturedApps">
          <a-form-item>
            <a-input
              v-model:value="featuredSearch.appName"
              allow-clear
              placeholder="搜索精选应用"
              @pressEnter="loadFeaturedApps"
            />
          </a-form-item>
          <a-form-item>
            <a-button type="primary" ghost @click="loadFeaturedApps">搜索</a-button>
          </a-form-item>
        </a-form>
      </div>

      <a-spin :spinning="featuredLoading">
        <div class="card-grid">
          <AppCard
            v-for="item in featuredApps"
            :key="item.id"
            :app="item"
            :show-author="true"
            @chat="goChat"
            @view-work="viewWork"
          />
        </div>
        <a-empty v-if="!featuredLoading && featuredApps.length === 0" description="暂无精选应用" />
        <div class="pagination-bar">
          <a-pagination
            v-model:current="featuredSearch.pageNum"
            v-model:page-size="featuredSearch.pageSize"
            :page-size-options="['6', '12', '18', '20']"
            :total="featuredTotal"
            show-size-changer
            @change="loadFeaturedApps"
            @showSizeChange="loadFeaturedApps"
          />
        </div>
      </a-spin>
    </section>
  </div>
</template>

<style scoped>
.home-page {
  display: flex;
  flex-direction: column;
  gap: 28px;
}

.hero-section {
  position: relative;
  overflow: hidden;
  border-radius: 32px;
  padding: 56px 32px;
  background:
    radial-gradient(circle at top right, rgba(106, 227, 255, 0.45), transparent 28%),
    radial-gradient(circle at bottom left, rgba(71, 144, 255, 0.2), transparent 30%),
    linear-gradient(180deg, #f9fcff 0%, #e6f6ff 48%, #d8f4ff 100%);
}

.hero-inner {
  max-width: 900px;
  margin: 0 auto;
  text-align: center;
}

.hero-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 8px 14px;
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.86);
  color: #1e5672;
  font-size: 13px;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.hero-inner h1 {
  margin: 18px 0 12px;
  font-size: 48px;
  line-height: 1.15;
  color: #0f2233;
}

.hero-inner p {
  margin: 0 0 28px;
  color: #587085;
  font-size: 17px;
}

.prompt-card {
  padding: 22px;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.88);
  box-shadow: 0 24px 60px rgba(71, 109, 147, 0.12);
  border: 1px solid rgba(21, 58, 90, 0.08);
}

.prompt-toolbar {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: center;
  margin-top: 16px;
  flex-wrap: wrap;
}

.content-section {
  padding: 32px;
  border-radius: 32px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 50px rgba(31, 56, 88, 0.08);
}

.featured-section {
  background:
    linear-gradient(180deg, rgba(244, 251, 255, 0.92) 0%, rgba(255, 255, 255, 0.96) 100%);
}

.section-header {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  align-items: flex-start;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.section-header h2 {
  margin: 0;
  font-size: 32px;
  color: #11263a;
}

.section-header p {
  margin: 8px 0 0;
  color: #6d7f91;
}

.card-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}

@media (max-width: 768px) {
  .hero-section {
    padding: 36px 20px;
  }

  .hero-inner h1 {
    font-size: 34px;
  }

  .content-section {
    padding: 24px 18px;
  }

  .section-header h2 {
    font-size: 26px;
  }
}
</style>
