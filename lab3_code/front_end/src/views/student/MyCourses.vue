<template>
  <div class="my-courses-container">
    <el-card class="page-header">
      <div class="header-content">
        <div class="title-section">
          <h2>我的课程</h2>
          <p class="subtitle">查看已选课程和可选课程</p>
        </div>
      </div>
    </el-card>

    <el-tabs v-model="activeTab" class="course-tabs">
      <!-- 已选课程 -->
      <el-tab-pane label="已选课程" name="enrolled">
        <el-card>
          <el-table :data="enrolledCourses" v-loading="loading" stripe>
            <el-table-column prop="classId" label="教学班号" width="120" />
            <el-table-column prop="courseName" label="课程名称" min-width="150" />
            <el-table-column prop="teacherName" label="授课教师" width="120" />
            <el-table-column prop="semester" label="学期" width="120" />
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column prop="schedule" label="上课安排" min-width="150" />
            <el-table-column prop="enrollTime" label="选课时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.enrollTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-popconfirm
                  title="确定要退选这门课程吗？"
                  @confirm="handleDrop(row)"
                >
                  <template #reference>
                    <el-button type="danger" size="small" link>退选</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 可选课程 -->
      <el-tab-pane label="选课中心" name="available">
        <el-card>
          <template #header>
            <div class="filter-section">
              <el-input
                v-model="searchKeyword"
                placeholder="搜索课程名称"
                style="width: 200px"
                clearable
                @clear="fetchAvailableCourses"
                @keyup.enter="fetchAvailableCourses"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
              </el-input>
              <el-select v-model="selectedSemester" placeholder="选择学期" style="width: 150px; margin-left: 10px" clearable>
                <el-option label="2023-2024-1" value="2023-2024-1" />
                <el-option label="2023-2024-2" value="2023-2024-2" />
                <el-option label="2024-2025-1" value="2024-2025-1" />
              </el-select>
              <el-button type="primary" style="margin-left: 10px" @click="fetchAvailableCourses">搜索</el-button>
            </div>
          </template>
          
          <el-table :data="availableCourses" v-loading="loadingAvailable" stripe>
            <el-table-column prop="classId" label="教学班号" width="120" />
            <el-table-column prop="courseName" label="课程名称" min-width="150" />
            <el-table-column prop="teacherName" label="授课教师" width="120" />
            <el-table-column prop="semester" label="学期" width="120" />
            <el-table-column prop="credit" label="学分" width="80" align="center" />
            <el-table-column label="容量" width="120" align="center">
              <template #default="{ row }">
                <el-tag :type="row.currentSize >= row.capacity ? 'danger' : 'success'">
                  {{ row.currentSize || 0 }} / {{ row.capacity }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="description" label="课程描述" min-width="200" show-overflow-tooltip />
            <el-table-column label="操作" width="100" fixed="right">
              <template #default="{ row }">
                <el-button
                  type="primary"
                  size="small"
                  :disabled="row.currentSize >= row.capacity || isEnrolled(row.id)"
                  @click="handleEnroll(row)"
                >
                  {{ isEnrolled(row.id) ? '已选' : '选课' }}
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { enrollmentApi, teachingClassApi } from '@/api'
import { useSystemStore } from '@/stores/system'

const systemStore = useSystemStore()
const activeTab = ref('enrolled')
const loading = ref(false)
const loadingAvailable = ref(false)

const enrolledCourses = ref([])
const availableCourses = ref([])
const searchKeyword = ref('')
const selectedSemester = ref('')

// 获取当前学生ID（实际项目中应从用户信息获取）
const currentStudentDbId = computed(() => {
  // 这里应该从登录用户信息中获取学生ID
  // 暂时使用模拟数据
  return systemStore.userInfo?.studentDbId || 1
})

// 已选课程的教学班ID列表
const enrolledClassIds = computed(() => {
  return enrolledCourses.value.map(c => c.teachingClassDbId)
})

// 判断是否已选
const isEnrolled = (classId) => {
  return enrolledClassIds.value.includes(classId)
}

// 获取已选课程
const fetchEnrolledCourses = async () => {
  loading.value = true
  try {
    const res = await enrollmentApi.getStudentEnrollments(currentStudentDbId.value)
    if (res.code === 200) {
      enrolledCourses.value = res.data || []
    }
  } catch (error) {
    console.error('获取已选课程失败:', error)
    ElMessage.error('获取已选课程失败')
  } finally {
    loading.value = false
  }
}

// 获取可选课程
const fetchAvailableCourses = async () => {
  loadingAvailable.value = true
  try {
    const res = await teachingClassApi.getList()
    if (res.code === 200) {
      let courses = res.data || []
      // 过滤搜索关键词
      if (searchKeyword.value) {
        courses = courses.filter(c => 
          c.courseName?.includes(searchKeyword.value) || 
          c.classId?.includes(searchKeyword.value)
        )
      }
      // 过滤学期
      if (selectedSemester.value) {
        courses = courses.filter(c => c.semester === selectedSemester.value)
      }
      availableCourses.value = courses
    }
  } catch (error) {
    console.error('获取可选课程失败:', error)
    ElMessage.error('获取可选课程失败')
  } finally {
    loadingAvailable.value = false
  }
}

// 选课
const handleEnroll = async (course) => {
  try {
    const res = await enrollmentApi.enroll({
      studentDbId: currentStudentDbId.value,
      teachingClassDbId: course.id
    })
    if (res.code === 200) {
      ElMessage.success('选课成功')
      fetchEnrolledCourses()
      fetchAvailableCourses()
    } else {
      ElMessage.error(res.message || '选课失败')
    }
  } catch (error) {
    console.error('选课失败:', error)
    ElMessage.error('选课失败')
  }
}

// 退选
const handleDrop = async (enrollment) => {
  try {
    const res = await enrollmentApi.drop(enrollment.id)
    if (res.code === 200) {
      ElMessage.success('退选成功')
      fetchEnrolledCourses()
      fetchAvailableCourses()
    } else {
      ElMessage.error(res.message || '退选失败')
    }
  } catch (error) {
    console.error('退选失败:', error)
    ElMessage.error('退选失败')
  }
}

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleString('zh-CN')
}

onMounted(() => {
  fetchEnrolledCourses()
  fetchAvailableCourses()
})
</script>

<style scoped lang="scss">
.my-courses-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  
  .header-content {
    .title-section {
      h2 {
        margin: 0 0 8px 0;
        font-size: 20px;
        color: #303133;
      }
      
      .subtitle {
        margin: 0;
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.course-tabs {
  :deep(.el-tabs__content) {
    padding: 0;
  }
}

.filter-section {
  display: flex;
  align-items: center;
}
</style>
