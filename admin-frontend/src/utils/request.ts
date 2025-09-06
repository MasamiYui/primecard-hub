import axios, { type AxiosRequestConfig, type AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'
import { useUserStore } from '@/stores/user'

// 创建axios实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const authStore = useAuthStore()
    if (authStore.token) {
      console.log('请求拦截器 - 使用 authStore token:', authStore.token)
      config.headers.Authorization = `Bearer ${authStore.token}`
    } else {
      // 如果 authStore 中没有 token，尝试从 localStorage 获取
      const token = localStorage.getItem('token')
      if (token) {
        console.log('请求拦截器 - 使用 localStorage token:', token)
        config.headers.Authorization = `Bearer ${token}`
      } else {
        console.log('请求拦截器 - 没有可用的 token')
      }
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse) => {
    const { data } = response
    console.log('响应拦截器 - 原始响应:', response.config.url, data)
    
    // 如果是文件下载等特殊情况，直接返回
    if (response.config.responseType === 'blob') {
      console.log('响应拦截器 - 文件下载，直接返回')
      return response
    }
    
    // 正常业务响应
    if (data.code === 200 || data.success) {
      console.log('响应拦截器 - 业务成功，返回data:', data)
      return data
    }
    
    // 业务错误
    console.error('响应拦截器 - 业务错误:', data.message || '请求失败')
    ElMessage.error(data.message || '请求失败')
    return Promise.reject(new Error(data.message || '请求失败'))
  },
  (error) => {
    const { response } = error
    
    if (response) {
      const { status, data } = response
      
      switch (status) {
        case 401:
          ElMessage.error('登录已过期，请重新登录')
          
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
          
          // 使用强制导航方式跳转
          console.log('使用强制导航方式跳转到登录页')
          window.location.href = window.location.origin + '/login'
          break
        case 403:
          ElMessage.error('没有权限访问该资源')
          break
        case 404:
          ElMessage.error('请求的资源不存在')
          break
        case 500:
          ElMessage.error('服务器内部错误')
          break
        default:
          ElMessage.error(data?.message || `请求失败 (${status})`)
      }
    } else if (error.code === 'ECONNABORTED') {
      ElMessage.error('请求超时，请稍后重试')
    } else {
      ElMessage.error('网络错误，请检查网络连接')
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export const request = <T = any>(config: AxiosRequestConfig): Promise<T> => {
  return service(config)
}

export default service