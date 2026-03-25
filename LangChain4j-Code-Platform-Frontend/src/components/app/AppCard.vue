<script setup lang="ts">
import { computed } from 'vue'
import { DeleteOutlined, EditOutlined, EyeOutlined, MessageOutlined } from '@ant-design/icons-vue'
import {
  formatRelativeTime,
  getAppDeployUrl,
  getCodeGenTypeLabel,
  getPriorityTagColor,
  getAppPreviewUrl,
} from '@/utils/app'

const props = defineProps<{
  app: API.AppVo
  editable?: boolean
  showAuthor?: boolean
}>()

const emit = defineEmits<{
  edit: [app: API.AppVo]
  delete: [app: API.AppVo]
  chat: [app: API.AppVo]
  viewWork: [app: API.AppVo]
}>()

const previewUrl = computed(() => getAppPreviewUrl(props.app))
const deployUrl = computed(() => getAppDeployUrl(props.app))
</script>

<template>
  <a-card class="app-card" hoverable>
    <div class="preview-wrapper">
      <iframe
        v-if="previewUrl"
        class="preview-frame"
        :src="previewUrl"
        title="preview"
        loading="lazy"
      />
      <div v-else class="preview-placeholder">
        <span>等待生成网页</span>
      </div>
    </div>
    <div class="card-body">
      <div class="card-main">
        <div class="title-row">
          <h3 class="app-title">{{ app.appName || '未命名应用' }}</h3>
          <a-tag :color="getPriorityTagColor(app.priority)">
            {{ (app.priority ?? 0) >= 99 ? '精选' : '应用' }}
          </a-tag>
        </div>
        <p class="meta-row">
          <span>{{ getCodeGenTypeLabel(app.codeGenType) }}</span>
          <span>{{ formatRelativeTime(app.updateTime || app.createTime) }}</span>
        </p>
        <p v-if="showAuthor" class="author-row">创建者：{{ app.user?.userName || '未知用户' }}</p>
      </div>
      <div class="actions">
        <a-button size="small" @click="emit('chat', app)">
          <template #icon><MessageOutlined /></template>
          查看对话
        </a-button>
        <a-button v-if="deployUrl" size="small" @click="emit('viewWork', app)">
          <template #icon><EyeOutlined /></template>
          查看作品
        </a-button>
        <a-button v-if="editable" size="small" @click="emit('edit', app)">
          <template #icon><EditOutlined /></template>
          编辑
        </a-button>
        <a-popconfirm
          v-if="editable"
          title="确认删除这个应用？"
          ok-text="删除"
          cancel-text="取消"
          @confirm="emit('delete', app)"
        >
          <a-button size="small" danger>
            <template #icon><DeleteOutlined /></template>
            删除
          </a-button>
        </a-popconfirm>
      </div>
    </div>
  </a-card>
</template>

<style scoped>
.app-card {
  border-radius: 24px;
  border: 1px solid rgba(18, 42, 66, 0.08);
  overflow: hidden;
  box-shadow: 0 16px 40px rgba(57, 84, 106, 0.08);
}

.preview-wrapper {
  height: 220px;
  overflow: hidden;
  border-radius: 18px;
  background: linear-gradient(135deg, #eef5ff 0%, #f7fbff 100%);
  border: 1px solid rgba(89, 126, 164, 0.12);
}

.preview-frame {
  width: 100%;
  height: 100%;
  border: 0;
  background: white;
}

.preview-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #7d8ca0;
  font-size: 14px;
}

.card-body {
  margin-top: 18px;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.app-title {
  margin: 0;
  font-size: 20px;
  color: #102336;
}

.meta-row,
.author-row {
  margin: 8px 0 0;
  color: #6b7a8c;
  font-size: 13px;
  display: flex;
  gap: 12px;
}

.actions {
  display: flex;
  gap: 8px;
  margin-top: 18px;
  flex-wrap: wrap;
}
</style>
