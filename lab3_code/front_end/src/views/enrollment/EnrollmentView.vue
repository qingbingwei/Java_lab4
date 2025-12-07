<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><List /></el-icon>
      <span>选课管理</span>
    </div>

    <!-- 选课统计 -->
    <el-row :gutter="24" style="margin-bottom: 24px">
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card-simple">
          <div class="stat-simple">
            <el-icon class="stat-icon" color="#409eff"><User /></el-icon>
            <div>
              <div class="value">{{ enrolledStudents }}</div>
              <div class="label">已选课学生</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card-simple">
          <div class="stat-simple">
            <el-icon class="stat-icon" color="#67c23a"><DocumentChecked /></el-icon>
            <div>
              <div class="value">{{ totalEnrollments }}</div>
              <div class="label">选课总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="stat-card-simple">
          <div class="stat-simple">
            <el-icon class="stat-icon" color="#e6a23c"><TrendCharts /></el-icon>
            <div>
              <div class="value">{{ avgCoursesPerStudent }}</div>
              <div class="label">人均选课数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 教学班选课情况 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>教学班选课情况</span>
          <el-input
            v-model="searchText"
            placeholder="搜索课程或教师"
            :prefix-icon="Search"
            style="width: 300px"
            clearable
          />
        </div>
      </template>

      <el-table :data="filteredClasses" stripe>
        <el-table-column prop="classId" label="班级编号" width="120" />
        <el-table-column prop="course.courseName" label="课程名称" width="150" />
        <el-table-column prop="teacher.name" label="任课教师" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="选课人数" width="150">
          <template #default="{ row }">
            <div class="enrollment-progress">
              <span>{{ row.students?.length || 0 }} / {{ row.capacity }}</span>
              <el-progress
                :percentage="getEnrollmentRate(row)"
                :color="getProgressColor(getEnrollmentRate(row))"
                :status="getEnrollmentRate(row) >= 100 ? 'success' : undefined"
              />
            </div>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row)" size="small">
              {{ getStatusText(row) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学生名单" min-width="200">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewStudentList(row)">
              查看名单 ({{ row.students?.length || 0 }}人)
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生名单对话框 -->
    <el-dialog
      v-model="studentListVisible"
      :title="`${selectedClass?.course?.courseName} - 学生名单`"
      width="800px"
    >
      <el-table :data="studentListData" stripe max-height="400">
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'MALE' ? 'primary' : 'danger'" size="small">
              {{ formatGender(row.gender) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="已选课程数" width="120">
          <template #default="{ row }">
            {{ row.enrolledClasses?.length || 0 }} 门
          </template>
        </el-table-column>
        <el-table-column label="成绩状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="hasScore(row)" type="success" size="small">已录入</el-tag>
            <el-tag v-else type="info" size="small">未录入</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewStudentDetail(row.studentId)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { formatGender } from '@/utils'
import {
  List,
  User,
  DocumentChecked,
  TrendCharts,
  Search
} from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const searchText = ref('')
const studentListVisible = ref(false)
const selectedClass = ref(null)

// 统计数据
const enrolledStudents = computed(() => {
  return systemStore.students.filter(s => s.enrolledClasses && s.enrolledClasses.length > 0).length
})

const totalEnrollments = computed(() => {
  return systemStore.students.reduce((sum, s) => sum + (s.enrolledClasses?.length || 0), 0)
})

const avgCoursesPerStudent = computed(() => {
  if (enrolledStudents.value === 0) return '0.00'
  return (totalEnrollments.value / enrolledStudents.value).toFixed(2)
})

// 过滤后的教学班
const filteredClasses = computed(() => {
  if (!searchText.value) return systemStore.teachingClasses

  const search = searchText.value.toLowerCase()
  return systemStore.teachingClasses.filter(tc =>
    tc.course?.courseName?.toLowerCase().includes(search) ||
    tc.teacher?.name?.toLowerCase().includes(search) ||
    tc.classId?.toLowerCase().includes(search)
  )
})

// 学生名单数据
const studentListData = computed(() => {
  if (!selectedClass.value) return []
  
  return (selectedClass.value.students || []).map(studentId => {
    return systemStore.getStudent(studentId)
  }).filter(s => s)
})

// 方法
const getEnrollmentRate = (tc) => {
  const enrolled = tc.students?.length || 0
  return Math.round((enrolled / tc.capacity) * 100)
}

const getProgressColor = (rate) => {
  if (rate >= 100) return '#f56c6c'
  if (rate >= 80) return '#e6a23c'
  if (rate >= 50) return '#409eff'
  return '#67c23a'
}

const getStatusType = (tc) => {
  const rate = getEnrollmentRate(tc)
  if (rate >= 100) return 'danger'
  if (rate >= 80) return 'warning'
  if (rate >= 50) return 'primary'
  return 'success'
}

const getStatusText = (tc) => {
  const rate = getEnrollmentRate(tc)
  if (rate >= 100) return '已满'
  if (rate >= 80) return '接近满员'
  if (rate >= 50) return '正常'
  return '充足'
}

const viewStudentList = (tc) => {
  selectedClass.value = tc
  studentListVisible.value = true
}

const hasScore = (student) => {
  if (!selectedClass.value || !student.scores) return false
  return !!student.scores[selectedClass.value.classId]
}

const viewStudentDetail = (studentId) => {
  studentListVisible.value = false
  router.push(`/students/${studentId}`)
}
</script>

<style lang="scss" scoped>
.stat-card-simple {
  .stat-simple {
    display: flex;
    align-items: center;
    gap: 16px;

    .stat-icon {
      font-size: 48px;
    }

    .value {
      font-size: 28px;
      font-weight: bold;
      color: var(--el-text-color-primary);
    }

    .label {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-top: 4px;
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.enrollment-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;

  > span {
    font-weight: 600;
  }
}
</style>
