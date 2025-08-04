<template>
  <div class="news-preview">
    <div v-if="loading" class="loading-spinner"></div>
    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-if="news" class="news-content">
      <h1 class="news-title">{{ news.title }}</h1>
      <div class="news-meta">
        <span>作者: {{ news.author }}</span>
        <span v-if="news.publishedAt">发布于: {{ formatDate(news.publishedAt) }}</span>
        <span v-else>未发布</span>
      </div>
      <div class="news-body" v-html="news.content"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { newsApi } from '@/api/news';
import type { News } from '@/types/api';

const route = useRoute();
const news = ref<News | null>(null);
const loading = ref(true);
const error = ref<string | null>(null);

const fetchNews = async (id: number) => {
  try {
    const response = await newsApi.getById(id);
    news.value = response.data;
  } catch (err) {
    error.value = '加载新闻失败。';
    console.error(err);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  const newsId = Number(route.params.id);
  if (newsId) {
    fetchNews(newsId);
  }
});

const formatDate = (dateString: string | undefined) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', { hour12: false });
};
</script>

<style scoped>
.news-preview {
  padding: 24px;
  background-color: #fff;
  border-radius: 4px;
}
.news-title {
  font-size: 28px;
  font-weight: 600;
  margin-bottom: 16px;
}
.news-meta {
  color: #888;
  margin-bottom: 24px;
}
.news-meta span {
  margin-right: 16px;
}
.news-body {
  line-height: 1.8;
}
</style>