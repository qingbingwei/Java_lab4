<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><School /></el-icon>
      <span>教学班管理</span>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索教学班" :prefix-icon="Search" clearable style="width: 250px" @input="handleSearch" />
        <el-select v-model="queryParams.semester" placeholder="学期筛选" clearable style="width: 150px" @change="loadData">
          <el-option label="2023-2024-1" value="2023-2024-1" />
          <el-option label="2023-2024-2" value="2023-2024-2" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增教学班</el-button>
      </div>
    </div>

    <el-card shadow="hover">
      <el-table v-loading="loading" :data="classList" stripe>
        <el-table-column prop="classId" label="教学班号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="teacherName" label="授课教师" width="100" />
        <el-table-column prop="semester" label="学期" width="120" />
        <el-table-column label="容量" width="120">
          <template #default="{ row }">
            <el-progress :percentage="(row.currentSize / row.capacity) * 100" :format="() => `${row.currentSize}/${row.capacity}`" />
          </template>
        </el-table-column>
        <el-table-column prop="scheduleTime" label="上课时间" width="120" />
        <el-table-column prop="classroom" label="上课地点" width="120" />
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewStudents(row)">学生名单</el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 学生名单对话框 -->
    <el-dialog v-model="studentDialogVisible" title="学生名单" width="600px">
      <el-table :data="studentList" stripe max-height="400">
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column prop="className" label="班级" />
      </el-table>
    </el-dialog>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教学班' : '新增教学班'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="教学班号" prop="classId">
          <el-input v-model="form.classId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="课程" prop="courseDbId">
          <el-select v-model="form.courseDbId" filterable style="width: 100%">
            <el-option v-for="c in courses" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="教师" prop="teacherDbId">
          <el-select v-model="form.teacherDbId" filterable style="width: 100%">
            <el-option v-for="t in teachers" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="form.semester" style="width: 100%">
            <el-option label="2023-2024-1" value="2023-2024-1" />
            <el-option label="2023-2024-2" value="2023-2024-2" />
          </el-select>
        </el-form-item>
        <el-form-item label="容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="10" :max="200" />
        </el-form-item>
        <el-form-item label="上课时间">
          <el-input v-model="form.scheduleTime" />
        </el-form-item>
        <el-form-item label="上课地点">
          <el-input v-model="form.classroom" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { teachingClassApi, courseApi, teacherApi } from '@/api'
import { useSystemStore } from '@/stores/system'
import { ElMessage, ElMessageBox } from 'element-plus'
import { School, Search, Plus } from '@element-plus/icons-vue'

const systemStore = useSystemStore()
const loading = ref(false)
const classList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const studentDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const studentList = ref([])
const courses = ref([])
const teachers = ref([])

const queryParams = reactive({ current: 1, size: 20, keyword: '', semester: '' })

const form = reactive({
  classId: '', courseDbId: '', teacherDbId: '', semester: '2023-2024-1', capacity: 50, scheduleTime: '', classroom: ''
})

const rules = {
  classId: [{ required: true, message: '请输入教学班号', trigger: 'blur' }],
  courseDbId: [{ required: true, message: '请选择课程', trigger: 'change' }],
  teacherDbId: [{ required: true, message: '请选择教师', trigger: 'change' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await teachingClassApi.getPage(queryParams)
    classList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const loadOptions = async () => {
  const [courseRes, teacherRes] = await Promise.all([courseApi.getList(), teacherApi.getList()])
  courses.value = courseRes.data || []
  teachers.value = teacherRes.data || []
}

const handleSearch = () => { queryParams.current = 1; loadData() }

const viewStudents = async (row) => {
  const res = await teachingClassApi.getStudents(row.id)
  studentList.value = res.data || []
  studentDialogVisible.value = true
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: undefined, classId: '', courseDbId: '', teacherDbId: '', semester: '2023-2024-1', capacity: 50, scheduleTime: '', classroom: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除教学班"${row.classId}"？此操作将删除相关的选课和成绩记录！`, '确认', { type: 'warning' })
    await teachingClassApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
    systemStore.loadOverview() // 刷新首页概览数据
  } catch {}
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) await teachingClassApi.update(form.id, form)
    else {
      // 新增时，创建一个不包含id的副本
      const { id, ...formData } = form
      await teachingClassApi.create(formData)
      systemStore.loadOverview() // 刷新首页概览数据
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {}
}

onMounted(() => { loadData(); loadOptions() })
</script>

<style lang="scss" scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
