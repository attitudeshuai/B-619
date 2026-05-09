<template>
  <div class="login-container">
    <!-- 背景装饰 -->
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
      <div class="circle circle-3"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- Logo 区域 -->
      <div class="login-header">
        <div class="logo-icon">
          <el-icon :size="32"><OfficeBuilding /></el-icon>
        </div>
        <h1 class="login-title">酒店管理系统</h1>
        <p class="login-subtitle">酒店运营管理平台</p>
      </div>

      <!-- 登录表单 -->
      <el-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="User"
            clearable
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            clearable
          />
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            :loading="loading"
            class="login-button"
            @click="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 版权信息 -->
    <div class="footer">
      <p>酒店管理系统 &copy; {{ currentYear }}</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, OfficeBuilding } from '@element-plus/icons-vue'
import { login } from '../api/auth'

const router = useRouter()
const route = useRoute()

// 表单引用
const loginFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 当前年份
const currentYear = computed(() => new Date().getFullYear())

// 登录表单数据
const loginForm = reactive({
  username: '',
  password: ''
})

// 表单验证规则
const loginRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 4, max: 50, message: '密码长度在 4 到 50 个字符', trigger: 'blur' }
  ]
}

// 处理登录
const handleLogin = async () => {
  // 表单验证
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true

  try {
    const res = await login({
      username: loginForm.username,
      password: loginForm.password
    })

    // 保存 token 和用户信息
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify({
      username: res.data.username,
      nickname: res.data.nickname
    }))

    ElMessage.success('登录成功')

    // 跳转到目标页面
    const redirect = route.query.redirect || '/dashboard'
    router.push(redirect)
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #1f7a8c 0%, #5fc0c9 100%);
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  overflow: hidden;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 300px;
  height: 300px;
  top: -100px;
  left: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 200px;
  height: 200px;
  top: 50%;
  right: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 150px;
  height: 150px;
  bottom: -50px;
  left: 30%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-30px);
  }
}

/* 登录卡片 */
.login-card {
  position: relative;
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 20px;
  box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(10px);
  z-index: 10;
}

/* Logo 区域 */
.login-header {
  text-align: center;
  margin-bottom: 32px;
}

.logo-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  background: linear-gradient(135deg, #1f7a8c 0%, #5fc0c9 100%);
  border-radius: 16px;
  color: #fff;
  box-shadow: 0 10px 20px rgba(31, 122, 140, 0.25);
}

.login-title {
  font-size: 26px;
  font-weight: 700;
  color: #303133;
  margin: 0 0 8px 0;
  letter-spacing: 1px;
}

.login-subtitle {
  font-size: 13px;
  color: #909399;
  margin: 0;
  letter-spacing: 2px;
  text-transform: uppercase;
}

/* 登录表单 */
.login-form {
  margin-top: 24px;
}

.login-form :deep(.el-input__wrapper) {
  padding: 8px 15px;
  border-radius: 10px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #1f7a8c inset;
}

.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 10px;
  background: linear-gradient(135deg, #1f7a8c 0%, #5fc0c9 100%);
  border: none;
  transition: all 0.3s;
}

.login-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 20px rgba(31, 122, 140, 0.25);
}

/* 提示信息 */
.login-tips {
  margin-top: 24px;
  text-align: center;
  font-size: 13px;
  color: #909399;
}

.login-tips :deep(.el-divider__text) {
  background-color: #fff;
  color: #909399;
  font-size: 12px;
}

.login-tips code {
  padding: 3px 8px;
  background: #f5f7fa;
  border-radius: 4px;
  font-family: 'Monaco', monospace;
  color: #1f7a8c;
}

/* 版权信息 */
.footer {
  position: absolute;
  bottom: 20px;
  text-align: center;
}

.footer p {
  margin: 0;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.7);
}

/* 响应式 */
@media (max-width: 480px) {
  .login-card {
    padding: 30px 24px;
    border-radius: 16px;
  }

  .login-title {
    font-size: 22px;
  }

  .logo-icon {
    width: 56px;
    height: 56px;
  }
}
</style>
