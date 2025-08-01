import apiClient from './config'
import type { News, NewsCreate, ApiResponse, PageResponse, QueryParams } from '@/types/api'

export const newsApi = {
  // 获取新闻列表
  getList: (params?: QueryParams): Promise<ApiResponse<PageResponse<News>>> => {
    return apiClient.get('/news', { params })
  },

  // 获取新闻详情
  getById: (id: number): Promise<ApiResponse<News>> => {
    return apiClient.get(`/news/${id}`)
  },

  // 创建新闻
  create: (data: NewsCreate): Promise<ApiResponse<News>> => {
    return apiClient.post('/news', data)
  },

  // 更新新闻
  update: (id: number, data: Partial<NewsCreate>): Promise<ApiResponse<News>> => {
    return apiClient.put(`/news/${id}`, data)
  },

  // 删除新闻
  delete: (id: number): Promise<ApiResponse<void>> => {
    return apiClient.delete(`/news/${id}`)
  },

  // 批量删除新闻
  batchDelete: (ids: number[]): Promise<ApiResponse<void>> => {
    return apiClient.delete('/news/batch', { data: { ids } })
  },

  // 发布/取消发布新闻
  togglePublish: (id: number, isPublished: boolean): Promise<ApiResponse<News>> => {
    return apiClient.patch(`/news/${id}/publish`, { isPublished })
  },

  // 更新新闻状态
  updateStatus: (id: number, status: string): Promise<ApiResponse<News>> => {
    return apiClient.patch(`/news/${id}/status`, { status })
  },

  // 上传新闻图片
  uploadImage: (file: File): Promise<ApiResponse<{ url: string }>> => {
    const formData = new FormData()
    formData.append('file', file)
    return apiClient.post('/news/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
  },
}