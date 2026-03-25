declare namespace API {
  type AppAddDto = {
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
  }

  type AppAdminQueryDto = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppAdminUpdateDto = {
    id?: number | string
    appName?: string
    cover?: string
    priority?: number
  }

  type AppDeployDto = {
    appId?: number | string
  }

  type AppQueryDto = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    priority?: number
    userId?: number
  }

  type AppUpdateDto = {
    id?: number | string
    appName?: string
  }

  type AppVo = {
    id?: number | string
    appName?: string
    cover?: string
    initPrompt?: string
    codeGenType?: string
    deployKey?: string
    deployedTime?: string
    priority?: number
    userId?: number | string
    editTime?: string
    createTime?: string
    updateTime?: string
    user?: UserVo
  }

  type BaseResponseAppVo = {
    code?: number
    data?: AppVo
    message?: string
  }

  type BaseResponseBoolean = {
    code?: number
    data?: boolean
    message?: string
  }

  type BaseResponseLoginUserVo = {
    code?: number
    data?: LoginUserVo
    message?: string
  }

  type BaseResponseLong = {
    code?: number
    data?: number | string
    message?: string
  }

  type BaseResponsePageAppVo = {
    code?: number
    data?: PageAppVo
    message?: string
  }

  type BaseResponsePageUserVo = {
    code?: number
    data?: PageUserVo
    message?: string
  }

  type BaseResponseString = {
    code?: number
    data?: string
    message?: string
  }

  type BaseResponseUserVo = {
    code?: number
    data?: UserVo
    message?: string
  }

  type chatTogGenCodeParams = {
    appId: number | string
    userMessage: string
  }

  type DeleteRequest = {
    id?: number | string
  }

  type getAppByAdminParams = {
    id: number | string
  }

  type getAppParams = {
    id: number | string
  }

  type getUserParams = {
    id: number | string
  }

  type LoginUserVo = {
    id?: number | string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
    updateTime?: string
  }

  type PageAppVo = {
    records?: AppVo[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type PageUserVo = {
    records?: UserVo[]
    pageNumber?: number
    pageSize?: number
    totalPage?: number
    totalRow?: number
    optimizeCountQuery?: boolean
  }

  type ServerSentEventString = true

  type UserAddDto = {
    userName?: string
    userAccount?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserLoginDto = {
    userAccount?: string
    userPassword?: string
  }

  type UserQueryDto = {
    pageNum?: number
    pageSize?: number
    sortField?: string
    sortOrder?: string
    id?: number | string
    userName?: string
    userAccount?: string
    userProfile?: string
    userRole?: string
  }

  type UserRegisterDto = {
    userAccount?: string
    userPassword?: string
    checkPassword?: string
  }

  type UserUpdateDto = {
    id?: number | string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
  }

  type UserVo = {
    id?: number | string
    userAccount?: string
    userName?: string
    userAvatar?: string
    userProfile?: string
    userRole?: string
    createTime?: string
  }
}
