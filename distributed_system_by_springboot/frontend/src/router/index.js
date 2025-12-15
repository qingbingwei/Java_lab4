import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录', public: true }
  },
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', icon: 'DataLine', permission: 'dashboard' }
      },
      {
        path: '/students',
        name: 'Students',
        component: () => import('@/views/students/StudentList.vue'),
        meta: { title: '学生管理', icon: 'User', permission: 'students', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/students/:id',
        name: 'StudentDetail',
        component: () => import('@/views/students/StudentDetail.vue'),
        meta: { title: '学生详情', hidden: true, roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/teachers',
        name: 'Teachers',
        component: () => import('@/views/teachers/TeacherList.vue'),
        meta: { title: '教师管理', icon: 'UserFilled', permission: 'teachers', roles: ['ADMIN'] }
      },
      {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/courses/CourseList.vue'),
        meta: { title: '课程管理', icon: 'Reading', permission: 'courses', roles: ['ADMIN'] }
      },
      {
        path: '/teaching-classes',
        name: 'TeachingClasses',
        component: () => import('@/views/classes/ClassList.vue'),
        meta: { title: '教学班管理', icon: 'School', permission: 'classes', roles: ['ADMIN'] }
      },
      {
        path: '/scores/entry',
        name: 'ScoreEntry',
        component: () => import('@/views/scores/ScoreEntry.vue'),
        meta: { title: '成绩录入', icon: 'EditPen', permission: 'scores', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/scores/query',
        name: 'ScoreQuery',
        component: () => import('@/views/scores/ScoreQuery.vue'),
        meta: { title: '成绩查询', icon: 'Search', permission: 'scores:view' }
      },
      {
        path: '/scores/ranking',
        name: 'ScoreRanking',
        component: () => import('@/views/scores/ScoreRanking.vue'),
        meta: { title: '成绩排名', icon: 'TrendCharts', permission: 'scores:view', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { title: '统计分析', icon: 'PieChart', permission: 'statistics', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/enrollment',
        name: 'Enrollment',
        component: () => import('@/views/enrollment/EnrollmentView.vue'),
        meta: { title: '选课管理', icon: 'List', permission: 'enrollment' }
      },
      {
        path: '/my-scores',
        name: 'MyScores',
        component: () => import('@/views/student/MyScores.vue'),
        meta: { title: '我的成绩', icon: 'Document', permission: 'scores:view', roles: ['STUDENT'] }
      },
      {
        path: '/my-courses',
        name: 'MyCourses',
        component: () => import('@/views/student/MyCourses.vue'),
        meta: { title: '我的课程', icon: 'Notebook', permission: 'enrollment', roles: ['STUDENT'] }
      },
      // 教师专属页面
      {
        path: '/teacher/classes',
        name: 'TeacherClasses',
        component: () => import('@/views/teacher/MyClasses.vue'),
        meta: { title: '我的教学班', icon: 'School', permission: 'classes', roles: ['TEACHER'] }
      },
      {
        path: '/teacher/scores',
        name: 'TeacherScores',
        component: () => import('@/views/teacher/MyScores.vue'),
        meta: { title: '班级成绩', icon: 'Document', permission: 'scores', roles: ['TEACHER'] }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 学生成绩管理系统` : '学生成绩管理系统'
  
  // 公开页面直接放行
  if (to.meta.public) {
    next()
    return
  }
  
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    next('/login')
    return
  }
  
  // 检查角色权限
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userRole = userInfo.role
  const allowedRoles = to.meta.roles
  
  if (allowedRoles && allowedRoles.length > 0 && !allowedRoles.includes(userRole)) {
    // 没有权限，跳转到首页
    next('/dashboard')
    return
  }
  
  next()
})

export default router
