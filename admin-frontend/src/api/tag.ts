import apiClient from './config'
import type { Tag, TagCreate, ApiResponse, PageResponse, QueryParams } from '@/types/api'

export const tagApi = {
  // 获取所有标签
  getAll: (): Promise<ApiResponse<Tag[]>> => {
    return apiClient.get('/tags/all')
  },

  // 分页获取标签
  getPage: (params: QueryParams): Promise<ApiResponse<PageResponse<Tag>>> => {
    return apiClient.get('/tags/page', { params })
  },

  // 获取单个标签
  getById: (id: number): Promise<ApiResponse<Tag>> => {
    return apiClient.get(`/tags/${id}`)
  },

  // 创建标签
  create: (tag: TagCreate): Promise<ApiResponse<Tag>> => {
    return apiClient.post('/tags', tag)
  },

  // 更新标签
  update: (id: number, tag: TagCreate): Promise<ApiResponse<Tag>> => {
    return apiClient.put(`/tags/${id}`, tag)
  },

  // 删除标签
  delete: (id: number): Promise<ApiResponse<void>> => {
    return apiClient.delete(`/tags/${id}`)
  },

  // 批量删除标签
  batchDelete: (ids: number[]): Promise<ApiResponse<void>> => {
    return apiClient.delete('/tags/batch', { data: { ids } })
  },

  // 更新标签状态
  updateStatus: (id: number, status: string): Promise<ApiResponse<void>> => {
    return apiClient.patch(`/tags/${id}/status`, { status })
  }
}