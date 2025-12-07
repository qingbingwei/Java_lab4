import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { statisticsApi } from '@/api'
import { storage } from '@/utils'

export const useSystemStore = defineStore('system', () => {
  // 状态
  const overview = ref({
    studentCount: 0,
    teacherCount: 0,
    courseCount: 0,
    classCount: 0,
    scoreCount: 0,
    avgScore: 0,
    passRate: 0
  })
  const isDarkMode = ref(false)
  const isLoading = ref(false)

  // 计算属性
  const studentCount = computed(() => overview.value.studentCount)
  const teacherCount = computed(() => overview.value.teacherCount)
  const courseCount = computed(() => overview.value.courseCount)
  const classCount = computed(() => overview.value.teachingClassCount || overview.value.classCount)

  // 加载系统概览数据
  const loadOverview = async () => {
    try {
      isLoading.value = true
      const res = await statisticsApi.getOverview()
      if (res.data) {
        overview.value = res.data
      }
    } catch (error) {
      console.error('加载概览数据失败:', error)
    } finally {
      isLoading.value = false
    }
  }

  // 切换主题
  const toggleTheme = (dark) => {
    isDarkMode.value = dark
    if (dark) {
      document.documentElement.classList.add('dark')
    } else {
      document.documentElement.classList.remove('dark')
    }
    storage.set('theme', dark ? 'dark' : 'light')
  }

  // 初始化主题
  const initTheme = () => {
    const savedTheme = storage.get('theme')
    if (savedTheme) {
      isDarkMode.value = savedTheme === 'dark'
      toggleTheme(isDarkMode.value)
    }
  }

  // 初始化时加载主题
  initTheme()

  return {
    // 状态
    overview,
    isDarkMode,
    isLoading,
    // 计算属性
    studentCount,
    teacherCount,
    courseCount,
    classCount,
    // 方法
    loadOverview,
    toggleTheme
  }
})
