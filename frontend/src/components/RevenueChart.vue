<template>
  <div class="line-chart" ref="chartRef">
    <template v-if="data && data.length">
      <svg :viewBox="`0 0 ${width} ${height}`" class="chart-svg" role="img">
        <defs>
          <linearGradient id="lineGradient" x1="0" y1="0" x2="0" y2="1">
            <stop offset="0%" stop-color="#1f7a8c" />
            <stop offset="100%" stop-color="#9fd7d8" />
          </linearGradient>
        </defs>
        <path
          :d="smoothPath"
          fill="none"
          stroke="url(#lineGradient)"
          stroke-width="3"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        <g v-for="point in points" :key="point.key">
          <circle
            :cx="point.x"
            :cy="point.y"
            r="5"
            class="dot"
            @mouseenter="showTooltip(point)"
            @mouseleave="hideTooltip"
          />
        </g>
      </svg>
      <div class="x-axis">
        <span v-for="item in axisLabels" :key="item.key">{{ item.label }}</span>
      </div>
      <div
        v-if="tooltip.visible"
        class="tooltip"
        :class="{ down: tooltip.direction === 'down' }"
        :style="{ left: `${tooltip.x}px`, top: `${tooltip.y}px` }"
      >
        <div class="tooltip-title">{{ tooltip.title }}</div>
        <div class="tooltip-value">收入 ¥{{ tooltip.value }}</div>
      </div>
    </template>
    <el-empty v-else description="暂无收入数据" />
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'

const props = defineProps({
  data: { type: Array, default: () => [] }
})

const chartRef = ref(null)

const width = 720
const height = 220
const paddingX = 24
const paddingY = 24

const normalizedData = computed(() => {
  return props.data.map((item) => {
    const period = String(item.period || '')
    const isDaily = /^\d{4}-\d{2}-\d{2}$/.test(period)
    const isWeekly = /^\d{4}-W\d+$/.test(period)
    let label = period
    if (isDaily) {
      label = period.slice(5)
    } else if (isWeekly) {
      const [year, weekPart] = period.split('-W')
      label = `${year}第${weekPart}周`
    }
    const total = Number(item.total || 0)
    return {
      period,
      label,
      total,
      title: `${label} 收入 ¥${total}`
    }
  })
})

const maxValue = computed(() => {
  return Math.max(...normalizedData.value.map((item) => item.total), 0)
})

const points = computed(() => {
  const data = normalizedData.value
  if (!data.length) return []
  const step = data.length > 1 ? (width - paddingX * 2) / (data.length - 1) : 0
  return data.map((item, index) => {
    const x = paddingX + step * index
    const ratio = maxValue.value > 0 ? item.total / maxValue.value : 0
    const y = height - paddingY - ratio * (height - paddingY * 2)
    return { x, y, key: `${item.period}-${index}`, title: item.title, value: item.total }
  })
})

const smoothPath = computed(() => {
  const pts = points.value
  if (pts.length === 0) return ''
  if (pts.length === 1) {
    return `M ${pts[0].x} ${pts[0].y}`
  }
  const tension = 0.2
  let d = `M ${pts[0].x} ${pts[0].y}`
  for (let i = 0; i < pts.length - 1; i += 1) {
    const p0 = pts[i - 1] || pts[i]
    const p1 = pts[i]
    const p2 = pts[i + 1]
    const p3 = pts[i + 2] || p2
    const cp1x = p1.x + (p2.x - p0.x) * tension
    const cp1y = p1.y + (p2.y - p0.y) * tension
    const cp2x = p2.x - (p3.x - p1.x) * tension
    const cp2y = p2.y - (p3.y - p1.y) * tension
    d += ` C ${cp1x} ${cp1y}, ${cp2x} ${cp2y}, ${p2.x} ${p2.y}`
  }
  return d
})

const axisLabels = computed(() => {
  const data = normalizedData.value
  if (!data.length) return []
  const limit = 6
  const step = Math.max(1, Math.ceil(data.length / limit))
  return data.filter((_, index) => index % step === 0).map((item, index) => ({
    key: `${item.period}-${index}`,
    label: item.label
  }))
})

const tooltip = reactive({
  visible: false,
  x: 0,
  y: 0,
  title: '',
  value: 0,
  direction: 'up'
})

const showTooltip = (point) => {
  const containerWidth = chartRef.value?.clientWidth || width
  const containerHeight = chartRef.value?.clientHeight || height
  const safePadding = 70
  const leftBound = safePadding
  const rightBound = containerWidth - safePadding
  const x = Math.min(Math.max(point.x, leftBound), rightBound)
  const tooltipHeight = 56
  const upperSpace = point.y
  const lowerSpace = containerHeight - point.y
  const shouldDrop = upperSpace < tooltipHeight && lowerSpace > tooltipHeight
  const y = shouldDrop
    ? point.y + tooltipHeight / 2 + 8
    : Math.max(point.y - 16, 10)

  tooltip.visible = true
  tooltip.title = point.title
  tooltip.value = point.value
  tooltip.x = x
  tooltip.y = y
  tooltip.direction = shouldDrop ? 'down' : 'up'
}

const hideTooltip = () => {
  tooltip.visible = false
}
</script>

<style scoped>
.line-chart {
  min-height: 260px;
  position: relative;
}

.chart-svg {
  width: 100%;
  height: 220px;
}

.dot {
  fill: #1f7a8c;
  stroke: #ffffff;
  stroke-width: 2;
  cursor: pointer;
}

.x-axis {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(80px, 1fr));
  gap: 8px;
  margin-top: 6px;
  font-size: 12px;
  color: var(--text-secondary);
}

.tooltip {
  position: absolute;
  transform: translate(-50%, -100%);
  background: #ffffff;
  padding: 8px 12px;
  border-radius: 10px;
  box-shadow: var(--card-shadow-light);
  border: 1px solid rgba(31, 122, 140, 0.1);
  font-size: 12px;
  color: var(--text-primary);
  pointer-events: none;
  min-width: 120px;
  text-align: center;
}

.tooltip.down {
  transform: translate(-50%, 0);
}

.tooltip-title {
  color: var(--text-secondary);
  margin-bottom: 4px;
}

.tooltip-value {
  font-weight: 600;
  color: var(--primary-color);
}
</style>
