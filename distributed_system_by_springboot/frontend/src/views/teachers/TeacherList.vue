<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><UserFilled /></el-icon>
      <span>教师管理</span>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索教师编号或姓名"
          :prefix-icon="Search"
          clearable
          style="width: 250px"
          @input="handleSearch"
        />
        <el-select v-model="queryParams.department" placeholder="学院筛选" clearable style="width: 150px" @change="loadData">
          <el-option v-for="d in departmentList" :key="d" :label="d" :value="d" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增教师</el-button>
      </div>
    </div>

    <el-card shadow="hover">
      <el-table v-loading="loading" :data="teacherList" stripe style="width: 100%">
        <el-table-column prop="teacherId" label="教师编号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="title" label="职称" width="100" />
        <el-table-column prop="department" label="所属学院" min-width="150" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
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
          :page-sizes="[10, 20, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 对话框 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑教师' : '新增教师'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="教师编号" prop="teacherId">
          <el-input v-model="form.teacherId" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="职称" prop="title">
          <el-select v-model="form.title" style="width: 100%">
            <el-option label="教授" value="教授" />
            <el-option label="副教授" value="副教授" />
            <el-option label="讲师" value="讲师" />
            <el-option label="助教" value="助教" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属学院" prop="department">
          <el-input v-model="form.department" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { teacherApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UserFilled, Search, Plus } from '@element-plus/icons-vue'

const loading = ref(false)
const teacherList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const queryParams = reactive({
  current: 1,
  size: 20,
  keyword: '',
  department: ''
})

const form = reactive({
  teacherId: '',
  name: '',
  title: '',
  department: '',
  phone: '',
  email: ''
})

const rules = {
  teacherId: [{ required: true, message: '请输入教师编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const departmentList = computed(() => {
  const depts = new Set(teacherList.value.map(t => t.department).filter(Boolean))
  return Array.from(depts)
})

const loadData = async () => {
  loading.value = true
  try {
    const res = await teacherApi.getPage(queryParams)
    teacherList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载失败:', error)
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  queryParams.current = 1
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: undefined, teacherId: '', name: '', title: '', department: '', phone: '', email: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除教师"${row.name}"？`, '确认删除', { type: 'warning' })
    await teacherApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch {}
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await teacherApi.update(form.id, form)
    } else {
      // 新增时，创建一个不包含id的副本
      const { id, ...formData } = form
      await teacherApi.create(formData)
    }
    ElMessage.success(isEdit.value ? '更新成功' : '新增成功')
    dialogVisible.value = false
    loadData()
  } catch {}
}

onMounted(() => loadData())
</script>

<style lang="scss" scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
