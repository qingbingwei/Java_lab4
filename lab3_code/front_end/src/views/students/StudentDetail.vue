<template>
  <div class="page-container">
    <el-page-header @back="goBack" title="返回">
      <template #content>
        <div class="page-title">
          <el-icon class="icon"><User /></el-icon>
          <span>学生详情</span>
        </div>
      </template>
    </el-page-header>

    <div v-if="student" style="margin-top: 24px">
      <!-- 基本信息 -->
      <el-row :gutter="24">
        <el-col :xs="24" :md="8">
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><User /></el-icon>
                <span>基本信息</span>
              </div>
            </template>

            <div class="student-info">
              <el-avatar :size="80" :icon="UserFilled" />
              <div class="info-list">
                <div class="info-item">
                  <span class="label">学号：</span>
                  <span class="value">{{ student.studentId }}</span>
                </div>
                <div class="info-item">
                  <span class="label">姓名：</span>
                  <span class="value">{{ student.name }}</span>
                </div>
                <div class="info-item">
                  <span class="label">性别：</span>
                  <el-tag :type="student.gender === 'MALE' ? 'primary' : 'danger'" size="small">
                    {{ formatGender(student.gender) }}
                  </el-tag>
                </div>
                <div class="info-item">
                  <span class="label">已选课程：</span>
                  <span class="value">{{ student.enrolledClasses?.length || 0 }} 门</span>
                </div>
                <div class="info-item">
                  <span class="label">平均分：</span>
                  <span class="value" :class="getScoreClass(avgScore)">{{ avgScore }}</span>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 成绩统计 -->
          <el-card shadow="hover" style="margin-top: 24px">
            <template #header>
              <div class="card-header">
                <el-icon><PieChart /></el-icon>
                <span>成绩统计</span>
              </div>
            </template>

            <div class="score-stats">
              <div class="stat-item">
                <div class="stat-label">最高分</div>
                <div class="stat-value excellent">{{ maxScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">最低分</div>
                <div class="stat-value fail">{{ minScore }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">总学分</div>
                <div class="stat-value">{{ totalCredits }}</div>
              </div>
            </div>
          </el-card>
        </el-col>

        <el-col :xs="24" :md="16">
          <!-- 成绩列表 -->
          <el-card shadow="hover">
            <template #header>
              <div class="card-header">
                <el-icon><Document /></el-icon>
                <span>成绩详情</span>
              </div>
            </template>

            <el-table :data="scoreList" stripe>
              <el-table-column prop="courseName" label="课程名称" width="150" />
              <el-table-column prop="teacher" label="任课教师" width="100" />
              <el-table-column prop="regularScore" label="平时" width="80" align="center">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.regularScore)">{{ row.regularScore }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="midtermScore" label="期中" width="80" align="center">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.midtermScore)">{{ row.midtermScore }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="experimentScore" label="实验" width="80" align="center">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.experimentScore)">{{ row.experimentScore }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="finalExamScore" label="期末" width="80" align="center">
                <template #default="{ row }">
                  <span :class="getScoreClass(row.finalExamScore)">{{ row.finalExamScore }}</span>
                </template>
              </el-table-column>
              <el-table-column prop="finalScore" label="综合成绩" width="100" align="center">
                <template #default="{ row }">
                  <el-tag :type="getScoreLevel(row.finalScore).type" size="large">
                    {{ row.finalScore }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="level" label="等级" width="100">
                <template #default="{ row }">
                  <el-tag :type="getScoreLevel(row.finalScore).type">
                    {{ getScoreLevel(row.finalScore).label }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="credits" label="学分" width="80" />
            </el-table>
          </el-card>

          <!-- 选课信息 -->
          <el-card shadow="hover" style="margin-top: 24px">
            <template #header>
              <div class="card-header">
                <el-icon><List /></el-icon>
                <span>选课信息</span>
              </div>
            </template>

            <el-descriptions :column="2" border>
              <el-descriptions-item
                v-for="classId in student.enrolledClasses"
                :key="classId"
                label-class-name="course-label"
              >
                <template #label>
                  <el-icon><Reading /></el-icon>
                  {{ getClassName(classId) }}
                </template>
                {{ getClassInfo(classId) }}
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <el-empty v-else description="未找到学生信息" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { formatGender, getScoreClass, getScoreLevel } from '@/utils'
import { User, UserFilled, PieChart, Document, List, Reading } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const systemStore = useSystemStore()

const studentId = route.params.id
const student = ref(null)

const avgScore = computed(() => {
  if (!student.value?.scores || Object.keys(student.value.scores).length === 0) return '0.00'
  const scores = Object.values(student.value.scores)
  const sum = scores.reduce((acc, score) => acc + (score.finalScore || 0), 0)
  return (sum / scores.length).toFixed(2)
})

const maxScore = computed(() => {
  if (!student.value?.scores || Object.keys(student.value.scores).length === 0) return 0
  const scores = Object.values(student.value.scores).map(s => s.finalScore || 0)
  return Math.max(...scores).toFixed(2)
})

const minScore = computed(() => {
  if (!student.value?.scores || Object.keys(student.value.scores).length === 0) return 0
  const scores = Object.values(student.value.scores).map(s => s.finalScore || 0)
  return Math.min(...scores).toFixed(2)
})

const totalCredits = computed(() => {
  if (!student.value?.enrolledClasses) return 0
  return student.value.enrolledClasses.reduce((sum, classId) => {
    const tc = systemStore.getTeachingClass(classId)
    return sum + (tc?.course?.credits || 0)
  }, 0)
})

const scoreList = computed(() => {
  if (!student.value?.scores) return []
  return Object.entries(student.value.scores).map(([classId, score]) => {
    const tc = systemStore.getTeachingClass(classId)
    return {
      classId,
      courseName: tc?.course?.courseName || '-',
      teacher: tc?.teacher?.name || '-',
      credits: tc?.course?.credits || 0,
      ...score
    }
  })
})

const getClassName = (classId) => {
  const tc = systemStore.getTeachingClass(classId)
  return tc?.course?.courseName || classId
}

const getClassInfo = (classId) => {
  const tc = systemStore.getTeachingClass(classId)
  if (!tc) return '-'
  return `${tc.teacher?.name} | ${tc.semester} | ${tc.students?.length || 0}人`
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  student.value = systemStore.getStudent(studentId)
})
</script>

<style lang="scss" scoped>
.student-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;

  .info-list {
    width: 100%;

    .info-item {
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
        font-size: 14px;
      }

      .value {
        font-weight: 500;
        color: var(--el-text-color-primary);
      }
    }
  }
}

.score-stats {
  display: flex;
  justify-content: space-around;
  text-align: center;

  .stat-item {
    .stat-label {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin-bottom: 8px;
    }

    .stat-value {
      font-size: 28px;
      font-weight: bold;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

:deep(.course-label) {
  width: 150px;
}
</style>
