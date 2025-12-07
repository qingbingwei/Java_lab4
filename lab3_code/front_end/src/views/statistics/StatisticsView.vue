<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><PieChart /></el-icon>
      <span>统计分析</span>
    </div>

    <!-- 概览统计 -->
    <el-row :gutter="24" style="margin-bottom: 24px">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-box" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-icon"><el-icon><User /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ totalStudents }}</div>
            <div class="stat-label">学生总数</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-box" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-icon"><el-icon><Document /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ totalScores }}</div>
            <div class="stat-label">成绩记录</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-box" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-icon"><el-icon><TrendCharts /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ overallAvg }}</div>
            <div class="stat-label">总体平均分</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-box" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <div class="stat-icon"><el-icon><Select /></el-icon></div>
          <div class="stat-info">
            <div class="stat-value">{{ passRate }}%</div>
            <div class="stat-label">总体及格率</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="24">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" style="margin-bottom: 24px">
          <template #header>
            <div class="card-header">
              <el-icon><PieChart /></el-icon>
              <span>成绩分布</span>
            </div>
          </template>
          <div ref="scoreDistChart" style="height: 400px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" style="margin-bottom: 24px">
          <template #header>
            <div class="card-header">
              <el-icon><Histogram /></el-icon>
              <span>课程平均分对比</span>
            </div>
          </template>
          <div ref="courseAvgChart" style="height: 400px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" style="margin-bottom: 24px">
          <template #header>
            <div class="card-header">
              <el-icon><DataLine /></el-icon>
              <span>成绩段分布</span>
            </div>
          </template>
          <div ref="scoreRangeChart" style="height: 400px"></div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" style="margin-bottom: 24px">
          <template #header>
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span>及格率统计</span>
            </div>
          </template>
          <div ref="passRateChart" style="height: 400px"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程详细统计 -->
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Tickets /></el-icon>
          <span>课程详细统计</span>
        </div>
      </template>
      
      <el-table :data="courseStats" stripe>
        <el-table-column prop="courseName" label="课程名称" width="150" />
        <el-table-column prop="studentCount" label="选课人数" width="100" align="center" />
        <el-table-column prop="avgScore" label="平均分" width="100" align="center">
          <template #default="{ row }">
            <span :class="getScoreClass(row.avgScore)">{{ row.avgScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="maxScore" label="最高分" width="100" align="center">
          <template #default="{ row }">
            <span class="excellent">{{ row.maxScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minScore" label="最低分" width="100" align="center">
          <template #default="{ row }">
            <span class="fail">{{ row.minScore }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="passRate" label="及格率" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.passRate >= 80 ? 'success' : row.passRate >= 60 ? 'warning' : 'danger'">
              {{ row.passRate }}%
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="excellentRate" label="优秀率" width="100" align="center">
          <template #default="{ row }">
            {{ row.excellentRate }}%
          </template>
        </el-table-column>
        <el-table-column label="成绩分布" min-width="200">
          <template #default="{ row }">
            优秀:{{ row.dist.excellent }} | 
            良好:{{ row.dist.good }} | 
            中等:{{ row.dist.medium }} | 
            及格:{{ row.dist.pass }} | 
            不及格:{{ row.dist.fail }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick } from 'vue'
import { useSystemStore } from '@/stores/system'
import { getScoreClass } from '@/utils'
import * as echarts from 'echarts'
import {
  PieChart,
  User,
  Document,
  TrendCharts,
  Select,
  Histogram,
  DataLine,
  DataAnalysis,
  Tickets
} from '@element-plus/icons-vue'

const systemStore = useSystemStore()

const scoreDistChart = ref(null)
const courseAvgChart = ref(null)
const scoreRangeChart = ref(null)
const passRateChart = ref(null)

// 统计数据
const totalStudents = computed(() => systemStore.studentCount)

const totalScores = computed(() => {
  return systemStore.students.reduce((sum, s) => {
    return sum + Object.keys(s.scores || {}).length
  }, 0)
})

const allScoreValues = computed(() => {
  const scores = []
  systemStore.students.forEach(s => {
    Object.values(s.scores || {}).forEach(score => {
      if (score.finalScore > 0) scores.push(score.finalScore)
    })
  })
  return scores
})

const overallAvg = computed(() => {
  const scores = allScoreValues.value
  if (scores.length === 0) return '0.00'
  const sum = scores.reduce((a, b) => a + b, 0)
  return (sum / scores.length).toFixed(2)
})

const passRate = computed(() => {
  const scores = allScoreValues.value
  if (scores.length === 0) return '0.00'
  const passCount = scores.filter(s => s >= 60).length
  return ((passCount / scores.length) * 100).toFixed(2)
})

// 课程统计
const courseStats = computed(() => {
  return systemStore.courses.map(course => {
    const scores = []
    systemStore.students.forEach(student => {
      Object.entries(student.scores || {}).forEach(([classId, score]) => {
        const tc = systemStore.getTeachingClass(classId)
        if (tc?.course?.courseId === course.courseId && score.finalScore > 0) {
          scores.push(score.finalScore)
        }
      })
    })

    const dist = {
      excellent: scores.filter(s => s >= 90).length,
      good: scores.filter(s => s >= 80 && s < 90).length,
      medium: scores.filter(s => s >= 70 && s < 80).length,
      pass: scores.filter(s => s >= 60 && s < 70).length,
      fail: scores.filter(s => s < 60).length
    }

    return {
      courseName: course.courseName,
      studentCount: scores.length,
      avgScore: scores.length > 0 ? (scores.reduce((a, b) => a + b, 0) / scores.length).toFixed(2) : '0.00',
      maxScore: scores.length > 0 ? Math.max(...scores).toFixed(2) : '0.00',
      minScore: scores.length > 0 ? Math.min(...scores).toFixed(2) : '0.00',
      passRate: scores.length > 0 ? ((scores.filter(s => s >= 60).length / scores.length) * 100).toFixed(2) : '0.00',
      excellentRate: scores.length > 0 ? ((scores.filter(s => s >= 90).length / scores.length) * 100).toFixed(2) : '0.00',
      dist
    }
  })
})

// 初始化图表
const initCharts = () => {
  // 成绩分布饼图
  if (scoreDistChart.value) {
    const chart1 = echarts.init(scoreDistChart.value)
    const dist = {
      excellent: 0,
      good: 0,
      medium: 0,
      pass: 0,
      fail: 0
    }
    allScoreValues.value.forEach(score => {
      if (score >= 90) dist.excellent++
      else if (score >= 80) dist.good++
      else if (score >= 70) dist.medium++
      else if (score >= 60) dist.pass++
      else dist.fail++
    })

    chart1.setOption({
      tooltip: { trigger: 'item' },
      legend: { orient: 'vertical', left: 'left' },
      series: [{
        name: '成绩分布',
        type: 'pie',
        radius: '60%',
        data: [
          { value: dist.excellent, name: '优秀(≥90)', itemStyle: { color: '#67c23a' } },
          { value: dist.good, name: '良好(80-89)', itemStyle: { color: '#409eff' } },
          { value: dist.medium, name: '中等(70-79)', itemStyle: { color: '#e6a23c' } },
          { value: dist.pass, name: '及格(60-69)', itemStyle: { color: '#f56c6c' } },
          { value: dist.fail, name: '不及格(<60)', itemStyle: { color: '#909399' } }
        ],
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }]
    })
  }

  // 课程平均分柱状图
  if (courseAvgChart.value) {
    const chart2 = echarts.init(courseAvgChart.value)
    chart2.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      xAxis: {
        type: 'category',
        data: courseStats.value.map(c => c.courseName),
        axisLabel: { rotate: 30 }
      },
      yAxis: { type: 'value', max: 100 },
      series: [{
        name: '平均分',
        type: 'bar',
        data: courseStats.value.map(c => parseFloat(c.avgScore)),
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#409eff' },
            { offset: 1, color: '#67c23a' }
          ])
        },
        label: { show: true, position: 'top' }
      }]
    })
  }

  // 成绩段分布雷达图
  if (scoreRangeChart.value) {
    const chart3 = echarts.init(scoreRangeChart.value)
    chart3.setOption({
      tooltip: {},
      radar: {
        indicator: [
          { name: '优秀', max: 100 },
          { name: '良好', max: 100 },
          { name: '中等', max: 100 },
          { name: '及格', max: 100 },
          { name: '不及格', max: 100 }
        ]
      },
      series: [{
        type: 'radar',
        data: courseStats.value.map(c => ({
          value: [c.dist.excellent, c.dist.good, c.dist.medium, c.dist.pass, c.dist.fail],
          name: c.courseName
        }))
      }]
    })
  }

  // 及格率对比
  if (passRateChart.value) {
    const chart4 = echarts.init(passRateChart.value)
    chart4.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['及格率', '优秀率'] },
      xAxis: {
        type: 'category',
        data: courseStats.value.map(c => c.courseName),
        axisLabel: { rotate: 30 }
      },
      yAxis: { type: 'value', max: 100, axisLabel: { formatter: '{value}%' } },
      series: [
        {
          name: '及格率',
          type: 'line',
          data: courseStats.value.map(c => parseFloat(c.passRate)),
          smooth: true,
          itemStyle: { color: '#67c23a' }
        },
        {
          name: '优秀率',
          type: 'line',
          data: courseStats.value.map(c => parseFloat(c.excellentRate)),
          smooth: true,
          itemStyle: { color: '#409eff' }
        }
      ]
    })
  }
}

onMounted(() => {
  nextTick(() => {
    initCharts()
  })
})
</script>

<style lang="scss" scoped>
.stat-box {
  display: flex;
  align-items: center;
  padding: 24px;
  border-radius: 12px;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);

  .stat-icon {
    font-size: 48px;
    opacity: 0.8;
    margin-right: 20px;
  }

  .stat-info {
    .stat-value {
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.9;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
</style>
