<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><User /></el-icon>
      <span>学生管理</span>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchText"
          placeholder="搜索学号或姓名"
          :prefix-icon="Search"
          clearable
          style="width: 300px"
          @input="handleSearch"
        />
        <el-select v-model="genderFilter" placeholder="性别筛选" clearable style="width: 120px">
          <el-option label="全部" value="" />
          <el-option label="男" value="MALE" />
          <el-option label="女" value="FEMALE" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button :icon="Download" @click="handleExport">导出Excel</el-button>
        <el-button type="primary" :icon="Refresh" @click="loadData">刷新</el-button>
      </div>
    </div>

    <!-- 学生列表 -->
    <el-card shadow="hover">
      <el-table
        v-loading="loading"
        :data="filteredStudents"
        stripe
        style="width: 100%"
        :default-sort="{ prop: 'studentId', order: 'ascending' }"
      >
        <el-table-column prop="studentId" label="学号" width="150" sortable />
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
        <el-table-column label="平均分" width="120" sortable :sort-method="(a, b) => getAvgScore(a) - getAvgScore(b)">
          <template #default="{ row }">
            <span :class="getScoreClass(getAvgScore(row))">
              {{ getAvgScore(row) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="成绩状态" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="getScoreProgress(row)"
              :color="getProgressColor(row)"
              :format="() => `${getCompletedScores(row)}/${row.enrolledClasses?.length || 0}`"
            />
          </template>
        </el-table-column>
        <el-table-column label="选课情况" min-width="200">
          <template #default="{ row }">
            <el-tag
              v-for="classId in row.enrolledClasses?.slice(0, 3)"
              :key="classId"
              size="small"
              style="margin-right: 4px"
            >
              {{ getCourseName(classId) }}
            </el-tag>
            <el-tag v-if="row.enrolledClasses?.length > 3" size="small" type="info">
              +{{ row.enrolledClasses.length - 3 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              :icon="View"
              link
              @click="viewDetail(row)"
            >
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="totalStudents"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { formatGender, getScoreClass } from '@/utils'
import { ElMessage } from 'element-plus'
import { User, Search, Download, Refresh, View } from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const loading = ref(false)
const searchText = ref('')
const genderFilter = ref('')
const currentPage = ref(1)
const pageSize = ref(20)

// 计算属性
const allStudents = computed(() => systemStore.students)

const filteredStudents = computed(() => {
  let students = allStudents.value

  // 性别筛选
  if (genderFilter.value) {
    students = students.filter(s => s.gender === genderFilter.value)
  }

  // 搜索
  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    students = students.filter(s =>
      s.studentId.toLowerCase().includes(search) ||
      s.name.toLowerCase().includes(search)
    )
  }

  // 分页
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return students.slice(start, end)
})

const totalStudents = computed(() => {
  let students = allStudents.value

  if (genderFilter.value) {
    students = students.filter(s => s.gender === genderFilter.value)
  }

  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    students = students.filter(s =>
      s.studentId.toLowerCase().includes(search) ||
      s.name.toLowerCase().includes(search)
    )
  }

  return students.length
})

// 方法
const loadData = () => {
  loading.value = true
  setTimeout(() => {
    loading.value = false
    ElMessage.success('数据已刷新')
  }, 500)
}

const handleSearch = () => {
  currentPage.value = 1
}

const handleSizeChange = () => {
  currentPage.value = 1
}

const handleCurrentChange = () => {
  // 页码改变
}

const handleExport = () => {
  ElMessage.success('正在导出Excel文件...')
}

const viewDetail = (student) => {
  router.push(`/students/${student.studentId}`)
}

const getCourseName = (classId) => {
  const teachingClass = systemStore.getTeachingClass(classId)
  return teachingClass?.course?.courseName || classId
}

const getAvgScore = (student) => {
  if (!student.scores || Object.keys(student.scores).length === 0) return 0
  const scores = Object.values(student.scores)
  const sum = scores.reduce((acc, score) => acc + (score.finalScore || 0), 0)
  return (sum / scores.length).toFixed(2)
}

const getCompletedScores = (student) => {
  if (!student.scores) return 0
  return Object.keys(student.scores).length
}

const getScoreProgress = (student) => {
  const total = student.enrolledClasses?.length || 0
  if (total === 0) return 0
  const completed = getCompletedScores(student)
  return Math.round((completed / total) * 100)
}

const getProgressColor = (student) => {
  const progress = getScoreProgress(student)
  if (progress === 100) return '#67c23a'
  if (progress >= 50) return '#409eff'
  return '#e6a23c'
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

:deep(.el-table) {
  .score-cell {
    font-weight: 600;
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .toolbar {
    .toolbar-left {
      flex-direction: column;
      width: 100%;

      .el-input,
      .el-select {
        width: 100% !important;
      }
    }
  }
}
</style>
