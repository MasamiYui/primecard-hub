import apiClient from './config'
import type { LoginRequest, LoginResponse, User, ApiResponse } from '@/types/api'

export const authApi = {
  // 用户登录
  login: (data: LoginRequest): Promise<ApiResponse<LoginResponse>> => {
    return apiClient.post('/auth/login', data)
  },

  // 刷新token
  refreshToken: (refreshToken: string): Promise<ApiResponse<LoginResponse>> => {
    return apiClient.post('/auth/refresh', { refreshToken })
  },

  // 用户登出
  logout: (): Promise<ApiResponse<void>> => {
    return apiClient.post('/auth/logout')
  },

  // 获取当前用户信息
  getCurrentUser: (): Promise<ApiResponse<User>> => {
    return apiClient.get('/auth/me')
  },

  // 修改密码
  changePassword: (data: { oldPassword: string; newPassword: string }): Promise<ApiResponse<void>> => {
    return apiClient.put('/auth/change-password', data)
  },
}