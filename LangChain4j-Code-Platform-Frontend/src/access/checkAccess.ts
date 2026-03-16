import ACCESS_ENUM from '@/access/accessEnum.ts'

/**
 * 检查权限
 *
 * @param loginUser 当前登录用户
 * @param needAccess 需要的权限
 * @return boolean 有无权限
 */
const checkAccess = (loginUser: API.LoginUserVo, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  // 获取当前用户具有的权限
  const loginUserAccess = (loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN).toLocaleLowerCase()
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true
  }
  // 需要登录
  if (needAccess === ACCESS_ENUM.USER) {
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false
    }
  }
  // 需要管理员
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false
    }
  }
  return true
}

export default checkAccess
