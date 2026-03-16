<script setup lang="ts">
import {healthCheck} from '@/api/healthController.ts'

healthCheck().then((res) => {
  console.log(res.data)
  })
</script>

<template>
  <main>
  </main>
</template>
