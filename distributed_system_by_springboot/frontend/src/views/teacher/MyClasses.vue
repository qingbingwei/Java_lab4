<template>
  <div class="teacher-classes">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的教学班</span>
        </div>
      </template>

      <el-table :data="classList" v-loading="loading" style="width: 100%">
        <el-table-column prop="classId" label="教学班号" width="150" />
        <el-table-column prop="courseName" label="课程名称" />
        <el-table-column prop="credits" label="学分" width="80" align="center" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column prop="currentSize" label="已选人数" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.currentSize >= row.capacity ? 'danger' : 'success'">
              {{ row.currentSize || 0 }} / {{ row.capacity || 0 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewStudents(row)">
              <el-icon><User /></el-icon> 学生名单
            </el-button>
            <el-button type="success" link @click="enterScores(row)">
              <el-icon><EditPen /></el-icon> 成绩录入
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学生名单对话框 -->
    <el-dialog 
      v-model="studentDialogVisible" 
      :title="`${currentClass?.courseName} - 学生名单`" 
      width="800px"
    >
      <el-table :data="studentList" v-loading="studentLoading">
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="className" label="行政班级" />
        <el-table-column label="成绩状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.score ? 'success' : 'info'">
              {{ row.score ? '已录入' : '未录入' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="综合成绩" width="100" align="center">
          <template #default="{ row }">
            <span v-if="row.score">{{ row.score.finalScore }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { teachingClassApi } from '@/api'
import { ElMessage } from 'element-plus'
import { User, EditPen } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const classList = ref([])

const studentDialogVisible = ref(false)
const studentLoading = ref(false)
const studentList = ref([])
const currentClass = ref(null)

// 获取教师的教学班列表
const fetchClasses = async () => {
  loading.value = true
  try {
    // 使用教师的 refId 获取其教学班
    const teacherDbId = userStore.refId
    if (!teacherDbId) {
      ElMessage.warning('无法获取教师信息，请重新登录')
      return
    }
    const res = await teachingClassApi.getTeacherClasses(teacherDbId)
    classList.value = res.data || []
  } catch (error) {
    console.error('获取教学班列表失败:', error)
    ElMessage.error('获取教学班列表失败')
  } finally {
    loading.value = false
  }
}

// 查看学生名单
const viewStudents = async (row) => {
  currentClass.value = row
  studentDialogVisible.value = true
  studentLoading.value = true
  try {
    const res = await teachingClassApi.getClassStudents(row.id)
    studentList.value = res.data || []
  } catch (error) {
    console.error('获取学生名单失败:', error)
    ElMessage.error('获取学生名单失败')
  } finally {
    studentLoading.value = false
  }
}

// 跳转到成绩录入
const enterScores = (row) => {
  router.push({ 
    path: '/scores/entry', 
    query: { classId: row.id, className: row.courseName }
  })
}

onMounted(() => {
  fetchClasses()
})
</script>

<style scoped lang="scss">
.teacher-classes {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
</style>
