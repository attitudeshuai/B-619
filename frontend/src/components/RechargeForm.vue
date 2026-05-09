<template>
  <el-dialog v-model="visible" title="会员充值" width="420px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="充值金额" prop="amount">
        <el-input-number v-model="form.amount" :min="1" :precision="2" class="full-width" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">确认充值</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { rechargeMember } from '../api/member'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  memberId: { type: Number, default: null }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  amount: 100
})

const rules = {
  amount: [{ required: true, message: '请输入充值金额', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const handleClose = () => {
  visible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid || !props.memberId) return

  saving.value = true
  try {
    await rechargeMember(props.memberId, form.amount)
    ElMessage.success('充值成功')
    emit('success')
    visible.value = false
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.full-width {
  width: 100%;
}
</style>
