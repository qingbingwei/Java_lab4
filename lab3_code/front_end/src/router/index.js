import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import { useSystemStore } from '@/stores/system'

const routes = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/dashboard',
    children: [
      {
        path: '/dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页概览', icon: 'DataLine', roles: ['ADMIN', 'TEACHER', 'STUDENT'] }
      },
      // 管理员专属 - 用户管理
      {
        path: '/students',
        name: 'Students',
        component: () => import('@/views/students/StudentList.vue'),
        meta: { title: '学生管理', icon: 'User', roles: ['ADMIN'] }
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
        meta: { title: '教师管理', icon: 'UserFilled', roles: ['ADMIN'] }
      },
      // 管理员专属 - 课程与教学班
      {
        path: '/courses',
        name: 'Courses',
        component: () => import('@/views/courses/CourseList.vue'),
        meta: { title: '课程管理', icon: 'Reading', roles: ['ADMIN'] }
      },
      {
        path: '/teaching-classes',
        name: 'TeachingClasses',
        component: () => import('@/views/classes/ClassList.vue'),
        meta: { title: '教学班管理', icon: 'School', roles: ['ADMIN'] }
      },
      // 教师专属页面
      {
        path: '/teacher/classes',
        name: 'TeacherClasses',
        component: () => import('@/views/teacher/MyClasses.vue'),
        meta: { title: '我的教学班', icon: 'School', roles: ['TEACHER'] }
      },
      {
        path: '/teacher/scores',
        name: 'TeacherScores',
        component: () => import('@/views/teacher/MyScores.vue'),
        meta: { title: '班级成绩', icon: 'Document', roles: ['TEACHER'] }
      },
      // 成绩管理 - 共享页面
      {
        path: '/scores/entry',
        name: 'ScoreEntry',
        component: () => import('@/views/scores/ScoreEntry.vue'),
        meta: { title: '成绩录入', icon: 'EditPen', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/scores/query',
        name: 'ScoreQuery',
        component: () => import('@/views/scores/ScoreQuery.vue'),
        meta: { title: '成绩查询', icon: 'Search', roles: ['ADMIN', 'TEACHER'] }
      },
      {
        path: '/scores/ranking',
        name: 'ScoreRanking',
        component: () => import('@/views/scores/ScoreRanking.vue'),
        meta: { title: '成绩排名', icon: 'TrendCharts', roles: ['ADMIN', 'TEACHER', 'STUDENT'] }
      },
      // 统计分析 - 管理员和教师可用
      {
        path: '/statistics',
        name: 'Statistics',
        component: () => import('@/views/statistics/StatisticsView.vue'),
        meta: { title: '统计分析', icon: 'PieChart', roles: ['ADMIN', 'TEACHER'] }
      },
      // 选课管理 - 管理员管理选课，学生自主选课
      {
        path: '/enrollment',
        name: 'Enrollment',
        component: () => import('@/views/enrollment/EnrollmentView.vue'),
        meta: { title: '选课管理', icon: 'List', roles: ['ADMIN'] }
      },
      // 学生专属页面
      {
        path: '/student/courses',
        name: 'StudentCourses',
        component: () => import('@/views/student/MyCourses.vue'),
        meta: { title: '我的课程', icon: 'Reading', roles: ['STUDENT'] }
      },
      {
        path: '/student/scores',
        name: 'StudentScores',
        component: () => import('@/views/student/MyScores.vue'),
        meta: { title: '我的成绩', icon: 'Document', roles: ['STUDENT'] }
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
  
  // 检查权限
  const systemStore = useSystemStore()
  const userRole = systemStore.currentRole
  const requiredRoles = to.meta.roles
  
  if (requiredRoles && requiredRoles.length > 0) {
    if (!userRole || !requiredRoles.includes(userRole)) {
      // 无权限，重定向到首页
      if (to.path !== '/dashboard') {
        return next('/dashboard')
      }
    }
  }
  
  next()
})

export default router
