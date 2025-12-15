<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><User /></el-icon>
      <span>学生管理</span>
    </div>

    <!-- 工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.keyword"
          placeholder="搜索学号或姓名"
          :prefix-icon="Search"
          clearable
          style="width: 250px"
          @input="handleSearch"
        />
        <el-select v-model="queryParams.gender" placeholder="性别筛选" clearable style="width: 120px" @change="loadData">
          <el-option label="全部" value="" />
          <el-option label="男" value="MALE" />
          <el-option label="女" value="FEMALE" />
        </el-select>
        <el-select v-model="queryParams.className" placeholder="班级筛选" clearable style="width: 150px" @change="loadData">
          <el-option v-for="c in classList" :key="c" :label="c" :value="c" />
        </el-select>
      </div>
      <div class="toolbar-right">
        <el-button :icon="Download" @click="handleExport">导出</el-button>
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增学生</el-button>
      </div>
    </div>

    <!-- 学生列表 -->
    <el-card shadow="hover">
      <el-table v-loading="loading" :data="studentList" stripe style="width: 100%">
        <el-table-column prop="studentId" label="学号" width="150" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="gender" label="性别" width="80">
          <template #default="{ row }">
            <el-tag :type="row.gender === 'MALE' ? 'primary' : 'danger'" size="small">
              {{ row.gender === 'MALE' ? '男' : '女' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="className" label="班级" width="150" />
        <el-table-column prop="grade" label="年级" width="100" />
        <el-table-column prop="major" label="专业" min-width="180" />
        <el-table-column prop="phone" label="手机号" width="140" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="viewDetail(row)">详情</el-button>
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="queryParams.current"
          v-model:page-size="queryParams.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadData"
          @current-change="loadData"
        />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学号" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="性别" prop="gender">
          <el-radio-group v-model="form.gender">
            <el-radio value="MALE">男</el-radio>
            <el-radio value="FEMALE">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="班级" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级" />
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="form.grade" placeholder="请输入年级" />
        </el-form-item>
        <el-form-item label="专业" prop="major">
          <el-input v-model="form.major" placeholder="请输入专业" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
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
import { useRouter } from 'vue-router'
import { studentApi, excelApi } from '@/api'
import { useSystemStore } from '@/stores/system'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Search, Download, Plus } from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const loading = ref(false)
const studentList = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()

const queryParams = reactive({
  current: 1,
  size: 20,
  keyword: '',
  gender: '',
  className: ''
})

const form = reactive({
  studentId: '',
  name: '',
  gender: 'MALE',
  className: '',
  grade: '',
  major: '',
  phone: '',
  email: ''
})

const rules = {
  studentId: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }]
}

const dialogTitle = computed(() => isEdit.value ? '编辑学生' : '新增学生')

// 班级列表（从已有数据提取）
const classList = computed(() => {
  const classes = new Set(studentList.value.map(s => s.className).filter(Boolean))
  return Array.from(classes)
})

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const res = await studentApi.getPage(queryParams)
    studentList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('加载学生数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 搜索
const handleSearch = () => {
  queryParams.current = 1
  loadData()
}

// 查看详情
const viewDetail = (row) => {
  router.push(`/students/${row.id}`)
}

// 新增
const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

// 删除
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除学生"${row.name}"吗？`, '确认删除', {
      type: 'warning'
    })
    await studentApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
    systemStore.loadOverview() // 刷新首页概览数据
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除失败:', error)
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (isEdit.value) {
      await studentApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      // 新增时，创建一个不包含id的副本
      const { id, ...formData } = form
      await studentApi.create(formData)
      ElMessage.success('新增成功')
      systemStore.loadOverview() // 刷新首页概览数据
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 导出
const handleExport = () => {
  const url = excelApi.exportStudents({
    className: queryParams.className,
    grade: queryParams.grade
  })
  window.open(url)
}

// 重置表单
const resetForm = () => {
  Object.assign(form, {
    studentId: '',
    name: '',
    gender: 'MALE',
    className: '',
    grade: '',
    major: '',
    phone: '',
    email: ''
  })
  // 删除id字段（如果存在）
  delete form.id
}

onMounted(() => {
  loadData()
})
</script>

<style lang="scss" scoped>
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
