<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><TrendCharts /></el-icon>
      <span>成绩排名</span>
    </div>

    <!-- 排名设置 -->
    <el-card shadow="hover" style="margin-bottom: 24px">
      <el-row :gutter="16">
        <el-col :xs="24" :sm="12" :md="8">
          <el-select v-model="rankType" placeholder="选择排名方式" @change="updateRanking">
            <el-option label="综合成绩排名" value="total" />
            <el-option label="平均分排名" value="average" />
            <el-option label="按课程排名" value="course" />
          </el-select>
        </el-col>
        <el-col v-if="rankType === 'course'" :xs="24" :sm="12" :md="8">
          <el-select v-model="selectedCourse" placeholder="选择课程" @change="updateRanking">
            <el-option
              v-for="course in systemStore.courses"
              :key="course.courseId"
              :label="course.courseName"
              :value="course.courseId"
            />
          </el-select>
        </el-col>
        <el-col :xs="24" :sm="12" :md="8">
          <el-select v-model="displayCount" placeholder="显示数量" @change="updateRanking">
            <el-option label="前10名" :value="10" />
            <el-option label="前20名" :value="20" />
            <el-option label="前50名" :value="50" />
            <el-option label="全部" :value="9999" />
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- 排行榜 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>{{ getRankTitle() }}</span>
          <el-button :icon="Download" size="small">导出排名</el-button>
        </div>
      </template>

      <el-table :data="rankedStudents" stripe>
        <el-table-column label="排名" width="100" align="center">
          <template #default="{ $index }">
            <div class="rank-badge" :class="getRankClass($index)">
              <el-icon v-if="$index < 3" :size="24">
                <Trophy />
              </el-icon>
              <span v-else>{{ $index + 1 }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'MALE' ? 'primary' : 'danger'" size="small">
              {{ formatGender(row.gender) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="rankType !== 'course'" label="已选课程" width="100">
          <template #default="{ row }">
            {{ row.enrolledClasses?.length || 0 }} 门
          </template>
        </el-table-column>
        <el-table-column v-if="rankType === 'total'" label="总分" width="120" align="center">
          <template #default="{ row }">
            <span class="score-value excellent">{{ row.totalScore.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="rankType === 'average' || rankType === 'course'" label="平均分/成绩" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getScoreLevel(row.avgScore).type" size="large">
              {{ row.avgScore.toFixed(2) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="等级" width="100">
          <template #default="{ row }">
            <el-tag :type="getScoreLevel(row.avgScore).type">
              {{ getScoreLevel(row.avgScore).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column v-if="rankType === 'course'" label="课程详情" min-width="200">
          <template #default="{ row }">
            平时:{{ row.courseScore?.regularScore || '-' }} | 
            期中:{{ row.courseScore?.midtermScore || '-' }} | 
            实验:{{ row.courseScore?.experimentScore || '-' }} | 
            期末:{{ row.courseScore?.finalExamScore || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="课程列表" min-width="200" v-if="rankType !== 'course'">
          <template #default="{ row }">
            <el-tag
              v-for="(classId, index) in row.enrolledClasses?.slice(0, 3)"
              :key="index"
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
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewDetail(row.studentId)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { formatGender, getScoreLevel } from '@/utils'
import { TrendCharts, Download, Trophy } from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const rankType = ref('average')
const selectedCourse = ref('')
const displayCount = ref(20)

const rankedStudents = computed(() => {
  let students = systemStore.students.map(s => {
    const totalScore = Object.values(s.scores || {}).reduce((sum, score) => sum + (score.finalScore || 0), 0)
    const scoreCount = Object.keys(s.scores || {}).length
    const avgScore = scoreCount > 0 ? totalScore / scoreCount : 0

    return {
      ...s,
      totalScore,
      avgScore,
      courseScore: null
    }
  })

  // 根据排名类型排序
  if (rankType.value === 'total') {
    students.sort((a, b) => b.totalScore - a.totalScore)
  } else if (rankType.value === 'course' && selectedCourse.value) {
    // 按课程排名
    students = students.filter(s => {
      const classId = s.enrolledClasses?.find(cId => {
        const tc = systemStore.getTeachingClass(cId)
        return tc?.course?.courseId === selectedCourse.value
      })
      if (classId && s.scores?.[classId]) {
        s.courseScore = s.scores[classId]
        s.avgScore = s.scores[classId].finalScore || 0
        return true
      }
      return false
    })
    students.sort((a, b) => b.avgScore - a.avgScore)
  } else {
    // 按平均分排名
    students.sort((a, b) => b.avgScore - a.avgScore)
  }

  // 限制显示数量
  return students.slice(0, Math.min(displayCount.value, students.length))
})

const getRankTitle = () => {
  if (rankType.value === 'total') return '综合成绩总分排名'
  if (rankType.value === 'course') {
    const course = systemStore.courses.find(c => c.courseId === selectedCourse.value)
    return course ? `${course.courseName} - 课程成绩排名` : '按课程排名'
  }
  return '平均成绩排名'
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-1'
  if (index === 1) return 'rank-2'
  if (index === 2) return 'rank-3'
  return ''
}

const getCourseName = (classId) => {
  const tc = systemStore.getTeachingClass(classId)
  return tc?.course?.courseName || classId
}

const updateRanking = () => {
  // 触发重新计算
}

const viewDetail = (studentId) => {
  router.push(`/students/${studentId}`)
}
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 48px;
  height: 48px;
  border-radius: 50%;
  font-size: 18px;
  font-weight: bold;
  background: var(--el-fill-color);
  color: var(--el-text-color-primary);

  &.rank-1 {
    background: linear-gradient(135deg, #ffd700 0%, #ffed4e 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(255, 215, 0, 0.4);
  }

  &.rank-2 {
    background: linear-gradient(135deg, #c0c0c0 0%, #e8e8e8 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(192, 192, 192, 0.4);
  }

  &.rank-3 {
    background: linear-gradient(135deg, #cd7f32 0%, #e6a857 100%);
    color: #fff;
    box-shadow: 0 4px 12px rgba(205, 127, 50, 0.4);
  }
}

.score-value {
  font-size: 18px;
  font-weight: bold;
}
</style>
