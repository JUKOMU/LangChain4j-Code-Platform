<script setup lang="ts">
const props = defineProps<{
  src?: string
  loading?: boolean
  emptyText?: string
}>()
</script>

<template>
  <div class="preview-shell">
    <div class="preview-header">
      <span class="dot red"></span>
      <span class="dot yellow"></span>
      <span class="dot green"></span>
      <span class="header-text">实时网页预览</span>
    </div>
    <div class="preview-body">
      <a-spin v-if="loading" class="preview-state" tip="正在生成网页..." />
      <iframe v-else-if="props.src" class="preview-iframe" :src="props.src" title="app-preview" />
      <div v-else class="preview-state">
        {{ props.emptyText || '生成完成后会在这里展示网页效果' }}
      </div>
    </div>
  </div>
</template>

<style scoped>
.preview-shell {
  height: 100%;
  min-height: 680px;
  border-radius: 24px;
  overflow: hidden;
  border: 1px solid rgba(24, 53, 83, 0.08);
  background: white;
  box-shadow: 0 16px 40px rgba(40, 65, 95, 0.08);
}

.preview-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 18px;
  background: linear-gradient(90deg, #edf4ff 0%, #f8fbff 100%);
  border-bottom: 1px solid rgba(24, 53, 83, 0.08);
}

.dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.red {
  background: #ff6b6b;
}

.yellow {
  background: #ffcc4d;
}

.green {
  background: #34d399;
}

.header-text {
  margin-left: 4px;
  color: #4d6073;
  font-size: 13px;
}

.preview-body {
  height: calc(100% - 52px);
  min-height: 628px;
  background: #f5f8fb;
}

.preview-iframe {
  width: 100%;
  height: 100%;
  border: 0;
  background: white;
}

.preview-state {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #73859a;
  font-size: 15px;
}
</style>
