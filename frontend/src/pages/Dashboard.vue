<template>
  <div class="dashboard">
    <section class="hero">
      <div class="hero-text">
        <h2>欢迎回来，{{ userInfo?.nickname || '管理员' }}</h2>
        <p>今天是 {{ currentDate }}，祝您工作顺利。</p>
        <div class="hero-actions">
          <el-button type="primary" @click="goTo('/rooms')">客房管理</el-button>
          <el-button @click="goTo('/bookings')">快速预订</el-button>
        </div>
      </div>
      <div class="hero-card">
        <div class="hero-card-title">入住率</div>
        <div class="hero-rate">{{ occupancyRateDisplay }}%</div>
        <el-progress :percentage="occupancyRateNumber" :stroke-width="10" status="success" />
        <div class="hero-meta">
          <span>已入住 {{ stats.occupiedRooms }}</span>
          <span>总房间 {{ stats.totalRooms }}</span>
        </div>
      </div>
    </section>

    <section class="stats">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="stat-grid">
            <div v-for="n in 4" :key="n" class="stat-card skeleton"></div>
          </div>
        </template>
        <template #default>
          <div class="stat-grid">
            <div class="stat-card">
              <div class="stat-label">客房总数</div>
              <div class="stat-value">{{ stats.totalRooms }}</div>
              <div class="stat-foot">空闲 {{ stats.availableRooms }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">已入住</div>
              <div class="stat-value">{{ stats.occupiedRooms }}</div>
              <div class="stat-foot">预订 {{ stats.bookedRooms }}</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">会员总数</div>
              <div class="stat-value">{{ stats.members }}</div>
              <div class="stat-foot">活跃等级占比高</div>
            </div>
            <div class="stat-card">
              <div class="stat-label">维护中</div>
              <div class="stat-value">{{ stats.maintenanceRooms }}</div>
              <div class="stat-foot">关注客房状态</div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </section>

    <section class="quick-actions">
      <h3>快捷入口</h3>
      <div class="action-grid">
        <div class="action-card" @click="goTo('/rooms')">
          <el-icon :size="28"><House /></el-icon>
          <span>客房管理</span>
        </div>
        <div class="action-card" @click="goTo('/bookings')">
          <el-icon :size="28"><Calendar /></el-icon>
          <span>预订管理</span>
        </div>
        <div class="action-card" @click="goTo('/checkins')">
          <el-icon :size="28"><Checked /></el-icon>
          <span>入住登记</span>
        </div>
        <div class="action-card" @click="goTo('/members')">
          <el-icon :size="28"><Avatar /></el-icon>
          <span>会员管理</span>
        </div>
        <div class="action-card" @click="goTo('/reports')">
          <el-icon :size="28"><TrendCharts /></el-icon>
          <span>财务报表</span>
        </div>
        <div class="action-card" @click="goTo('/settings')">
          <el-icon :size="28"><Setting /></el-icon>
          <span>系统设置</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Calendar, Checked, House, Avatar, TrendCharts, Setting } from '@element-plus/icons-vue'
import { formatDateWithWeekday } from '../utils/date'
import { getRoomStatistics } from '../api/room'
import { getMembers } from '../api/member'

const router = useRouter()
const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || '{}'))
const loading = ref(true)

const stats = reactive({
  totalRooms: 0,
  availableRooms: 0,
  bookedRooms: 0,
  occupiedRooms: 0,
  maintenanceRooms: 0,
  occupancyRate: 0,
  members: 0
})

const occupancyRateNumber = computed(() => Math.round(Number(stats.occupancyRate || 0)))
const occupancyRateDisplay = computed(() => occupancyRateNumber.value)

const currentDate = computed(() => formatDateWithWeekday(new Date()))

const fetchStats = async () => {
  loading.value = true
  try {
    const [roomRes, memberRes] = await Promise.all([
      getRoomStatistics(),
      getMembers({ page: 0, size: 1 })
    ])
    const roomData = roomRes.data || {}
    stats.totalRooms = roomData.total || 0
    stats.availableRooms = roomData.available || 0
    stats.bookedRooms = roomData.booked || 0
    stats.occupiedRooms = roomData.occupied || 0
    stats.maintenanceRooms = roomData.maintenance || 0
    stats.occupancyRate = Number(roomData.occupancyRate || 0)
    stats.members = memberRes.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const goTo = (path) => {
  router.push(path)
}

onMounted(fetchStats)
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.hero {
  display: grid;
  grid-template-columns: minmax(0, 1.4fr) minmax(280px, 0.8fr);
  gap: 20px;
}

.hero-text {
  background: #ffffff;
  padding: 24px;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
}

.hero-text h2 {
  font-size: 24px;
  margin-bottom: 8px;
}

.hero-text p {
  color: var(--text-secondary);
  margin-bottom: 18px;
}

.hero-actions {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.hero-card {
  background: linear-gradient(135deg, #ffffff 0%, #f2fbfa 100%);
  padding: 24px;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
}

.hero-card-title {
  color: var(--text-secondary);
  font-size: 13px;
  margin-bottom: 6px;
}

.hero-rate {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  color: var(--primary-color);
}

.hero-meta {
  display: flex;
  justify-content: space-between;
  margin-top: 12px;
  color: var(--text-secondary);
  font-size: 12px;
}

.stats {
  background: #ffffff;
  padding: 20px;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
}

.stat-card {
  padding: 18px;
  border-radius: 14px;
  background: #f8fbfb;
  border: 1px solid rgba(31, 122, 140, 0.08);
}

.stat-card.skeleton {
  height: 100px;
}

.stat-label {
  color: var(--text-secondary);
  font-size: 13px;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  margin: 6px 0;
}

.stat-foot {
  color: var(--text-secondary);
  font-size: 12px;
}

.quick-actions {
  background: #ffffff;
  padding: 20px;
  border-radius: 18px;
  box-shadow: var(--card-shadow);
}

.action-grid {
  margin-top: 12px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 16px;
}

.action-card {
  padding: 16px;
  border-radius: 14px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.action-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--card-shadow-light);
}

@media (max-width: 900px) {
  .hero {
    grid-template-columns: 1fr;
  }
}
</style>
