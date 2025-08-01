import apiClient from './config'
import type { Statistics, ApiResponse } from '@/types/api'

export const statisticsApi = {
  // 获取仪表板统计数据
  getDashboard: (): Promise<ApiResponse<Statistics>> => {
    return apiClient.get('/statistics/dashboard')
  },

  // 获取用户统计
  getUserStats: (period?: string): Promise<ApiResponse<any>> => {
    return apiClient.get('/statistics/users', { params: { period } })
  },

  // 获取信用卡统计
  getCreditCardStats: (period?: string): Promise<ApiResponse<any>> => {
    return apiClient.get('/statistics/credit-cards', { params: { period } })
  },

  // 获取新闻统计
  getNewsStats: (period?: string): Promise<ApiResponse<any>> => {
    return apiClient.get('/statistics/news', { params: { period } })
  },
}