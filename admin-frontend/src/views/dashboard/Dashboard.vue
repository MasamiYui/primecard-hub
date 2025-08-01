<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>仪表板</h1>
      <p>欢迎回来，{{ userStore.user?.username }}！</p>
    </div>

    <!-- 统计卡片 -->
    <a-row :gutter="[24, 24]" class="stats-row">
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="总用户数"
            :value="statistics?.totalUsers || 0"
            :prefix="h(UserOutlined)"
            :value-style="{ color: '#3f8600' }"
          />
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="信用卡数量"
            :value="statistics?.totalCreditCards || 0"
            :prefix="h(CreditCardOutlined)"
            :value-style="{ color: '#1890ff' }"
          />
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="新闻文章"
            :value="statistics?.totalNews || 0"
            :prefix="h(FileTextOutlined)"
            :value-style="{ color: '#722ed1' }"
          />
        </a-card>
      </a-col>
      
      <a-col :xs="24" :sm="12" :lg="6">
        <a-card class="stat-card">
          <a-statistic
            title="分类数量"
            :value="statistics?.totalCategories || 0"
            :prefix="h(AppstoreOutlined)"
            :value-style="{ color: '#fa8c16' }"
          />
        </a-card>
      </a-col>
    </a-row>

    <!-- 内容区域 -->
    <a-row :gutter="[24, 24]" class="content-row">
      <!-- 最近用户 -->
      <a-col :xs="24" :lg="12">
        <a-card title="最近注册用户" class="content-card">
          <template #extra>
            <router-link to="/users">查看全部</router-link>
          </template>
          
          <a-list
            :data-source="statistics?.recentUsers || []"
            :loading="loading"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar :style="{ backgroundColor: getRandomColor() }">
                      {{ item.username?.charAt(0).toUpperCase() }}
                    </a-avatar>
                  </template>
                  <template #title>
                    {{ item.username }}
                  </template>
                  <template #description>
                    {{ item.email }} • {{ formatDate(item.createdAt) }}
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>

      <!-- 最近新闻 -->
      <a-col :xs="24" :lg="12">
        <a-card title="最新新闻" class="content-card">
          <template #extra>
            <router-link to="/news">查看全部</router-link>
          </template>
          
          <a-list
            :data-source="statistics?.recentNews || []"
            :loading="loading"
          >
            <template #renderItem="{ item }">
              <a-list-item>
                <a-list-item-meta>
                  <template #avatar>
                    <a-avatar
                      v-if="item.imageUrl"
                      :src="item.imageUrl"
                      shape="square"
                    />
                    <a-avatar
                      v-else
                      shape="square"
                      :style="{ backgroundColor: '#f56a00' }"
                    >
                      <FileTextOutlined />
                    </a-avatar>
                  </template>
                  <template #title>
                    {{ item.title }}
                  </template>
                  <template #description>
                    <div class="news-meta">
                      <span>{{ item.author?.username }}</span>
                      <span>{{ formatDate(item.createdAt) }}</span>
                      <a-tag
                        :color="item.isPublished ? 'green' : 'orange'"
                        size="small"
                      >
                        {{ item.isPublished ? '已发布' : '草稿' }}
                      </a-tag>
                    </div>
                  </template>
                </a-list-item-meta>
              </a-list-item>
            </template>
          </a-list>
        </a-card>
      </a-col>
    </a-row>

    <!-- 快速操作 -->
    <a-card title="快速操作" class="quick-actions">
      <a-row :gutter="[16, 16]">
        <a-col :xs="24" :sm="12" :md="6">
          <a-button
            type="primary"
            size="large"
            block
            @click="$router.push('/credit-cards/create')"
          >
            <CreditCardOutlined />
            新增信用卡
          </a-button>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="6">
          <a-button
            type="primary"
            size="large"
            block
            @click="$router.push('/news/create')"
          >
            <FileTextOutlined />
            发布新闻
          </a-button>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="6">
          <a-button
            size="large"
            block
            @click="$router.push('/categories')"
          >
            <AppstoreOutlined />
            管理分类
          </a-button>
        </a-col>
        
        <a-col :xs="24" :sm="12" :md="6">
          <a-button
            size="large"
            block
            @click="$router.push('/tags')"
          >
            <TagsOutlined />
            管理标签
          </a-button>
        </a-col>
      </a-row>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, h } from 'vue'
import { useUserStore } from '@/stores/user'
import { statisticsApi } from '@/api/statistics'
import {
  UserOutlined,
  CreditCardOutlined,
  FileTextOutlined,
  AppstoreOutlined,
  TagsOutlined,
} from '@ant-design/icons-vue'
import type { Statistics } from '@/types/api'
import dayjs from 'dayjs'

const userStore = useUserStore()

const statistics = ref<Statistics | null>(null)
const loading = ref(false)

// 获取统计数据
const fetchStatistics = async () => {
  try {
    loading.value = true
    const response = await statisticsApi.getDashboard()
    if (response.success) {
      statistics.value = response.data
    }
  } catch (error) {
    console.error('Failed to fetch statistics:', error)
  } finally {
    loading.value = false
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 获取随机颜色
const getRandomColor = () => {
  const colors = ['#f56a00', '#7265e6', '#ffbf00', '#00a2ae', '#87d068']
  return colors[Math.floor(Math.random() * colors.length)]
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.dashboard-header {
  margin-bottom: 24px;
}

.dashboard-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
}

.dashboard-header p {
  color: #666;
  margin: 0;
  font-size: 16px;
}

.stats-row {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.content-row {
  margin-bottom: 24px;
}

.content-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.content-card :deep(.ant-card-body) {
  padding: 16px;
}

.news-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #666;
}

.quick-actions {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.quick-actions :deep(.ant-btn) {
  height: 48px;
  border-radius: 8px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

:deep(.ant-statistic-title) {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

:deep(.ant-statistic-content) {
  font-size: 24px;
  font-weight: 600;
}

:deep(.ant-list-item-meta-title) {
  font-size: 14px;
  font-weight: 500;
}

:deep(.ant-list-item-meta-description) {
  font-size: 12px;
}

@media (max-width: 768px) {
  .dashboard-header h1 {
    font-size: 24px;
  }
  
  .dashboard-header p {
    font-size: 14px;
  }
  
  :deep(.ant-statistic-content) {
    font-size: 20px;
  }
}
</style>