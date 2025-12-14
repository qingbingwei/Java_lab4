<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><TrendCharts /></el-icon>
      <span>成绩排名</span>
    </div>

    <!-- 筛选条件 -->
    <el-card shadow="hover" style="margin-bottom: 20px">
      <el-form inline>
        <el-form-item label="教学班">
          <el-select v-model="queryParams.teachingClassDbId" placeholder="选择教学班" clearable filterable style="width: 300px" @change="loadData">
            <el-option v-for="c in classList" :key="c.id" :label="`${c.classId} - ${c.courseName}`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期">
          <el-select v-model="queryParams.semester" placeholder="选择学期" clearable style="width: 150px" @change="loadData">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 排名列表 -->
    <el-card shadow="hover">
      <template #header>成绩排名</template>
      <el-table v-loading="loading" :data="rankingList" stripe>
        <el-table-column label="排名" width="80">
          <template #default="{ $index }">
            <el-tag v-if="$index === 0" type="danger">🥇 1</el-tag>
            <el-tag v-else-if="$index === 1" type="warning">🥈 2</el-tag>
            <el-tag v-else-if="$index === 2" type="success">🥉 3</el-tag>
            <span v-else>{{ $index + 1 }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="studentName" label="姓名" width="100" />
        <el-table-column prop="courseName" label="课程" min-width="150" />
        <el-table-column prop="regularScore" label="平时" width="80" />
        <el-table-column prop="midtermScore" label="期中" width="80" />
        <el-table-column prop="experimentScore" label="实验" width="80" />
        <el-table-column prop="finalExamScore" label="期末" width="80" />
        <el-table-column prop="finalScore" label="综合成绩" width="120">
          <template #default="{ row }">
            <span :class="getScoreClass(row.finalScore)" style="font-weight: bold; font-size: 16px;">
              {{ row.finalScore?.toFixed(2) }}
            </span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { scoreApi, teachingClassApi } from '@/api'
import { getScoreClass } from '@/utils'
import { TrendCharts } from '@element-plus/icons-vue'

const loading = ref(false)
const rankingList = ref([])
const classList = ref([])

const queryParams = reactive({
  teachingClassDbId: '',
  semester: ''
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await scoreApi.getRanking(queryParams)
    rankingList.value = res.data || []
  } finally { loading.value = false }
}

const loadClasses = async () => {
  const res = await teachingClassApi.getList()
  classList.value = res.data || []
}

onMounted(() => {
  loadClasses()
  loadData()
})
</script>
