import apiClient from './config'
import type { Category, CategoryCreate, Tag, TagCreate, ApiResponse, PageResponse, QueryParams } from '@/types/api'

export const categoryApi = {
  // 获取分类列表
  getList: (params?: QueryParams): Promise<ApiResponse<PageResponse<Category>>> => {
    return apiClient.get('/categories', { params })
  },

  // 获取所有分类（不分页）
  getAll: (): Promise<ApiResponse<Category[]>> => {
    return apiClient.get('/categories/all')
  },

  // 获取分类详情
  getById: (id: number): Promise<ApiResponse<Category>> => {
    return apiClient.get(`/categories/${id}`)
  },

  // 创建分类
  create: (data: CategoryCreate): Promise<ApiResponse<Category>> => {
    return apiClient.post('/categories', data)
  },

  // 更新分类
  update: (id: number, data: Partial<CategoryCreate>): Promise<ApiResponse<Category>> => {
    return apiClient.put(`/categories/${id}`, data)
  },

  // 删除分类
  delete: (id: number): Promise<ApiResponse<void>> => {
    return apiClient.delete(`/categories/${id}`)
  },

  // 更新分类排序
  updateOrder: (categoryOrders: { id: number, order: number }[]): Promise<ApiResponse<Category[]>> => {
    return apiClient.put('/categories/order', categoryOrders)
  },
}

export const tagApi = {
  // 获取标签列表
  getList: (params?: QueryParams): Promise<ApiResponse<PageResponse<Tag>>> => {
    return apiClient.get('/tags', { params })
  },

  // 获取所有标签（不分页）
  getAll: (): Promise<ApiResponse<Tag[]>> => {
    return apiClient.get('/tags/all')
  },

  // 获取标签详情
  getById: (id: number): Promise<ApiResponse<Tag>> => {
    return apiClient.get(`/tags/${id}`)
  },

  // 创建标签
  create: (data: TagCreate): Promise<ApiResponse<Tag>> => {
    return apiClient.post('/tags', data)
  },

  // 更新标签
  update: (id: number, data: Partial<TagCreate>): Promise<ApiResponse<Tag>> => {
    return apiClient.put(`/tags/${id}`, data)
  },

  // 删除标签
  delete: (id: number): Promise<ApiResponse<void>> => {
    return apiClient.delete(`/tags/${id}`)
  },
}