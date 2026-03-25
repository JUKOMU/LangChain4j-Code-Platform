<template>
  <a-layout-header class="header">
    <a-row :wrap="false" align="middle">
      <a-col flex="320px">
        <RouterLink to="/" class="brand-link">
          <div class="header-left">
            <img class="logo" src="@/assets/logo.png" alt="Logo" />
            <div>
              <h1 class="site-title">AI 应用生成平台</h1>
              <p class="site-subtitle">对话式创建网站与应用</p>
            </div>
          </div>
        </RouterLink>
      </a-col>
      <a-col flex="auto">
        <a-menu
          v-model:selectedKeys="selectedKeys"
          mode="horizontal"
          :items="menuItems"
          @click="handleMenuClick"
        />
      </a-col>
      <a-col>
        <div class="user-login-status">
          <div v-if="loginUserStore.loginUser.id">
            <a-dropdown>
              <a-space>
                <a-avatar :src="loginUserStore.loginUser.userAvatar" />
                {{ loginUserStore.loginUser.userName ?? '无名用户' }}
              </a-space>
              <template #overlay>
                <a-menu>
                  <a-menu-item @click="handleLogout">
                    <LogoutOutlined />
                    退出登录
                  </a-menu-item>
                </a-menu>
              </template>
            </a-dropdown>
          </div>
          <div v-else>
            <a-button type="primary" href="/user/login">登录</a-button>
          </div>
        </div>
      </a-col>
    </a-row>
  </a-layout-header>
</template>

<script setup lang="ts">
import { computed, h, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { type MenuProps, message } from 'ant-design-vue'
import { AppstoreOutlined, HomeOutlined, LogoutOutlined, TeamOutlined } from '@ant-design/icons-vue'
import { useLoginUserStore } from '@/stores/loginUser'
import { logout } from '@/api/userController'
import ACCESS_ENUM from '@/access/accessEnum'
import checkAccess from '@/access/checkAccess'

const handleLogout = async () => {
  const res = await logout()
  if (res.data.code === 200) {
    loginUserStore.setLoginUser({
      userName: '未登录',
      userRole: ACCESS_ENUM.NOT_LOGIN,
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error(`退出登录失败，${res.data.message}`)
  }
}

const loginUserStore = useLoginUserStore()
const router = useRouter()
const selectedKeys = ref<string[]>(['/'])

router.afterEach((to) => {
  selectedKeys.value = [to.path]
})

const originMenuItems = [
  {
    key: '/',
    icon: () => h(HomeOutlined),
    label: '首页',
    title: '首页',
  },
  {
    key: '/admin/userManage',
    icon: () => h(TeamOutlined),
    label: '用户管理',
    title: '用户管理',
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    key: '/admin/appManage',
    icon: () => h(AppstoreOutlined),
    label: '应用管理',
    title: '应用管理',
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
]

const filterMenus = (menus = [] as typeof originMenuItems) =>
  menus.filter((menu) => checkAccess(loginUserStore.loginUser, menu?.meta?.access as string))

const menuItems = computed<MenuProps['items']>(() => filterMenus(originMenuItems))

const handleMenuClick: MenuProps['onClick'] = (event) => {
  const key = event.key as string
  selectedKeys.value = [key]
  if (key.startsWith('/')) {
    router.push(key)
  }
}

onMounted(() => {
  if (!loginUserStore.loginUser.id) {
    loginUserStore.fetchLoginUser()
  }
})
</script>

<style scoped>
.header {
  position: sticky;
  top: 0;
  z-index: 100;
  height: auto;
  line-height: normal;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(14px);
  padding: 10px 24px;
  border-bottom: 1px solid rgba(16, 38, 58, 0.06);
}

.brand-link {
  display: block;
}

.header :deep(.ant-row) {
  min-height: 44px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;
}

.logo {
  height: 24px;
  width: 24px;
  flex: 0 0 auto;
}

.site-title {
  margin: 0;
  font-size: 18px;
  line-height: 1.2;
  color: #15344d;
}

.site-subtitle {
  margin: 2px 0 0;
  color: #75879a;
  font-size: 12px;
  line-height: 1.2;
}

.ant-menu-horizontal {
  border-bottom: none !important;
  background: transparent;
}
</style>
