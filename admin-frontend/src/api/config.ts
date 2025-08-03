import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/user'

// API基础配置
export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api'

// 创建axios实例
const apiClient: AxiosInstance = axios.create({
  // 使用环境变量中的API基础URL
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    // 添加认证token
    const token = localStorage.getItem('token')
    console.log('apiClient请求拦截器 - 当前token:', token)
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
      console.log('apiClient请求拦截器 - 已添加Authorization头:', `Bearer ${token}`)
      console.log('apiClient请求拦截器 - 请求URL:', config.url)
    } else {
      console.log('apiClient请求拦截器 - 未添加Authorization头，token不存在或headers不存在')
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      
      switch (status) {
        case 401:
          console.error('401错误 - 未授权:', { 
            url: error.config?.url,
            method: error.config?.method,
            headers: error.config?.headers,
            responseData: data
          })
          ElMessage.error('未授权，请重新登录')
          
          // 清除本地存储
          localStorage.removeItem('token')
          localStorage.removeItem('refreshToken')
          localStorage.removeItem('user')
          
          // 清除 store 状态
          try {
            const authStore = useAuthStore()
            const userStore = useUserStore()
            console.log('清除 authStore 和 userStore 状态')
            authStore.logout()
            userStore.logout()
          } catch (e) {
            console.error('清除 store 状态失败:', e)
          }
          
          // 使用Vue Router进行导航
          import('@/router').then(({ default: router }) => {
            console.log('当前路由:', router.currentRoute.value.path)
            if (router.currentRoute.value.path !== '/login') {
              console.log('重定向到登录页')
              window.location.href = window.location.origin + '/login'
            }
          })
          break
        case 403:
          ElMessage.error('权限不足')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || '请求失败')
      }
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

export default apiClient