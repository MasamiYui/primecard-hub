<template>
  <div class="card-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">信用卡管理</h1>
        <a-button type="primary" @click="showCreateModal">
          <template #icon>
            <PlusOutlined />
          </template>
          新增信用卡
        </a-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="卡片名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入卡片名称"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="银行">
          <a-select
            v-model:value="searchForm.bankId"
            placeholder="请选择银行"
            allow-clear
            style="width: 150px"
          >
            <a-select-option
              v-for="bank in banks"
              :key="bank.id"
              :value="bank.id"
            >
              {{ bank.name }}
            </a-select-option>
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
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 120px"
          >
            <a-select-option value="ACTIVE">启用</a-select-option>
            <a-select-option value="INACTIVE">禁用</a-select-option>
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
            @click="handleBatchEnable"
          >
            批量启用
          </a-button>
          <a-button
            :disabled="!hasSelected"
            @click="handleBatchDisable"
          >
            批量禁用
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
        :data-source="cards"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'image'">
            <a-image
              :src="record.imageUrl"
              :width="60"
              :height="38"
              :preview="true"
              fallback="/card-placeholder.svg"
            />
          </template>
          
          <template v-else-if="column.key === 'name'">
            <div class="card-info">
              <div class="card-name">{{ record.name }}</div>
              <div class="card-subtitle">{{ record.subtitle }}</div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'bank'">
            <a-tag color="blue">{{ record.bank?.name }}</a-tag>
          </template>
          
          <template v-else-if="column.key === 'category'">
            <a-tag color="green">{{ record.category?.name }}</a-tag>
          </template>
          
          <template v-else-if="column.key === 'tags'">
            <a-space wrap>
              <a-tag
                v-for="tag in record.tags"
                :key="tag.id"
                color="orange"
                size="small"
              >
                {{ tag.name }}
              </a-tag>
            </a-space>
          </template>
          
          <template v-else-if="column.key === 'annualFee'">
            <span v-if="record.annualFee === 0" class="text-success">免年费</span>
            <span v-else>¥{{ record.annualFee }}</span>
          </template>
          
          <template v-else-if="column.key === 'status'">
            <a-switch
              :checked="record.status === 'ACTIVE'"
              :loading="record.statusLoading"
              @change="(checked: boolean) => handleStatusToggle(record, checked)"
            />
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

    <!-- 新增/编辑信用卡弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑信用卡' : '新增信用卡'"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="800px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="卡片名称" name="name">
              <a-input
                v-model:value="formData.name"
                placeholder="请输入卡片名称"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="副标题" name="subtitle">
              <a-input
                v-model:value="formData.subtitle"
                placeholder="请输入副标题"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="银行" name="bankId">
              <a-select
                v-model:value="formData.bankId"
                placeholder="请选择银行"
              >
                <a-select-option
                  v-for="bank in banks"
                  :key="bank.id"
                  :value="bank.id"
                >
                  {{ bank.name }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="分类" name="categoryId">
              <a-select
                v-model:value="formData.categoryId"
                placeholder="请选择分类"
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
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="年费" name="annualFee">
              <a-input-number
                v-model:value="formData.annualFee"
                placeholder="请输入年费"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="状态" name="status">
              <a-select v-model:value="formData.status" placeholder="请选择状态">
                <a-select-option value="ACTIVE">启用</a-select-option>
                <a-select-option value="INACTIVE">禁用</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-form-item label="标签" name="tagIds">
          <a-select
            v-model:value="formData.tagIds"
            mode="multiple"
            placeholder="请选择标签"
            style="width: 100%"
          >
            <a-select-option
              v-for="tag in tags"
              :key="tag.id"
              :value="tag.id"
            >
              {{ tag.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="卡片图片" name="imageUrl">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :before-upload="beforeUpload"
            @change="handleUploadChange"
            :max-count="1"
          >
            <div v-if="fileList.length < 1">
              <PlusOutlined />
              <div style="margin-top: 8px">上传图片</div>
            </div>
          </a-upload>
        </a-form-item>
        
        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入描述信息"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons-vue'
import type { TableColumnsType, UploadFile } from 'ant-design-vue'
import { cardApi } from '@/api/card'
import { bankApi } from '@/api/bank'
import { categoryApi } from '@/api/category'
import { tagApi } from '@/api/tag'
import type { Card, CardCreate, Bank, Category, Tag } from '@/types/api'

// 响应式数据
const loading = ref(false)
const cards = ref<(Card & { statusLoading?: boolean })[]>([])
const banks = ref<Bank[]>([])
const categories = ref<Category[]>([])
const tags = ref<Tag[]>([])
const selectedRowKeys = ref<number[]>([])
const modalVisible = ref(false)
const modalLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileList = ref<UploadFile[]>([])

// 搜索表单
const searchForm = reactive({
  name: '',
  bankId: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  status: undefined as string | undefined
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

// 表单数据
const formData = reactive<CardCreate & { id?: number }>({
  name: '',
  subtitle: '',
  bankId: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  annualFee: 0,
  status: 'ACTIVE',
  tagIds: [],
  imageUrl: '',
  description: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入卡片名称', trigger: 'blur' }
  ],
  bankId: [
    { required: true, message: '请选择银行', trigger: 'change' }
  ],
  categoryId: [
    { required: true, message: '请选择分类', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 表格列配置
const columns: TableColumnsType = [
  {
    title: '卡片图片',
    key: 'image',
    width: 100,
    align: 'center'
  },
  {
    title: '卡片信息',
    key: 'name',
    width: 200
  },
  {
    title: '银行',
    key: 'bank',
    width: 120,
    align: 'center'
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
    width: 200
  },
  {
    title: '年费',
    key: 'annualFee',
    width: 100,
    align: 'center'
  },
  {
    title: '状态',
    key: 'status',
    width: 100,
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
const loadCards = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      bankId: searchForm.bankId || undefined,
      categoryId: searchForm.categoryId || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await cardApi.getList(params)
    cards.value = response.data.content.map(card => ({ ...card, statusLoading: false }))
    pagination.total = response.data.totalElements
  } catch (error) {
    message.error('加载信用卡列表失败')
  } finally {
    loading.value = false
  }
}

const loadBanks = async () => {
  try {
    const response = await bankApi.getAll()
    banks.value = response.data
  } catch (error) {
    message.error('加载银行列表失败')
  }
}

const loadCategories = async () => {
  try {
    const response = await categoryApi.getAll()
    categories.value = response.data
  } catch (error) {
    message.error('加载分类列表失败')
  }
}

const loadTags = async () => {
  try {
    const response = await tagApi.getAll()
    tags.value = response.data
  } catch (error) {
    message.error('加载标签列表失败')
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadCards()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.bankId = undefined
  searchForm.categoryId = undefined
  searchForm.status = undefined
  pagination.current = 1
  loadCards()
}

const handleRefresh = () => {
  loadCards()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadCards()
}

const clearSelection = () => {
  selectedRowKeys.value = []
}

const showCreateModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

const handleView = (record: Card) => {
  message.info('查看信用卡功能待实现')
}

const handleEdit = (record: Card) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    subtitle: record.subtitle,
    bankId: record.bank?.id,
    categoryId: record.category?.id,
    annualFee: record.annualFee,
    status: record.status,
    tagIds: record.tags?.map(tag => tag.id) || [],
    imageUrl: record.imageUrl,
    description: record.description
  })
  
  if (record.imageUrl) {
    fileList.value = [{
      uid: '-1',
      name: 'image.jpg',
      status: 'done',
      url: record.imageUrl
    }]
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    if (isEdit.value) {
      const { id, ...updateData } = formData
      await cardApi.update(id!, updateData)
      message.success('信用卡更新成功')
    } else {
      await cardApi.create(formData)
      message.success('信用卡创建成功')
    }
    
    modalVisible.value = false
    loadCards()
  } catch (error) {
    message.error(isEdit.value ? '信用卡更新失败' : '信用卡创建失败')
  } finally {
    modalLoading.value = false
  }
}

const handleModalCancel = () => {
  modalVisible.value = false
  resetForm()
}

const resetForm = () => {
  formData.id = undefined
  formData.name = ''
  formData.subtitle = ''
  formData.bankId = undefined
  formData.categoryId = undefined
  formData.annualFee = 0
  formData.status = 'ACTIVE'
  formData.tagIds = []
  formData.imageUrl = ''
  formData.description = ''
  fileList.value = []
  formRef.value?.resetFields()
}

const handleStatusToggle = async (record: Card & { statusLoading?: boolean }, checked: boolean) => {
  try {
    record.statusLoading = true
    const newStatus = checked ? 'ACTIVE' : 'INACTIVE'
    await cardApi.updateStatus(record.id, newStatus)
    record.status = newStatus
    message.success('状态更新成功')
  } catch (error) {
    message.error('状态更新失败')
  } finally {
    record.statusLoading = false
  }
}

const handleDelete = (record: Card) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除信用卡"${record.name}"吗？`,
    onOk: async () => {
      try {
        await cardApi.delete(record.id)
        message.success('信用卡删除成功')
        loadCards()
      } catch (error) {
        message.error('信用卡删除失败')
      }
    }
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 张信用卡吗？`,
    onOk: async () => {
      try {
        await cardApi.batchDelete(selectedRowKeys.value)
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadCards()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

const handleBatchEnable = () => {
  Modal.confirm({
    title: '确认批量启用',
    content: `确定要启用选中的 ${selectedRowKeys.value.length} 张信用卡吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => cardApi.updateStatus(id, 'ACTIVE')))
        message.success('批量启用成功')
        selectedRowKeys.value = []
        loadCards()
      } catch (error) {
        message.error('批量启用失败')
      }
    }
  })
}

const handleBatchDisable = () => {
  Modal.confirm({
    title: '确认批量禁用',
    content: `确定要禁用选中的 ${selectedRowKeys.value.length} 张信用卡吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => cardApi.updateStatus(id, 'INACTIVE')))
        message.success('批量禁用成功')
        selectedRowKeys.value = []
        loadCards()
      } catch (error) {
        message.error('批量禁用失败')
      }
    }
  })
}

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
  if (!isJpgOrPng) {
    message.error('只能上传 JPG/PNG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  return false // 阻止自动上传
}

const handleUploadChange = (info: any) => {
  fileList.value = info.fileList
  if (info.file.status === 'done') {
    formData.imageUrl = info.file.response?.data?.url || ''
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadCards()
  loadBanks()
  loadCategories()
  loadTags()
})
</script>

<style scoped>
.card-list {
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

.card-info {
  text-align: left;
}

.card-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.card-subtitle {
  font-size: 12px;
  color: #8c8c8c;
}

.text-success {
  color: #52c41a;
  font-weight: 500;
}
</style>