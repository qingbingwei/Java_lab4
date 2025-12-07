<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><School /></el-icon>
      <span>教学班管理</span>
    </div>

    <el-row :gutter="16">
      <el-col 
        v-for="tc in systemStore.teachingClasses" 
        :key="tc.classId"
        :xs="24" 
        :sm="12" 
        :md="8"
      >
        <el-card shadow="hover" class="class-card">
          <template #header>
            <div class="card-header">
              <span class="class-name">{{ tc.course.courseName }}</span>
              <el-tag size="small">{{ tc.classId }}</el-tag>
            </div>
          </template>

          <div class="class-info">
            <div class="info-row">
              <el-icon><UserFilled /></el-icon>
              <span>授课教师：{{ tc.teacher.name }}</span>
            </div>
            <div class="info-row">
              <el-icon><Calendar /></el-icon>
              <span>学期：{{ tc.semester }}</span>
            </div>
            <div class="info-row">
              <el-icon><User /></el-icon>
              <span>学生人数：{{ tc.students?.length || 0 }} / {{ tc.capacity }}</span>
              <el-progress
                :percentage="((tc.students?.length || 0) / tc.capacity * 100)"
                :color="getCapacityColor((tc.students?.length || 0) / tc.capacity)"
                style="flex: 1; margin-left: 12px"
              />
            </div>
          </div>

          <el-button type="primary" text @click="viewClassDetail(tc)">
            查看详情
          </el-button>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useSystemStore } from '@/stores/system'
import { ElMessage } from 'element-plus'
import { School, UserFilled, Calendar, User } from '@element-plus/icons-vue'

const systemStore = useSystemStore()

const getCapacityColor = (ratio) => {
  if (ratio >= 0.9) return '#f56c6c'
  if (ratio >= 0.7) return '#e6a23c'
  return '#67c23a'
}

const viewClassDetail = (tc) => {
  ElMessage.info(`查看教学班 ${tc.classId} 的详细信息`)
}
</script>

<style lang="scss" scoped>
.class-card {
  margin-bottom: 16px;
  transition: all 0.3s;

  &:hover {
    transform: translateY(-2px);
  }

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .class-name {
      font-weight: 600;
      font-size: 16px;
    }
  }

  .class-info {
    margin-bottom: 16px;

    .info-row {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 12px;
      font-size: 14px;
      color: var(--el-text-color-regular);

      .el-icon {
        color: var(--el-color-primary);
      }
    }
  }
}
</style>
