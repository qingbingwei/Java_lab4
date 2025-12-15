<template>
  <div class="my-scores">
    <el-card class="page-header">
      <div class="header-content">
        <h2>我的成绩</h2>
        <span class="subtitle">查看您的课程成绩信息</span>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#409EFF"><Reading /></el-icon>
            <div class="stat-info">
              <span class="stat-value">{{ totalCourses }}</span>
              <span class="stat-label">已修课程</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#67C23A"><Trophy /></el-icon>
            <div class="stat-info">
              <span class="stat-value">{{ averageScore.toFixed(1) }}</span>
              <span class="stat-label">平均分</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#E6A23C"><Medal /></el-icon>
            <div class="stat-info">
              <span class="stat-value">{{ highestScore }}</span>
              <span class="stat-label">最高分</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <el-icon class="stat-icon" color="#F56C6C"><TrendCharts /></el-icon>
            <div class="stat-info">
              <span class="stat-value">{{ totalCredits }}</span>
              <span class="stat-label">已修学分</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 成绩列表 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>成绩明细</span>
          <el-button type="primary" :icon="Download" @click="handleExport">导出成绩单</el-button>
        </div>
      </template>

      <el-table :data="scoreList" v-loading="loading" stripe>
        <el-table-column type="index" label="序号" width="60" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="courseCode" label="课程代码" width="120" />
        <el-table-column prop="credits" label="学分" width="80" align="center" />
        <el-table-column prop="teacherName" label="任课教师" width="100" />
        <el-table-column prop="className" label="教学班" width="120" />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreTagType(row.score)" size="large">
              {{ row.score ?? '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="等级" width="80" align="center">
          <template #default="{ row }">
            <span :class="['grade', `grade-${getGrade(row.score)}`]">
              {{ getGrade(row.score) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { scoreApi } from '@/api'
import { ElMessage } from 'element-plus'
import { Reading, Trophy, Medal, TrendCharts, Download } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const scoreList = ref([])

// 统计数据
const totalCourses = computed(() => scoreList.value.length)
const averageScore = computed(() => {
  const validScores = scoreList.value.filter(s => s.score != null && !isNaN(s.score))
  if (validScores.length === 0) return 0
  return validScores.reduce((sum, s) => sum + Number(s.score), 0) / validScores.length
})
const highestScore = computed(() => {
  const validScores = scoreList.value.filter(s => s.score != null && !isNaN(s.score))
  if (validScores.length === 0) return '--'
  return Math.max(...validScores.map(s => Number(s.score)))
})
const totalCredits = computed(() => {
  return scoreList.value
    .filter(s => s.score != null && s.score >= 60)
    .reduce((sum, s) => sum + Number(s.credits || s.credit || 0), 0)
})

// 获取成绩等级
const getGrade = (score) => {
  if (score === null || score === undefined) return '--'
  if (score >= 90) return 'A'
  if (score >= 80) return 'B'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

// 获取成绩标签类型
const getScoreTagType = (score) => {
  if (score === null || score === undefined) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 60) return 'warning'
  return 'danger'
}

// 加载成绩数据
const loadScores = async () => {
  loading.value = true
  try {
    // 根据学号获取成绩
    const studentId = userStore.businessId
    if (!studentId) {
      ElMessage.warning('未找到学生信息')
      return
    }
    const res = await scoreApi.getStudentScores(studentId)
    scoreList.value = res.data || []
  } catch (e) {
    console.error('加载成绩失败', e)
    ElMessage.error('加载成绩失败')
  } finally {
    loading.value = false
  }
}

// 导出成绩单
const handleExport = () => {
  ElMessage.info('成绩单导出功能开发中...')
}

onMounted(() => {
  loadScores()
})
</script>

<style lang="scss" scoped>
.my-scores {
  padding: 20px;

  .page-header {
    margin-bottom: 20px;
    
    .header-content {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
      }
      .subtitle {
        color: var(--el-text-color-secondary);
        font-size: 14px;
      }
    }
  }

  .stats-row {
    margin-bottom: 20px;
    
    .stat-card {
      .stat-content {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .stat-icon {
          font-size: 40px;
        }
        
        .stat-info {
          display: flex;
          flex-direction: column;
          
          .stat-value {
            font-size: 28px;
            font-weight: bold;
            color: var(--el-text-color-primary);
          }
          
          .stat-label {
            font-size: 14px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }
  }

  .table-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
  }

  .grade {
    font-weight: bold;
    font-size: 16px;
    
    &.grade-A { color: #67C23A; }
    &.grade-B { color: #409EFF; }
    &.grade-C { color: #E6A23C; }
    &.grade-D { color: #909399; }
    &.grade-F { color: #F56C6C; }
    &.grade--- { color: #909399; }
  }
}
</style>
