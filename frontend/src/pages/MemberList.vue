<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>会员管理</h2>
        <p class="sub">会员信息与充值积分</p>
      </div>
      <el-button type="primary" @click="openCreate">新增会员</el-button>
    </div>

    <el-card shadow="never">
      <el-skeleton :loading="loading" animated>
        <template #template>
          <div class="skeleton-table"></div>
        </template>
        <template #default>
          <el-table :data="members" style="width: 100%">
            <el-table-column prop="name" label="姓名" />
            <el-table-column prop="phone" label="手机号" />
            <el-table-column prop="level" label="等级" width="120" />
            <el-table-column prop="balance" label="余额" width="120">
              <template #default="scope">¥ {{ scope.row.balance }}</template>
            </el-table-column>
            <el-table-column prop="points" label="积分" width="100" />
            <el-table-column label="操作" width="240" fixed="right">
              <template #default="scope">
                <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
                <el-button size="small" @click="openRecharge(scope.row)">充值</el-button>
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

    <MemberForm v-model="formVisible" :member="currentMember" @success="fetchMembers" />
    <RechargeForm v-model="rechargeVisible" :member-id="currentMemberId" @success="fetchMembers" />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteMember, getMembers } from '../api/member'
import MemberForm from '../components/MemberForm.vue'
import RechargeForm from '../components/RechargeForm.vue'

const members = ref([])
const loading = ref(false)
const total = ref(0)
const pageSize = 10
const currentPage = ref(1)

const formVisible = ref(false)
const currentMember = ref(null)

const rechargeVisible = ref(false)
const currentMemberId = ref(null)

const fetchMembers = async () => {
  loading.value = true
  try {
    const res = await getMembers({ page: currentPage.value - 1, size: pageSize })
    members.value = res.data?.content || []
    total.value = res.data?.totalElements || 0
  } finally {
    loading.value = false
  }
}

const openCreate = () => {
  currentMember.value = null
  formVisible.value = true
}

const openEdit = (member) => {
  currentMember.value = member
  formVisible.value = true
}

const openRecharge = (member) => {
  currentMemberId.value = member.id
  rechargeVisible.value = true
}

const handleDelete = async (member) => {
  try {
    await ElMessageBox.confirm(`确认删除会员 ${member.name}？`, '删除确认', { type: 'warning' })
    await deleteMember(member.id)
    ElMessage.success('会员已删除')
    fetchMembers()
  } catch {
    // 取消删除
  }
}

const handlePageChange = (page) => {
  currentPage.value = page
  fetchMembers()
}

onMounted(fetchMembers)
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
