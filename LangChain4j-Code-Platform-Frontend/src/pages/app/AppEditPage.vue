<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { message } from 'ant-design-vue'
import { getApp, getAppByAdmin, updateApp, updateMyApp } from '@/api/appController'
import { useLoginUserStore } from '@/stores/loginUser'
import { getIdString, isAdmin } from '@/utils/app'

const route = useRoute()
const router = useRouter()
const loginUserStore = useLoginUserStore()

const loading = ref(false)
const submitting = ref(false)
const formState = reactive({
  appName: '',
  cover: '',
  priority: 0,
})

const appId = computed(() => getIdString(route.params.id))
const adminMode = computed(() => route.path.startsWith('/app/admin/edit'))

const loadApp = async () => {
  loading.value = true
  try {
    if (!appId.value) {
      message.error('应用 ID 无效')
      return
    }
    const api = adminMode.value ? getAppByAdmin : getApp
    const res = await api({ id: appId.value } as never)
    if (res.data.code === 200 && res.data.data) {
      formState.appName = res.data.data.appName || ''
      formState.cover = res.data.data.cover || ''
      formState.priority = res.data.data.priority ?? 0
      return
    }
    message.error(res.data.message || '获取应用信息失败')
  } finally {
    loading.value = false
  }
}

const submitForm = async () => {
  if (!formState.appName.trim()) {
    message.warning('请输入应用名称')
    return
  }
  submitting.value = true
  try {
    if (!appId.value) {
      message.error('应用 ID 无效')
      return
    }
    if (adminMode.value) {
      if (!isAdmin(loginUserStore.loginUser)) {
        message.error('仅管理员可编辑该页面')
        return
      }
      const res = await updateApp({
        id: appId.value,
        appName: formState.appName.trim(),
        cover: formState.cover.trim(),
        priority: formState.priority,
      })
      if (res.data.code === 200) {
        message.success('更新成功')
        router.push(`/app/detail/${appId.value}`)
        return
      }
      message.error(res.data.message || '更新失败')
      return
    }
    const res = await updateMyApp({
      id: appId.value,
      appName: formState.appName.trim(),
    })
    if (res.data.code === 200) {
      message.success('更新成功')
      router.push(`/app/detail/${appId.value}`)
      return
    }
    message.error(res.data.message || '更新失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadApp()
})
</script>

<template>
  <a-spin :spinning="loading">
    <div class="edit-page">
      <div class="page-head">
        <h1>{{ adminMode ? '编辑应用信息' : '修改我的应用' }}</h1>
        <p>{{ adminMode ? '管理员可更新应用名称、封面和优先级。' : '普通用户当前仅支持修改应用名称。' }}</p>
      </div>

      <a-form layout="vertical" class="edit-form">
        <a-form-item label="应用名称" required>
          <a-input v-model:value="formState.appName" placeholder="请输入应用名称" />
        </a-form-item>

        <template v-if="adminMode">
          <a-form-item label="应用封面">
            <a-input v-model:value="formState.cover" placeholder="请输入封面 URL" />
          </a-form-item>
          <a-form-item label="优先级">
            <a-input-number v-model:value="formState.priority" :min="0" :max="999" style="width: 100%" />
          </a-form-item>
        </template>

        <div class="form-actions">
          <a-button @click="router.back()">取消</a-button>
          <a-button type="primary" :loading="submitting" @click="submitForm">保存</a-button>
        </div>
      </a-form>
    </div>
  </a-spin>
</template>

<style scoped>
.edit-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px;
  border-radius: 28px;
  background: white;
  box-shadow: 0 16px 40px rgba(40, 65, 95, 0.08);
}

.page-head h1 {
  margin: 0;
  color: #12293e;
}

.page-head p {
  margin: 10px 0 0;
  color: #6f8197;
}

.edit-form {
  margin-top: 28px;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
