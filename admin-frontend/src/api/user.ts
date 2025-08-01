import { request } from '@/utils/request'
import type { User, UserCreate, UserUpdate, QueryParams, ApiResponse, PageResponse } from '@/types/api'

export const userApi = {
  // 获取用户列表
  getList: (params: QueryParams & {
    username?: string
    email?: string
    role?: string
    status?: string
  }) => {
    return request<PageResponse<User>>({
      url: '/api/admin/users',
      method: 'GET',
      params
    })
  },

  // 根据ID获取用户
  getById: (id: number) => {
    return request<ApiResponse<User>>({
      url: `/api/admin/users/${id}`,
      method: 'GET'
    })
  },

  // 创建用户
  create: (data: UserCreate) => {
    return request<ApiResponse<User>>({
      url: '/api/admin/users',
      method: 'POST',
      data
    })
  },

  // 更新用户
  update: (id: number, data: UserUpdate) => {
    return request<ApiResponse<User>>({
      url: `/api/admin/users/${id}`,
      method: 'PUT',
      data
    })
  },

  // 删除用户
  delete: (id: number) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/users/${id}`,
      method: 'DELETE'
    })
  },

  // 批量删除用户
  batchDelete: (ids: number[]) => {
    return request<ApiResponse<void>>({
      url: '/api/admin/users/batch',
      method: 'DELETE',
      data: { ids }
    })
  },

  // 更新用户状态
  updateStatus: (id: number, status: string) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/users/${id}/status`,
      method: 'PUT',
      data: { status }
    })
  },

  // 重置用户密码
  resetPassword: (id: number) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/users/${id}/reset-password`,
      method: 'PUT'
    })
  },

  // 上传用户头像
  uploadAvatar: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request<ApiResponse<{ url: string }>>({
      url: '/api/admin/users/upload-avatar',
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取当前用户信息
  getCurrentUser: () => {
    return request<ApiResponse<User>>({
      url: '/api/user/profile',
      method: 'GET'
    })
  },

  // 更新个人资料
  updateProfile: (data: UserUpdate) => {
    return request<ApiResponse<User>>({
      url: '/api/user/profile',
      method: 'PUT',
      data
    })
  },

  // 修改密码
  changePassword: (data: { currentPassword: string; newPassword: string }) => {
    return request<ApiResponse<void>>({
      url: '/api/user/change-password',
      method: 'PUT',
      data
    })
  }
}