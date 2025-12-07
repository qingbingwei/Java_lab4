<template>
  <div class="my-courses">
    <el-card class="page-header">
      <div class="header-content">
        <h2>我的选课</h2>
        <span class="subtitle">查看和管理您的课程选择</span>
      </div>
    </el-card>

    <el-row :gutter="20">
      <!-- 已选课程 -->
      <el-col :span="16">
        <el-card class="enrolled-card">
          <template #header>
            <div class="card-header">
              <span>已选课程</span>
              <el-tag type="success">共 {{ enrolledCourses.length }} 门</el-tag>
            </div>
          </template>

          <el-table :data="enrolledCourses" v-loading="loading" stripe>
            <el-table-column type="index" label="序号" width="60" />
            <el-table-column prop="courseName" label="课程名称" min-width="150">
              <template #default="{ row }">
                <div class="course-info">
                  <span class="course-name">{{ row.courseName }}</span>
                  <span class="course-code">{{ row.courseCode }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="credit" label="学分" width="80" align="center">
              <template #default="{ row }">
                <el-tag size="small">{{ row.credit }} 学分</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="teacherName" label="任课教师" width="100" />
            <el-table-column prop="className" label="教学班" width="120" />
            <el-table-column prop="schedule" label="上课时间" width="150" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">
                  {{ getStatusText(row.status) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button 
                  v-if="row.status === 'PENDING'"
                  type="danger" 
                  size="small" 
                  text
                  @click="handleWithdraw(row)"
                >
                  退选
                </el-button>
                <span v-else class="text-muted">--</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <!-- 学分统计 -->
      <el-col :span="8">
        <el-card class="summary-card">
          <template #header>
            <span>学分统计</span>
          </template>
          
          <div class="credit-summary">
            <div class="credit-item">
              <span class="label">已选学分</span>
              <span class="value">{{ totalCredits }}</span>
            </div>
            <div class="credit-item">
              <span class="label">已修学分</span>
              <span class="value completed">{{ completedCredits }}</span>
            </div>
            <div class="credit-item">
              <span class="label">进行中</span>
              <span class="value pending">{{ pendingCredits }}</span>
            </div>
          </div>

          <el-divider />

          <div class="course-type-summary">
            <h4>课程类型分布</h4>
            <div class="type-list">
              <div v-for="(count, type) in courseTypeStats" :key="type" class="type-item">
                <span class="type-name">{{ type }}</span>
                <el-progress :percentage="getTypePercentage(count)" :stroke-width="10" />
              </div>
            </div>
          </div>
        </el-card>

        <el-card class="notice-card" style="margin-top: 20px;">
          <template #header>
            <span>选课须知</span>
          </template>
          
          <div class="notice-content">
            <p><el-icon><InfoFilled /></el-icon> 选课期间可随时退选课程</p>
            <p><el-icon><InfoFilled /></el-icon> 课程开始后无法退选</p>
            <p><el-icon><InfoFilled /></el-icon> 请注意学分上限要求</p>
            <p><el-icon><InfoFilled /></el-icon> 如有问题请联系教务处</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { enrollmentApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { InfoFilled } from '@element-plus/icons-vue'

const userStore = useUserStore()
const loading = ref(false)
const enrolledCourses = ref([])

// 学分统计
const totalCredits = computed(() => {
  return enrolledCourses.value.reduce((sum, c) => sum + Number(c.credit || 0), 0)
})

const completedCredits = computed(() => {
  return enrolledCourses.value
    .filter(c => c.status === 'COMPLETED')
    .reduce((sum, c) => sum + Number(c.credit || 0), 0)
})

const pendingCredits = computed(() => {
  return enrolledCourses.value
    .filter(c => c.status === 'PENDING' || c.status === 'IN_PROGRESS' || c.status === 'ENROLLED')
    .reduce((sum, c) => sum + Number(c.credit || 0), 0)
})

// 课程类型统计
const courseTypeStats = computed(() => {
  const stats = {}
  enrolledCourses.value.forEach(c => {
    const type = c.courseType || '其他'
    stats[type] = (stats[type] || 0) + 1
  })
  return stats
})

const getTypePercentage = (count) => {
  const total = enrolledCourses.value.length
  return total > 0 ? Math.round((count / total) * 100) : 0
}

// 状态相关
const getStatusType = (status) => {
  const map = {
    'PENDING': 'warning',
    'IN_PROGRESS': 'primary',
    'COMPLETED': 'success',
    'WITHDRAWN': 'info'
  }
  return map[status] || 'info'
}

const getStatusText = (status) => {
  const map = {
    'PENDING': '待开课',
    'IN_PROGRESS': '进行中',
    'COMPLETED': '已完成',
    'WITHDRAWN': '已退选'
  }
  return map[status] || status
}

// 加载已选课程
const loadEnrolledCourses = async () => {
  loading.value = true
  try {
    const studentId = userStore.refId
    if (!studentId) {
      ElMessage.warning('未找到学生信息')
      return
    }
    const res = await enrollmentApi.getStudentEnrollments(studentId)
    enrolledCourses.value = res.data || []
  } catch (e) {
    console.error('加载选课信息失败', e)
    ElMessage.error('加载选课信息失败')
  } finally {
    loading.value = false
  }
}

// 退选
const handleWithdraw = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要退选课程"${row.courseName}"吗？`, '退选确认', {
      type: 'warning'
    })
    await enrollmentApi.withdraw(row.enrollmentId)
    ElMessage.success('退选成功')
    loadEnrolledCourses()
  } catch (e) {
    if (e !== 'cancel') {
      console.error('退选失败', e)
    }
  }
}

onMounted(() => {
  loadEnrolledCourses()
})
</script>

<style lang="scss" scoped>
.my-courses {
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

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .course-info {
    display: flex;
    flex-direction: column;
    
    .course-name {
      font-weight: 500;
    }
    .course-code {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  .text-muted {
    color: var(--el-text-color-placeholder);
  }

  .summary-card {
    .credit-summary {
      .credit-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 12px 0;
        border-bottom: 1px solid var(--el-border-color-lighter);
        
        &:last-child {
          border-bottom: none;
        }
        
        .label {
          color: var(--el-text-color-secondary);
        }
        
        .value {
          font-size: 24px;
          font-weight: bold;
          color: var(--el-text-color-primary);
          
          &.completed { color: #67C23A; }
          &.pending { color: #E6A23C; }
        }
      }
    }

    .course-type-summary {
      h4 {
        margin: 0 0 16px 0;
        font-size: 14px;
        color: var(--el-text-color-secondary);
      }
      
      .type-item {
        margin-bottom: 12px;
        
        .type-name {
          font-size: 13px;
          margin-bottom: 4px;
          display: block;
        }
      }
    }
  }

  .notice-card {
    .notice-content {
      p {
        display: flex;
        align-items: center;
        gap: 8px;
        margin: 0 0 12px 0;
        font-size: 13px;
        color: var(--el-text-color-secondary);
        
        &:last-child {
          margin-bottom: 0;
        }
        
        .el-icon {
          color: var(--el-color-primary);
        }
      }
    }
  }
}
</style>
