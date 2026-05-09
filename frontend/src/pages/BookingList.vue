<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>预订管理</h2>
        <p class="sub">管理客户预订与确认入住</p>
      </div>
      <el-button type="primary" @click="openCreate">新建预订</el-button>
    </div>

    <el-card shadow="never">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-table"></div>
        </template>
        <template #default>
          <el-table :data="bookings" style="width: 100%">
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="roomId" label="房间ID" width="100" />
            <el-table-column prop="memberId" label="会员ID" width="100" />
            <el-table-column prop="checkInDate" label="入住日期">
              <template #default="scope">{{ formatDate(scope.row.checkInDate) }}</template>
            </el-table-column>
            <el-table-column prop="checkOutDate" label="退房日期">
              <template #default="scope">{{ formatDate(scope.row.checkOutDate) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="handleConfirm(scope.row)" :disabled="scope.row.status !== '已预订'">
                  确认入住
                </el-button>
                <el-button size="small" type="danger" @click="handleCancel(scope.row)">
                  取消
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div class="pagination">
            <el-pagination
              background
              layout="total, prev, pager, next"
              :total="total"
              :page-size="pageSize"
              @current-change="handlePageChange"
            />
          </div>
        </template>
      </el-skeleton>
    </el-card>

    <BookingForm v-model="formVisible" @success="fetchBookings" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { cancelBooking, confirmBooking, getBookings } from '../api/booking'
import { formatDate } from '../utils/date'
import BookingForm from '../components/BookingForm.vue'

const bookings = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = 10
const currentPage = ref(1)
const formVisible = ref(false)

const fetchBookings = async () => {
  loading.value = true
  try {
    const res = await getBookings({ page: currentPage.value - 1, size: pageSize })
    bookings.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  formVisible.value = true
}

const handleConfirm = async (booking) => {
  try {
    await ElMessageBox.confirm('确认将该预订转为入住？', '确认入住', { type: 'warning' })
    await confirmBooking(booking.id)
    ElMessage.success('已转为入住')
    fetchBookings()
  } catch {
    // 取消操作
  }
}

const handleCancel = async (booking) => {
  try {
    await ElMessageBox.confirm('确认取消该预订？', '取消预订', { type: 'warning' })
    await cancelBooking(booking.id)
    ElMessage.success('预订已取消')
    fetchBookings()
  } catch {
    // 取消操作
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchBookings()
}

const statusType = (status) => {
  if (status === '已预订') return 'warning'
  if (status === '已确认') return ''
  if (status === '已取消') return 'info'
  return 'success'
}

onMounted(fetchBookings)
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

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.skeleton-table {
  height: 240px;
}
</style>
