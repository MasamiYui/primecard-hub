import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User } from '@/types/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(localStorage.getItem('token'))
  const user = ref<User | null>(null)
  const isLoggedIn = ref<boolean>(!!token.value)

  const setToken = (newToken: string) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
    isLoggedIn.value = true
  }

  const setUser = (userData: User) => {
    user.value = userData
  }

  const logout = () => {
    token.value = null
    user.value = null
    isLoggedIn.value = false
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  const login = (tokenValue: string, userData: User) => {
    setToken(tokenValue)
    setUser(userData)
    localStorage.setItem('user', JSON.stringify(userData))
  }

  // 初始化时从localStorage恢复用户信息
  const initAuth = () => {
    const savedUser = localStorage.getItem('user')
    if (savedUser && token.value) {
      try {
        user.value = JSON.parse(savedUser)
        isLoggedIn.value = true
      } catch (error) {
        logout()
      }
    }
  }

  return {
    token,
    user,
    isLoggedIn,
    setToken,
    setUser,
    logout,
    login,
    initAuth
  }
})