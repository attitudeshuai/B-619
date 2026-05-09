<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>客房管理</h2>
        <p class="sub">维护客房信息与状态</p>
      </div>
      <div class="actions">
        <el-button type="primary" @click="openCreate">新增客房</el-button>
      </div>
    </div>

    <div class="filters">
      <el-select v-model="filters.status" placeholder="状态" clearable class="filter-item">
        <el-option label="空闲" value="空闲" />
        <el-option label="已预订" value="已预订" />
        <el-option label="已入住" value="已入住" />
        <el-option label="维护中" value="维护中" />
        <el-option label="停用" value="停用" />
      </el-select>
      <el-select v-model="filters.roomType" placeholder="房型" clearable class="filter-item">
        <el-option label="标准间" value="标准间" />
        <el-option label="大床房" value="大床房" />
        <el-option label="家庭房" value="家庭房" />
        <el-option label="豪华套房" value="豪华套房" />
      </el-select>
      <el-button @click="applyFilters">筛选</el-button>
      <el-button text @click="resetFilters">重置</el-button>
    </div>

    <el-card shadow="never">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-table"></div>
        </template>
        <template #default>
          <el-table :data="rooms" style="width: 100%">
            <el-table-column prop="roomNumber" label="房间号" width="100" />
            <el-table-column prop="roomType" label="房型" />
            <el-table-column prop="floor" label="楼层" width="80" />
            <el-table-column prop="price" label="价格" width="120">
              <template #default="scope">¥ {{ scope.row.price }}</template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120">
              <template #default="scope">
                <el-tag :type="statusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="220" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
                <el-button size="small" @click="openStatus(scope.row)">状态</el-button>
                <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
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

    <RoomForm v-model="formVisible" :room="currentRoom" @success="fetchRooms" />

    <el-dialog v-model="statusVisible" title="变更状态" width="420px" destroy-on-close>
      <el-select v-model="statusValue" placeholder="请选择状态" class="full-width">
        <el-option label="空闲" value="空闲" />
        <el-option label="已预订" value="已预订" />
        <el-option label="已入住" value="已入住" />
        <el-option label="维护中" value="维护中" />
        <el-option label="停用" value="停用" />
      </el-select>
      <template #footer>
        <el-button @click="statusVisible = false">取消</el-button>
        <el-button type="primary" :loading="statusSaving" @click="confirmStatus">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { changeRoomStatus, deleteRoom, getRooms } from '../api/room'
import RoomForm from '../components/RoomForm.vue'

const rooms = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = 10
const currentPage = ref(1)

const filters = reactive({
  status: '',
  roomType: ''
})

const formVisible = ref(false)
const currentRoom = ref(null)

const statusVisible = ref(false)
const statusValue = ref('')
const statusSaving = ref(false)
const statusRoom = ref(null)

const fetchRooms = async () => {
  loading.value = true
  try {
    const res = await getRooms({
      page: currentPage.value - 1,
      size: pageSize,
      status: filters.status || undefined,
      roomType: filters.roomType || undefined
    })
    rooms.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const applyFilters = () => {
  currentPage.value = 1
  fetchRooms()
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchRooms()
}

const resetFilters = () => {
  filters.status = ''
  filters.roomType = ''
  currentPage.value = 1
  fetchRooms()
}

const openCreate = () => {
  currentRoom.value = null
  formVisible.value = true
}

const openEdit = (room) => {
  currentRoom.value = room
  formVisible.value = true
}

const openStatus = (room) => {
  statusRoom.value = room
  statusValue.value = room.status
  statusVisible.value = true
}

const confirmStatus = async () => {
  if (!statusRoom.value) return
  statusSaving.value = true
  try {
    await changeRoomStatus(statusRoom.value.id, statusValue.value)
    ElMessage.success('状态已更新')
    statusVisible.value = false
    fetchRooms()
  } finally {
    statusSaving.value = false
  }
}

const handleDelete = async (room) => {
  try {
    await ElMessageBox.confirm('仅停用状态可删除，确认删除该客房？', '删除确认', {
      type: 'warning'
    })
    await deleteRoom(room.id)
    ElMessage.success('客房已删除')
    fetchRooms()
  } catch {
    // 取消删除
  }
}

const statusType = (status) => {
  if (status === '空闲') return 'success'
  if (status === '已预订') return 'warning'
  if (status === '已入住') return ''
  if (status === '维护中') return 'info'
  return 'danger'
}

onMounted(fetchRooms)
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

.page-header h2 {
  margin: 0;
  font-size: 20px;
}

.sub {
  margin: 4px 0 0;
  color: var(--text-secondary);
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.filter-item {
  min-width: 160px;
}

.pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.skeleton-table {
  height: 240px;
}

.full-width {
  width: 100%;
}
</style>
