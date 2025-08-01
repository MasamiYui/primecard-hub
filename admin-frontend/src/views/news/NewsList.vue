<template>
  <div class="news-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">新闻管理</h1>
        <a-button type="primary" @click="handleCreate">
          <template #icon>
            <PlusOutlined />
          </template>
          新增新闻
        </a-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="标题">
          <a-input
            v-model:value="searchForm.title"
            placeholder="请输入新闻标题"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option value="DRAFT">草稿</a-select-option>
            <a-select-option value="PUBLISHED">已发布</a-select-option>
            <a-select-option value="ARCHIVED">已归档</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="分类">
          <a-select
            v-model:value="searchForm.categoryId"
            placeholder="请选择分类"
            allow-clear
            style="width: 150px"
          >
            <a-select-option
              v-for="category in categories"
              :key="category.id"
              :value="category.id"
            >
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="标签">
          <a-select
            v-model:value="searchForm.tagIds"
            mode="multiple"
            placeholder="请选择标签"
            allow-clear
            style="width: 200px"
          >
            <a-select-option
              v-for="tag in tags"
              :key="tag.id"
              :value="tag.id"
            >
              <a-tag :color="tag.color" style="margin: 0">{{ tag.name }}</a-tag>
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item>
          <a-button type="primary" html-type="submit">搜索</a-button>
          <a-button style="margin-left: 8px" @click="resetSearch">重置</a-button>
        </a-form-item>
      </a-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-header">
        <div class="table-actions">
          <a-button
            type="primary"
            danger
            :disabled="!hasSelected"
            @click="handleBatchDelete"
          >
            批量删除
          </a-button>
          <a-button
            :disabled="!hasSelected"
            @click="handleBatchPublish"
          >
            批量发布
          </a-button>
          <a-button
            :disabled="!hasSelected"
            @click="handleBatchArchive"
          >
            批量归档
          </a-button>
          <a-button @click="handleRefresh">
            <template #icon>
              <ReloadOutlined />
            </template>
            刷新
          </a-button>
        </div>
        <div class="table-info">
          <span v-if="hasSelected">
            已选择 {{ selectedRowKeys.length }} 项
            <a style="margin-left: 8px" @click="clearSelection">取消选择</a>
          </span>
        </div>
      </div>

      <a-table
        :columns="columns"
        :data-source="newsList"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'title'">
            <div class="title-cell">
              <a-tooltip :title="record.title">
                <span class="title-text">{{ record.title }}</span>
              </a-tooltip>
              <div class="title-meta">
                <span class="author">作者：{{ record.author }}</span>
                <span class="views">阅读：{{ record.viewCount || 0 }}</span>
              </div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'category'">
            <a-tag v-if="record.category" color="blue">
              {{ record.category.name }}
            </a-tag>
            <span v-else class="text-gray">未分类</span>
          </template>
          
          <template v-else-if="column.key === 'tags'">
            <div class="tags-cell">
              <a-tag
                v-for="tag in record.tags"
                :key="tag.id"
                :color="tag.color"
                style="margin: 2px"
              >
                {{ tag.name }}
              </a-tag>
              <span v-if="!record.tags || record.tags.length === 0" class="text-gray">
                无标签
              </span>
            </div>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'publishedAt'">
            <div v-if="record.publishedAt">
              {{ formatDate(record.publishedAt) }}
            </div>
            <span v-else class="text-gray">未发布</span>
          </template>
          
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a-button type="link" size="small" @click="handleView(record)">
                查看
              </a-button>
              <a-button type="link" size="small" @click="handleEdit(record)">
                编辑
              </a-button>
              <a-dropdown>
                 <template #overlay>
                   <a-menu @click="(info: any) => handleStatusChange(record, info.key)">
                     <a-menu-item key="PUBLISHED" :disabled="record.status === 'PUBLISHED'">
                       发布
                     </a-menu-item>
                     <a-menu-item key="DRAFT" :disabled="record.status === 'DRAFT'">
                       转为草稿
                     </a-menu-item>
                     <a-menu-item key="ARCHIVED" :disabled="record.status === 'ARCHIVED'">
                       归档
                     </a-menu-item>
                   </a-menu>
                 </template>
                 <a-button type="link" size="small">
                   更多 <DownOutlined />
                 </a-button>
               </a-dropdown>
              <a-button
                type="link"
                size="small"
                danger
                @click="handleDelete(record)"
              >
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined, DownOutlined } from '@ant-design/icons-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { newsApi } from '@/api/news'
import { categoryApi, tagApi } from '@/api/category'
import type { News, Category, Tag } from '@/types/api'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const newsList = ref<News[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const selectedRowKeys = ref<number[]>([])

// 搜索表单
const searchForm = reactive({
  title: '',
  status: undefined as string | undefined,
  categoryId: undefined as number | undefined,
  tagIds: [] as number[]
})

// 分页配置
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`
})

// 表格列配置
const columns: TableColumnsType = [
  {
    title: '标题',
    key: 'title',
    width: 300
  },
  {
    title: '分类',
    key: 'category',
    width: 120,
    align: 'center'
  },
  {
    title: '标签',
    key: 'tags',
    width: 200,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
    align: 'center'
  },
  {
    title: '发布时间',
    key: 'publishedAt',
    width: 150,
    align: 'center'
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 150,
    align: 'center'
  },
  {
    title: '操作',
    key: 'action',
    width: 200,
    align: 'center',
    fixed: 'right'
  }
]

// 计算属性
const hasSelected = computed(() => selectedRowKeys.value.length > 0)

// 行选择配置
const rowSelection = computed(() => ({
  selectedRowKeys: selectedRowKeys.value,
  onChange: (keys: any[]) => {
    selectedRowKeys.value = keys as number[]
  }
}))

// 方法
const loadNews = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      title: searchForm.title || undefined,
      status: searchForm.status || undefined,
      categoryId: searchForm.categoryId || undefined,
      tagIds: searchForm.tagIds.length > 0 ? searchForm.tagIds : undefined
    }
    
    const response = await newsApi.getList(params)
    newsList.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    message.error('加载新闻列表失败')
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const response = await categoryApi.getAll()
    categories.value = response.data
  } catch (error) {
    console.error('加载分类列表失败:', error)
  }
}

const loadTags = async () => {
  try {
    const response = await tagApi.getAll()
    tags.value = response.data
  } catch (error) {
    console.error('加载标签列表失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadNews()
}

const resetSearch = () => {
  searchForm.title = ''
  searchForm.status = undefined
  searchForm.categoryId = undefined
  searchForm.tagIds = []
  pagination.current = 1
  loadNews()
}

const handleRefresh = () => {
  loadNews()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadNews()
}

const clearSelection = () => {
  selectedRowKeys.value = []
}

const handleCreate = () => {
  router.push('/news/create')
}

const handleView = (record: News) => {
  router.push(`/news/${record.id}`)
}

const handleEdit = (record: News) => {
  router.push(`/news/${record.id}/edit`)
}

const handleDelete = (record: News) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除新闻"${record.title}"吗？`,
    onOk: async () => {
      try {
        await newsApi.delete(record.id)
        message.success('新闻删除成功')
        loadNews()
      } catch (error) {
        message.error('新闻删除失败')
      }
    }
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 条新闻吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => newsApi.delete(id)))
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadNews()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

const handleStatusChange = async (record: News, status: string) => {
  try {
    await newsApi.updateStatus(record.id, status)
    message.success('状态更新成功')
    loadNews()
  } catch (error) {
    message.error('状态更新失败')
  }
}

const handleBatchPublish = () => {
  Modal.confirm({
    title: '确认批量发布',
    content: `确定要发布选中的 ${selectedRowKeys.value.length} 条新闻吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => newsApi.updateStatus(id, 'PUBLISHED')))
        message.success('批量发布成功')
        selectedRowKeys.value = []
        loadNews()
      } catch (error) {
        message.error('批量发布失败')
      }
    }
  })
}

const handleBatchArchive = () => {
  Modal.confirm({
    title: '确认批量归档',
    content: `确定要归档选中的 ${selectedRowKeys.value.length} 条新闻吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => newsApi.updateStatus(id, 'ARCHIVED')))
        message.success('批量归档成功')
        selectedRowKeys.value = []
        loadNews()
      } catch (error) {
        message.error('批量归档失败')
      }
    }
  })
}

const getStatusColor = (status: string) => {
  const statusColors = {
    DRAFT: 'orange',
    PUBLISHED: 'green',
    ARCHIVED: 'gray'
  }
  return statusColors[status as keyof typeof statusColors] || 'default'
}

const getStatusText = (status: string) => {
  const statusTexts = {
    DRAFT: '草稿',
    PUBLISHED: '已发布',
    ARCHIVED: '已归档'
  }
  return statusTexts[status as keyof typeof statusTexts] || status
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadNews()
  loadCategories()
  loadTags()
})
</script>

<style scoped>
.news-list {
  padding: 24px;
  background: #fff;
  min-height: 100vh;
}

.page-header {
  margin-bottom: 24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #262626;
}

.search-section {
  background: #fafafa;
  padding: 16px;
  border-radius: 6px;
  margin-bottom: 16px;
}

.table-section {
  background: #fff;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-actions {
  display: flex;
  gap: 8px;
}

.table-info {
  color: #666;
  font-size: 14px;
}

.title-cell {
  text-align: left;
}

.title-text {
  display: block;
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 250px;
}

.title-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #8c8c8c;
}

.tags-cell {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 2px;
}

.text-gray {
  color: #8c8c8c;
}
</style>