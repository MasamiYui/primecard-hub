<template>
  <div class="banner-news-relation">
    <div class="page-header">
      <h1 class="page-title">轮播图关联关系管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="refreshData">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </div>
    </div>

    <!-- 关联关系统计 -->
    <div class="statistics-section">
      <el-row :gutter="16">
        <el-col :span="6">
          <el-card class="stat-card">
            <el-statistic title="总轮播图数量" :value="statistics.totalBanners" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <el-statistic title="关联新闻数量" :value="statistics.newsLinkedCount" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <el-statistic title="关联信用卡数量" :value="statistics.cardLinkedCount" />
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <el-statistic title="无关联数量" :value="statistics.unrelatedCount" />
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- Banner与新闻关联列表 -->
    <div class="relation-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>轮播图与新闻关联关系</span>
            <el-button type="success" size="small" @click="showBulkEditDialog">
              批量设置关联
            </el-button>
          </div>
        </template>

        <el-table :data="newsLinkedBanners" v-loading="loading">
          <el-table-column prop="title" label="轮播图标题" min-width="200" />
          <el-table-column label="关联新闻" min-width="200">
            <template #default="{ row }">
              <div v-if="row.relationStatus === 'VALID'" class="relation-valid">
                <el-icon><Document /></el-icon>
                <span style="margin-left: 4px;">{{ row.linkedNewsTitle }}</span>
                <el-tag size="small" type="success" style="margin-left: 8px;">有效</el-tag>
              </div>
              <div v-else class="relation-broken">
                <el-icon><Warning /></el-icon>
                <span style="margin-left: 4px;">新闻不存在 (ID: {{ row.linkId }})</span>
                <el-tag size="small" type="danger" style="margin-left: 8px;">损坏</el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
          <el-table-column label="操作" width="200" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="editRelation(row)">
                修改关联
              </el-button>
              <el-button 
                v-if="row.relationStatus === 'BROKEN'" 
                type="danger" 
                size="small" 
                @click="fixRelation(row)"
              >
                修复
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 无关联的轮播图 -->
    <div class="unrelated-section" v-if="unrelatedBanners.length > 0">
      <el-card>
        <template #header>
          <span>未关联新闻的轮播图</span>
        </template>

        <el-table :data="unrelatedBanners">
          <el-table-column prop="title" label="轮播图标题" min-width="200" />
          <el-table-column label="当前类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getLinkTypeTag(row.linkType)">{{ getLinkTypeLabel(row.linkType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="建议关联" min-width="250">
            <template #default="{ row }">
              <div class="recommended-news">
                <span v-if="!getRecommendedNews(row).length" class="no-recommendation">
                  暂无推荐
                </span>
                <div v-else>
                  <div 
                    v-for="news in getRecommendedNews(row).slice(0, 2)" 
                    :key="news.id"
                    class="recommendation-item"
                  >
                    <span class="news-title">{{ news.title }}</span>
                    <el-button 
                      type="text" 
                      size="small" 
                      @click="quickSetRelation(row, news)"
                    >
                      快速关联
                    </el-button>
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="setNewsRelation(row)">
                设置关联
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 关联设置对话框 -->
    <el-dialog 
      v-model="relationDialog.visible" 
      :title="relationDialog.title"
      width="600px"
    >
      <el-form :model="relationDialog.form" label-width="100px">
        <el-form-item label="轮播图">
          <el-input v-model="relationDialog.form.bannerTitle" disabled />
        </el-form-item>
        <el-form-item label="关联类型">
          <el-radio-group v-model="relationDialog.form.linkType">
            <el-radio :label="BannerLinkType.NEWS">新闻资讯</el-radio>
            <el-radio :label="BannerLinkType.CARD">信用卡</el-radio>
            <el-radio :label="BannerLinkType.EXTERNAL">外部链接</el-radio>
            <el-radio :label="BannerLinkType.NONE">无链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="relationDialog.form.linkType === BannerLinkType.NEWS" label="选择新闻">
          <el-select 
            v-model="relationDialog.form.linkId" 
            placeholder="请选择新闻"
            filterable
            style="width: 100%"
          >
            <el-option
              v-for="news in allNews"
              :key="news.id"
              :label="news.title"
              :value="news.id"
            >
              <div style="display: flex; justify-content: space-between;">
                <span>{{ news.title }}</span>
                <el-tag size="small" type="info">{{ news.categoryName }}</el-tag>
              </div>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="relationDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveRelation">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Document, Warning } from '@element-plus/icons-vue'
import { newsApi } from '@/api/news'
import { bannerApi } from '@/api/banner'
import { BannerLinkType } from '@/types/api'
import type { News, Banner } from '@/types/api'

// 数据状态
const loading = ref(false)
const newsLinkedBanners = ref<any[]>([])
const unrelatedBanners = ref<any[]>([])
const allNews = ref<News[]>([])
const allBanners = ref<Banner[]>([])

// 统计信息
const statistics = reactive({
  totalBanners: 0,
  newsLinkedCount: 0,
  cardLinkedCount: 0,
  unrelatedCount: 0
})

// 关联设置对话框
const relationDialog = reactive({
  visible: false,
  title: '',
  form: {
    bannerId: 0,
    bannerTitle: '',
    linkType: BannerLinkType.NEWS,
    linkId: null as number | null
  }
})

// 获取轮播图与新闻关联数据
const fetchRelationData = async () => {
  loading.value = true
  try {
    // 并行获取所有数据
    const [bannersRes, newsRes] = await Promise.all([
      bannerApi.getList({ page: 0, size: 100 }),
      newsApi.getList({ page: 0, size: 100, status: 'PUBLISHED' })
    ])

    allBanners.value = bannersRes.data.content || []
    allNews.value = newsRes.data.content || []

    // 分析关联关系
    analyzeRelations()

  } catch (error) {
    console.error('获取关联数据失败', error)
    ElMessage.error('获取关联数据失败')
  } finally {
    loading.value = false
  }
}

// 分析关联关系
const analyzeRelations = () => {
  const newsLinked: any[] = []
  const unrelated: any[] = []
  let cardLinkedCount = 0

  allBanners.value.forEach(banner => {
    if (banner.linkType === BannerLinkType.NEWS) {
      const linkedNews = allNews.value.find(n => n.id === banner.linkId)
      newsLinked.push({
        ...banner,
        linkedNewsTitle: linkedNews?.title || '未找到',
        relationStatus: linkedNews ? 'VALID' : 'BROKEN'
      })
    } else if (banner.linkType === BannerLinkType.CARD) {
      cardLinkedCount++
    } else if (banner.linkType === BannerLinkType.NONE || banner.linkType === BannerLinkType.EXTERNAL) {
      unrelated.push({
        ...banner,
        relationStatus: 'NO_RELATION'
      })
    }
  })

  newsLinkedBanners.value = newsLinked
  unrelatedBanners.value = unrelated

  // 更新统计
  statistics.totalBanners = allBanners.value.length
  statistics.newsLinkedCount = newsLinked.length
  statistics.cardLinkedCount = cardLinkedCount
  statistics.unrelatedCount = unrelated.length
}

// 获取推荐新闻
const getRecommendedNews = (banner: any) => {
  // 简单的标题相似度推荐
  return allNews.value
    .filter(news => {
      const similarity = calculateSimilarity(banner.title, news.title)
      return similarity > 0.1
    })
    .sort((a, b) => {
      const simA = calculateSimilarity(banner.title, a.title)
      const simB = calculateSimilarity(banner.title, b.title)
      return simB - simA
    })
    .slice(0, 3)
}

// 简单相似度计算
const calculateSimilarity = (str1: string, str2: string) => {
  const chars1 = str1.split('')
  const chars2 = str2.split('')
  let commonCount = 0
  
  chars1.forEach(char => {
    if (str2.includes(char)) {
      commonCount++
    }
  })
  
  return commonCount / Math.max(chars1.length, chars2.length)
}

// 获取链接类型标签样式
const getLinkTypeTag = (linkType: BannerLinkType) => {
  const typeMap = {
    [BannerLinkType.NONE]: 'info',
    [BannerLinkType.CARD]: 'success',
    [BannerLinkType.NEWS]: 'primary',
    [BannerLinkType.EXTERNAL]: 'warning',
    [BannerLinkType.MINIPROGRAM]: 'danger',
  }
  return typeMap[linkType] || 'info'
}

// 获取链接类型标签文本
const getLinkTypeLabel = (linkType: BannerLinkType) => {
  const labelMap = {
    [BannerLinkType.NONE]: '无链接',
    [BannerLinkType.CARD]: '信用卡',
    [BannerLinkType.NEWS]: '资讯',
    [BannerLinkType.EXTERNAL]: '外部链接',
    [BannerLinkType.MINIPROGRAM]: '小程序',
  }
  return labelMap[linkType] || '未知'
}

// 编辑关联关系
const editRelation = (banner: any) => {
  relationDialog.title = '修改关联关系'
  relationDialog.form.bannerId = banner.id
  relationDialog.form.bannerTitle = banner.title
  relationDialog.form.linkType = banner.linkType
  relationDialog.form.linkId = banner.linkId
  relationDialog.visible = true
}

// 设置新闻关联
const setNewsRelation = (banner: any) => {
  relationDialog.title = '设置新闻关联'
  relationDialog.form.bannerId = banner.id
  relationDialog.form.bannerTitle = banner.title
  relationDialog.form.linkType = BannerLinkType.NEWS
  relationDialog.form.linkId = null
  relationDialog.visible = true
}

// 快速设置关联
const quickSetRelation = async (banner: any, news: News) => {
  try {
    await bannerApi.update(banner.id, {
      ...banner,
      linkType: BannerLinkType.NEWS,
      linkId: news.id,
      linkUrl: null
    })
    ElMessage.success(`已将「${banner.title}」关联到「${news.title}」`)
    refreshData()
  } catch (error) {
    console.error('快速设置关联失败', error)
    ElMessage.error('设置关联失败')
  }
}

// 保存关联关系
const saveRelation = async () => {
  try {
    const banner = allBanners.value.find(b => b.id === relationDialog.form.bannerId)
    if (!banner) return

    await bannerApi.update(banner.id, {
      ...banner,
      linkType: relationDialog.form.linkType,
      linkId: relationDialog.form.linkId,
      linkUrl: relationDialog.form.linkType === BannerLinkType.EXTERNAL ? banner.linkUrl : null
    })

    ElMessage.success('关联关系保存成功')
    relationDialog.visible = false
    refreshData()
  } catch (error) {
    console.error('保存关联关系失败', error)
    ElMessage.error('保存关联关系失败')
  }
}

// 刷新数据
const refreshData = () => {
  fetchRelationData()
}

// 批量编辑对话框
const showBulkEditDialog = () => {
  ElMessage.info('批量编辑功能开发中...')
}

// 修复关联
const fixRelation = (banner: any) => {
  setNewsRelation(banner)
}

onMounted(() => {
  fetchRelationData()
})
</script>

<style scoped>
.banner-news-relation {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: 600;
  margin: 0;
}

.statistics-section {
  margin-bottom: 24px;
}

.stat-card {
  text-align: center;
}

.relation-section, .unrelated-section {
  margin-bottom: 24px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.relation-valid {
  display: flex;
  align-items: center;
  color: #67c23a;
}

.relation-broken {
  display: flex;
  align-items: center;
  color: #f56c6c;
}

.recommended-news {
  font-size: 12px;
}

.recommendation-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
  padding: 4px 8px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.news-title {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  margin-right: 8px;
}

.no-recommendation {
  color: #909399;
  font-style: italic;
}
</style>