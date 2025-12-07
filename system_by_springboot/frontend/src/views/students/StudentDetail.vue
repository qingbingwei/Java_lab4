<template>
  <div class="page-container">
    <div class="page-title">
      <el-button :icon="ArrowLeft" link @click="router.back()">返回</el-button>
      <span>学生详情</span>
    </div>

    <el-row :gutter="24" v-loading="loading">
      <!-- 基本信息 -->
      <el-col :xs="24" :md="8">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><User /></el-icon>
              <span>基本信息</span>
            </div>
          </template>
          
          <div class="info-list" v-if="student">
            <div class="info-item">
              <span class="label">学号</span>
              <span class="value">{{ student.studentId }}</span>
            </div>
            <div class="info-item">
              <span class="label">姓名</span>
              <span class="value">{{ student.name }}</span>
            </div>
            <div class="info-item">
              <span class="label">性别</span>
              <el-tag :type="student.gender === 'MALE' ? 'primary' : 'danger'" size="small">
                {{ student.gender === 'MALE' ? '男' : '女' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="label">班级</span>
              <span class="value">{{ student.className }}</span>
            </div>
            <div class="info-item">
              <span class="label">年级</span>
              <span class="value">{{ student.grade }}</span>
            </div>
            <div class="info-item">
              <span class="label">专业</span>
              <span class="value">{{ student.major }}</span>
            </div>
            <div class="info-item">
              <span class="label">手机</span>
              <span class="value">{{ student.phone || '-' }}</span>
            </div>
            <div class="info-item">
              <span class="label">邮箱</span>
              <span class="value">{{ student.email || '-' }}</span>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 成绩列表 -->
      <el-col :xs="24" :md="16">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span>成绩记录</span>
            </div>
          </template>

          <el-table :data="scoreList" stripe style="width: 100%">
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
            <el-table-column prop="finalScore" label="综合成绩" width="100">
              <template #default="{ row }">
                <span :class="getScoreClass(row.finalScore)" style="font-weight: bold;">
                  {{ row.finalScore?.toFixed(2) ?? '-' }}
                </span>
              </template>
            </el-table-column>
          </el-table>

          <el-empty v-if="scoreList.length === 0" description="暂无成绩记录" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { studentApi } from '@/api'
import { getScoreClass } from '@/utils'
import { ElMessage } from 'element-plus'
import { ArrowLeft, User, Document } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const loading = ref(false)
const student = ref(null)
const scoreList = ref([])

const loadData = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    if (!id || isNaN(id)) {
      ElMessage.error('无效的学生ID')
      router.back()
      return
    }
    const res = await studentApi.getById(id)
    student.value = res.data

    // 加载成绩
    const scoresRes = await studentApi.getScores(id)
    scoreList.value = scoresRes.data || []
  } catch (error) {
    console.error('加载数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.page-title {
  display: flex;
  align-items: center;
  gap: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.info-list {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 12px 0;
    border-bottom: 1px solid var(--el-border-color-lighter);

    &:last-child {
      border-bottom: none;
    }

    .label {
      color: var(--el-text-color-secondary);
    }

    .value {
      font-weight: 500;
    }
  }
}
</style>
