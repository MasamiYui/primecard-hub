<template>
  <div class="credit-card-list">
    <div class="page-header">
      <div class="header-content">
        <h1>信用卡管理</h1>
        <p>管理所有信用卡信息</p>
      </div>
      <div class="header-actions">
        <a-button type="primary" @click="$router.push('/credit-cards/create')">
          <PlusOutlined />
          新增信用卡
        </a-button>
      </div>
    </div>

    <!-- 搜索和筛选 -->
    <a-card class="search-card">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="搜索">
          <a-input
            v-model:value="searchForm.search"
            placeholder="搜索信用卡名称或银行"
            style="width: 200px"
            allow-clear
          />
        </a-form-item>
        
        <a-form-item label="卡片类型">
          <a-select
            v-model:value="searchForm.cardType"
            placeholder="选择卡片类型"
            style="width: 150px"
            allow-clear
          >
            <a-select-option value="CREDIT">信用卡</a-select-option>
            <a-select-option value="DEBIT">借记卡</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.isActive"
            placeholder="选择状态"
            style="width: 120px"
            allow-clear
          >
            <a-select-option :value="true">启用</a-select-option>
            <a-select-option :value="false">禁用</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">
              <SearchOutlined />
              搜索
            </a-button>
            <a-button @click="handleReset">
              重置
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <!-- 数据表格 -->
    <a-card class="table-card">
      <template #title>
        <div class="table-header">
          <span>信用卡列表</span>
          <a-space>
            <a-button
              v-if="selectedRowKeys.length > 0"
              danger
              @click="handleBatchDelete"
            >
              <DeleteOutlined />
              批量删除 ({{ selectedRowKeys.length }})
            </a-button>
            <a-button @click="fetchData">
              <ReloadOutlined />
              刷新
            </a-button>
          </a-space>
        </div>
      </template>

      <a-table
        :columns="columns"
        :data-source="dataSource"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <a-avatar
              v-if="record.imageUrl"
              :src="record.imageUrl"
              shape="square"
              :size="48"
            />
            <a-avatar
              v-else
              shape="square"
              :size="48"
              style="background-color: #f0f0f0; color: #999"
            >
              <CreditCardOutlined />
            </a-avatar>
          </template>
          
          <template v-else-if="column.key === 'name'">
            <div class="card-info">
              <div class="card-name">{{ record.name }}</div>
              <div class="card-bank">{{ record.bankName }}</div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'cardType'">
            <a-tag :color="record.cardType === 'CREDIT' ? 'blue' : 'green'">
              {{ record.cardType === 'CREDIT' ? '信用卡' : '借记卡' }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'annualFee'">
            <span class="annual-fee">
              {{ record.annualFee === 0 ? '免年费' : `¥${record.annualFee}` }}
            </span>
          </template>
          
          <template v-else-if="column.key === 'categories'">
            <a-space wrap>
              <a-tag
                v-for="category in record.categories"
                :key="category.id"
                color="purple"
              >
                {{ category.name }}
              </a-tag>
            </a-space>
          </template>
          
          <template v-else-if="column.key === 'tags'">
            <a-space wrap>
              <a-tag
                v-for="tag in record.tags"
                :key="tag.id"
                :color="tag.color"
              >
                {{ tag.name }}
              </a-tag>
            </a-space>
          </template>
          
          <template v-else-if="column.key === 'isActive'">
            <a-switch
              :checked="record.isActive"
              @change="(checked) => handleStatusChange(record, checked)"
            />
          </template>
          
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
          </template>
          
          <template v-else-if="column.key === 'actions'">
            <a-space>
              <a-button
                type="link"
                size="small"
                @click="$router.push(`/credit-cards/${record.id}/edit`)"
              >
                <EditOutlined />
                编辑
              </a-button>
              <a-popconfirm
                title="确定要删除这张信用卡吗？"
                @confirm="handleDelete(record.id)"
              >
                <a-button type="link" size="small" danger>
                  <DeleteOutlined />
                  删除
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import { creditCardApi } from '@/api/creditCard'
import {
  PlusOutlined,
  SearchOutlined,
  ReloadOutlined,
  EditOutlined,
  DeleteOutlined,
  CreditCardOutlined,
} from '@ant-design/icons-vue'
import type { CreditCard, QueryParams } from '@/types/api'
import dayjs from 'dayjs'

const dataSource = ref<CreditCard[]>([])
const loading = ref(false)
const selectedRowKeys = ref<number[]>([])

const searchForm = reactive({
  search: '',
  cardType: undefined,
  isActive: undefined,
})

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showSizeChanger: true,
  showQuickJumper: true,
  showTotal: (total: number) => `共 ${total} 条记录`,
})

const columns = [
  {
    title: '图片',
    key: 'image',
    width: 80,
  },
  {
    title: '信用卡信息',
    key: 'name',
    width: 200,
  },
  {
    title: '卡片类型',
    key: 'cardType',
    width: 100,
  },
  {
    title: '年费',
    key: 'annualFee',
    width: 100,
  },
  {
    title: '分类',
    key: 'categories',
    width: 150,
  },
  {
    title: '标签',
    key: 'tags',
    width: 150,
  },
  {
    title: '状态',
    key: 'isActive',
    width: 80,
  },
  {
    title: '创建时间',
    key: 'createdAt',
    width: 150,
  },
  {
    title: '操作',
    key: 'actions',
    width: 150,
    fixed: 'right',
  },
]

const rowSelection = {
  selectedRowKeys,
  onChange: (keys: number[]) => {
    selectedRowKeys.value = keys
  },
}

// 获取数据
const fetchData = async () => {
  try {
    loading.value = true
    const params: QueryParams = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      ...searchForm,
    }
    
    const response = await creditCardApi.getList(params)
    if (response.success) {
      dataSource.value = response.data.content
      pagination.total = response.data.totalElements
    }
  } catch (error) {
    console.error('Failed to fetch credit cards:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

// 重置
const handleReset = () => {
  Object.assign(searchForm, {
    search: '',
    cardType: undefined,
    isActive: undefined,
  })
  pagination.current = 1
  fetchData()
}

// 表格变化
const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  fetchData()
}

// 状态变更
const handleStatusChange = async (record: CreditCard, checked: boolean) => {
  try {
    await creditCardApi.update(record.id, { isActive: checked })
    record.isActive = checked
    message.success('状态更新成功')
  } catch (error) {
    message.error('状态更新失败')
  }
}

// 删除
const handleDelete = async (id: number) => {
  try {
    await creditCardApi.delete(id)
    message.success('删除成功')
    fetchData()
  } catch (error) {
    message.error('删除失败')
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await creditCardApi.batchDelete(selectedRowKeys.value)
    message.success('批量删除成功')
    selectedRowKeys.value = []
    fetchData()
  } catch (error) {
    message.error('批量删除失败')
  }
}

// 格式化日期
const formatDate = (date: string) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.credit-card-list {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 24px;
}

.header-content h1 {
  font-size: 24px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 4px 0;
}

.header-content p {
  color: #666;
  margin: 0;
  font-size: 14px;
}

.search-card {
  margin-bottom: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table-card {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.card-info {
  display: flex;
  flex-direction: column;
}

.card-name {
  font-weight: 500;
  color: #1a1a1a;
  margin-bottom: 2px;
}

.card-bank {
  font-size: 12px;
  color: #666;
}

.annual-fee {
  font-weight: 500;
  color: #1890ff;
}

:deep(.ant-table-thead > tr > th) {
  background-color: #fafafa;
  font-weight: 600;
}

:deep(.ant-table-tbody > tr:hover > td) {
  background-color: #f5f5f5;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .header-actions {
    align-self: flex-start;
  }
  
  :deep(.ant-form-inline .ant-form-item) {
    margin-right: 0;
    margin-bottom: 16px;
  }
}
</style>