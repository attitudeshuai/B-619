<template>
  <div class="page">
    <div class="page-header">
      <div>
        <h2>系统设置</h2>
        <p class="sub">维护酒店基础配置</p>
      </div>
    </div>

    <el-card shadow="never">
      <el-table :data="configs" style="width: 100%">
        <el-table-column prop="configKey" label="配置项" />
        <el-table-column prop="configValue" label="配置值" />
        <el-table-column prop="description" label="说明" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" @click="openEdit(scope.row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="更新配置" width="520px" destroy-on-close>
      <el-form :model="editForm" label-width="90px">
        <el-form-item label="配置项">
          <el-input v-model="editForm.configKey" disabled />
        </el-form-item>
        <el-form-item label="配置值">
          <el-input v-model="editForm.configValue" />
        </el-form-item>
        <el-form-item label="说明">
          <el-input v-model="editForm.description" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getSettings, updateSetting } from '../api/settings'

const configs = ref([])
const editVisible = ref(false)
const saving = ref(false)

const editForm = reactive({
  configKey: '',
  configValue: '',
  description: ''
})

const fetchConfigs = async () => {
  const res = await getSettings()
  configs.value = res.data || []
}

const openEdit = (row) => {
  editForm.configKey = row.configKey
  editForm.configValue = row.configValue
  editForm.description = row.description || ''
  editVisible.value = true
}

const handleSave = async () => {
  saving.value = true
  try {
    await updateSetting(editForm.configKey, {
      configValue: editForm.configValue,
      description: editForm.description
    })
    ElMessage.success('配置已更新')
    editVisible.value = false
    fetchConfigs()
  } finally {
    saving.value = false
  }
}

onMounted(fetchConfigs)
</script>

<style scoped>
.page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.page-header h2 {
  margin: 0;
}

.sub {
  margin: 4px 0 0;
  color: var(--text-secondary);
}
</style>
