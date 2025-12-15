<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><Reading /></el-icon>
      <span>课程管理</span>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="queryParams.keyword" placeholder="搜索课程" :prefix-icon="Search" clearable style="width: 250px" @input="handleSearch" />
        <el-select v-model="queryParams.courseType" placeholder="课程类型" clearable style="width: 120px" @change="loadData">
          <el-option label="全部" value="" />
          <el-option label="必修" value="REQUIRED" />
          <el-option label="选修" value="ELECTIVE" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增课程</el-button>
      </div>
    </div>

    <el-card shadow="hover">
      <el-table v-loading="loading" :data="courseList" stripe>
        <el-table-column prop="courseId" label="课程编号" width="120" />
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column prop="credits" label="学分" width="80" />
        <el-table-column prop="courseType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.courseType === 'REQUIRED' ? 'danger' : 'success'" size="small">
              {{ row.courseType === 'REQUIRED' ? '必修' : '选修' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hours" label="学时" width="80" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑课程' : '新增课程'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程编号" prop="courseId">
          <el-input v-model="form.courseId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" />
        </el-form-item>
        <el-form-item label="学分" prop="credits">
          <el-input-number v-model="form.credits" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="课程类型" prop="courseType">
          <el-radio-group v-model="form.courseType">
            <el-radio value="REQUIRED">必修</el-radio>
            <el-radio value="ELECTIVE">选修</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="学时" prop="hours">
          <el-input-number v-model="form.hours" :min="8" :max="128" :step="8" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" />
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
import { courseApi } from '@/api'
import { useSystemStore } from '@/stores/system'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Reading, Search, Plus } from '@element-plus/icons-vue'

const systemStore = useSystemStore()
const loading = ref(false)
const courseList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const queryParams = reactive({ current: 1, size: 20, keyword: '', courseType: '' })

const form = reactive({
  courseId: '', courseName: '', credits: 2, courseType: 'REQUIRED', hours: 32, description: ''
})

const rules = {
  courseId: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }],
  credits: [{ required: true, message: '请输入学分', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await courseApi.getPage(queryParams)
    courseList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally { loading.value = false }
}

const handleSearch = () => { queryParams.current = 1; loadData() }

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: undefined, courseId: '', courseName: '', credits: 2, courseType: 'REQUIRED', hours: 32, description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除课程"${row.courseName}"？`, '确认', { type: 'warning' })
    await courseApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
    systemStore.loadOverview() // 刷新首页概览数据
  } catch {}
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) await courseApi.update(form.id, form)
    else {
      // 新增时，创建一个不包含id的副本
      const { id, ...formData } = form
      await courseApi.create(formData)
      systemStore.loadOverview() // 刷新首页概览数据
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {}
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.pagination { margin-top: 20px; display: flex; justify-content: flex-end; }
</style>
