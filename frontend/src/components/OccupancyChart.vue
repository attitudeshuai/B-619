<template>
  <div class="occupancy">
    <el-progress type="dashboard" :percentage="percentage" />
    <div class="occupancy-meta">
      <div>总房间 {{ data?.totalRooms || 0 }}</div>
      <div>已入住 {{ data?.occupiedRooms || 0 }}</div>
      <div>已预订 {{ data?.bookedRooms || 0 }}</div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  data: { type: Object, default: () => ({}) }
})

const percentage = computed(() => Math.round(Number(props.data?.occupancyRate || 0)))
</script>

<style scoped>
.occupancy {
  display: flex;
  gap: 20px;
  align-items: center;
  padding: 12px 0;
}

.occupancy-meta {
  display: flex;
  flex-direction: column;
  gap: 8px;
  color: var(--text-secondary);
}
</style>
