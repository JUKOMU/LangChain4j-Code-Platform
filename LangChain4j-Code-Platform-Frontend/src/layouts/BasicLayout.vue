<template>
  <a-layout class="basic-layout">
    <GlobalHeader />
    <a-layout-content class="main-content">
      <router-view />
    </a-layout-content>
    <GlobalFooter />
  </a-layout>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalFooter from '@/components/GlobalFooter.vue'
</script>

<style scoped>
.basic-layout {
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(117, 228, 255, 0.22), transparent 28%),
    linear-gradient(180deg, #f7fbff 0%, #eaf5ff 100%);
}

.main-content {
  width: min(1320px, calc(100% - 32px));
  margin: 20px auto 32px;
}
</style>
