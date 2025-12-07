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
          
          <el-form label-width="80px">
            <el-form-item label="选择学生">
              <el-select v-model="selectedStudent" filterable placeholder="搜索选择学生" style="width: 100%" @change="loadStudentEnrollments">
                <el-option v-for="s in students" :key="s.id" :label="`${s.studentId} - ${s.name}`" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-form>

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

      <!-- 教学班学生 -->
      <el-col :xs="24" :md="12">
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
import { studentApi, teachingClassApi, enrollmentApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { List } from '@element-plus/icons-vue'

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

const loadStudents = async () => {
  const res = await studentApi.getList()
  students.value = res.data || []
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
    await enrollmentApi.enroll({
      studentDbId: selectedStudent.value,
      teachingClassDbId: classInfo.id
    })
    ElMessage.success('选课成功')
    loadStudentEnrollments()
    loadClasses()
  } catch (error) {
    console.error('选课失败:', error)
  }
}

const handleDrop = async (enrollment) => {
  try {
    await ElMessageBox.confirm('确定要退选该课程吗？', '确认退选', { type: 'warning' })
    await enrollmentApi.drop({
      studentDbId: selectedStudent.value,
      teachingClassDbId: enrollment.teachingClassDbId
    })
    ElMessage.success('退选成功')
    loadStudentEnrollments()
    loadClasses()
  } catch {}
}

onMounted(() => {
  loadStudents()
  loadClasses()
})
</script>
