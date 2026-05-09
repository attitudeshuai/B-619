<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" />
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="昵称" prop="nickname">
        <el-input v-model="form.nickname" placeholder="可选" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createAdmin, updateAdmin } from '../api/admin'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  admin: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  id: null,
  username: '',
  password: '',
  nickname: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => (form.id ? '编辑管理员' : '新增管理员'))

watch(
  () => props.admin,
  (admin) => {
    if (admin) {
      form.id = admin.id
      form.username = admin.username
      form.password = admin.password
      form.nickname = admin.nickname || ''
    } else {
      form.id = null
      form.username = ''
      form.password = ''
      form.nickname = ''
    }
  },
  { immediate: true }
)

const handleClose = () => {
  visible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (form.id) {
      await updateAdmin(form.id, {
        username: form.username,
        password: form.password,
        nickname: form.nickname
      })
      ElMessage.success('管理员已更新')
    } else {
      await createAdmin({
        username: form.username,
        password: form.password,
        nickname: form.nickname
      })
      ElMessage.success('管理员已创建')
    }
    emit('success')
    visible.value = false
  } finally {
    saving.value = false
  }
}
</script>
