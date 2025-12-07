<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><Search /></el-icon>
      <span>成绩查询</span>
    </div>

    <!-- 查询条件 -->
    <el-card shadow="hover" style="margin-bottom: 24px">
      <el-form :model="queryForm" label-width="100px">
        <el-row :gutter="20">
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="学号">
              <el-input v-model="queryForm.studentId" placeholder="请输入学号" clearable />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="姓名">
              <el-input v-model="queryForm.name" placeholder="请输入姓名" clearable />
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="课程">
              <el-select v-model="queryForm.courseId" placeholder="请选择课程" clearable>
                <el-option
                  v-for="course in systemStore.courses"
                  :key="course.courseId"
                  :label="course.courseName"
                  :value="course.courseId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item label="成绩范围">
              <el-select v-model="queryForm.scoreRange" placeholder="请选择" clearable>
                <el-option label="优秀 (≥90)" value="excellent" />
                <el-option label="良好 (80-89)" value="good" />
                <el-option label="中等 (70-79)" value="medium" />
                <el-option label="及格 (60-69)" value="pass" />
                <el-option label="不及格 (<60)" value="fail" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :xs="24" :sm="12" :md="8">
            <el-form-item>
              <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
              <el-button :icon="RefreshLeft" @click="handleReset">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 查询结果 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>查询结果 ({{ queryResults.length }} 条)</span>
          <el-button :icon="Download" size="small" @click="handleExport">导出</el-button>
        </div>
      </template>

      <el-table :data="paginatedResults" stripe v-loading="loading" @sort-change="handleSortChange">
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="courseName" label="课程" width="150" />
        <el-table-column prop="teacher" label="任课教师" width="120" />
        <el-table-column label="平时" width="80" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.regularScore)">
              {{ row.regularScore >= 0 ? row.regularScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="期中" width="80" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.midtermScore)">
              {{ row.midtermScore >= 0 ? row.midtermScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="实验" width="80" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.experimentScore)">
              {{ row.experimentScore >= 0 ? row.experimentScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="期末" width="80" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.finalExamScore)">
              {{ row.finalExamScore >= 0 ? row.finalExamScore : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="综合成绩" width="120" align="center" sortable>
          <template #default="{ row }">
            <el-tag :type="getScoreLevel(row.finalScore).type" size="large">
              {{ row.finalScore.toFixed(2) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreLevel(row.finalScore).type">
              {{ getScoreLevel(row.finalScore).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewStudent(row.studentId)">
              查看详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="sortedResults.length > 0 ? sortedResults.length : queryResults.length"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { getScoreClass, getScoreLevel } from '@/utils'
import { ElMessage } from 'element-plus'
import { Search, RefreshLeft, Download } from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const loading = ref(false)
const queryForm = ref({
  studentId: '',
  name: '',
  courseId: '',
  scoreRange: ''
})

const currentPage = ref(1)
const pageSize = ref(20)
const queryResults = ref([])
const sortedResults = ref([])

// 获取所有成绩记录
const getAllScores = () => {
  const scores = []
  systemStore.students.forEach(student => {
    if (!student.scores) return
    Object.entries(student.scores).forEach(([classId, score]) => {
      const tc = systemStore.getTeachingClass(classId)
      if (!tc) return
      scores.push({
        studentId: student.studentId,
        studentName: student.name,
        classId,
        courseId: tc.course.courseId,
        courseName: tc.course.courseName,
        teacher: tc.teacher.name,
        ...score
      })
    })
  })
  return scores
}

// 分页结果
const paginatedResults = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const dataSource = sortedResults.value.length > 0 ? sortedResults.value : queryResults.value
  return dataSource.slice(start, start + pageSize.value)
})

const handleQuery = () => {
  loading.value = true
  
  setTimeout(() => {
    let results = getAllScores()

    // 学号筛选
    if (queryForm.value.studentId) {
      results = results.filter(r => r.studentId.includes(queryForm.value.studentId))
    }

    // 姓名筛选
    if (queryForm.value.name) {
      results = results.filter(r => r.studentName.includes(queryForm.value.name))
    }

    // 课程筛选
    if (queryForm.value.courseId) {
      results = results.filter(r => r.courseId === queryForm.value.courseId)
    }

    // 成绩范围筛选
    if (queryForm.value.scoreRange) {
      const ranges = {
        excellent: (score) => score >= 90,
        good: (score) => score >= 80 && score < 90,
        medium: (score) => score >= 70 && score < 80,
        pass: (score) => score >= 60 && score < 70,
        fail: (score) => score < 60
      }
      const checkRange = ranges[queryForm.value.scoreRange]
      results = results.filter(r => checkRange(r.finalScore))
    }
    queryResults.value = results
    sortedResults.value = []
    currentPage.value = 1
    loading.value = false
    
    ElMessage.success(`查询到 ${results.length} 条记录`)
  }, 300)
}

const handleSortChange = ({ prop, order }) => {
  if (!prop || !order) {
    sortedResults.value = []
    return
  }
  
  const sorted = [...queryResults.value]
  if (prop === 'finalScore') {
    sorted.sort((a, b) => {
      return order === 'ascending' ? a.finalScore - b.finalScore : b.finalScore - a.finalScore
    })
  }
  sortedResults.value = sorted
  currentPage.value = 1
}

const handleReset = () => {
  queryForm.value = {
    studentId: '',
    name: '',
    courseId: '',
    scoreRange: ''
  }
  queryResults.value = []
  sortedResults.value = []
}

const handleExport = () => {
  ElMessage.success('正在导出Excel文件...')
}

const viewStudent = (studentId) => {
  router.push(`/students/${studentId}`)
}

// 初始化时查询所有
handleQuery()
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
