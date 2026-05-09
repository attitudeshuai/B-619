<template>
  <el-dialog v-model="visible" title="办理退房" width="500px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="金额" prop="amount">
        <el-input-number v-model="form.amount" :min="0" :precision="2" class="full-width" />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentMethod">
        <el-select v-model="form.paymentMethod" placeholder="请选择支付方式" class="full-width">
          <el-option label="现金" value="现金" />
          <el-option label="微信" value="微信" />
          <el-option label="支付宝" value="支付宝" />
          <el-option label="银行卡" value="银行卡" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">确认退房</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { checkOut } from '../api/checkin'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  checkInId: { type: Number, default: null }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  amount: 0,
  paymentMethod: '',
  remark: ''
})

const rules = {
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  paymentMethod: [{ required: true, message: '请选择支付方式', trigger: 'change' }]
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
  if (!valid || !props.checkInId) return

  saving.value = true
  try {
    await checkOut(props.checkInId, {
      amount: form.amount,
      paymentMethod: form.paymentMethod,
      remark: form.remark
    })
    ElMessage.success('退房办理成功')
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
