import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest } from '@/types/api'
import { authApi } from '@/api/auth'
import { userApi } from '@/api/user'
import { message } from 'ant-design-vue'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const loading = ref(false)

  // 计算属性
  const isLoggedIn = computed(() => !!token.value && !!user.value)
  const isAdmin = computed(() => user.value?.role === 'ADMIN')

  // 登录
  const login = async (loginData: LoginRequest) => {
    try {
      loading.value = true
      const response = await authApi.login(loginData)
      
      if (response.success) {
        const { token: newToken, refreshToken: newRefreshToken, user: userData } = response.data
        
        // 保存到状态
        token.value = newToken
        refreshToken.value = newRefreshToken
        user.value = userData
        
        // 保存到本地存储
        localStorage.setItem('token', newToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        
        message.success('登录成功')
        return true
      } else {
        message.error(response.message || '登录失败')
        return false
      }
    } catch (error: any) {
      message.error(error.message || '登录失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 登出
  const logout = async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('Logout error:', error)
    } finally {
      // 清除状态
      user.value = null
      token.value = null
      refreshToken.value = null
      
      // 清除本地存储
      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      
      message.success('已退出登录')
    }
  }

  // 获取当前用户信息
  const getCurrentUser = async () => {
    if (!token.value) return false
    
    try {
      const response = await userApi.getCurrentUser()
      if (response.success) {
        user.value = response.data
        return true
      } else {
        await logout()
        return false
      }
    } catch (error) {
      await logout()
      return false
    }
  }

  // 刷新token
  const refreshAccessToken = async () => {
    if (!refreshToken.value) return false
    
    try {
      const response = await authApi.refreshToken(refreshToken.value)
      if (response.success) {
        const { token: newToken, refreshToken: newRefreshToken } = response.data
        
        token.value = newToken
        refreshToken.value = newRefreshToken
        
        localStorage.setItem('token', newToken)
        localStorage.setItem('refreshToken', newRefreshToken)
        
        return true
      } else {
        await logout()
        return false
      }
    } catch (error) {
      await logout()
      return false
    }
  }

  // 修改密码
  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      loading.value = true
      const response = await authApi.changePassword({ oldPassword, newPassword })
      
      if (response.success) {
        message.success('密码修改成功')
        return true
      } else {
        message.error(response.message || '密码修改失败')
        return false
      }
    } catch (error: any) {
      message.error(error.message || '密码修改失败')
      return false
    } finally {
      loading.value = false
    }
  }

  // 初始化用户信息
  const initUser = async () => {
    if (token.value) {
      await getCurrentUser()
    }
  }

  return {
    // 状态
    user,
    token,
    refreshToken,
    loading,
    
    // 计算属性
    isLoggedIn,
    isAdmin,
    
    // 方法
    login,
    logout,
    getCurrentUser,
    refreshAccessToken,
    changePassword,
    initUser,
  }
})