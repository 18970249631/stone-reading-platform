import { defineStore } from 'pinia'
import { ref } from 'vue'
import { login as apiLogin, register as apiRegister, getUserInfo as apiGetUserInfo } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

  const isLoggedIn = () => !!token.value

  const login = async (username, password) => {
    const res = await apiLogin(username, password)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
    await fetchUserInfo()
    return res
  }

  const register = async (data) => {
    const res = await apiRegister(data)
    return res
  }

  const fetchUserInfo = async () => {
    if (!token.value) return
    try {
      const res = await apiGetUserInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    } catch (e) {
      logout()
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = {}
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  const checkLogin = () => {
    if (token.value && !userInfo.value.id) {
      fetchUserInfo()
    }
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    login,
    register,
    fetchUserInfo,
    logout,
    checkLogin
  }
})