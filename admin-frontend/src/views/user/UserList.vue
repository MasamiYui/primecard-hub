<template>
  <div class="user-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <a-button type="primary" @click="showCreateModal">
          <template #icon>
            <PlusOutlined />
          </template>
          新增用户
        </a-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <a-form layout="inline" :model="searchForm" @finish="handleSearch">
        <a-form-item label="用户名">
          <a-input
            v-model:value="searchForm.username"
            placeholder="请输入用户名"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input
            v-model:value="searchForm.email"
            placeholder="请输入邮箱"
            allow-clear
            style="width: 200px"
          />
        </a-form-item>
        <a-form-item label="角色">
          <a-select
            v-model:value="searchForm.role"
            placeholder="请选择角色"
            allow-clear
            style="width: 120px"
          >
            <a-select-option value="ADMIN">管理员</a-select-option>
            <a-select-option value="USER">普通用户</a-select-option>
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
        :data-source="users"
        :loading="loading"
        :pagination="pagination"
        :row-selection="rowSelection"
        row-key="id"
        @change="handleTableChange"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'avatar'">
            <a-avatar :src="record.avatar" :size="40">
              {{ record.username?.charAt(0)?.toUpperCase() }}
            </a-avatar>
          </template>
          
          <template v-else-if="column.key === 'username'">
            <div class="user-info">
              <div class="username">{{ record.username }}</div>
              <div class="email">{{ record.email }}</div>
            </div>
          </template>
          
          <template v-else-if="column.key === 'role'">
            <a-tag :color="getRoleColor(record.role)">
              {{ getRoleText(record.role) }}
            </a-tag>
          </template>
          
          <template v-else-if="column.key === 'status'">
             <a-switch
               :checked="record.status === 'ACTIVE'"
               :loading="record.statusLoading"
               @change="(checked: boolean) => handleStatusToggle(record, checked)"
             />
           </template>
          
          <template v-else-if="column.key === 'lastLoginAt'">
            <div v-if="record.lastLoginAt">
              {{ formatDate(record.lastLoginAt) }}
            </div>
            <span v-else class="text-gray">从未登录</span>
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
              <a-button type="link" size="small" @click="handleResetPassword(record)">
                重置密码
              </a-button>
              <a-button
                type="link"
                size="small"
                danger
                :disabled="record.role === 'ADMIN'"
                @click="handleDelete(record)"
              >
                删除
              </a-button>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <a-modal
      v-model:open="modalVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
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
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="用户名" name="username">
              <a-input
                v-model:value="formData.username"
                placeholder="请输入用户名"
                :disabled="isEdit"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="邮箱" name="email">
              <a-input
                v-model:value="formData.email"
                placeholder="请输入邮箱"
                type="email"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16" v-if="!isEdit">
          <a-col :span="12">
            <a-form-item label="密码" name="password">
              <a-input-password
                v-model:value="formData.password"
                placeholder="请输入密码"
              />
            </a-form-item>
          </a-col>
          <a-col :span="12">
            <a-form-item label="确认密码" name="confirmPassword">
              <a-input-password
                v-model:value="formData.confirmPassword"
                placeholder="请再次输入密码"
              />
            </a-form-item>
          </a-col>
        </a-row>
        
        <a-row :gutter="16">
          <a-col :span="12">
            <a-form-item label="角色" name="role">
              <a-select v-model:value="formData.role" placeholder="请选择角色">
                <a-select-option value="ADMIN">管理员</a-select-option>
                <a-select-option value="USER">普通用户</a-select-option>
              </a-select>
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
        
        <a-form-item label="备注" name="remark">
          <a-textarea
            v-model:value="formData.remark"
            placeholder="请输入备注信息"
            :rows="3"
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
import type { TableColumnsType } from 'ant-design-vue'
import { userApi } from '@/api/user'
import type { User, UserCreate } from '@/types/api'

// 响应式数据
const loading = ref(false)
const users = ref<(User & { statusLoading?: boolean })[]>([])
const selectedRowKeys = ref<number[]>([])
const modalVisible = ref(false)
const modalLoading = ref(false)
const isEdit = ref(false)
const formRef = ref()

// 搜索表单
const searchForm = reactive({
  username: '',
  email: '',
  role: undefined as string | undefined,
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
const formData = reactive<UserCreate & { id?: number; confirmPassword?: string }>({
  username: '',
  email: '',
  password: '',
  confirmPassword: '',
  role: 'USER',
  status: 'ACTIVE',
  remark: ''
})

// 表单验证规则
const formRules = computed(() => ({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  password: isEdit.value ? [] : [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: isEdit.value ? [] : [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (_: any, value: string) => {
        if (value !== formData.password) {
          return Promise.reject('两次输入的密码不一致')
        }
        return Promise.resolve()
      },
      trigger: 'blur'
    }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}))

// 表格列配置
const columns: TableColumnsType = [
  {
    title: '头像',
    key: 'avatar',
    width: 80,
    align: 'center'
  },
  {
    title: '用户信息',
    key: 'username',
    width: 200
  },
  {
    title: '角色',
    key: 'role',
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
    title: '最后登录',
    key: 'lastLoginAt',
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
    width: 250,
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
  },
  getCheckboxProps: (record: any) => ({
    disabled: record.role === 'ADMIN' // 管理员不能被批量操作
  })
}))

// 方法
const loadUsers = async () => {
  try {
    loading.value = true
    const params = {
      page: pagination.current - 1,
      size: pagination.pageSize,
      username: searchForm.username || undefined,
      email: searchForm.email || undefined,
      role: searchForm.role || undefined,
      status: searchForm.status || undefined
    }
    
    const response = await userApi.getList(params)
    users.value = response.data.content.map(user => ({ ...user, statusLoading: false }))
    pagination.total = response.data.totalElements
  } catch (error) {
    message.error('加载用户列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadUsers()
}

const resetSearch = () => {
  searchForm.username = ''
  searchForm.email = ''
  searchForm.role = undefined
  searchForm.status = undefined
  pagination.current = 1
  loadUsers()
}

const handleRefresh = () => {
  loadUsers()
}

const handleTableChange = (pag: any) => {
  pagination.current = pag.current
  pagination.pageSize = pag.pageSize
  loadUsers()
}

const clearSelection = () => {
  selectedRowKeys.value = []
}

const showCreateModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

const handleView = (record: User) => {
  // 查看用户详情的逻辑
  message.info('查看用户功能待实现')
}

const handleEdit = (record: User) => {
  isEdit.value = true
  modalVisible.value = true
  Object.assign(formData, {
    id: record.id,
    username: record.username,
    email: record.email,
    role: record.role,
    status: record.status,
    remark: record.remark || ''
  })
}

const handleModalOk = async () => {
  try {
    await formRef.value.validate()
    modalLoading.value = true
    
    if (isEdit.value) {
      const { confirmPassword, ...updateData } = formData
      await userApi.update(formData.id!, updateData)
      message.success('用户更新成功')
    } else {
      const { confirmPassword, ...createData } = formData
      await userApi.create(createData)
      message.success('用户创建成功')
    }
    
    modalVisible.value = false
    loadUsers()
  } catch (error) {
    message.error(isEdit.value ? '用户更新失败' : '用户创建失败')
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
  formData.username = ''
  formData.email = ''
  formData.password = ''
  formData.confirmPassword = ''
  formData.role = 'USER'
  formData.status = 'ACTIVE'
  formData.remark = ''
  formRef.value?.resetFields()
}

const handleStatusToggle = async (record: User & { statusLoading?: boolean }, checked: boolean) => {
  if (record.role === 'ADMIN') {
    message.warning('不能修改管理员状态')
    return
  }
  
  try {
    record.statusLoading = true
    const newStatus = checked ? 'ACTIVE' : 'INACTIVE'
    await userApi.updateStatus(record.id, newStatus)
    record.status = newStatus
    message.success('状态更新成功')
  } catch (error) {
    message.error('状态更新失败')
  } finally {
    record.statusLoading = false
  }
}

const handleDelete = (record: User) => {
  if (record.role === 'ADMIN') {
    message.warning('不能删除管理员用户')
    return
  }
  
  Modal.confirm({
    title: '确认删除',
    content: `确定要删除用户"${record.username}"吗？`,
    onOk: async () => {
      try {
        await userApi.delete(record.id)
        message.success('用户删除成功')
        loadUsers()
      } catch (error) {
        message.error('用户删除失败')
      }
    }
  })
}

const handleBatchDelete = () => {
  const canDeleteIds = selectedRowKeys.value.filter(id => {
    const user = users.value.find(u => u.id === id)
    return user && user.role !== 'ADMIN'
  })
  
  if (canDeleteIds.length === 0) {
    message.warning('选中的用户都是管理员，无法删除')
    return
  }
  
  Modal.confirm({
    title: '确认批量删除',
    content: `确定要删除选中的 ${canDeleteIds.length} 个用户吗？`,
    onOk: async () => {
      try {
        await Promise.all(canDeleteIds.map(id => userApi.delete(id)))
        message.success('批量删除成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        message.error('批量删除失败')
      }
    }
  })
}

const handleBatchEnable = () => {
  Modal.confirm({
    title: '确认批量启用',
    content: `确定要启用选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => userApi.updateStatus(id, 'ACTIVE')))
        message.success('批量启用成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        message.error('批量启用失败')
      }
    }
  })
}

const handleBatchDisable = () => {
  Modal.confirm({
    title: '确认批量禁用',
    content: `确定要禁用选中的 ${selectedRowKeys.value.length} 个用户吗？`,
    onOk: async () => {
      try {
        await Promise.all(selectedRowKeys.value.map(id => userApi.updateStatus(id, 'INACTIVE')))
        message.success('批量禁用成功')
        selectedRowKeys.value = []
        loadUsers()
      } catch (error) {
        message.error('批量禁用失败')
      }
    }
  })
}

const handleResetPassword = (record: User) => {
  Modal.confirm({
    title: '确认重置密码',
    content: `确定要重置用户"${record.username}"的密码吗？重置后的密码为：123456`,
    onOk: async () => {
      try {
        await userApi.resetPassword(record.id)
        message.success('密码重置成功，新密码为：123456')
      } catch (error) {
        message.error('密码重置失败')
      }
    }
  })
}

const getRoleColor = (role: string) => {
  const roleColors = {
    ADMIN: 'red',
    USER: 'blue'
  }
  return roleColors[role as keyof typeof roleColors] || 'default'
}

const getRoleText = (role: string) => {
  const roleTexts = {
    ADMIN: '管理员',
    USER: '普通用户'
  }
  return roleTexts[role as keyof typeof roleTexts] || role
}

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadUsers()
})
</script>

<style scoped>
.user-list {
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

.user-info {
  text-align: left;
}

.username {
  font-weight: 500;
  color: #262626;
  margin-bottom: 4px;
}

.email {
  font-size: 12px;
  color: #8c8c8c;
}

.text-gray {
  color: #8c8c8c;
}
</style>