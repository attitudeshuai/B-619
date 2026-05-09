<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>财务报表</h2>
        <p class="sub">收入统计与入住率分析</p>
      </div>
      <el-button @click="handleExport">导出报表</el-button>
    </div>

    <el-card shadow="never" class="filter-card">
      <div class="filters">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          value-format="YYYY-MM-DD"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
        <el-select v-model="period" class="period-select">
          <el-option label="按日" value="day" />
          <el-option label="按周" value="week" />
          <el-option label="按月" value="month" />
        </el-select>
        <el-button type="primary" @click="fetchReports">查询</el-button>
      </div>
    </el-card>

    <div class="report-grid">
      <el-card shadow="never" class="report-card">
        <h3>收入趋势</h3>
        <el-skeleton :loading="loading" animated>
          <template #template>
            <div class="skeleton-chart"></div>
          </template>
          <template #default>
            <RevenueChart :data="revenueReport" />
          </template>
        </el-skeleton>
      </el-card>

      <el-card shadow="never" class="report-card">
        <h3>入住率</h3>
        <OccupancyChart :data="occupancy" />
      </el-card>
    </div>

    <el-card shadow="never" class="report-card">
      <h3>会员消费排行</h3>
      <el-table :data="memberReport" style="width: 100%">
        <el-table-column prop="memberName" label="会员" />
        <el-table-column prop="total" label="消费金额" width="160">
          <template #default="scope">¥ {{ scope.row.total }}</template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { exportReport, getMemberReport, getOccupancyReport, getRevenueReport } from '../api/report'
import RevenueChart from '../components/RevenueChart.vue'
import OccupancyChart from '../components/OccupancyChart.vue'

const dateRange = ref([])
const period = ref('day')
const loading = ref(false)

const revenueReport = ref([])
const occupancy = ref({
  totalRooms: 0,
  occupiedRooms: 0,
  bookedRooms: 0,
  occupancyRate: 0
})
const memberReport = ref([])

const normalizeDateRange = () => {
  if (!dateRange.value || dateRange.value.length !== 2) {
    const today = new Date()
    const start = new Date(today.getFullYear(), today.getMonth(), 1)
    const end = new Date(today.getFullYear(), today.getMonth() + 1, 0)
    dateRange.value = [
      start.toISOString().slice(0, 10),
      end.toISOString().slice(0, 10)
    ]
  }
}

const fetchReports = async () => {
  normalizeDateRange()
  loading.value = true
  try {
    const [revenueRes, occupancyRes, memberRes] = await Promise.all([
      getRevenueReport({ start: dateRange.value[0], end: dateRange.value[1], period: period.value }),
      getOccupancyReport(),
      getMemberReport({ start: dateRange.value[0], end: dateRange.value[1] })
    ])
    revenueReport.value = revenueRes.data || []
    occupancy.value = occupancyRes.data || occupancy.value
    memberReport.value = memberRes.data || []
  } finally {
    loading.value = false
  }
}

const handleExport = async () => {
  normalizeDateRange()
  const res = await exportReport({ start: dateRange.value[0], end: dateRange.value[1], period: period.value })
  const url = window.URL.createObjectURL(res)
  const link = document.createElement('a')
  link.href = url
  link.download = '收入报表.csv'
  link.click()
  window.URL.revokeObjectURL(url)
  ElMessage.success('报表已导出')
}

onMounted(fetchReports)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.sub {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.filter-card {
  background: #ffffff;
}

.filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.period-select {
  min-width: 120px;
}

.report-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 16px;
}

.report-card {
  background: #ffffff;
}

.skeleton-chart {
  height: 200px;
}

</style>
