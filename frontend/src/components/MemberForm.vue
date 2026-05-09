<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="姓名" prop="name">
        <el-input v-model="form.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="身份证" prop="idCard">
        <el-input v-model="form.idCard" placeholder="可选" />
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
import { createMember, updateMember } from '../api/member'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  member: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  id: null,
  name: '',
  phone: '',
  idCard: ''
})

const rules = {
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => (form.id ? '编辑会员' : '新增会员'))

watch(
  () => props.member,
  (member) => {
    if (member) {
      form.id = member.id
      form.name = member.name
      form.phone = member.phone
      form.idCard = member.idCard || ''
    } else {
      form.id = null
      form.name = ''
      form.phone = ''
      form.idCard = ''
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
      await updateMember(form.id, {
        name: form.name,
        phone: form.phone,
        idCard: form.idCard
      })
      ElMessage.success('会员已更新')
    } else {
      await createMember({
        name: form.name,
        phone: form.phone,
        idCard: form.idCard
      })
      ElMessage.success('会员已创建')
    }
    emit('success')
    visible.value = false
  } finally {
    saving.value = false
  }
}
</script>
