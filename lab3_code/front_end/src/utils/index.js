// 通用工具函数

import dayjs from 'dayjs'

// 格式化日期
export const formatDate = (date, format = 'YYYY-MM-DD HH:mm:ss') => {
  if (!date) return '-'
  return dayjs(date).format(format)
}

// 格式化性别
export const formatGender = (gender) => {
  const genderMap = {
    'MALE': '男',
    'FEMALE': '女'
  }
  return genderMap[gender] || '-'
}

// 获取成绩等级
export const getScoreLevel = (score) => {
  if (score >= 90) return { label: '优秀', type: 'success' }
  if (score >= 80) return { label: '良好', type: 'success' }
  if (score >= 70) return { label: '中等', type: 'primary' }
  if (score >= 60) return { label: '及格', type: 'warning' }
  return { label: '不及格', type: 'danger' }
}

// 获取成绩颜色类
export const getScoreClass = (score) => {
  if (score >= 90) return 'excellent'
  if (score >= 80) return 'good'
  if (score >= 60) return 'pass'
  return 'fail'
}

// 计算平均分
export const calculateAverage = (scores) => {
  if (!scores || scores.length === 0) return 0
  const sum = scores.reduce((acc, score) => acc + score, 0)
  return (sum / scores.length).toFixed(2)
}

// 计算及格率
export const calculatePassRate = (scores) => {
  if (!scores || scores.length === 0) return 0
  const passCount = scores.filter(score => score >= 60).length
  return ((passCount / scores.length) * 100).toFixed(2)
}

// 计算优秀率
export const calculateExcellentRate = (scores) => {
  if (!scores || scores.length === 0) return 0
  const excellentCount = scores.filter(score => score >= 90).length
  return ((excellentCount / scores.length) * 100).toFixed(2)
}

// 获取成绩分布
export const getScoreDistribution = (scores) => {
  const distribution = {
    excellent: 0,  // 90-100
    good: 0,       // 80-89
    medium: 0,     // 70-79
    pass: 0,       // 60-69
    fail: 0        // 0-59
  }
  
  scores.forEach(score => {
    if (score >= 90) distribution.excellent++
    else if (score >= 80) distribution.good++
    else if (score >= 70) distribution.medium++
    else if (score >= 60) distribution.pass++
    else distribution.fail++
  })
  
  return distribution
}

// 深拷贝
export const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj))
}

// 防抖
export const debounce = (func, wait = 300) => {
  let timeout
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout)
      func(...args)
    }
    clearTimeout(timeout)
    timeout = setTimeout(later, wait)
  }
}

// 节流
export const throttle = (func, limit = 300) => {
  let inThrottle
  return function(...args) {
    if (!inThrottle) {
      func.apply(this, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

// 导出Excel（模拟）
export const exportToExcel = (data, filename) => {
  console.log('导出数据:', data)
  console.log(`正在导出 ${filename}.xlsx`)
  // 实际项目中可以使用 xlsx 库来实现真正的导出功能
}

// 本地存储工具
export const storage = {
  get(key) {
    const value = localStorage.getItem(key)
    try {
      return JSON.parse(value)
    } catch {
      return value
    }
  },
  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value))
  },
  remove(key) {
    localStorage.removeItem(key)
  },
  clear() {
    localStorage.clear()
  }
}
