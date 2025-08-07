<template>
  <div class="category-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">分类管理</h1>
        <div class="header-actions">
          <a-button v-if="sortMode" type="primary" @click="saveSortOrder" :loading="sortLoading">
            保存排序
          </a-button>
          <a-button v-if="sortMode" @click="cancelSortMode">
            取消
          </a-button>
          <a-button v-if="!sortMode" type="primary" @click="enableSortMode">
            <template #icon>
              <MenuOutlined />
            </template>
            排序
          </a-button>
          <a-button type="primary" @click="showCreateModal">
            <template #icon>
              <PlusOutlined />
            </template>
            新增分类
          </a-button>
        </div>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="分类名称">
          <a-input
            v-model:value="searchForm.name"
            placeholder="请输入分类名称"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="searchForm.status"
            placeholder="请选择状态"
            allow-clear
            style="width: 150px"
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
      <div class="table-header" v-if="!sortMode">
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

      <!-- 普通表格模式 -->
      <a-table
        v-if="!sortMode"
        :columns="columns"
        :data-source="categories"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'status'">
            <a-tag :color="record.status === 'ACTIVE' ? 'green' : 'red'">
              {{ record.status === 'ACTIVE' ? '启用' : '禁用' }}
            </a-tag>
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
              <a-button
                type="link"
                size="small"
                @click="handleToggleStatus(record)"
              >
                {{ record.status === 'ACTIVE' ? '禁用' : '启用' }}
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>

      <!-- 排序模式 -->
      <div v-if="sortMode" class="sort-table-container">
        <div class="sort-tip">
          <InfoCircleOutlined /> 提示：拖拽分类项可调整顺序，完成后点击"保存排序"按钮
        </div>
        <draggable 
          v-model="sortableCategories" 
          item-key="id"
          handle=".drag-handle"
          ghost-class="ghost"
          chosen-class="chosen"
          animation="300"
          @end="handleDragEnd"
        >
          <template #item="{ element }">
            <div class="sort-item">
              <div class="drag-handle">
                <MenuOutlined />
              </div>
              <div class="sort-item-content">
                <div class="sort-item-name">{{ element.name }}</div>
                <div class="sort-item-desc">{{ element.description || '无描述' }}</div>
              </div>
              <div class="sort-item-status">
                <a-tag :color="element.status === 'ACTIVE' ? 'green' : 'red'">
                  {{ element.status === 'ACTIVE' ? '启用' : '禁用' }}
                </a-tag>
              </div>
            </div>
          </template>
        </draggable>
      </div>
    </div>

    <!-- 新增/编辑分类弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑分类' : '新增分类'"
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
        <a-form-item label="分类名称" name="name">
          <a-input
            v-model:value="formData.name"
            placeholder="请输入分类名称"
          />
        </a-form-item>
        
        <a-form-item label="状态" name="status">
          <a-select v-model:value="formData.status" placeholder="请选择状态">
            <a-select-option value="ACTIVE">启用</a-select-option>
            <a-select-option value="INACTIVE">禁用</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="描述" name="description">
          <a-textarea
            v-model:value="formData.description"
            placeholder="请输入分类描述"
            :rows="4"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { message, Modal } from 'ant-design-vue'
import { PlusOutlined, ReloadOutlined, MenuOutlined, InfoCircleOutlined } from '@ant-design/icons-vue'
import type { TableColumnsType } from 'ant-design-vue'
import { categoryApi } from '@/api/category'
import type { Category } from '@/types/api'
import draggable from 'vuedraggable'

// 响应式数据
const loading = ref(false)
const sortLoading = ref(false)
const categories = ref<Category[]>([])
const sortableCategories = ref<Category[]>([])
const selectedRowKeys = ref<number[]>([])
const modalVisible = ref(false)
const modalLoading = ref(false)
const isEdit = ref(false)
const sortMode = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  name: '',
  status: undefined
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
const formData = reactive<Omit<Category, 'id' | 'createdAt' | 'updatedAt'> & { id?: number, status: string }>({ 
  name: '',
  description: '',
  status: 'ACTIVE'
})

// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

// 表格列配置
const columns: TableColumnsType = [
  {
    title: '分类名称',
    dataIndex: 'name',
    key: 'name',
    width: 200
  },
  {
    title: '描述',
    dataIndex: 'description',
    ellipsis: true
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
const loadCategories = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      name: searchForm.name || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await categoryApi.getList(params)
    categories.value = response.data.content
    pagination.total = response.data.totalElements
  } catch (error) {
    message.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadCategories()
}

const resetSearch = () => {
  searchForm.name = ''
  searchForm.status = undefined
  pagination.current = 1
  loadCategories()
}

const handleRefresh = () => {
  loadCategories()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadCategories()
}

const clearSelection = () => {
  selectedRowKeys.value = []
}

const showCreateModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

const handleEdit = (record: Category) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    name: record.name,
    description: record.description,
    status: record.status
  })
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    if (isEdit.value) {
      const { id, ...updateData } = formData
      await categoryApi.update(id!, updateData)
      message.success('分类更新成功')
    } else {
      await categoryApi.create(formData)
      message.success('分类创建成功')
    }
    
    modalVisible.value = false
    loadCategories()
  } catch (error) {
    message.error(isEdit.value ? '分类更新失败' : '分类创建失败')
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
  formData.description = ''
  formData.status = 'ACTIVE'
  formRef.value?.resetFields()
}

const handleDelete = (record: Category) => {
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除分类"${record.name}"吗？`,
    onOk: async () => {
      try {
        await categoryApi.delete(record.id)
        message.success('分类删除成功')
        loadCategories()
      } catch (error) {
        message.error('分类删除失败')
      }
    }
  })
}

const handleBatchDelete = () => {
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${selectedRowKeys.value.length} 个分类吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => categoryApi.delete(id)))
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadCategories()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

const handleToggleStatus = (record: Category) => {
  const newStatus = record.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
  const statusText = newStatus === 'ACTIVE' ? '启用' : '禁用'
  
  Modal.confirm({
    title: `确认${statusText}`,
    content: `确定要${statusText}分类"${record.name}"吗？`,
    onOk: async () => {
      try {
        await categoryApi.update(record.id, { status: newStatus })
        message.success(`分类${statusText}成功`)
        loadCategories()
      } catch (error) {
        message.error(`分类${statusText}失败`)
      }
    }
  })
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 排序相关方法
const enableSortMode = async () => {
  try {
    loading.value = true
    // 获取所有分类（不分页）
    const response = await categoryApi.getAll()
    sortableCategories.value = [...response.data]
    sortMode.value = true
  } catch (error) {
    message.error('加载分类列表失败')
  } finally {
    loading.value = false
  }
}

const cancelSortMode = () => {
  sortMode.value = false
  sortableCategories.value = []
}

const handleDragEnd = () => {
  // 拖拽结束后的处理，可以在这里添加一些逻辑
  console.log('拖拽结束', sortableCategories.value)
}

const saveSortOrder = async () => {
  try {
    sortLoading.value = true
    
    // 构建排序数据
    const categoryOrders = sortableCategories.value.map((category, index) => ({
      id: category.id,
      order: index
    }))
    
    // 调用API更新排序
    await categoryApi.updateOrder(categoryOrders)
    message.success('分类排序更新成功')
    
    // 退出排序模式并刷新数据
    sortMode.value = false
    await loadCategories()
  } catch (error) {
    message.error('更新分类排序失败')
  } finally {
    sortLoading.value = false
  }
}

// 生命周期
onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.category-list {
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

/* 排序模式样式 */
.sort-table-container {
  margin-top: 16px;
}

.sort-tip {
  background-color: #e6f7ff;
  padding: 12px;
  border-radius: 4px;
  margin-bottom: 16px;
  color: #1890ff;
  display: flex;
  align-items: center;
  gap: 8px;
}

.sort-item {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  border: 1px solid #f0f0f0;
  border-radius: 4px;
  margin-bottom: 8px;
  background-color: #fff;
  transition: all 0.3s;
}

.sort-item:hover {
  background-color: #f9f9f9;
}

.drag-handle {
  cursor: move;
  padding: 8px;
  color: #999;
  margin-right: 12px;
}

.drag-handle:hover {
  color: #1890ff;
}

.sort-item-content {
  flex: 1;
}

.sort-item-name {
  font-weight: 500;
  margin-bottom: 4px;
}

.sort-item-desc {
  color: #666;
  font-size: 14px;
}

.sort-item-status {
  margin-left: 16px;
}

.ghost {
  opacity: 0.5;
  background: #c8ebfb;
}

.chosen {
  border: 1px solid #1890ff;
}

.header-actions {
  display: flex;
  gap: 8px;
}
</style>