<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>入住登记</h2>
        <p class="sub">办理入住、续住与退房结算</p>
      </div>
      <el-button type="primary" @click="openCheckIn">办理入住</el-button>
    </div>

    <el-card shadow="never">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-table"></div>
        </template>
        <template #default>
          <el-table :data="checkIns" style="width: 100%">
            <el-table-column prop="id" label="编号" width="80" />
            <el-table-column prop="roomId" label="房间ID" width="100" />
            <el-table-column prop="memberId" label="会员ID" width="100" />
            <el-table-column prop="checkInTime" label="入住时间">
              <template #default="scope">{{ formatDateTime(scope.row.checkInTime) }}</template>
            </el-table-column>
            <el-table-column prop="checkOutTime" label="退房时间">
              <template #default="scope">{{ formatDateTime(scope.row.checkOutTime) }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="scope.row.status === '已入住' ? 'success' : 'info'">
                  {{ scope.row.status }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="openCheckOut(scope.row)" :disabled="scope.row.status !== '已入住'">
                  退房
                </el-button>
                <el-button size="small" @click="handleExtend(scope.row)" :disabled="scope.row.status !== '已入住'">
                  续住
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

    <CheckInForm v-model="checkInVisible" @success="fetchCheckIns" />
    <CheckOutForm v-model="checkOutVisible" :check-in-id="currentCheckInId" @success="fetchCheckIns" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { extendStay, getCheckIns } from '../api/checkin'
import { formatDateTime } from '../utils/date'
import CheckInForm from '../components/CheckInForm.vue'
import CheckOutForm from '../components/CheckOutForm.vue'

const checkIns = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = 10
const currentPage = ref(1)

const checkInVisible = ref(false)
const checkOutVisible = ref(false)
const currentCheckInId = ref(null)

const fetchCheckIns = async () => {
  loading.value = true
  try {
    const res = await getCheckIns({ page: currentPage.value - 1, size: pageSize })
    checkIns.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const openCheckIn = () => {
  checkInVisible.value = true
}

const openCheckOut = (row) => {
  currentCheckInId.value = row.id
  checkOutVisible.value = true
}

const handleExtend = async (row) => {
  try {
    const { value } = await ElMessageBox.prompt('请输入续住天数', '续住办理', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      inputPattern: /^\d+$/,
      inputErrorMessage: '请输入有效的数字'
    })
    await extendStay(row.id, { extendDays: Number(value) })
    ElMessage.success('续住已办理')
    fetchCheckIns()
  } catch {
    // 取消操作
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchCheckIns()
}

onMounted(fetchCheckIns)
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
