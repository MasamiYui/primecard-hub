<template>
  <div class="news-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">新闻管理</h1>
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增新闻
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="标题">
          <el-input
            v-model="searchForm.title"
            placeholder="请输入新闻标题"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已归档" value="ARCHIVED" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类">
          <el-select
            v-model="searchForm.categoryId"
            placeholder="请选择分类"
            clearable
            style="width: 150px"
          >
            <el-option
              v-for="category in categories"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-select
            v-model="searchForm.tagIds"
            multiple
            placeholder="请选择标签"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="tag in tags"
              :key="tag.id"
              :label="tag.name"
              :value="tag.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button style="margin-left: 8px" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 数据表格 -->
    <div class="table-section">
      <div class="table-header">
        <div class="table-actions">
          <el-button
            type="danger"
            :disabled="!hasSelected"
            @click="handleBatchDelete"
          >
            批量删除
          </el-button>
          <el-button
            :disabled="!hasSelected"
            @click="handleBatchPublish"
          >
            批量发布
          </el-button>
          <el-button
            :disabled="!hasSelected"
            @click="handleBatchArchive"
          >
            批量归档
          </el-button>
          <el-button @click="handleRefresh">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
        <div class="table-info">
          <span v-if="hasSelected">
            已选择 {{ selectedRows.length }} 项
            <el-link type="primary" style="margin-left: 8px" @click="clearSelection">取消选择</el-link>
          </span>
        </div>
      </div>

      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="newsList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        row-key="id"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="title" label="标题" width="300">
          <template #default="{ row }">
            <div class="title-cell">
              <el-tooltip :content="row.title" placement="top">
                <span class="title-text">{{ row.title }}</span>
              </el-tooltip>
              <div class="title-meta">
                <span class="author">作者：{{ row.author }}</span>
                <span class="views">阅读：{{ row.viewCount || 0 }}</span>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.category" type="success" size="small">{{ row.category.name }}</el-tag>
            <span v-else class="text-gray">未分类</span>
          </template>
        </el-table-column>
        <el-table-column prop="tags" label="标签" width="200" align="center">
          <template #default="{ row }">
            <div class="tags-cell">
              <el-tag
                v-for="tag in row.tags"
                :key="tag.id"
                :color="tag.color"
                size="small"
                style="margin: 2px"
              >
                {{ tag.name }}
              </el-tag>
              <span v-if="!row.tags || row.tags.length === 0" class="text-gray">
                无标签
              </span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="publishedAt" label="发布时间" width="180" align="center">
          <template #default="{ row }">
            <div v-if="row.publishedAt">
              {{ formatDate(row.publishedAt) }}
            </div>
            <span v-else class="text-gray">未发布</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="180" align="center">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right" align="center">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="handleView(row)">查看</el-button>
            <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
            <el-dropdown @command="handleStatusCommand(row)">
              <el-button type="primary" link size="small">
                更多<el-icon class="el-icon--right"><arrow-down /></el-icon>
              </el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="PUBLISHED" :disabled="row.status === 'PUBLISHED'">发布</el-dropdown-item>
                  <el-dropdown-item command="DRAFT" :disabled="row.status === 'DRAFT'">转为草稿</el-dropdown-item>
                  <el-dropdown-item command="ARCHIVED" :disabled="row.status === 'ARCHIVED'">归档</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
            <el-button
              type="danger"
              link
              size="small"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-section">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pagination.total"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox, ElTable } from 'element-plus';
import { Plus, Refresh, ArrowDown } from '@element-plus/icons-vue';
import { newsApi } from '@/api/news';
import { categoryApi } from '@/api/category';
import { tagApi } from '@/api/tag';
import type { News, Category, Tag } from '@/types/api';

const router = useRouter();
const tableRef = ref<InstanceType<typeof ElTable>>();

// 搜索表单
const searchForm = reactive({
  title: '',
  status: undefined as string | undefined,
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
});

// 表格数据
const loading = ref(false);
const newsList = ref<News[]>([]);
const categories = ref<Category[]>([]);
const tags = ref<Tag[]>([]);

// 分页
const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
});

// 行选择
const selectedRows = ref<News[]>([]);
const hasSelected = computed(() => selectedRows.value.length > 0);

const handleSelectionChange = (selection: News[]) => {
  selectedRows.value = selection;
};

const clearSelection = () => {
  tableRef.value?.clearSelection();
};

// 获取新闻列表
const fetchNewsList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      title: searchForm.title || undefined,
      status: searchForm.status || undefined,
      categoryId: searchForm.categoryId || undefined,
      tagIds: searchForm.tagIds.length > 0 ? searchForm.tagIds.join(',') : undefined,
    };
    const response = await newsApi.getList(params);
    newsList.value = response.data.content;
    pagination.total = response.data.totalElements;
  } catch (error) {
    ElMessage.error('获取新闻列表失败');
    console.error(error);
  } finally {
    loading.value = false;
  }
};

// 获取分类和标签
const fetchCategoriesAndTags = async () => {
  try {
    const [categoryRes, tagRes] = await Promise.all([
      categoryApi.getAll(),
      tagApi.getAll(),
    ]);
    categories.value = categoryRes.data;
    tags.value = tagRes.data;
  } catch (error) {
    ElMessage.error('获取分类和标签失败');
    console.error(error);
  }
};

onMounted(() => {
  fetchNewsList();
  fetchCategoriesAndTags();
});

// 搜索
const handleSearch = () => {
  pagination.current = 1;
  fetchNewsList();
};

// 重置搜索
const resetSearch = () => {
  searchForm.title = '';
  searchForm.status = undefined;
  searchForm.categoryId = undefined;
  searchForm.tagIds = [];
  handleSearch();
};

// 刷新
const handleRefresh = () => {
  fetchNewsList();
};

// 分页大小变化
const handleSizeChange = (size: number) => {
  pagination.pageSize = size;
  fetchNewsList();
};

// 当前页变化
const handleCurrentChange = (page: number) => {
  pagination.current = page;
  fetchNewsList();
};

// 新增新闻
const handleCreate = () => {
  router.push({ name: 'NewsCreate' });
};

// 查看新闻
const handleView = (row: News) => {
  const routeUrl = router.resolve({
    name: 'NewsPreview',
    params: { id: row.id },
  });
  window.open(routeUrl.href, '_blank');
};

// 编辑新闻
const handleEdit = (row: News) => {
  router.push({ name: 'NewsEdit', params: { id: row.id } });
};

// 删除新闻
const handleDelete = (record: News) => {
  ElMessageBox.confirm(`确定要删除新闻 "${record.title}" 吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    try {
      await newsApi.delete(record.id);
      ElMessage.success('新闻删除成功');
      fetchNewsList();
    } catch (error) {
      ElMessage.error('新闻删除失败');
    }
  }).catch(() => {
    // catch cancel
  });
};

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(
    `确定删除所选的 ${selectedRows.value.length} 篇新闻吗？此操作不可撤销。`,
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map((row) => row.id);
      await newsApi.batchDelete(ids);
      ElMessage.success('批量删除成功');
      fetchNewsList(); // 重新加载数据
    } catch (error) {
      ElMessage.error('批量删除失败');
      console.error(error);
    }
  }).catch(() => {
    // 用户取消操作
  });
};

// 更新新闻状态
const updateNewsStatus = async (ids: number[], status: string) => {
  try {
    await Promise.all(ids.map(id => newsApi.updateStatus(id, status)));
    ElMessage.success('操作成功');
    fetchNewsList();
    clearSelection();
  } catch (error) {
    ElMessage.error('操作失败');
    console.error(error);
  }
};

// 单个状态变更
const handleStatusCommand = (row: News) => (command: string) => {
  updateNewsStatus([row.id], command);
};

// 批量状态变更
const handleBatchPublish = () => {
  const ids = selectedRows.value.map((row) => row.id);
  updateNewsStatus(ids, 'PUBLISHED');
};

const handleBatchArchive = () => {
  const ids = selectedRows.value.map((row) => row.id);
  updateNewsStatus(ids, 'ARCHIVED');
};


// 状态显示
const getStatusType = (status: string): 'success' | 'warning' | 'info' => {
  switch (status) {
    case 'DRAFT':
      return 'warning';
    case 'PUBLISHED':
      return 'success';
    case 'ARCHIVED':
      return 'info';
    default:
      return 'info';
  }
};

const getStatusText = (status: string) => {
  switch (status) {
    case 'DRAFT':
      return '草稿';
    case 'PUBLISHED':
      return '已发布';
    case 'ARCHIVED':
      return '已归档';
    default:
      return '未知';
  }
};

// 日期格式化
const formatDate = (dateString: string) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', { hour12: false });
};

</script>

<style scoped>
.news-list {
  padding: 24px;
}

.page-header {
  background-color: #fff;
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  margin: -24px -24px 24px -24px;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
}

.search-section {
  background: #fff;
  padding: 24px;
  margin-bottom: 24px;
  border-radius: 4px;
}

.table-section {
  background: #fff;
  padding: 24px;
  border-radius: 4px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-actions > .el-button {
  margin-right: 8px;
}

.title-cell {
  display: flex;
  flex-direction: column;
}

.title-text {
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 250px; /* 根据需要调整 */
}

.title-meta {
  font-size: 12px;
  color: #8c8c8c;
  margin-top: 4px;
}

.title-meta .author {
  margin-right: 16px;
}

.tags-cell {
  max-width: 200px; /* 根据需要调整 */
}

.text-gray {
  color: #8c8c8c;
}

.pagination-section {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.el-icon--right {
  margin-left: 4px;
}
</style>