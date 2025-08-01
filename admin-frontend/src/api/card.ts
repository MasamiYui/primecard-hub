import { request } from '@/utils/request'
import type { Card, CardCreate, CardUpdate, QueryParams, ApiResponse, PageResponse } from '@/types/api'

export const cardApi = {
  // 获取信用卡列表
  getList: (params: QueryParams & {
    name?: string
    bankId?: number
    categoryId?: number
    status?: string
  }) => {
    return request<PageResponse<Card>>({
      url: '/api/admin/cards',
      method: 'GET',
      params
    })
  },

  // 根据ID获取信用卡
  getById: (id: number) => {
    return request<ApiResponse<Card>>({
      url: `/api/admin/cards/${id}`,
      method: 'GET'
    })
  },

  // 创建信用卡
  create: (data: CardCreate) => {
    return request<ApiResponse<Card>>({
      url: '/api/admin/cards',
      method: 'POST',
      data
    })
  },

  // 更新信用卡
  update: (id: number, data: CardUpdate) => {
    return request<ApiResponse<Card>>({
      url: `/api/admin/cards/${id}`,
      method: 'PUT',
      data
    })
  },

  // 删除信用卡
  delete: (id: number) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/cards/${id}`,
      method: 'DELETE'
    })
  },

  // 批量删除信用卡
  batchDelete: (ids: number[]) => {
    return request<ApiResponse<void>>({
      url: '/api/admin/cards/batch',
      method: 'DELETE',
      data: { ids }
    })
  },

  // 更新信用卡状态
  updateStatus: (id: number, status: string) => {
    return request<ApiResponse<void>>({
      url: `/api/admin/cards/${id}/status`,
      method: 'PUT',
      data: { status }
    })
  },

  // 上传信用卡图片
  uploadImage: (file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return request<ApiResponse<{ url: string }>>({
      url: '/api/admin/cards/upload-image',
      method: 'POST',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}