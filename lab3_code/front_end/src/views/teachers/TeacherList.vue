<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><UserFilled /></el-icon>
      <span>教师管理</span>
    </div>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="searchText"
          placeholder="搜索教师编号或姓名"
          :prefix-icon="Search"
          clearable
          style="width: 300px"
        />
      </div>
      <div class="toolbar-right">
        <el-button :icon="Download">导出</el-button>
        <el-button type="primary" :icon="Refresh">刷新</el-button>
      </div>
    </div>

    <el-card shadow="hover">
      <el-table :data="filteredTeachers" stripe>
        <el-table-column prop="teacherId" label="教师编号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column label="授课班级数" width="120">
          <template #default="{ row }">
            {{ row.teachingClasses?.length || 0 }} 个
          </template>
        </el-table-column>
        <el-table-column label="授课课程" min-width="300">
          <template #default="{ row }">
            <el-tag
              v-for="(tc, index) in row.teachingClasses?.slice(0, 3)"
              :key="index"
              size="small"
              style="margin-right: 4px"
            >
              {{ getCourseInfo(tc) }}
            </el-tag>
            <el-tag v-if="row.teachingClasses?.length > 3" size="small" type="info">
              +{{ row.teachingClasses.length - 3 }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="学生总数" width="100">
          <template #default="{ row }">
            {{ getTotalStudents(row) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useSystemStore } from '@/stores/system'
import { UserFilled, Search, Download, Refresh } from '@element-plus/icons-vue'

const systemStore = useSystemStore()
const searchText = ref('')

const filteredTeachers = computed(() => {
  let teachers = systemStore.teachers
  if (searchText.value) {
    const search = searchText.value.toLowerCase()
    teachers = teachers.filter(t =>
      t.teacherId.toLowerCase().includes(search) ||
      t.name.toLowerCase().includes(search)
    )
  }
  return teachers
})

const getCourseInfo = (classId) => {
  const tc = systemStore.getTeachingClass(classId)
  return tc?.course?.courseName || classId
}

const getTotalStudents = (teacher) => {
  if (!teacher.teachingClasses) return 0
  return teacher.teachingClasses.reduce((sum, classId) => {
    const tc = systemStore.getTeachingClass(classId)
    return sum + (tc?.students?.length || 0)
  }, 0)
}
</script>
