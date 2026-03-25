export const APP_CODE_GEN_TYPES = [
  {
    label: '单页面',
    value: 'html',
    description: '适合落地页、展示页和简单工具页',
  },
  {
    label: '多文件结构',
    value: 'multi_file',
    description: '适合博客、后台和多页面应用',
  },
] as const

export type AppCodeGenType = (typeof APP_CODE_GEN_TYPES)[number]['value']

export const DEFAULT_APP_CODE_GEN_TYPE: AppCodeGenType = 'multi_file'

const PREVIEW_BASE_URL = 'http://localhost:8023/api/static'
const DEPLOY_BASE_URL = 'http://localhost:8023/deploy'

export function getIdString(id?: string | number | string[] | null) {
  if (id === undefined || id === null || id === '') {
    return ''
  }
  if (Array.isArray(id)) {
    return id[0] ? String(id[0]) : ''
  }
  return String(id)
}

export function getAppPreviewUrl(app?: API.AppVo | null) {
  const appId = getIdString(app?.id)
  if (!appId || !app?.codeGenType) {
    return ''
  }
  return `${PREVIEW_BASE_URL}/${app.codeGenType}_${appId}/`
}

export function getAppDeployUrl(app?: API.AppVo | null) {
  if (!app?.deployKey) {
    return ''
  }
  return `${DEPLOY_BASE_URL}/${app.deployKey}`
}

export function getCodeGenTypeLabel(codeGenType?: string) {
  return APP_CODE_GEN_TYPES.find((item) => item.value === codeGenType)?.label ?? codeGenType ?? '-'
}

export function formatDateTime(value?: string) {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  if (Number.isNaN(date.getTime())) {
    return value
  }
  return new Intl.DateTimeFormat('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  }).format(date)
}

export function formatRelativeTime(value?: string) {
  if (!value) {
    return '-'
  }
  const date = new Date(value)
  const diff = Date.now() - date.getTime()
  if (Number.isNaN(diff)) {
    return value
  }
  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  if (diff < hour) {
    return `${Math.max(1, Math.floor(diff / minute))} 分钟前`
  }
  if (diff < day) {
    return `${Math.max(1, Math.floor(diff / hour))} 小时前`
  }
  if (diff < 30 * day) {
    return `${Math.max(1, Math.floor(diff / day))} 天前`
  }
  return formatDateTime(value)
}

export function isAdmin(user?: API.LoginUserVo | null) {
  return user?.userRole?.toLowerCase() === 'admin'
}

export function isAppOwner(app?: API.AppVo | null, user?: API.LoginUserVo | null) {
  const appUserId = getIdString(app?.userId)
  const loginUserId = getIdString(user?.id)
  return Boolean(appUserId && loginUserId && appUserId === loginUserId)
}

export function buildNewAppName(prompt: string) {
  const trimmedPrompt = prompt.trim()
  if (!trimmedPrompt) {
    return '未命名应用'
  }
  return trimmedPrompt.slice(0, 18)
}

export function getPriorityTagColor(priority?: number) {
  if ((priority ?? 0) >= 99) {
    return 'gold'
  }
  if ((priority ?? 0) > 0) {
    return 'blue'
  }
  return 'default'
}
