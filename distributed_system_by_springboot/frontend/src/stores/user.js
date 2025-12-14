import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'
import router from '@/router'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  // 计算属性
  const isLoggedIn = computed(() => !!token.value)
  const role = computed(() => userInfo.value?.role || '')
  const permissions = computed(() => userInfo.value?.permissions || [])
  const realName = computed(() => userInfo.value?.realName || '')
  const username = computed(() => userInfo.value?.username || '')
  const refId = computed(() => userInfo.value?.refId || null)

  // 角色判断
  const isAdmin = computed(() => role.value === 'ADMIN')
  const isTeacher = computed(() => role.value === 'TEACHER')
  const isStudent = computed(() => role.value === 'STUDENT')

  // 登录
  async function login(loginData) {
    const res = await authApi.login(loginData)
    const data = res.data
    
    token.value = data.token
    userInfo.value = {
      userId: data.userId,
      username: data.username,
      realName: data.realName,
      role: data.role,
      refId: data.refId,
      permissions: data.permissions
    }
    
    localStorage.setItem('token', data.token)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    
    return data
  }

  // 登出
  async function logout() {
    try {
      await authApi.logout()
    } catch (e) {
      console.error('登出请求失败', e)
    }
    
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
    router.push('/login')
  }

  // 获取用户信息
  async function fetchUserInfo() {
    if (!token.value) return null
    
    try {
      const res = await authApi.getUserInfo()
      userInfo.value = res.data
      localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
      return res.data
    } catch (e) {
      console.error('获取用户信息失败', e)
      logout()
      return null
    }
  }

  // 检查权限
  function hasPermission(permission) {
    if (isAdmin.value) return true
    return permissions.value.includes(permission)
  }

  // 检查是否有任一权限
  function hasAnyPermission(permissionList) {
    if (isAdmin.value) return true
    return permissionList.some(p => permissions.value.includes(p))
  }

  return {
    token,
    userInfo,
    isLoggedIn,
    role,
    permissions,
    realName,
    username,
    refId,
    isAdmin,
    isTeacher,
    isStudent,
    login,
    logout,
    fetchUserInfo,
    hasPermission,
    hasAnyPermission
  }
})
