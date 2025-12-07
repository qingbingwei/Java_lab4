<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="login-box">
        <div class="login-header">
          <h1>学生成绩管理系统</h1>
          <p>Student Score Management System</p>
        </div>
        
        <el-form ref="formRef" :model="loginForm" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="请输入用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              class="login-btn"
              @click="handleLogin"
            >
              {{ loading ? '登录中...' : '登 录' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <div class="login-tips">
          <el-divider>默认账号</el-divider>
          <div class="tips-content">
            <p><el-tag type="danger">管理员</el-tag> admin / admin123</p>
            <p><el-tag type="warning">教师</el-tag> 教师编号 / 123456</p>
            <p><el-tag type="success">学生</el-tag> 学号 / 123456</p>
          </div>
          <el-button type="warning" size="small" @click="initUsers" style="margin-top: 12px;">
            首次使用？点击初始化账号
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { authApi } from '@/api'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, message: '密码长度不能少于4位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}

// 初始化用户账号
const initUsers = async () => {
  try {
    await authApi.initUsers()
    ElMessage.success('账号初始化成功！现在可以使用默认账号登录了')
  } catch (e) {
    console.error('初始化失败:', e)
    ElMessage.error('初始化失败，请确保后端服务已启动')
  }
}
</script>

<style lang="scss" scoped>
.login-container {
  width: 100%;
  height: 100vh;
  
  .login-bg {
    width: 100%;
    height: 100%;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    justify-content: center;
    align-items: center;
  }
  
  .login-box {
    width: 420px;
    padding: 40px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
    
    .login-header {
      text-align: center;
      margin-bottom: 30px;
      
      h1 {
        font-size: 26px;
        color: #333;
        margin-bottom: 8px;
      }
      
      p {
        font-size: 14px;
        color: #999;
      }
    }
    
    .login-form {
      .el-form-item {
        margin-bottom: 24px;
      }
      
      .login-btn {
        width: 100%;
        height: 44px;
        font-size: 16px;
      }
    }
    
    .login-tips {
      .el-divider {
        margin: 20px 0 15px;
      }
      
      .tips-content {
        p {
          margin: 8px 0;
          font-size: 13px;
          color: #666;
          
          .el-tag {
            margin-right: 8px;
          }
        }
      }
    }
  }
}
</style>
