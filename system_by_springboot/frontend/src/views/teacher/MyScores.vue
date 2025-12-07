<template>
  <div class="teacher-scores">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的班级成绩</span>
          <el-select 
            v-model="selectedClassId" 
            placeholder="选择教学班" 
            @change="fetchScores"
            style="width: 300px;"
          >
            <el-option 
              v-for="cls in classList" 
              :key="cls.id" 
              :label="`${cls.classId} - ${cls.courseName || '未关联课程'}`" 
              :value="cls.id" 
            />
          </el-select>
        </div>
      </template>

      <!-- 成绩统计卡片 -->
      <el-row :gutter="20" v-if="selectedClassId" style="margin-bottom: 20px;">
        <el-col :span="6">
          <el-statistic title="学生人数" :value="statistics.studentCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="已录入" :value="statistics.enteredCount" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="平均分" :value="statistics.avgScore" :precision="2" />
        </el-col>
        <el-col :span="6">
          <el-statistic title="及格率" :value="statistics.passRate" suffix="%" :precision="1" />
        </el-col>
      </el-row>

      <el-table :data="scoreList" v-loading="loading" style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="regularScore" label="平时成绩" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.regularScore != null">{{ row.regularScore }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="midtermScore" label="期中成绩" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.midtermScore != null">{{ row.midtermScore }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="experimentScore" label="实验成绩" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.experimentScore != null">{{ row.experimentScore }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalExamScore" label="期末成绩" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.finalExamScore != null">{{ row.finalExamScore }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="finalScore" label="综合成绩" width="100" align="center">
          <template #default="{ row }">
            <el-tag 
              v-if="row.finalScore != null" 
              :type="row.finalScore >= 60 ? 'success' : 'danger'"
            >
              {{ row.finalScore }}
            </el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="gradePoint" label="绩点" width="80" align="center">
          <template #default="{ row }">
            <span v-if="row.gradePoint != null">{{ row.gradePoint }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="!selectedClassId" class="empty-tip">
        <el-empty description="请先选择一个教学班" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { teachingClassApi, scoreApi } from '@/api'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()

const loading = ref(false)
const classList = ref([])
const selectedClassId = ref(null)
const scoreList = ref([])

// 统计信息
const statistics = computed(() => {
  const list = scoreList.value
  const studentCount = list.length
  const enteredCount = list.filter(s => s.finalScore != null).length
  const validScores = list.filter(s => s.finalScore != null).map(s => s.finalScore)
  const avgScore = validScores.length > 0 
    ? validScores.reduce((a, b) => a + b, 0) / validScores.length 
    : 0
  const passCount = validScores.filter(s => s >= 60).length
  const passRate = validScores.length > 0 ? (passCount / validScores.length) * 100 : 0
  
  return { studentCount, enteredCount, avgScore, passRate }
})

// 获取教师的教学班列表
const fetchClasses = async () => {
  try {
    const teacherDbId = userStore.refId
    if (!teacherDbId) {
      ElMessage.warning('无法获取教师信息，请重新登录')
      return
    }
    const res = await teachingClassApi.getTeacherClasses(teacherDbId)
    classList.value = res.data || []
    // 如果有班级，默认选中第一个
    if (classList.value.length > 0) {
      selectedClassId.value = classList.value[0].id
      fetchScores()
    }
  } catch (error) {
    console.error('获取教学班列表失败:', error)
  }
}

// 获取选中班级的成绩
const fetchScores = async () => {
  if (!selectedClassId.value) return
  
  loading.value = true
  try {
    // 获取班级学生及成绩
    const res = await teachingClassApi.getClassStudents(selectedClassId.value)
    scoreList.value = res.data || []
  } catch (error) {
    console.error('获取成绩失败:', error)
    ElMessage.error('获取成绩失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchClasses()
})
</script>

<style scoped lang="scss">
.teacher-scores {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}

.text-muted {
  color: #909399;
}

.empty-tip {
  padding: 40px 0;
}
</style>
