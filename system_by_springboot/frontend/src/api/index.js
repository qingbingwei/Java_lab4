import request from '@/utils/request'

/**
 * 学生API
 */
export const studentApi = {
  // 分页查询学生
  getPage(params) {
    return request({
      url: '/students/page',
      method: 'get',
      params
    })
  },
  // 查询所有学生
  getList(params) {
    return request({
      url: '/students/list',
      method: 'get',
      params
    })
  },
  // 根据ID查询学生
  getById(id) {
    return request({
      url: `/students/${id}`,
      method: 'get'
    })
  },
  // 根据学号查询
  getByStudentId(studentId) {
    return request({
      url: `/students/studentId/${studentId}`,
      method: 'get'
    })
  },
  // 新增学生
  create(data) {
    return request({
      url: '/students',
      method: 'post',
      data
    })
  },
  // 更新学生
  update(id, data) {
    return request({
      url: `/students/${id}`,
      method: 'put',
      data
    })
  },
  // 删除学生
  delete(id) {
    return request({
      url: `/students/${id}`,
      method: 'delete'
    })
  },
  // 查询学生成绩详情
  getScores(id) {
    return request({
      url: `/students/${id}/scores`,
      method: 'get'
    })
  }
}

/**
 * 教师API
 */
export const teacherApi = {
  getPage(params) {
    return request({
      url: '/teachers/page',
      method: 'get',
      params
    })
  },
  getList() {
    return request({
      url: '/teachers/list',
      method: 'get'
    })
  },
  getById(id) {
    return request({
      url: `/teachers/${id}`,
      method: 'get'
    })
  },
  create(data) {
    return request({
      url: '/teachers',
      method: 'post',
      data
    })
  },
  update(id, data) {
    return request({
      url: `/teachers/${id}`,
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/teachers/${id}`,
      method: 'delete'
    })
  },
  // 查询教师的教学班
  getClasses(id) {
    return request({
      url: `/teachers/${id}/classes`,
      method: 'get'
    })
  }
}

/**
 * 课程API
 */
export const courseApi = {
  getPage(params) {
    return request({
      url: '/courses/page',
      method: 'get',
      params
    })
  },
  getList() {
    return request({
      url: '/courses/list',
      method: 'get'
    })
  },
  getById(id) {
    return request({
      url: `/courses/${id}`,
      method: 'get'
    })
  },
  create(data) {
    return request({
      url: '/courses',
      method: 'post',
      data
    })
  },
  update(id, data) {
    return request({
      url: `/courses/${id}`,
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/courses/${id}`,
      method: 'delete'
    })
  }
}

/**
 * 教学班API
 */
export const teachingClassApi = {
  getPage(params) {
    return request({
      url: '/teaching-classes/page',
      method: 'get',
      params
    })
  },
  getList(params) {
    return request({
      url: '/teaching-classes/list',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/teaching-classes/${id}`,
      method: 'get'
    })
  },
  create(data) {
    return request({
      url: '/teaching-classes',
      method: 'post',
      data
    })
  },
  update(id, data) {
    return request({
      url: `/teaching-classes/${id}`,
      method: 'put',
      data
    })
  },
  delete(id) {
    return request({
      url: `/teaching-classes/${id}`,
      method: 'delete'
    })
  },
  // 查询教学班学生
  getStudents(id) {
    return request({
      url: `/teaching-classes/${id}/students`,
      method: 'get'
    })
  },
  // 查询教学班学生及成绩
  getClassStudents(id) {
    return request({
      url: `/teaching-classes/${id}/students`,
      method: 'get'
    })
  },
  // 查询教师的教学班
  getTeacherClasses(teacherDbId) {
    return request({
      url: `/teaching-classes/teacher/${teacherDbId}`,
      method: 'get'
    })
  }
}

/**
 * 成绩API
 */
export const scoreApi = {
  getPage(params) {
    return request({
      url: '/scores/page',
      method: 'get',
      params
    })
  },
  getById(id) {
    return request({
      url: `/scores/${id}`,
      method: 'get'
    })
  },
  // 录入成绩
  create(data) {
    return request({
      url: '/scores',
      method: 'post',
      data
    })
  },
  // 更新成绩
  update(id, data) {
    return request({
      url: '/scores',
      method: 'put',
      data: { ...data, id }
    })
  },
  // 删除成绩
  delete(id) {
    return request({
      url: `/scores/${id}`,
      method: 'delete'
    })
  },
  // 批量录入成绩
  batchCreate(dataList) {
    return request({
      url: '/scores/batch',
      method: 'post',
      data: dataList
    })
  },
  // 查询学生成绩
  getStudentScores(studentDbId, params) {
    return request({
      url: `/scores/student/${studentDbId}`,
      method: 'get',
      params
    })
  },
  // 成绩排名
  getRanking(params) {
    return request({
      url: '/scores/ranking',
      method: 'get',
      params
    })
  },
  // 课程统计
  getCourseStatistics(courseId) {
    return request({
      url: `/scores/statistics/course/${courseId}`,
      method: 'get'
    })
  }
}

/**
 * 选课API
 */
export const enrollmentApi = {
  // 学生选课
  enroll(data) {
    return request({
      url: '/enrollments',
      method: 'post',
      data
    })
  },
  // 学生退课
  drop(params) {
    return request({
      url: '/enrollments',
      method: 'delete',
      params
    })
  },
  // 退选（通过选课ID）
  withdraw(enrollmentId) {
    return request({
      url: `/enrollments/${enrollmentId}`,
      method: 'delete'
    })
  },
  // 查询学生选课
  getStudentEnrollments(studentDbId) {
    return request({
      url: `/enrollments/student/${studentDbId}`,
      method: 'get'
    })
  },
  // 查询教学班学生
  getClassEnrollments(teachingClassDbId) {
    return request({
      url: `/enrollments/class/${teachingClassDbId}`,
      method: 'get'
    })
  }
}

/**
 * 统计API
 */
export const statisticsApi = {
  // 系统概览
  getOverview() {
    return request({
      url: '/statistics/overview',
      method: 'get'
    })
  },
  // 成绩分布
  getScoreDistribution(params) {
    return request({
      url: '/statistics/score-distribution',
      method: 'get',
      params
    })
  },
  // 课程平均分
  getCourseAverage(params) {
    return request({
      url: '/statistics/course-average',
      method: 'get',
      params
    })
  },
  // 成绩趋势
  getScoreTrend(params) {
    return request({
      url: '/statistics/score-trend',
      method: 'get',
      params
    })
  },
  // 班级对比
  getClassComparison(params) {
    return request({
      url: '/statistics/class-comparison',
      method: 'get',
      params
    })
  }
}

/**
 * Excel API
 */
export const excelApi = {
  // 导出学生
  exportStudents(params) {
    return `/api/excel/students/export?${new URLSearchParams(params).toString()}`
  },
  // 导入学生
  importStudents(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/excel/students/import',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  // 下载学生模板
  downloadStudentTemplate() {
    return '/api/excel/students/template'
  },
  // 导出成绩
  exportScores(params) {
    return `/api/excel/scores/export?${new URLSearchParams(params).toString()}`
  },
  // 导入成绩
  importScores(file) {
    const formData = new FormData()
    formData.append('file', file)
    return request({
      url: '/excel/scores/import',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  // 下载成绩模板
  downloadScoreTemplate() {
    return '/api/excel/scores/template'
  }
}

/**
 * 文件API
 */
export const fileApi = {
  upload(file, category) {
    const formData = new FormData()
    formData.append('file', file)
    if (category) formData.append('category', category)
    return request({
      url: '/files/upload',
      method: 'post',
      data: formData,
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  download(fileId) {
    return `/api/files/download/${fileId}`
  },
  delete(fileId) {
    return request({
      url: `/files/${fileId}`,
      method: 'delete'
    })
  },
  getList(category) {
    return request({
      url: '/files/list',
      method: 'get',
      params: { category }
    })
  }
}

/**
 * 认证API
 */
export const authApi = {
  // 登录
  login(data) {
    return request({
      url: '/auth/login',
      method: 'post',
      data
    })
  },
  // 登出
  logout() {
    return request({
      url: '/auth/logout',
      method: 'post'
    })
  },
  // 获取当前用户信息
  getUserInfo() {
    return request({
      url: '/auth/user-info',
      method: 'get'
    })
  },
  // 修改密码
  changePassword(oldPassword, newPassword) {
    return request({
      url: '/auth/change-password',
      method: 'post',
      params: { oldPassword, newPassword }
    })
  },
  // 初始化用户
  initUsers() {
    return request({
      url: '/auth/init-users',
      method: 'post'
    })
  }
}
