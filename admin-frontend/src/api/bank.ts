import { request } from '@/utils/request'
import type { Bank, QueryParams, ApiResponse, PageResponse } from '@/types/api'

export const bankApi = {
  // 获取银行列表
  getList: (params?: QueryParams & {
    name?: string
    code?: string
  }) => {
    return request<PageResponse<Bank>>({
      url: '/api/admin/banks',
      method: 'GET',
      params
    })
  },

  // 获取所有银行（不分页）
  getAll: () => {
    return request<ApiResponse<Bank[]>>({
      url: '/api/admin/banks/all',
      method: 'GET'
    })
  },

  // 根据ID获取银行
  getById: (id: number) => {
    return request<ApiResponse<Bank>>({
      url: `/api/admin/banks/${id}`,
      method: 'GET'
    })
  },

  // 创建银行
  create: (data: Omit<Bank, 'id' | 'createdAt' | 'updatedAt'>) => {
    return request<ApiResponse<Bank>>({
      url: '/api/admin/banks',
      method: 'POST',
      data
    })
  },

  // 更新银行
  update: (id: number, data: Partial<Omit<Bank, 'id' | 'createdAt' | 'updatedAt'>>) => {
    return request<ApiResponse<Bank>>({
      url: `/api/admin/banks/${id}`,
      method: 'PUT',
      data
    })
  },

  // 删除银行
  delete: (id: number) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/banks/${id}`,
      method: 'DELETE'
    })
  },

  // 上传银行logo
  uploadLogo: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request<ApiResponse<{ url: string }>>({
      url: '/api/admin/banks/upload-logo',
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}