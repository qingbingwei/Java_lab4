import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { initializeAllData } from '@/utils/mockData'
import { storage } from '@/utils'

export const useSystemStore = defineStore('system', () => {
  // 状态
  const students = ref([])
  const teachers = ref([])
  const courses = ref([])
  const teachingClasses = ref([])
  const isInitialized = ref(false)
  const isDarkMode = ref(false)

  // 计算属性
  const studentCount = computed(() => students.value.length)
  const teacherCount = computed(() => teachers.value.length)
  const courseCount = computed(() => courses.value.length)
  const classCount = computed(() => teachingClasses.value.length)

  // 初始化系统
  const initSystem = () => {
    // 尝试从本地存储加载数据
    const savedData = storage.get('systemData')
    if (savedData && savedData.students && savedData.students.length > 0) {
      students.value = savedData.students
      teachers.value = savedData.teachers
      courses.value = savedData.courses
      teachingClasses.value = savedData.teachingClasses
      isInitialized.value = true
    }
    
    // 加载主题设置
    const savedTheme = storage.get('theme')
    if (savedTheme) {
      isDarkMode.value = savedTheme === 'dark'
      toggleTheme(isDarkMode.value)
    }
  }

  // 初始化数据
  const initializeData = () => {
    const data = initializeAllData()
    students.value = data.students
    teachers.value = data.teachers
    courses.value = data.courses
    teachingClasses.value = data.teachingClasses
    isInitialized.value = true
    
    // 保存到本地存储
    saveData()
    
    return data
  }

  // 保存数据到本地存储
  const saveData = () => {
    storage.set('systemData', {
      students: students.value,
      teachers: teachers.value,
      courses: courses.value,
      teachingClasses: teachingClasses.value
    })
  }

  // 清空数据
  const clearData = () => {
    students.value = []
    teachers.value = []
    courses.value = []
    teachingClasses.value = []
    isInitialized.value = false
    storage.remove('systemData')
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

  // 获取学生
  const getStudent = (studentId) => {
    return students.value.find(s => s.studentId === studentId)
  }

  // 获取教师
  const getTeacher = (teacherId) => {
    return teachers.value.find(t => t.teacherId === teacherId)
  }

  // 获取课程
  const getCourse = (courseId) => {
    return courses.value.find(c => c.courseId === courseId)
  }

  // 获取教学班
  const getTeachingClass = (classId) => {
    return teachingClasses.value.find(tc => tc.classId === classId)
  }

  // 更新学生成绩
  const updateStudentScore = (studentId, classId, scoreData) => {
    const student = getStudent(studentId)
    if (student) {
      if (!student.scores) {
        student.scores = {}
      }
      student.scores[classId] = scoreData
      saveData()
    }
  }

  return {
    // 状态
    students,
    teachers,
    courses,
    teachingClasses,
    isInitialized,
    isDarkMode,
    // 计算属性
    studentCount,
    teacherCount,
    courseCount,
    classCount,
    // 方法
    initSystem,
    initializeData,
    saveData,
    clearData,
    toggleTheme,
    getStudent,
    getTeacher,
    getCourse,
    getTeachingClass,
    updateStudentScore
  }
})
