<template>
  <div class="page-container">
    <div class="page-title">
      <el-icon class="icon"><DataLine /></el-icon>
      <span>首页概览</span>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="24" class="stat-cards">
      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%)">
          <div class="stat-content">
            <div class="stat-label">学生总数</div>
            <div class="stat-value">{{ systemStore.studentCount }}</div>
            <div class="stat-desc">已注册学生</div>
          </div>
          <el-icon class="stat-icon"><User /></el-icon>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%)">
          <div class="stat-content">
            <div class="stat-label">教师总数</div>
            <div class="stat-value">{{ systemStore.teacherCount }}</div>
            <div class="stat-desc">在职教师</div>
          </div>
          <el-icon class="stat-icon"><UserFilled /></el-icon>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)">
          <div class="stat-content">
            <div class="stat-label">课程总数</div>
            <div class="stat-value">{{ systemStore.courseCount }}</div>
            <div class="stat-desc">开设课程</div>
          </div>
          <el-icon class="stat-icon"><Reading /></el-icon>
        </div>
      </el-col>

      <el-col :xs="24" :sm="12" :md="6">
        <div class="stat-card" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)">
          <div class="stat-content">
            <div class="stat-label">教学班数</div>
            <div class="stat-value">{{ systemStore.classCount }}</div>
            <div class="stat-desc">活跃班级</div>
          </div>
          <el-icon class="stat-icon"><School /></el-icon>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 -->
    <el-row :gutter="24" style="margin-top: 24px">
      <el-col :xs="24" :md="16">
        <el-card class="quick-access-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><Grid /></el-icon>
              <span>快捷入口</span>
            </div>
          </template>
          
          <el-row :gutter="16">
            <el-col 
              v-for="item in quickAccess" 
              :key="item.path"
              :xs="12" 
              :sm="8" 
              :md="6"
            >
              <div class="quick-item" @click="router.push(item.path)">
                <el-icon :size="32" :color="item.color">
                  <component :is="item.icon" />
                </el-icon>
                <div class="quick-label">{{ item.label }}</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>

      <el-col :xs="24" :md="8">
        <el-card class="info-card" shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><InfoFilled /></el-icon>
              <span>系统信息</span>
            </div>
          </template>
          
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">系统版本</span>
              <span class="info-value">v1.0.0</span>
            </div>
            <div class="info-item">
              <span class="info-label">数据状态</span>
              <el-tag :type="systemStore.isInitialized ? 'success' : 'info'" size="small">
                {{ systemStore.isInitialized ? '已初始化' : '未初始化' }}
              </el-tag>
            </div>
            <div class="info-item">
              <span class="info-label">当前学期</span>
              <span class="info-value">2023-2024-1</span>
            </div>
            <div class="info-item">
              <span class="info-label">主题模式</span>
              <el-tag :type="systemStore.isDarkMode ? 'info' : 'warning'" size="small">
                {{ systemStore.isDarkMode ? '深色' : '浅色' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 功能说明 -->
    <el-row :gutter="24" style="margin-top: 24px">
      <el-col :span="24">
        <el-card shadow="hover">
          <template #header>
            <div class="card-header">
              <el-icon><DocumentChecked /></el-icon>
              <span>功能说明</span>
            </div>
          </template>
          
          <el-collapse>
            <el-collapse-item title="📚 数据管理" name="1">
              <div class="feature-desc">
                <p><strong>学生管理：</strong>查看所有学生信息，支持搜索和筛选功能。</p>
                <p><strong>教师管理：</strong>管理教师信息，查看教师所授课程。</p>
                <p><strong>课程管理：</strong>查看课程列表，包含课程编号、名称、学分等信息。</p>
                <p><strong>教学班管理：</strong>管理教学班信息，查看班级学生名单。</p>
              </div>
            </el-collapse-item>

            <el-collapse-item title="✏️ 成绩管理" name="2">
              <div class="feature-desc">
                <p><strong>成绩录入：</strong>分阶段录入平时、期中、实验、期末成绩。</p>
                <p><strong>成绩查询：</strong>支持按学号、姓名、课程等条件查询成绩。</p>
                <p><strong>成绩排名：</strong>查看学生成绩排名，支持多种排序方式。</p>
              </div>
            </el-collapse-item>

            <el-collapse-item title="📊 统计分析" name="3">
              <div class="feature-desc">
                <p><strong>数据统计：</strong>提供成绩分布、平均分、及格率等统计数据。</p>
                <p><strong>图表展示：</strong>使用ECharts可视化展示成绩趋势和分布。</p>
                <p><strong>对比分析：</strong>支持不同课程、班级间的成绩对比。</p>
              </div>
            </el-collapse-item>

            <el-collapse-item title="🎯 选课管理" name="4">
              <div class="feature-desc">
                <p><strong>选课功能：</strong>查看学生选课情况，管理课程分配。</p>
                <p><strong>容量控制：</strong>自动检查教学班容量，避免超额选课。</p>
                <p><strong>冲突检测：</strong>防止学生重复选择同一课程。</p>
              </div>
            </el-collapse-item>
          </el-collapse>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import {
  DataLine,
  User,
  UserFilled,
  Reading,
  School,
  Grid,
  InfoFilled,
  DocumentChecked,
  EditPen,
  Search,
  TrendCharts,
  PieChart,
  List
} from '@element-plus/icons-vue'

const router = useRouter()
const systemStore = useSystemStore()

const quickAccess = [
  { label: '学生管理', path: '/students', icon: 'User', color: '#409eff' },
  { label: '教师管理', path: '/teachers', icon: 'UserFilled', color: '#67c23a' },
  { label: '课程管理', path: '/courses', icon: 'Reading', color: '#e6a23c' },
  { label: '教学班', path: '/teaching-classes', icon: 'School', color: '#f56c6c' },
  { label: '成绩录入', path: '/scores/entry', icon: 'EditPen', color: '#909399' },
  { label: '成绩查询', path: '/scores/query', icon: 'Search', color: '#409eff' },
  { label: '成绩排名', path: '/scores/ranking', icon: 'TrendCharts', color: '#67c23a' },
  { label: '统计分析', path: '/statistics', icon: 'PieChart', color: '#e6a23c' }
]
</script>

<style lang="scss" scoped>
.stat-cards {
  margin-bottom: 0;

  .stat-card {
    position: relative;
    padding: 24px;
    border-radius: 12px;
    color: white;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    cursor: pointer;
    overflow: hidden;
    margin-bottom: 24px;

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);
    }

    .stat-content {
      position: relative;
      z-index: 1;
    }

    .stat-label {
      font-size: 14px;
      opacity: 0.9;
      margin-bottom: 8px;
    }

    .stat-value {
      font-size: 36px;
      font-weight: bold;
      margin-bottom: 4px;
    }

    .stat-desc {
      font-size: 12px;
      opacity: 0.8;
    }

    .stat-icon {
      position: absolute;
      right: 20px;
      top: 50%;
      transform: translateY(-50%);
      font-size: 64px;
      opacity: 0.2;
    }
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.quick-access-card {
  .quick-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 20px;
    border-radius: 8px;
    background: var(--el-fill-color-light);
    cursor: pointer;
    transition: all 0.3s ease;
    margin-bottom: 16px;

    &:hover {
      background: var(--el-fill-color);
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .quick-label {
      margin-top: 12px;
      font-size: 14px;
      font-weight: 500;
      color: var(--el-text-color-primary);
    }
  }
}

.info-card {
  .info-list {
    .info-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 12px 0;
      border-bottom: 1px solid var(--el-border-color-lighter);

      &:last-child {
        border-bottom: none;
      }

      .info-label {
        color: var(--el-text-color-secondary);
        font-size: 14px;
      }

      .info-value {
        font-weight: 500;
        color: var(--el-text-color-primary);
      }
    }
  }
}

.feature-desc {
  p {
    margin: 8px 0;
    line-height: 1.6;
    color: var(--el-text-color-regular);

    strong {
      color: var(--el-color-primary);
    }
  }
}

/* 响应式 */
@media (max-width: 768px) {
  .stat-card {
    .stat-value {
      font-size: 28px;
    }

    .stat-icon {
      font-size: 48px;
    }
  }
}
</style>
