<template>
  <el-dialog v-model="visible" title="办理入住" width="520px" destroy-on-close>
    <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
      <el-form-item label="客房" prop="roomId">
        <el-select v-model="form.roomId" placeholder="请选择客房" filterable class="full-width">
          <el-option
            v-for="room in roomOptions"
            :key="room.id"
            :label="`${room.roomNumber} · ${room.roomType}`"
            :value="room.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="会员" prop="memberId">
        <el-select v-model="form.memberId" placeholder="请选择会员" filterable class="full-width">
          <el-option
            v-for="member in memberOptions"
            :key="member.id"
            :label="`${member.name} · ${member.phone}`"
            :value="member.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="预订ID" prop="bookingId">
        <el-input v-model="form.bookingId" placeholder="可选，关联预订" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="可选备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSubmit">提交</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createCheckIn } from '../api/checkin'
import { getAvailableRooms } from '../api/booking'
import { getMembers } from '../api/member'

const props = defineProps({
  modelValue: { type: Boolean, default: false }
})

const emit = defineEmits(['update:modelValue', 'success'])

const formRef = ref(null)
const saving = ref(false)
const roomOptions = ref([])
const memberOptions = ref([])

const form = reactive({
  roomId: null,
  memberId: null,
  bookingId: '',
  remark: ''
})

const rules = {
  roomId: [{ required: true, message: '请选择客房', trigger: 'change' }],
  memberId: [{ required: true, message: '请选择会员', trigger: 'change' }]
}

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const fetchOptions = async () => {
  const [roomsRes, membersRes] = await Promise.all([
    getAvailableRooms(),
    getMembers({ page: 0, size: 100 })
  ])
  roomOptions.value = roomsRes.data || []
  memberOptions.value = membersRes.data?.content || []
}

watch(
  () => visible.value,
  (val) => {
    if (val) {
      fetchOptions()
    }
  }
)

const handleClose = () => {
  visible.value = false
}

const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    await createCheckIn({
      roomId: form.roomId,
      memberId: form.memberId,
      bookingId: form.bookingId ? Number(form.bookingId) : null,
      remark: form.remark
    })
    ElMessage.success('入住已办理')
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
