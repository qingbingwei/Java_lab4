<template>
  <div class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '240px'" class="sidebar">
      <div class="logo">
        <el-icon class="logo-icon"><Management /></el-icon>
        <span v-if="!isCollapse" class="logo-text">成绩管理系统</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="sidebar-menu"
      >
        <el-menu-item 
          v-for="route in menuRoutes" 
          :key="route.path"
          :index="route.path"
        >
          <el-icon><component :is="route.meta.icon" /></el-icon>
          <template #title>{{ route.meta.title }}</template>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRoute.meta?.title">
              {{ currentRoute.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 数据状态 -->
          <el-tag v-if="systemStore.isInitialized" type="success" size="small">
            <el-icon><SuccessFilled /></el-icon>
            数据已加载
          </el-tag>
          <el-tag v-else type="info" size="small">
            <el-icon><WarningFilled /></el-icon>
            未初始化
          </el-tag>

          <!-- 主题切换 -->
          <el-switch
            v-model="systemStore.isDarkMode"
            inline-prompt
            :active-icon="Moon"
            :inactive-icon="Sunny"
            @change="systemStore.toggleTheme"
            style="margin: 0 16px;"
          />

          <!-- 初始化按钮 -->
          <el-button 
            type="primary" 
            :icon="RefreshRight"
            @click="handleInitData"
            size="small"
          >
            初始化数据
          </el-button>

          <!-- 用户信息 -->
          <el-dropdown>
            <div class="user-info">
              <el-avatar :size="32" :icon="UserFilled" />
              <span class="username">管理员</span>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :icon="User">个人中心</el-dropdown-item>
                <el-dropdown-item :icon="Setting">系统设置</el-dropdown-item>
                <el-dropdown-item divided :icon="SwitchButton">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useSystemStore } from '@/stores/system'
import { ElMessageBox, ElMessage } from 'element-plus'
import {
  Management,
  Fold,
  Expand,
  Moon,
  Sunny,
  RefreshRight,
  UserFilled,
  User,
  Setting,
  SwitchButton,
  SuccessFilled,
  WarningFilled
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const systemStore = useSystemStore()

const isCollapse = ref(false)
const currentRoute = computed(() => route)
const activeMenu = computed(() => route.path)

// 获取菜单路由（过滤掉hidden的路由）
const menuRoutes = computed(() => {
  return router.options.routes[0].children.filter(r => !r.meta?.hidden)
})

// 切换侧边栏
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 初始化数据
const handleInitData = async () => {
  try {
    await ElMessageBox.confirm(
      '初始化将生成新的学生、教师、课程和成绩数据。如果已有数据将被覆盖，是否继续？',
      '确认初始化',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    systemStore.initializeData()
    ElMessage.success('数据初始化成功！')
  } catch {
    // 用户取消
  }
}

// 监听路由变化
watch(
  () => route.path,
  () => {
    // 可以在这里添加路由切换的逻辑
  }
)
</script>

<style lang="scss" scoped>
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  background: var(--el-bg-color);
  border-right: 1px solid var(--el-border-color);
  transition: width 0.3s ease;
  overflow-x: hidden;

  .logo {
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 12px;
    padding: 0 20px;
    border-bottom: 1px solid var(--el-border-color);
    font-size: 18px;
    font-weight: 600;
    color: var(--el-color-primary);

    .logo-icon {
      font-size: 28px;
    }

    .logo-text {
      white-space: nowrap;
      overflow: hidden;
    }
  }

  .sidebar-menu {
    border: none;
    height: calc(100vh - 60px);
  }

  :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
  }
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 24px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);

  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .collapse-icon {
      font-size: 20px;
      cursor: pointer;
      transition: color 0.3s;

      &:hover {
        color: var(--el-color-primary);
      }
    }
  }

  .header-right {
    display: flex;
    align-items: center;
    gap: 12px;

    .user-info {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 4px 12px;
      border-radius: 20px;
      cursor: pointer;
      transition: background 0.3s;

      &:hover {
        background: var(--el-fill-color-light);
      }

      .username {
        font-size: 14px;
      }
    }
  }
}

.main-content {
  flex: 1;
  overflow-y: auto;
  background: var(--el-bg-color-page);
}

/* 响应式 */
@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    z-index: 1000;
  }

  .header {
    padding: 0 16px;

    .header-left {
      .el-breadcrumb {
        display: none;
      }
    }

    .header-right {
      .username {
        display: none;
      }
    }
  }
}
</style>
