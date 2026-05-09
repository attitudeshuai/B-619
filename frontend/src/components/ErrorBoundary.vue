<template>
  <div class="error-boundary">
    <slot v-if="!hasError" />
    <div v-else class="error-fallback">
      <el-result icon="warning" title="页面出现异常" :sub-title="errorMessage">
        <template #extra>
          <el-button type="primary" @click="reloadPage">刷新页面</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onErrorCaptured } from 'vue'
import { ElMessage } from 'element-plus'

const hasError = ref(false)
const errorMessage = ref('系统出现异常，请稍后重试')

onErrorCaptured((error) => {
  hasError.value = true
  errorMessage.value = error?.message || '系统出现异常，请稍后重试'
  ElMessage.error('页面异常已被捕获')
  return false
})

const reloadPage = () => {
  window.location.reload()
}
</script>

<style scoped>
.error-boundary {
  min-height: 100vh;
}

.error-fallback {
  padding: 48px 24px;
}
</style>
