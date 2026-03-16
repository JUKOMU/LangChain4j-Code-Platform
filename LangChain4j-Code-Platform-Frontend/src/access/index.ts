import { useLoginUserStore } from '@/stores/loginUser.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import ACCESS_ENUM from '@/access/accessEnum.ts'
import checkAccess from '@/access/checkAccess.ts'

/**
 * 全局权限校验
 */
router.beforeEach(async (to, from, next) => {
  const loginUserStore = useLoginUserStore()
  let loginUser = loginUserStore.loginUser
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN
  // 首次加载，需要获取到用户信息后再校验
  if (!loginUser.id) {
    await loginUserStore.fetchLoginUser()
    loginUser = loginUserStore.loginUser
  }

  if (needAccess !== ACCESS_ENUM.NOT_LOGIN) {
    if (loginUser.userRole === ACCESS_ENUM.NOT_LOGIN) {
      next(`/user/login?redirect=${to.fullPath}`)
      return
    }
  }
  if (!checkAccess(loginUser, needAccess)) {
    next(`/noAuth`)
    return
  }
  next()
})
