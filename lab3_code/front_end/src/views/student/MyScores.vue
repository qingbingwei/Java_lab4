<template>
  <div class="my-scores-container">
    <el-card class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>我的成绩</h2>
          <p class="subtitle">查看各科目成绩详情</p>
        </div>
        <div class="stats-section">
          <div class="stat-item">
            <span class="stat-label">总学分</span>
            <span class="stat-value">{{ totalCredits }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">已修课程</span>
            <span class="stat-value">{{ scores.length }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">平均分</span>
            <span class="stat-value">{{ averageScore }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">GPA</span>
            <span class="stat-value">{{ gpa }}</span>
          </div>
        </div>
      </div>
    </el-card>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>成绩列表</span>
          <div class="filter-section">
            <el-select v-model="selectedSemester" placeholder="筛选学期" style="width: 150px" clearable @change="filterScores">
              <el-option label="全部学期" value="" />
              <el-option label="2023-2024-1" value="2023-2024-1" />
              <el-option label="2023-2024-2" value="2023-2024-2" />
              <el-option label="2024-2025-1" value="2024-2025-1" />
            </el-select>
          </div>
        </div>
      </template>
      
      <el-table :data="filteredScores" v-loading="loading" stripe>
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="classId" label="教学班号" width="120" />
        <el-table-column prop="teacherName" label="授课教师" width="120" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="credit" label="学分" width="80" align="center" />
        <el-table-column prop="score" label="成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreType(row.score)" size="large">
              {{ row.score !== null ? row.score : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="80" align="center">
          <template #default="{ row }">
            <span :style="{ color: getGradeColor(row.score) }">{{ getGrade(row.score) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="绩点" width="80" align="center">
          <template #default="{ row }">
            {{ getGradePoint(row.score) }}
          </template>
        </el-table-column>
        <el-table-column prop="examTime" label="考试时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.examTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 成绩分布图表 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>成绩分布</span>
      </template>
      <div class="chart-container">
        <div class="distribution-bars">
          <div 
            v-for="(item, index) in scoreDistribution" 
            :key="index" 
            class="bar-item"
          >
            <div class="bar-label">{{ item.label }}</div>
            <div class="bar-wrapper">
              <div 
                class="bar" 
                :style="{ width: item.percentage + '%', backgroundColor: item.color }"
              ></div>
            </div>
            <div class="bar-count">{{ item.count }}门</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { scoreApi } from '@/api'
import { useSystemStore } from '@/stores/system'

const systemStore = useSystemStore()
const loading = ref(false)
const scores = ref([])
const selectedSemester = ref('')

// 获取当前学生ID
const currentStudentDbId = computed(() => {
  return systemStore.userInfo?.studentDbId || 1
})

// 过滤后的成绩
const filteredScores = computed(() => {
  if (!selectedSemester.value) {
    return scores.value
  }
  return scores.value.filter(s => s.semester === selectedSemester.value)
})

// 总学分
const totalCredits = computed(() => {
  return filteredScores.value.reduce((sum, s) => {
    if (s.score !== null && s.score >= 60) {
      return sum + (s.credit || 0)
    }
    return sum
  }, 0)
})

// 平均分
const averageScore = computed(() => {
  const validScores = filteredScores.value.filter(s => s.score !== null)
  if (validScores.length === 0) return '-'
  const sum = validScores.reduce((acc, s) => acc + s.score, 0)
  return (sum / validScores.length).toFixed(1)
})

// GPA计算
const gpa = computed(() => {
  const validScores = filteredScores.value.filter(s => s.score !== null && s.credit)
  if (validScores.length === 0) return '-'
  
  let totalPoints = 0
  let totalCredits = 0
  
  validScores.forEach(s => {
    const point = parseFloat(getGradePoint(s.score))
    if (!isNaN(point)) {
      totalPoints += point * s.credit
      totalCredits += s.credit
    }
  })
  
  return totalCredits > 0 ? (totalPoints / totalCredits).toFixed(2) : '-'
})

// 成绩分布
const scoreDistribution = computed(() => {
  const distribution = [
    { label: '优秀 (90-100)', min: 90, max: 100, count: 0, color: '#67c23a' },
    { label: '良好 (80-89)', min: 80, max: 89, count: 0, color: '#409eff' },
    { label: '中等 (70-79)', min: 70, max: 79, count: 0, color: '#e6a23c' },
    { label: '及格 (60-69)', min: 60, max: 69, count: 0, color: '#909399' },
    { label: '不及格 (<60)', min: 0, max: 59, count: 0, color: '#f56c6c' }
  ]
  
  const validScores = filteredScores.value.filter(s => s.score !== null)
  validScores.forEach(s => {
    const item = distribution.find(d => s.score >= d.min && s.score <= d.max)
    if (item) item.count++
  })
  
  const total = validScores.length || 1
  distribution.forEach(d => {
    d.percentage = (d.count / total * 100).toFixed(0)
  })
  
  return distribution
})

// 获取成绩类型（用于Tag颜色）
const getScoreType = (score) => {
  if (score === null) return 'info'
  if (score >= 90) return 'success'
  if (score >= 80) return ''
  if (score >= 70) return 'warning'
  if (score >= 60) return 'info'
  return 'danger'
}

// 获取等级
const getGrade = (score) => {
  if (score === null) return '-'
  if (score >= 90) return 'A'
  if (score >= 80) return 'B'
  if (score >= 70) return 'C'
  if (score >= 60) return 'D'
  return 'F'
}

// 获取等级颜色
const getGradeColor = (score) => {
  if (score === null) return '#909399'
  if (score >= 90) return '#67c23a'
  if (score >= 80) return '#409eff'
  if (score >= 70) return '#e6a23c'
  if (score >= 60) return '#909399'
  return '#f56c6c'
}

// 获取绩点
const getGradePoint = (score) => {
  if (score === null) return '-'
  if (score >= 90) return '4.0'
  if (score >= 85) return '3.7'
  if (score >= 82) return '3.3'
  if (score >= 78) return '3.0'
  if (score >= 75) return '2.7'
  if (score >= 72) return '2.3'
  if (score >= 68) return '2.0'
  if (score >= 64) return '1.5'
  if (score >= 60) return '1.0'
  return '0'
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 获取成绩
const fetchScores = async () => {
  loading.value = true
  try {
    const res = await scoreApi.getStudentScores(currentStudentDbId.value)
    if (res.code === 200) {
      scores.value = res.data || []
    }
  } catch (error) {
    console.error('获取成绩失败:', error)
    ElMessage.error('获取成绩失败')
  } finally {
    loading.value = false
  }
}

const filterScores = () => {
  // 触发computed重新计算
}

onMounted(() => {
  fetchScores()
})
</script>

<style scoped lang="scss">
.my-scores-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 20px;
    
    .title-section {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }
      
      .subtitle {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
    
    .stats-section {
      display: flex;
      gap: 30px;
      
      .stat-item {
        text-align: center;
        
        .stat-label {
          display: block;
          font-size: 12px;
          color: #909399;
          margin-bottom: 4px;
        }
        
        .stat-value {
          display: block;
          font-size: 24px;
          font-weight: bold;
          color: #409eff;
        }
      }
    }
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-container {
  padding: 20px;
  
  .distribution-bars {
    .bar-item {
      display: flex;
      align-items: center;
      margin-bottom: 15px;
      
      .bar-label {
        width: 120px;
        font-size: 14px;
        color: #606266;
      }
      
      .bar-wrapper {
        flex: 1;
        height: 24px;
        background: #f5f7fa;
        border-radius: 4px;
        margin: 0 15px;
        overflow: hidden;
        
        .bar {
          height: 100%;
          border-radius: 4px;
          transition: width 0.3s ease;
        }
      }
      
      .bar-count {
        width: 50px;
        text-align: right;
        font-size: 14px;
        color: #606266;
      }
    }
  }
}
</style>
