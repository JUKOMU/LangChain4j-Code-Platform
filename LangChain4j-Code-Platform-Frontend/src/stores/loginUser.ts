import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getLoginUser } from '@/api/userController.ts'
import ACCESS_ENUM from '@/access/accessEnum.ts'

export const useLoginUserStore = defineStore('loginUser', () => {
  // 默认值
  const loginUser = ref<API.LoginUserVo>({
    userName: '未登录',
    userRole: ACCESS_ENUM.NOT_LOGIN,
  })
  // 获取登录用户信息
  async function fetchLoginUser() {
    const res = await getLoginUser()
    if (res.data.code === 200 && res.data.data) {
      loginUser.value = res.data.data
    }
  }
  // 更新登录用户信息
  function setLoginUser(newLoginUser: API.LoginUserVo) {
    loginUser.value = newLoginUser
  }

  return { loginUser, fetchLoginUser, setLoginUser }
})
