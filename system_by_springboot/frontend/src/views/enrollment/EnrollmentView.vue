<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><List /></el-icon>
      <span>选课管理</span>
    </div>

    <el-row :gutter="20">
      <!-- 学生选课 -->
      <el-col :xs="24" :md="12">
        <el-card shadow="hover">
          <template #header>学生选课</template>
          
          <el-form label-width="80px" v-if="!isStudent">
            <el-form-item label="选择学生">
              <el-select v-model="selectedStudent" filterable placeholder="搜索选择学生" style="width: 100%" @change="loadStudentEnrollments">
                <el-option v-for="s in students" :key="s.id" :label="`${s.studentId} - ${s.name}`" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-form>

          <el-alert v-if="isStudent" title="我的选课" type="info" :closable="false" style="margin-bottom: 16px" />

          <div v-if="selectedStudent">
            <el-divider>已选课程</el-divider>
            <el-table :data="studentEnrollments" stripe max-height="300">
              <el-table-column prop="className" label="教学班" />
              <el-table-column prop="courseName" label="课程" />
              <el-table-column label="操作" width="80">
                <template #default="{ row }">
                  <el-button type="danger" size="small" link @click="handleDrop(row)">退选</el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-divider>可选课程</el-divider>
            <el-table :data="availableClasses" stripe max-height="300">
              <el-table-column prop="classId" label="教学班" width="120" />
              <el-table-column prop="courseName" label="课程" />
              <el-table-column prop="teacherName" label="教师" width="80" />
              <el-table-column label="容量" width="100">
                <template #default="{ row }">
                  {{ row.currentSize }}/{{ row.capacity }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80">
                <template #default="{ row }">
                  <el-button type="primary" size="small" link @click="handleEnroll(row)" :disabled="row.currentSize >= row.capacity">选课</el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-card>
      </el-col>

      <!-- 教学班学生 - 仅管理员和教师可见 -->
      <el-col :xs="24" :md="12" v-if="!isStudent">
        <el-card shadow="hover">
          <template #header>教学班学生名单</template>
          
          <el-form label-width="80px">
            <el-form-item label="选择班级">
              <el-select v-model="selectedClass" filterable placeholder="选择教学班" style="width: 100%" @change="loadClassStudents">
                <el-option v-for="c in classList" :key="c.id" :label="`${c.classId} - ${c.courseName || '未关联课程'}`" :value="c.id" />
              </el-select>
            </el-form-item>
          </el-form>

          <div v-if="selectedClass">
            <el-descriptions :column="2" border size="small" style="margin-bottom: 16px">
              <el-descriptions-item label="当前人数">{{ classStudents.length }}</el-descriptions-item>
              <el-descriptions-item label="班级容量">{{ currentClassInfo?.capacity }}</el-descriptions-item>
            </el-descriptions>

            <el-table :data="classStudents" stripe max-height="400">
              <el-table-column prop="studentId" label="学号" width="150" />
              <el-table-column prop="name" label="姓名" width="100" />
              <el-table-column prop="className" label="班级" />
            </el-table>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { studentApi, teachingClassApi, enrollmentApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const userStore = useUserStore()
const isStudent = computed(() => userStore.isStudent)
const isTeacher = computed(() => userStore.isTeacher)

const students = ref([])
const classList = ref([])
const selectedStudent = ref(null)
const selectedClass = ref(null)
const studentEnrollments = ref([])
const classStudents = ref([])

const currentClassInfo = computed(() => classList.value.find(c => c.id === selectedClass.value))

const availableClasses = computed(() => {
  const enrolledIds = new Set(studentEnrollments.value.map(e => e.teachingClassDbId))
  return classList.value.filter(c => !enrolledIds.has(c.id))
})

// 判断是否为学生角色
const isStudent = computed(() => userStore.user?.role === 'STUDENT')

const loadStudents = async () => {
<<<<<<< HEAD
  if (isStudent.value) {
    // 学生角色自动加载自己的信息
    const res = await studentApi.getById(userStore.refId)
    if (res.data) {
      students.value = [res.data]
      selectedStudent.value = res.data.id
      await loadStudentEnrollments()
    }
  } else {
    const res = await studentApi.getList()
    students.value = res.data || []
  }
=======
  // 如果是学生角色，不加载学生列表，直接使用当前用户的refId
  if (isStudent.value) {
    selectedStudent.value = userStore.user?.refId
    if (selectedStudent.value) {
      loadStudentEnrollments()
    }
    return
  }

  // 管理员和教师可以查看所有学生
  const res = await studentApi.getList()
  students.value = res.data || []
>>>>>>> eaf7128358a106ff42be47185efe559cc016247c
}

const loadClasses = async () => {
  const res = await teachingClassApi.getList()
  classList.value = res.data || []
}

const loadStudentEnrollments = async () => {
  if (!selectedStudent.value) return
  const res = await enrollmentApi.getStudentEnrollments(selectedStudent.value)
  studentEnrollments.value = res.data || []
}

const loadClassStudents = async () => {
  if (!selectedClass.value) return
  const res = await teachingClassApi.getStudents(selectedClass.value)
  classStudents.value = res.data || []
}

const handleEnroll = async (classInfo) => {
  try {
    // 检查权限：学生只能为自己选课
    if (isStudent.value && selectedStudent.value !== userStore.refId) {
      ElMessage.error('无权限操作')
      return
    }
    await enrollmentApi.enroll({
      studentDbId: selectedStudent.value,
      teachingClassDbId: classInfo.id
    })
    ElMessage.success('选课成功')
    await loadStudentEnrollments()
    await loadClasses()
  } catch (error) {
    console.error('选课失败:', error)
  }
}

const handleDrop = async (enrollment) => {
  try {
    await ElMessageBox.confirm('确定要退选该课程吗？', '确认退选', { type: 'warning' })
    // 检查权限：学生只能退选自己的课
    if (isStudent.value && selectedStudent.value !== userStore.refId) {
      ElMessage.error('无权限操作')
      return
    }
    // 使用正确的参数名
    await enrollmentApi.drop({
      studentDbId: selectedStudent.value,
      teachingClassDbId: enrollment.teachingClassDbId
    })
    ElMessage.success('退选成功')
    await loadStudentEnrollments()
    await loadClasses()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('退选失败:', error)
    }
  }
}

onMounted(() => {
  loadStudents()
  loadClasses()
})
</script>
