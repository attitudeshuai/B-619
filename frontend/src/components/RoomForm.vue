<template>
  <el-dialog v-model="visible" :title="dialogTitle" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="房间号" prop="roomNumber">
        <el-input v-model="form.roomNumber" placeholder="如 101" />
      </el-form-item>
      <el-form-item label="房型" prop="roomType">
        <el-select v-model="form.roomType" placeholder="请选择房型" class="full-width">
          <el-option label="标准间" value="标准间" />
          <el-option label="大床房" value="大床房" />
          <el-option label="家庭房" value="家庭房" />
          <el-option label="豪华套房" value="豪华套房" />
        </el-select>
      </el-form-item>
      <el-form-item label="价格" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" class="full-width" />
      </el-form-item>
      <el-form-item label="楼层" prop="floor">
        <el-input-number v-model="form.floor" :min="1" class="full-width" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" :rows="3" placeholder="客房描述" />
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
import { createRoom, updateRoom } from '../api/room'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  room: { type: Object, default: null }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)

const form = reactive({
  id: null,
  roomNumber: '',
  roomType: '',
  price: 0,
  floor: 1,
  description: ''
})

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  roomType: [{ required: true, message: '请选择房型', trigger: 'change' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const dialogTitle = computed(() => (form.id ? '编辑客房' : '新增客房'))

watch(
  () => props.room,
  (room) => {
    if (room) {
      form.id = room.id
      form.roomNumber = room.roomNumber
      form.roomType = room.roomType
      form.price = Number(room.price || 0)
      form.floor = room.floor
      form.description = room.description || ''
    } else {
      form.id = null
      form.roomNumber = ''
      form.roomType = ''
      form.price = 0
      form.floor = 1
      form.description = ''
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
      await updateRoom(form.id, {
        roomNumber: form.roomNumber,
        roomType: form.roomType,
        price: form.price,
        floor: form.floor,
        description: form.description
      })
      ElMessage.success('客房已更新')
    } else {
      await createRoom({
        roomNumber: form.roomNumber,
        roomType: form.roomType,
        price: form.price,
        floor: form.floor,
        description: form.description
      })
      ElMessage.success('客房已创建')
    }
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
