<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '72px' : '240px'" class="sidebar">
      <div class="brand">
        <el-icon :size="24"><OfficeBuilding /></el-icon>
        <span v-if="!isCollapse">星河酒店</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        class="menu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/rooms">
          <el-icon><House /></el-icon>
          <span>客房管理</span>
        </el-menu-item>
        <el-menu-item index="/bookings">
          <el-icon><Calendar /></el-icon>
          <span>预订管理</span>
        </el-menu-item>
        <el-menu-item index="/checkins">
          <el-icon><Checked /></el-icon>
          <span>入住登记</span>
        </el-menu-item>
        <el-menu-item index="/members">
          <el-icon><Avatar /></el-icon>
          <span>会员管理</span>
        </el-menu-item>
        <el-menu-item index="/reports">
          <el-icon><TrendCharts /></el-icon>
          <span>财务报表</span>
        </el-menu-item>
        <el-menu-item index="/settings">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
        <el-menu-item index="/admins">
          <el-icon><UserFilled /></el-icon>
          <span>管理员管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <header class="header-inner">
          <div class="header-left">
            <el-button text class="collapse-btn" @click="toggleCollapse">
              <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
            </el-button>
            <div class="page-title">{{ currentTitle }}</div>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <div class="user-info">
                <el-avatar :size="36" class="user-avatar">
                  {{ userInfo?.nickname?.charAt(0) || 'A' }}
                </el-avatar>
                <span class="user-name">{{ userInfo?.nickname || '管理员' }}</span>
                <el-icon><ArrowDown /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item disabled>
                    <el-icon><User /></el-icon>
                    {{ userInfo?.username || 'admin' }}
                  </el-dropdown-item>
                  <el-dropdown-item divided command="logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </header>
      </el-header>

      <el-main class="main">
        <main class="main-inner">
          <router-view />
        </main>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  OfficeBuilding,
  DataLine,
  House,
  Calendar,
  Checked,
  Avatar,
  TrendCharts,
  Setting,
  UserFilled,
  Fold,
  Expand,
  ArrowDown,
  User,
  SwitchButton
} from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

const isCollapse = ref(false)
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))

const activeMenu = computed(() => route.path)

const titles = {
  '/dashboard': '仪表盘',
  '/rooms': '客房管理',
  '/bookings': '预订管理',
  '/checkins': '入住登记',
  '/members': '会员管理',
  '/reports': '财务报表',
  '/settings': '系统设置',
  '/admins': '管理员管理'
}

const currentTitle = computed(() => titles[route.path] || '酒店管理系统')

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = async (command) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      ElMessage.success('已退出登录')
      router.push('/login')
    } catch {
      // 取消操作
    }
  }
}
</script>

<style scoped>
.layout-container {
  min-height: 100vh;
  background: var(--bg-gradient);
}

.sidebar {
  background: #ffffff;
  border-right: 1px solid rgba(0, 0, 0, 0.04);
  box-shadow: var(--card-shadow-light);
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 18px 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.menu {
  border-right: none;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 64px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.04);
  padding: 0 20px;
}

.header-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.collapse-btn {
  font-size: 18px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 10px;
  transition: background 0.2s ease;
}

.user-info:hover {
  background: rgba(31, 122, 140, 0.08);
}

.user-avatar {
  background: linear-gradient(135deg, #1f7a8c 0%, #4da3b3 100%);
  color: #fff;
}

.main {
  padding: 24px;
}

.main-inner {
  min-height: calc(100vh - 112px);
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    z-index: 999;
    height: 100vh;
  }

  .main {
    padding: 16px;
  }
}
</style>
