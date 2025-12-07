<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><PieChart /></el-icon>
      <span>统计分析</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.scoreCount || 0 }}</div>
          <div class="stat-label">成绩记录数</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.avgScore?.toFixed(1) || 0 }}</div>
          <div class="stat-label">平均分</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.passRate?.toFixed(1) || 0 }}%</div>
          <div class="stat-label">及格率</div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ overview.excellentRate?.toFixed(1) || 0 }}%</div>
          <div class="stat-label">优秀率</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20">
      <!-- 成绩分布图 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>成绩分布</template>
          <div ref="distributionChart" style="height: 300px"></div>
        </el-card>
      </el-col>

      <!-- 课程平均分 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>课程平均分</template>
          <div ref="courseChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <!-- 班级对比 -->
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>班级成绩对比</template>
          <div ref="classChart" style="height: 300px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { statisticsApi } from '@/api'
import * as echarts from 'echarts'
import { PieChart } from '@element-plus/icons-vue'

const overview = ref({})
const distributionChart = ref()
const courseChart = ref()
const classChart = ref()

let charts = []

const loadOverview = async () => {
  const res = await statisticsApi.getOverview()
  overview.value = res.data || {}
}

const loadDistribution = async () => {
  const res = await statisticsApi.getScoreDistribution()
  const data = res.data || {}
  
  const chart = echarts.init(distributionChart.value)
  charts.push(chart)
  
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: data['90-100'] || 0, name: '优秀(90-100)', itemStyle: { color: '#67c23a' } },
        { value: data['80-89'] || 0, name: '良好(80-89)', itemStyle: { color: '#409eff' } },
        { value: data['70-79'] || 0, name: '中等(70-79)', itemStyle: { color: '#e6a23c' } },
        { value: data['60-69'] || 0, name: '及格(60-69)', itemStyle: { color: '#909399' } },
        { value: data['0-59'] || 0, name: '不及格(<60)', itemStyle: { color: '#f56c6c' } }
      ]
    }]
  })
}

const loadCourseAverage = async () => {
  const res = await statisticsApi.getCourseAverage()
  const data = res.data || []
  
  const chart = echarts.init(courseChart.value)
  charts.push(chart)
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: {
      type: 'category',
      data: data.map(d => d.courseName),
      axisLabel: { rotate: 30, fontSize: 10 }
    },
    yAxis: { type: 'value', min: 0, max: 100 },
    series: [{
      type: 'bar',
      data: data.map(d => d.avgScore?.toFixed(1)),
      itemStyle: {
        color: (params) => {
          const value = params.value
          if (value >= 80) return '#67c23a'
          if (value >= 60) return '#409eff'
          return '#f56c6c'
        }
      },
      label: { show: true, position: 'top', formatter: '{c}' }
    }]
  })
}

const loadClassComparison = async () => {
  const res = await statisticsApi.getClassComparison()
  const data = res.data || []
  
  const chart = echarts.init(classChart.value)
  charts.push(chart)
  
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['平均分', '最高分', '最低分'] },
    xAxis: {
      type: 'category',
      data: data.map(d => d.className)
    },
    yAxis: { type: 'value', min: 0, max: 100 },
    series: [
      { name: '平均分', type: 'bar', data: data.map(d => d.avgScore?.toFixed(1)) },
      { name: '最高分', type: 'bar', data: data.map(d => d.maxScore) },
      { name: '最低分', type: 'bar', data: data.map(d => d.minScore) }
    ]
  })
}

const handleResize = () => {
  charts.forEach(chart => chart.resize())
}

onMounted(async () => {
  await loadOverview()
  await loadDistribution()
  await loadCourseAverage()
  await loadClassComparison()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  charts.forEach(chart => chart.dispose())
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.stat-card {
  text-align: center;
  padding: 20px 0;
  
  .stat-value {
    font-size: 32px;
    font-weight: bold;
    color: var(--el-color-primary);
  }
  
  .stat-label {
    margin-top: 8px;
    color: var(--el-text-color-secondary);
  }
}
</style>
