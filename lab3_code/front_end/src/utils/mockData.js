// Mock数据生成工具

const surnames = ['王', '李', '张', '刘', '陈', '杨', '黄', '赵', '周', '吴', '徐', '孙', '马', '朱', '胡', '郭', '何', '高', '林', '罗']
const givenNames = ['伟', '芳', '娜', '秀英', '敏', '静', '丽', '强', '磊', '军', '洋', '勇', '艳', '杰', '涛', '明', '超', '红', '娟', '霞', '鹏', '飞', '慧', '萍', '华', '玲', '宇', '辉', '刚', '凯']
const courseNames = ['高等数学', '大学英语', '计算机科学', '数据结构', '操作系统', '数据库原理', '软件工程', '计算机网络']

// 生成随机姓名
export const generateName = () => {
  const surname = surnames[Math.floor(Math.random() * surnames.length)]
  const givenName1 = givenNames[Math.floor(Math.random() * givenNames.length)]
  const givenName2 = Math.random() > 0.5 ? givenNames[Math.floor(Math.random() * givenNames.length)] : ''
  return surname + givenName1 + givenName2
}

// 生成随机学号
export const generateStudentId = (index) => {
  const year = 2023
  const major = String(Math.floor(Math.random() * 10)).padStart(2, '0')
  const num = String(index).padStart(4, '0')
  return `${year}${major}${num}`
}

// 生成随机教师编号
export const generateTeacherId = (index) => {
  return `T${String(index).padStart(4, '0')}`
}

// 生成随机课程编号
export const generateCourseId = (index) => {
  return `C${String(index).padStart(3, '0')}`
}

// 生成随机分数
export const generateScore = (min = 60, max = 100) => {
  return Math.floor(Math.random() * (max - min + 1)) + min
}

// 生成随机日期
export const generateDate = (start = new Date(2023, 8, 1), end = new Date()) => {
  return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
}

// 生成学生数据
export const generateStudents = (count = 120) => {
  const students = []
  for (let i = 1; i <= count; i++) {
    students.push({
      studentId: generateStudentId(i),
      name: generateName(),
      gender: Math.random() > 0.5 ? 'MALE' : 'FEMALE',
      enrolledClasses: [],
      scores: {}
    })
  }
  return students
}

// 生成教师数据
export const generateTeachers = (count = 8) => {
  const teachers = []
  for (let i = 1; i <= count; i++) {
    teachers.push({
      teacherId: generateTeacherId(i),
      name: generateName(),
      teachingClasses: []
    })
  }
  return teachers
}

// 生成课程数据
export const generateCourses = (count = 6) => {
  const courses = []
  const selectedCourses = courseNames.slice(0, count)
  
  for (let i = 0; i < count; i++) {
    courses.push({
      courseId: generateCourseId(i + 1),
      courseName: selectedCourses[i],
      credits: Math.floor(Math.random() * 3) + 2 // 2-4学分
    })
  }
  return courses
}

// 生成教学班数据
export const generateTeachingClasses = (courses, teachers) => {
  const classes = []
  let classIndex = 1
  
  courses.forEach(course => {
    // 每门课程2-3个教学班
    const classCount = Math.floor(Math.random() * 2) + 2
    for (let i = 0; i < classCount; i++) {
      const teacher = teachers[Math.floor(Math.random() * teachers.length)]
      const teachingClass = {
        classId: `${course.courseId}-${String(classIndex).padStart(2, '0')}`,
        course: course,
        teacher: teacher,
        students: [],
        capacity: Math.floor(Math.random() * 20) + 30, // 30-50人
        semester: '2023-2024-1'
      }
      classes.push(teachingClass)
      // 将教学班ID添加到教师的teachingClasses数组中
      if (!teacher.teachingClasses.includes(teachingClass.classId)) {
        teacher.teachingClasses.push(teachingClass.classId)
      }
      classIndex++
    }
  })
  
  return classes
}

// 为学生分配课程
export const enrollStudentsInClasses = (students, teachingClasses) => {
  students.forEach(student => {
    // 每个学生选3-5门课
    const courseCount = Math.floor(Math.random() * 3) + 3
    const selectedCourses = new Set()
    const enrolledClasses = []
    
    while (selectedCourses.size < courseCount) {
      const randomClass = teachingClasses[Math.floor(Math.random() * teachingClasses.length)]
      const courseId = randomClass.course.courseId
      
      if (!selectedCourses.has(courseId) && randomClass.students.length < randomClass.capacity) {
        selectedCourses.add(courseId)
        enrolledClasses.push(randomClass.classId)
        randomClass.students.push(student.studentId)
      }
    }
    
    student.enrolledClasses = enrolledClasses
  })
}

// 生成成绩数据
export const generateScores = (students, teachingClasses) => {
  students.forEach(student => {
    student.enrolledClasses.forEach(classId => {
      const teachingClass = teachingClasses.find(tc => tc.classId === classId)
      if (teachingClass) {
        const regularScore = generateScore(70, 100)
        const midtermScore = generateScore(65, 100)
        const experimentScore = generateScore(70, 100)
        const finalExamScore = generateScore(60, 100)
        
        const finalScore = (
          regularScore * 0.2 +
          midtermScore * 0.2 +
          experimentScore * 0.2 +
          finalExamScore * 0.4
        ).toFixed(2)
        
        student.scores[classId] = {
          classId,
          regularScore,
          midtermScore,
          experimentScore,
          finalExamScore,
          finalScore: parseFloat(finalScore),
          regularScoreTime: generateDate(new Date(2023, 8, 1), new Date(2023, 9, 1)),
          midtermScoreTime: generateDate(new Date(2023, 9, 15), new Date(2023, 10, 1)),
          experimentScoreTime: generateDate(new Date(2023, 10, 1), new Date(2023, 10, 15)),
          finalExamScoreTime: generateDate(new Date(2023, 11, 1), new Date(2023, 11, 15))
        }
      }
    })
  })
}

// 初始化所有数据
export const initializeAllData = () => {
  const students = generateStudents(120)
  const teachers = generateTeachers(8)
  const courses = generateCourses(6)
  const teachingClasses = generateTeachingClasses(courses, teachers)
  
  enrollStudentsInClasses(students, teachingClasses)
  generateScores(students, teachingClasses)
  
  return {
    students,
    teachers,
    courses,
    teachingClasses
  }
}
