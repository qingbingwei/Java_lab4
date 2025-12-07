<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><EditPen /></el-icon>
      <span>成绩录入</span>
    </div>

    <el-steps :active="activeStep" finish-status="success" align-center style="margin-bottom: 32px">
      <el-step title="选择教学班" />
      <el-step title="选择成绩类型" />
      <el-step title="录入成绩" />
      <el-step title="完成" />
    </el-steps>

    <!-- 步骤1: 选择教学班 -->
    <el-card v-if="activeStep === 0" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><School /></el-icon>
          <span>选择教学班</span>
        </div>
      </template>

      <el-radio-group v-model="selectedClass" class="class-selector">
        <el-radio
          v-for="tc in systemStore.teachingClasses"
          :key="tc.classId"
          :label="tc.classId"
          border
          size="large"
          class="class-radio"
        >
          <div class="class-option">
            <div class="class-name">{{ tc.course.courseName }}</div>
            <div class="class-meta">
              班级: {{ tc.classId }} | 教师: {{ tc.teacher.name }} | 学生: {{ tc.students?.length || 0 }}人
            </div>
          </div>
        </el-radio>
      </el-radio-group>

      <div class="step-actions">
        <el-button type="primary" :disabled="!selectedClass" @click="nextStep">
          下一步
        </el-button>
      </div>
    </el-card>

    <!-- 步骤2: 选择成绩类型 -->
    <el-card v-if="activeStep === 1" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Document /></el-icon>
          <span>选择成绩类型</span>
        </div>
      </template>

      <el-radio-group v-model="scoreType" class="score-type-selector">
        <el-radio label="regular" border size="large">
          <div class="type-option">
            <div class="type-name">平时成绩</div>
            <div class="type-desc">占比 20%</div>
          </div>
        </el-radio>
        <el-radio label="midterm" border size="large">
          <div class="type-option">
            <div class="type-name">期中成绩</div>
            <div class="type-desc">占比 20%</div>
          </div>
        </el-radio>
        <el-radio label="experiment" border size="large">
          <div class="type-option">
            <div class="type-name">实验成绩</div>
            <div class="type-desc">占比 20%</div>
          </div>
        </el-radio>
        <el-radio label="final" border size="large">
          <div class="type-option">
            <div class="type-name">期末成绩</div>
            <div class="type-desc">占比 40%</div>
          </div>
        </el-radio>
      </el-radio-group>

      <div class="step-actions">
        <el-button @click="prevStep">上一步</el-button>
        <el-button type="primary" :disabled="!scoreType" @click="nextStep">
          下一步
        </el-button>
      </div>
    </el-card>

    <!-- 步骤3: 录入成绩 -->
    <el-card v-if="activeStep === 2" shadow="hover">
      <template #header>
        <div class="card-header">
          <el-icon><Edit /></el-icon>
          <span>录入{{ scoreTypeLabel }}成绩</span>
          <el-button type="primary" size="small" @click="batchGenerate" style="margin-left: auto">
            <el-icon><MagicStick /></el-icon>
            批量生成
          </el-button>
        </div>
      </template>

      <el-table :data="studentScores" stripe max-height="500">
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column :label="`${scoreTypeLabel}成绩`" width="200">
          <template #default="{ row }">
            <el-input-number
              v-model="row.score"
              :min="0"
              :max="100"
              :step="1"
              controls-position="right"
              size="small"
            />
          </template>
        </el-table-column>
        <el-table-column label="等级" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.score !== null" :type="getScoreLevel(row.score).type">
              {{ getScoreLevel(row.score).label }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="已有成绩" min-width="200">
          <template #default="{ row }">
            <span v-if="row.existingScore">
              平时:{{ row.existingScore.regularScore || '-' }} | 
              期中:{{ row.existingScore.midtermScore || '-' }} | 
              实验:{{ row.existingScore.experimentScore || '-' }} | 
              期末:{{ row.existingScore.finalExamScore || '-' }}
            </span>
            <span v-else>暂无</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="step-actions">
        <el-button @click="prevStep">上一步</el-button>
        <el-button type="primary" @click="submitScores">
          提交成绩
        </el-button>
      </div>
    </el-card>

    <!-- 步骤4: 完成 -->
    <el-result
      v-if="activeStep === 3"
      icon="success"
      title="成绩录入成功"
      sub-title="已成功录入所选教学班的成绩"
    >
      <template #extra>
        <el-button type="primary" @click="reset">继续录入</el-button>
        <el-button @click="$router.push('/scores/query')">查看成绩</el-button>
      </template>
    </el-result>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useSystemStore } from '@/stores/system'
import { getScoreLevel } from '@/utils'
import { ElMessage } from 'element-plus'
import {
  EditPen,
  School,
  Document,
  Edit,
  MagicStick
} from '@element-plus/icons-vue'

const systemStore = useSystemStore()

const activeStep = ref(0)
const selectedClass = ref('')
const scoreType = ref('')
const studentScores = ref([])

const scoreTypeLabel = computed(() => {
  const map = {
    regular: '平时',
    midterm: '期中',
    experiment: '实验',
    final: '期末'
  }
  return map[scoreType.value] || ''
})

const scoreTypeField = computed(() => {
  const map = {
    regular: 'regularScore',
    midterm: 'midtermScore',
    experiment: 'experimentScore',
    final: 'finalExamScore'
  }
  return map[scoreType.value] || ''
})

const nextStep = () => {
  if (activeStep.value === 0 && selectedClass.value) {
    activeStep.value++
  } else if (activeStep.value === 1 && scoreType.value) {
    prepareScoreEntry()
    activeStep.value++
  }
}

const prevStep = () => {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

const prepareScoreEntry = () => {
  const tc = systemStore.getTeachingClass(selectedClass.value)
  if (!tc) return

  studentScores.value = (tc.students || []).map(studentId => {
    const student = systemStore.getStudent(studentId)
    const existingScore = student?.scores?.[selectedClass.value]
    return {
      studentId,
      name: student?.name || '',
      score: existingScore?.[scoreTypeField.value] ?? null,
      existingScore
    }
  })
}

const batchGenerate = () => {
  studentScores.value.forEach(item => {
    item.score = Math.floor(Math.random() * 41) + 60 // 60-100
  })
  ElMessage.success('已批量生成随机成绩')
}

const submitScores = () => {
  // 验证所有成绩都已填写
  const hasEmpty = studentScores.value.some(item => item.score === null)
  if (hasEmpty) {
    ElMessage.warning('请填写所有学生的成绩')
    return
  }

  // 更新成绩
  studentScores.value.forEach(item => {
    const student = systemStore.getStudent(item.studentId)
    if (!student) return

    if (!student.scores) {
      student.scores = {}
    }
    if (!student.scores[selectedClass.value]) {
      student.scores[selectedClass.value] = {
        classId: selectedClass.value,
        regularScore: -1,
        midtermScore: -1,
        experimentScore: -1,
        finalExamScore: -1,
        finalScore: 0
      }
    }

    student.scores[selectedClass.value][scoreTypeField.value] = item.score

    // 如果所有成绩都有了，计算综合成绩
    const score = student.scores[selectedClass.value]
    if (
      score.regularScore >= 0 &&
      score.midtermScore >= 0 &&
      score.experimentScore >= 0 &&
      score.finalExamScore >= 0
    ) {
      score.finalScore = parseFloat((
        score.regularScore * 0.2 +
        score.midtermScore * 0.2 +
        score.experimentScore * 0.2 +
        score.finalExamScore * 0.4
      ).toFixed(2))
    }
  })

  systemStore.saveData()
  activeStep.value = 3
  ElMessage.success('成绩录入成功！')
}

const reset = () => {
  activeStep.value = 0
  selectedClass.value = ''
  scoreType.value = ''
  studentScores.value = []
}
</script>

<style lang="scss" scoped>
.class-selector,
.score-type-selector {
  display: flex;
  flex-direction: column;
  gap: 16px;

  .class-radio,
  .el-radio {
    width: 100%;
    margin: 0;

    :deep(.el-radio__label) {
      width: 100%;
    }
  }

  .class-option,
  .type-option {
    width: 100%;

    .class-name,
    .type-name {
      font-size: 16px;
      font-weight: 600;
      margin-bottom: 4px;
    }

    .class-meta,
    .type-desc {
      font-size: 13px;
      color: var(--el-text-color-secondary);
    }
  }
}

.step-actions {
  margin-top: 24px;
  text-align: center;

  .el-button {
    min-width: 100px;
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}
</style>
