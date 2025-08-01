import apiClient from './config'
import type { CreditCard, CreditCardCreate, ApiResponse, PageResponse, QueryParams } from '@/types/api'

export const creditCardApi = {
  // 获取信用卡列表
  getList: (params?: QueryParams): Promise<ApiResponse<PageResponse<CreditCard>>> => {
    return apiClient.get('/credit-cards', { params })
  },

  // 获取信用卡详情
  getById: (id: number): Promise<ApiResponse<CreditCard>> => {
    return apiClient.get(`/credit-cards/${id}`)
  },

  // 创建信用卡
  create: (data: CreditCardCreate): Promise<ApiResponse<CreditCard>> => {
    return apiClient.post('/credit-cards', data)
  },

  // 更新信用卡
  update: (id: number, data: Partial<CreditCardCreate>): Promise<ApiResponse<CreditCard>> => {
    return apiClient.put(`/credit-cards/${id}`, data)
  },

  // 删除信用卡
  delete: (id: number): Promise<ApiResponse<void>> => {
    return apiClient.delete(`/credit-cards/${id}`)
  },

  // 批量删除信用卡
  batchDelete: (ids: number[]): Promise<ApiResponse<void>> => {
    return apiClient.delete('/credit-cards/batch', { data: { ids } })
  },

  // 上传信用卡图片
  uploadImage: (file: File): Promise<ApiResponse<{ url: string }>> => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post('/credit-cards/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}