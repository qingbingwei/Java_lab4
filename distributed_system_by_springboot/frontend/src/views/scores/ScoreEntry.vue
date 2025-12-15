<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><EditPen /></el-icon>
      <span>成绩录入</span>
    </div>

    <!-- 选择教学班 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form inline>
        <el-form-item label="选择教学班">
          <el-select v-model="selectedClassId" filterable placeholder="请选择教学班" style="width: 300px" @change="loadStudents">
            <el-option v-for="c in classList" :key="c.id" :label="`${c.classId} - ${c.courseName} (${c.teacherName})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="成绩类型">
          <el-radio-group v-model="scoreType">
            <el-radio value="regular">平时成绩</el-radio>
            <el-radio value="midterm">期中成绩</el-radio>
            <el-radio value="experiment">实验成绩</el-radio>
            <el-radio value="final">期末成绩</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 学生成绩列表 -->
    <el-card shadow="hover" v-if="selectedClassId">
      <template #header>
        <div style="display: flex; justify-content: space-between; align-items: center;">
          <span>学生成绩录入 ({{ scoreTypeLabel }})</span>
          <el-button type="primary" @click="handleBatchSave">批量保存</el-button>
        </div>
      </template>

      <el-table v-loading="loading" :data="studentScores" stripe>
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column label="平时成绩" width="120">
          <template #default="{ row }">
            <el-input-number v-if="scoreType === 'regular'" v-model="row.regularScore" :min="0" :max="100" size="small" style="width: 100px" />
            <span v-else :class="getScoreClass(row.regularScore)">{{ row.regularScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="期中成绩" width="120">
          <template #default="{ row }">
            <el-input-number v-if="scoreType === 'midterm'" v-model="row.midtermScore" :min="0" :max="100" size="small" style="width: 100px" />
            <span v-else :class="getScoreClass(row.midtermScore)">{{ row.midtermScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="实验成绩" width="120">
          <template #default="{ row }">
            <el-input-number v-if="scoreType === 'experiment'" v-model="row.experimentScore" :min="0" :max="100" size="small" style="width: 100px" />
            <span v-else :class="getScoreClass(row.experimentScore)">{{ row.experimentScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="期末成绩" width="120">
          <template #default="{ row }">
            <el-input-number v-if="scoreType === 'final'" v-model="row.finalExamScore" :min="0" :max="100" size="small" style="width: 100px" />
            <span v-else :class="getScoreClass(row.finalExamScore)">{{ row.finalExamScore ?? '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="综合成绩" width="120">
          <template #default="{ row }">
            <span :class="getScoreClass(row.finalScore)" style="font-weight: bold;">
              {{ row.finalScore?.toFixed(2) ?? '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleSave(row)">保存</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-empty v-else description="请先选择教学班" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { teachingClassApi, scoreApi } from '@/api'
import { getScoreClass } from '@/utils'
import { ElMessage } from 'element-plus'
import { EditPen } from '@element-plus/icons-vue'

const userStore = useUserStore()

const loading = ref(false)
const classList = ref([])
const selectedClassId = ref(null)
const scoreType = ref('regular')
const studentScores = ref([])

const scoreTypeLabel = computed(() => {
  const map = { regular: '平时成绩', midterm: '期中成绩', experiment: '实验成绩', final: '期末成绩' }
  return map[scoreType.value]
})

const loadClasses = async () => {
  try {
    let res
    // 教师只能看到自己的教学班，管理员可以看到所有
    if (userStore.isTeacher) {
      const teacherId = userStore.businessId
      if (!teacherId) {
        ElMessage.warning('无法获取教师信息，请重新登录')
        return
      }
      res = await teachingClassApi.getTeacherClasses(teacherId)
    } else {
      res = await teachingClassApi.getList()
    }
    classList.value = res.data || []
  } catch (error) {
    console.error('获取教学班列表失败:', error)
    ElMessage.error('获取教学班列表失败')
  }
}

const loadStudents = async () => {
  if (!selectedClassId.value) return
  loading.value = true
  try {
    const res = await teachingClassApi.getStudents(selectedClassId.value)
    // 转换为成绩录入格式
    studentScores.value = (res.data || []).map(s => ({
      studentDbId: s.id,
      studentId: s.studentId,
      studentName: s.name,
      teachingClassDbId: selectedClassId.value,
      regularScore: s.score?.regularScore,
      midtermScore: s.score?.midtermScore,
      experimentScore: s.score?.experimentScore,
      finalExamScore: s.score?.finalExamScore,
      finalScore: s.score?.finalScore,
      scoreId: s.score?.id
    }))
  } finally { loading.value = false }
}

const handleSave = async (row) => {
  try {
    const data = {
      id: row.scoreId,
      studentDbId: row.studentDbId,
      teachingClassDbId: row.teachingClassDbId,
      regularScore: row.regularScore,
      midtermScore: row.midtermScore,
      experimentScore: row.experimentScore,
      finalExamScore: row.finalExamScore
    }
    
    let res
    if (row.scoreId) {
      // 更新现有成绩
      res = await scoreApi.update(row.scoreId, data)
    } else {
      // 创建新成绩
      res = await scoreApi.create(data)
      // 保存返回的ID，下次就是更新操作
      if (res.data) {
        row.scoreId = res.data
      }
    }
    
    // 重新加载数据以获取最新的综合成绩
    await loadStudents()
    ElMessage.success('保存成功')
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败：' + (error.response?.data?.message || error.message))
  }
}

const handleBatchSave = async () => {
  loading.value = true
  try {
    const dataList = studentScores.value.map(row => ({
      studentDbId: row.studentDbId,
      teachingClassDbId: row.teachingClassDbId,
      regularScore: row.regularScore,
      midtermScore: row.midtermScore,
      experimentScore: row.experimentScore,
      finalExamScore: row.finalExamScore
    }))
    await scoreApi.batchCreate(dataList)
    ElMessage.success('批量保存成功')
    loadStudents()
  } finally { loading.value = false }
}

onMounted(() => loadClasses())
</script>
