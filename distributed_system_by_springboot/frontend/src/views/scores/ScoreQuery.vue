<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><Search /></el-icon>
      <span>成绩查询</span>
    </div>

    <!-- 查询条件 - 管理员/教师可查所有，学生只能查自己 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form inline>
        <!-- 学生角色不显示学号和姓名查询框 -->
        <template v-if="!isStudent">
          <el-form-item label="学号">
            <el-input v-model="queryParams.studentId" placeholder="输入学号" clearable style="width: 150px" />
          </el-form-item>
          <el-form-item label="姓名">
            <el-input v-model="queryParams.studentName" placeholder="输入姓名" clearable style="width: 150px" />
          </el-form-item>
        </template>
        <el-form-item v-else label="查询说明">
          <el-tag type="info">只能查询自己的成绩</el-tag>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="queryParams.semester" placeholder="选择学期" clearable style="width: 150px">
            <el-option label="2024-2025-1" value="2024-2025-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
            <el-option label="2023-2024-1" value="2023-2024-1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 成绩列表 -->
    <el-card shadow="hover">
      <template #header>
        <div style="display: flex; justify-content: space-between;">
          <span>成绩列表</span>
          <el-button :icon="Download" @click="handleExport">导出Excel</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="scoreList" stripe>
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="regularScore" label="平时" width="80">
          <template #default="{ row }">
            <span :class="getScoreClass(row.regularScore)">{{ row.regularScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="midtermScore" label="期中" width="80">
          <template #default="{ row }">
            <span :class="getScoreClass(row.midtermScore)">{{ row.midtermScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="experimentScore" label="实验" width="80">
          <template #default="{ row }">
            <span :class="getScoreClass(row.experimentScore)">{{ row.experimentScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalExamScore" label="期末" width="80">
          <template #default="{ row }">
            <span :class="getScoreClass(row.finalExamScore)">{{ row.finalExamScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="综合成绩" width="100" sortable>
          <template #default="{ row }">
            <span :class="getScoreClass(row.finalScore)" style="font-weight: bold;">
              {{ row.finalScore?.toFixed(2) ?? '-' }}
            </span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { scoreApi, excelApi } from '@/api'
import { getScoreClass } from '@/utils'
import { Search, Refresh, Download } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const scoreList = ref([])
const total = ref(0)

// 判断是否是学生角色
const isStudent = computed(() => userStore.isStudent)
// 判断是否是教师角色
const isTeacher = computed(() => userStore.isTeacher)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 20,
  studentId: '',
  studentName: '',
  semester: '',
  studentDbId: null, // 学生专用：限定只能查自己
  teacherDbId: null  // 教师专用：限定只能查自己教学班
})

const loadData = async () => {
  loading.value = true
  try {
    // 如果是学生，强制只查询自己的成绩
    const params = { ...queryParams }
    if (isStudent.value) {
      params.studentDbId = userStore.refId
      // 学生不应使用这些参数
      delete params.studentId
      delete params.studentName
    }
    // 如果是教师，只查询自己教学班的成绩
    if (isTeacher.value) {
      params.teacherDbId = userStore.refId
    }
    const res = await scoreApi.getPage(params)
    scoreList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  // 重置查询参数，但学生的studentDbId始终保留
  Object.assign(queryParams, { pageNum: 1, studentId: '', studentName: '', semester: '' })
  if (!isStudent.value) {
    queryParams.studentDbId = null
  }
  loadData()
}

const handleExport = () => {
  const params = { semester: queryParams.semester }
  // 如果是学生，只导出自己的成绩
  if (isStudent.value) {
    params.studentDbId = userStore.refId
  }
  // 如果是教师，只导出自己教学班学生的成绩
  if (isTeacher.value) {
    params.teacherDbId = userStore.refId
  }
  window.open(excelApi.exportScores(params))
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
