<template>
  <div class="news-form">
    <h1>{{ isEdit ? '编辑新闻' : '新增新闻' }}</h1>
    <p>新闻表单页面 - 开发中...</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isEdit = computed(() => !!route.params.id)
</script>

<style scoped>
.news-form {
  padding: 24px;
}
</style>