<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>管理员管理</h2>
        <p class="sub">维护系统管理员账号</p>
      </div>
      <el-button type="primary" @click="openCreate">新增管理员</el-button>
    </div>

    <el-card shadow="never">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-table"></div>
        </template>
        <template #default>
          <el-table :data="admins" style="width: 100%">
            <el-table-column prop="username" label="用户名" />
            <el-table-column prop="nickname" label="昵称" />
            <el-table-column prop="createdAt" label="创建时间">
              <template #default="scope">{{ formatDateTime(scope.row.createdAt) }}</template>
            </el-table-column>
            <el-table-column label="操作" width="200" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
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

    <AdminForm v-model="formVisible" :admin="currentAdmin" @success="fetchAdmins" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteAdmin, getAdmins } from '../api/admin'
import { formatDateTime } from '../utils/date'
import AdminForm from '../components/AdminForm.vue'

const admins = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = 10
const currentPage = ref(1)

const formVisible = ref(false)
const currentAdmin = ref(null)

const fetchAdmins = async () => {
  loading.value = true
  try {
    const res = await getAdmins({ page: currentPage.value - 1, size: pageSize })
    admins.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  currentAdmin.value = null
  formVisible.value = true
}

const openEdit = (admin) => {
  currentAdmin.value = admin
  formVisible.value = true
}

const handleDelete = async (admin) => {
  try {
    await ElMessageBox.confirm(`确认删除管理员 ${admin.username}？`, '删除确认', { type: 'warning' })
    await deleteAdmin(admin.id)
    ElMessage.success('管理员已删除')
    fetchAdmins()
  } catch {
    // 取消删除
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchAdmins()
}

onMounted(fetchAdmins)
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
