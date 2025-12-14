/**
 * 格式化性别
 */
export const formatGender = (gender) => {
  return gender === 'MALE' ? '男' : '女'
}

/**
 * 根据成绩获取样式类
 */
export const getScoreClass = (score) => {
  if (score >= 90) return 'score-excellent'
  if (score >= 80) return 'score-good'
  if (score >= 70) return 'score-medium'
  if (score >= 60) return 'score-pass'
  return 'score-fail'
}

/**
 * 格式化课程类型
 */
export const formatCourseType = (type) => {
  return type === 'REQUIRED' ? '必修' : '选修'
}

/**
 * 格式化日期时间
 */
export const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

/**
 * 格式化日期
 */
export const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

/**
 * 本地存储工具
 */
export const storage = {
  get(key) {
    const value = localStorage.getItem(key)
    try {
      return value ? JSON.parse(value) : null
    } catch {
      return value
    }
  },
  set(key, value) {
    localStorage.setItem(key, JSON.stringify(value))
  },
  remove(key) {
    localStorage.removeItem(key)
  }
}

/**
 * 下载文件
 */
export const downloadFile = (url, filename) => {
  const link = document.createElement('a')
  link.href = url
  link.download = filename
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}
