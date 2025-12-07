<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><Reading /></el-icon>
      <span>课程管理</span>
    </div>

    <el-row :gutter="24">
      <el-col 
        v-for="course in systemStore.courses" 
        :key="course.courseId"
        :xs="24" 
        :sm="12" 
        :md="8" 
        :lg="6"
      >
        <el-card shadow="hover" class="course-card">
          <div class="course-icon">
            <el-icon :size="48" color="#409eff"><Reading /></el-icon>
          </div>
          <div class="course-info">
            <h3>{{ course.courseName }}</h3>
            <p class="course-id">课程编号：{{ course.courseId }}</p>
            <div class="course-meta">
              <el-tag type="warning" size="small">
                <el-icon><Star /></el-icon>
                {{ course.credits }} 学分
              </el-tag>
              <el-tag type="info" size="small">
                <el-icon><School /></el-icon>
                {{ getClassCount(course.courseId) }} 个班级
              </el-tag>
            </div>
            <el-divider />
            <div class="course-stats">
              <div class="stat">
                <span class="label">选课人数</span>
                <span class="value">{{ getStudentCount(course.courseId) }}</span>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useSystemStore } from '@/stores/system'
import { Reading, Star, School } from '@element-plus/icons-vue'

const systemStore = useSystemStore()

const getClassCount = (courseId) => {
  return systemStore.teachingClasses.filter(tc => tc.course.courseId === courseId).length
}

const getStudentCount = (courseId) => {
  const classes = systemStore.teachingClasses.filter(tc => tc.course.courseId === courseId)
  return classes.reduce((sum, tc) => sum + (tc.students?.length || 0), 0)
}
</script>

<style lang="scss" scoped>
.course-card {
  margin-bottom: 24px;
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-4px);
  }

  .course-icon {
    text-align: center;
    margin-bottom: 16px;
  }

  .course-info {
    h3 {
      font-size: 18px;
      margin-bottom: 8px;
      color: var(--el-text-color-primary);
    }

    .course-id {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      margin-bottom: 12px;
    }

    .course-meta {
      display: flex;
      gap: 8px;
      margin-bottom: 16px;
    }

    .course-stats {
      .stat {
        display: flex;
        justify-content: space-between;
        margin-bottom: 8px;

        .label {
          color: var(--el-text-color-secondary);
          font-size: 14px;
        }

        .value {
          font-weight: 600;
          color: var(--el-color-primary);
        }
      }
    }
  }
}
</style>
