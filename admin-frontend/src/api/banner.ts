import service from '@/utils/request'
import type { ApiResponse, Banner, BannerCreate, PageResponse } from '@/types/api'

// 封装HTTP请求方法
const http = {
  get: <T>(url: string, config?: any) => service.get<any, T>(url, config),
  post: <T>(url: string, data?: any, config?: any) => service.post<any, T>(url, data, config),
  put: <T>(url: string, data?: any, config?: any) => service.put<any, T>(url, data, config),
  delete: <T>(url: string, config?: any) => service.delete<any, T>(url, config),
  patch: <T>(url: string, data?: any, config?: any) => service.patch<any, T>(url, data, config)
}

export const bannerApi = {
  /**
   * 获取轮播图列表
   */
  getList: (params: {
    page?: number
    size?: number
    keyword?: string
    status?: string
    linkType?: string
    startDate?: string
    endDate?: string
  }) => {
    return http.get<ApiResponse<PageResponse<Banner>>>('/banners', { params })
  },

  /**
   * 获取轮播图详情
   */
  getById: (id: number) => {
    return http.get<ApiResponse<Banner>>(`/banners/${id}`)
  },

  /**
   * 创建轮播图
   */
  create: (data: BannerCreate) => {
    return http.post<ApiResponse<Banner>>('/banners', data)
  },

  /**
   * 更新轮播图
   */
  update: (id: number, data: BannerCreate) => {
    return http.put<ApiResponse<Banner>>(`/banners/${id}`, data)
  },

  /**
   * 删除轮播图
   */
  delete: (id: number) => {
    return http.delete(`/banners/${id}`)
  },

  /**
   * 更新轮播图状态
   */
  updateStatus: (id: number, status: string) => {
    return http.patch<ApiResponse<Banner>>(`/banners/${id}/status`, { status })
  },

  /**
   * 调整轮播图顺序
   */
  updateOrder: (bannerIds: number[]) => {
    return http.put('/banners/order', bannerIds)
  }
}