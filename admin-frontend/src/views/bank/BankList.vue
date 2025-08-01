<template>
  <div class="bank-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">银行管理</h1>
        <a-button type="primary" @click="showCreateModal">
          <template #icon>
            <PlusOutlined />
          </template>
          新增银行
        </a-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="银行名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入银行名称"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="银行代码">
          <a-input
            v-model:value="searchForm.code"
            placeholder="请输入银行代码"
            allow-clear
            style="width: 150px"
          />
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
        :data-source="banks"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'logo'">
            <a-image
              :src="record.logoUrl"
              :width="40"
              :height="40"
              :preview="true"
              fallback="/bank-placeholder.svg"
              style="border-radius: 4px"
            />
          </template>
          
          <template v-else-if="column.key === 'name'">
            <div class="bank-info">
              <div class="bank-name">{{ record.name }}</div>
              <div class="bank-code">{{ record.code }}</div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'createdAt'">
            {{ formatDate(record.createdAt) }}
          </template>
          
          <template v-else-if="column.key === 'action'">
            <a-space>
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

    <!-- 新增/编辑银行弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑银行' : '新增银行'"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
      @cancel="handleModalCancel"
      width="600px"
    >
      <a-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        layout="vertical"
      >
        <a-form-item label="银行名称" name="name">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入银行名称"
          />
        </a-form-item>
        
        <a-form-item label="银行代码" name="code">
          <a-input
            v-model:value="formData.code"
            placeholder="请输入银行代码"
          />
        </a-form-item>
        
        <a-form-item label="银行Logo" name="logoUrl">
          <a-upload
            v-model:file-list="fileList"
            list-type="picture-card"
            :before-upload="beforeUpload"
            @change="handleUploadChange"
            :max-count="1"
          >
            <div v-if="fileList.length < 1">
              <PlusOutlined />
              <div style="margin-top: 8px">上传Logo</div>
            </div>
          </a-upload>
        </a-form-item>
        
        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入银行描述"
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
import { bankApi } from '@/api/bank'
import type { Bank } from '@/types/api'

// 响应式数据
const loading = ref(false)
const banks = ref<Bank[]>([])
const selectedRowKeys = ref<number[]>([])
const modalVisible = ref(false)
const modalLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()
const fileList = ref<UploadFile[]>([])

// 搜索表单
const searchForm = reactive({
  name: '',
  code: ''
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
const formData = reactive<Omit<Bank, 'id' | 'createdAt' | 'updatedAt'> & { id?: number }>({
  name: '',
  code: '',
  logoUrl: '',
  description: ''
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入银行名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入银行代码', trigger: 'blur' }
  ]
}

// 表格列配置
const columns: TableColumnsType = [
  {
    title: 'Logo',
    key: 'logo',
    width: 80,
    align: 'center'
  },
  {
    title: '银行信息',
    key: 'name',
    width: 200
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true
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
    width: 150,
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
const loadBanks = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      code: searchForm.code || undefined
    }
    
    const response = await bankApi.getList(params)
    banks.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    message.error('加载银行列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadBanks()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.code = ''
  pagination.current = 1
  loadBanks()
}

const handleRefresh = () => {
  loadBanks()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadBanks()
}

const clearSelection = () => {
  selectedRowKeys.value = []
}

const showCreateModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

const handleEdit = (record: Bank) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    code: record.code,
    logoUrl: record.logoUrl,
    description: record.description
  })
  
  if (record.logoUrl) {
    fileList.value = [{
      uid: '-1',
      name: 'logo.jpg',
      status: 'done',
      url: record.logoUrl
    }]
  }
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    if (isEdit.value) {
      const { id, ...updateData } = formData
      await bankApi.update(id!, updateData)
      message.success('银行更新成功')
    } else {
      await bankApi.create(formData)
      message.success('银行创建成功')
    }
    
    modalVisible.value = false
    loadBanks()
  } catch (error) {
    message.error(isEdit.value ? '银行更新失败' : '银行创建失败')
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
  formData.code = ''
  formData.logoUrl = ''
  formData.description = ''
  fileList.value = []
  formRef.value?.resetFields()
}

const handleDelete = (record: Bank) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除银行"${record.name}"吗？`,
    onOk: async () => {
      try {
        await bankApi.delete(record.id)
        message.success('银行删除成功')
        loadBanks()
      } catch (error) {
        message.error('银行删除失败')
      }
    }
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个银行吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => bankApi.delete(id)))
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadBanks()
      } catch (error) {
        message.error('批量删除失败')
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
    formData.logoUrl = info.file.response?.data?.url || ''
  }
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadBanks()
})
</script>

<style scoped>
.bank-list {
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

.bank-info {
  text-align: left;
}

.bank-name {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.bank-code {
  font-size: 12px;
  color: #8c8c8c;
}
</style>