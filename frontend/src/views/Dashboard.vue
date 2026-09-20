<template>
  <div>
    <!-- 统计卡片 -->
    <el-row :gutter="16">
      <el-col :span="4" v-for="card in cards" :key="card.label">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-icon" :style="{ background: card.bg }">
            <el-icon :size="22" color="#fff"><component :is="card.icon" /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ card.value }}</div>
            <div class="stat-label">{{ card.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表 -->
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never" header="学生成绩分布">
          <div ref="distRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" header="班级平均成绩对比">
          <div ref="classRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never" header="各课程平均成绩">
          <div ref="courseRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" header="各课程成绩雷达图">
          <div ref="radarRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getOverview, getScoreDistribution, getClassCompare, getCourseAverage, getRadar } from '../api'

const cards = ref([])
const distRef = ref()
const classRef = ref()
const courseRef = ref()
const radarRef = ref()
let charts = []

onMounted(async () => {
  const [ov, dist, cls, course, radar] = await Promise.all([
    getOverview(),
    getScoreDistribution(),
    getClassCompare(),
    getCourseAverage(),
    getRadar()
  ])

  const o = ov.data
  cards.value = [
    { label: '学生总数', value: o.studentCount, icon: 'User', bg: '#409eff' },
    { label: '教师总数', value: o.teacherCount, icon: 'Avatar', bg: '#67c23a' },
    { label: '课程总数', value: o.courseCount, icon: 'Notebook', bg: '#e6a23c' },
    { label: '平均成绩', value: o.avgScore, icon: 'TrendCharts', bg: '#f56c6c' },
    { label: '及格率', value: o.passRate + '%', icon: 'CircleCheck', bg: '#909399' },
    { label: '优秀率', value: o.excellentRate + '%', icon: 'Trophy', bg: '#8e44ad' }
  ]

  charts.push(renderBar(distRef.value, dist.data, '人数', ['#409eff']))
  charts.push(renderBar(classRef.value, cls.data, '平均分', ['#67c23a'], 'className', 'avg'))
  charts.push(renderBar(courseRef.value, course.data, '平均分', ['#e6a23c'], 'courseName', 'avg'))
  charts.push(renderRadar(radarRef.value, radar.data))
  window.addEventListener('resize', resizeAll)
})

function renderBar(el, data, label, colors, nameKey = 'name', valueKey = 'value') {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: {
      type: 'category',
      data: data.map((d) => d[nameKey]),
      axisLabel: { interval: 0, rotate: 20 }
    },
    yAxis: { type: 'value', name: label },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d[valueKey]),
        barMaxWidth: 40,
        itemStyle: { color: colors[0], borderRadius: [4, 4, 0, 0] },
        label: { show: true, position: 'top' }
      }
    ]
  })
  return chart
}

function renderRadar(el, data) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: {},
    radar: {
      indicator: data.map((d) => ({ name: d.courseName, max: 100 })),
      radius: '65%'
    },
    series: [
      {
        type: 'radar',
        data: [
          {
            value: data.map((d) => d.avg),
            name: '各科平均分',
            areaStyle: { opacity: 0.3 }
          }
        ]
      }
    ]
  })
  return chart
}

function resizeAll() {
  charts.forEach((c) => c.resize())
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeAll)
  charts.forEach((c) => c.dispose())
})
</script>

<style scoped>
.stat-card {
  display: flex;
  align-items: center;
}
.stat-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  gap: 14px;
  width: 100%;
}
.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}
.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
}
.stat-label {
  font-size: 13px;
  color: #909399;
}
.chart {
  height: 320px;
}
</style>
