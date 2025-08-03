<template>
  <div class="user-list">
    <!-- 页面头部 -->
    <div class="page-header">
      <div class="header-content">
        <h1 class="page-title">用户管理</h1>
        <el-button type="primary" @click="showCreateModal">
          <template #icon>
            <Plus />
          </template>
          新增用户
        </el-button>
      </div>
    </div>

    <!-- 搜索筛选 -->
    <div class="search-section">
      <el-form :inline="true" :model="searchForm" @submit.prevent="handleSearch">
        <el-form-item label="用户名">
          <el-input
            v-model="searchForm.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input
            v-model="searchForm.email"
            placeholder="请输入邮箱"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="角色">
          <el-select
            v-model="searchForm.role"
            placeholder="请选择角色"
            clearable
            style="width: 120px"
          >
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="普通用户" value="USER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="searchForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 120px"
          >
            <el-option label="启用" value="ACTIVE"></el-option>
            <el-option label="禁用" value="INACTIVE"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit">搜索</el-button>
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
            @click="handleBatchEnable"
          >
            批量启用
          </el-button>
          <el-button
            :disabled="!hasSelected"
            @click="handleBatchDisable"
          >
            批量禁用
          </el-button>
          <el-button @click="handleRefresh">
            <template #icon>
              <Refresh />
            </template>
            刷新
          </el-button>
        </div>
        <div class="table-info">
          <span v-if="hasSelected">
            已选择 {{ selectedUserIds.length }} 项
            <a style="margin-left: 8px" @click="clearSelection">取消选择</a>
          </span>
        </div>
      </div>

      <el-table
        :data="users"
        v-loading="loading"
        @selection-change="handleSelectionChange"
        row-key="id"
        @sort-change="handleSortChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" :size="40">
              {{ row.username?.charAt(0)?.toUpperCase() }}
            </el-avatar>
          </template>
        </el-table-column>
        <el-table-column prop="username" label="用户名" sortable="custom">
          <template #default="{ row }">
            <div class="user-info">
              <div class="username">{{ row.username }}</div>
              <div class="email">{{ row.email }}</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="role" label="角色">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.role)">
              {{ getRoleText(row.role) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              active-value="ACTIVE"
              inactive-value="INACTIVE"
              :loading="row.statusLoading"
              @change="(checked: string | number | boolean) => handleStatusToggle(row, checked as boolean)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="lastLoginAt" label="最后登录时间" sortable="custom">
          <template #default="{ row }">
            <div v-if="row.lastLoginAt">
              {{ formatDate(row.lastLoginAt) }}
            </div>
            <span v-else class="text-gray">从未登录</span>
          </template>
        </el-table-column>
        <el-table-column prop="createdAt" label="创建时间" sortable="custom">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280">
          <template #default="{ row }">
            <el-space>
              <el-button type="primary" link size="small" @click="handleView(row)">
                查看
              </el-button>
              <el-button type="primary" link size="small" @click="handleEdit(row)">
                编辑
              </el-button>
              <el-button type="primary" link size="small" @click="handleResetPassword(row)">
                重置密码
              </el-button>
              <el-button
                type="danger"
                link
                size="small"
                :disabled="row.role === 'ADMIN'"
                @click="handleDelete(row)"
              >
                删除
              </el-button>
            </el-space>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="modalVisible"
      :title="isEdit ? '编辑用户' : '新增用户'"
      :before-close="handleModalCancel"
      width="600px"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-position="top"
      >
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="用户名" prop="username">
              <el-input
                v-model="formData.username"
                placeholder="请输入用户名"
                :disabled="isEdit"
              />
            </el-form-item>
          </a-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input
                v-model="formData.email"
                placeholder="请输入邮箱"
                type="email"
              />
            </el-form-item>
          </el-col>
        </a-row>
        
        <el-row :gutter="16" v-if="!isEdit">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input
                v-model="formData.password"
                type="password"
                show-password
                placeholder="请输入密码"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input
                v-model="formData.confirmPassword"
                type="password"
                show-password
                placeholder="请再次输入密码"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="角色" prop="role">
              <el-select v-model="formData.role" placeholder="请选择角色">
                <el-option label="管理员" value="ADMIN"></el-option>
                <el-option label="普通用户" value="USER"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态">
                <el-option label="启用" value="ACTIVE"></el-option>
                <el-option label="禁用" value="INACTIVE"></el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-form-item label="备注" prop="remark">
          <el-input
            type="textarea"
            v-model="formData.remark"
            placeholder="请输入备注信息"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleModalCancel">取消</el-button>
          <el-button type="primary" :loading="modalLoading" @click="handleModalOk">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh } from '@element-plus/icons-vue'

import { userApi } from '@/api/user'
import type { User, UserCreate } from '@/types/api'

// 响应式数据
const loading = ref(false)
const users = ref<(User & { statusLoading?: boolean })[]>([])
const selectedUserIds = ref<number[]>([])
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



// 计算属性
const hasSelected = computed(() => selectedUserIds.value.length > 0)

// 行选择配置
const handleSelectionChange = (selection: any[]) => {
  selectedUserIds.value = selection.map((row) => row.id)
}

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
    users.value = response.content.map((user: User) => ({ ...user, statusLoading: false }))
    pagination.total = response.totalElements
  } catch (error) {
    ElMessage.error('加载用户列表失败')
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

const handleSortChange = ({ prop, order }: { prop: string; order: 'ascending' | 'descending' | null }) => {
  // query.sortBy = prop
  // query.sortOrder = order === 'ascending' ? 'asc' : 'desc'
  loadUsers()
}

const clearSelection = () => {
  selectedUserIds.value = []
}

const showCreateModal = () => {
  isEdit.value = false
  modalVisible.value = true
  resetForm()
}

const handleView = (record: User) => {
  // 查看用户详情的逻辑
  ElMessage.info('查看用户功能待实现')
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
      ElMessage.success('用户更新成功')
    } else {
      const { confirmPassword, ...createData } = formData
      await userApi.create(createData)
      ElMessage.success('用户创建成功')
    }
    
    modalVisible.value = false
    loadUsers()
  } catch (error) {
    ElMessage.error(isEdit.value ? '用户更新失败' : '用户创建失败')
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

const handleStatusToggle = async (record: any, checked: string | number | boolean) => {
  if (record.role === 'ADMIN') {
    ElMessage.warning('不能修改管理员状态')
    return
  }
  
  try {
    record.statusLoading = true
    const newStatus = checked as string
    await userApi.updateStatus(record.id, newStatus)
    record.status = newStatus
    ElMessage.success('状态更新成功')
  } catch (error) {
    ElMessage.error('状态更新失败')
  } finally {
    record.statusLoading = false
  }
}

const handleDelete = (record: User) => {
  if (record.role === 'ADMIN') {
    ElMessage.warning('不能删除管理员用户')
    return
  }

  ElMessageBox.confirm(`确定要删除用户"${record.username}"吗？`, '确认删除', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await userApi.delete(record.id)
      ElMessage.success('用户删除成功')
      loadUsers()
    } catch (error) {
      ElMessage.error('用户删除失败')
    }
  })
}

const handleBatchDelete = () => {
  const canDeleteIds = selectedUserIds.value.filter(id => {
    const user = users.value.find(u => u.id === id)
    return user && user.role !== 'ADMIN'
  })
  
  if (canDeleteIds.length === 0) {
    ElMessage.warning('选中的用户都是管理员，无法删除')
    return
  }
  
  ElMessageBox.confirm(`确定要删除选中的 ${canDeleteIds.length} 个用户吗？`, '确认批量删除', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await Promise.all(canDeleteIds.map(id => userApi.delete(id)))
      ElMessage.success('批量删除成功')
      selectedUserIds.value = []
      loadUsers()
    } catch (error) {
      ElMessage.error('批量删除失败')
    }
  })
}

const handleBatchEnable = () => {
  ElMessageBox.confirm(`确定要启用选中的 ${selectedUserIds.value.length} 个用户吗？`, '确认批量启用', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await Promise.all(selectedUserIds.value.map(id => userApi.updateStatus(id, 'ACTIVE')))
      ElMessage.success('批量启用成功')
      selectedUserIds.value = []
      loadUsers()
    } catch (error) {
      ElMessage.error('批量启用失败')
    }
  })
}

const handleBatchDisable = () => {
  ElMessageBox.confirm(`确定要禁用选中的 ${selectedUserIds.value.length} 个用户吗？`, '确认批量禁用', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await Promise.all(selectedUserIds.value.map(id => userApi.updateStatus(id, 'INACTIVE')))
      ElMessage.success('批量禁用成功')
      selectedUserIds.value = []
      loadUsers()
    } catch (error) {
      ElMessage.error('批量禁用失败')
    }
  })
}

const handleResetPassword = (record: User) => {
  ElMessageBox.confirm(`确定要重置用户"${record.username}"的密码吗？重置后的密码为：123456`, '确认重置密码', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await userApi.resetPassword(record.id)
      ElMessage.success('密码重置成功，新密码为：123456')
    } catch (error) {
      ElMessage.error('密码重置失败')
    }
  })
}

const getRoleType = (role: string) => {
  const roleTypes = {
    ADMIN: 'danger',
    USER: 'primary'
  }
  return roleTypes[role as keyof typeof roleTypes] || 'info'
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