<template>
  <div class="credit-card-form">
    <h1>{{ isEdit ? '编辑信用卡' : '新增信用卡' }}</h1>
    <p>信用卡表单页面 - 开发中...</p>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const isEdit = computed(() => !!route.params.id)
</script>

<style scoped>
.credit-card-form {
  padding: 24px;
}
</style>