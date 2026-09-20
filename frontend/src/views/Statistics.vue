<template>
  <div>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-card shadow="never" header="学生成绩分布">
          <div ref="distRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" header="各课程平均成绩与及格率">
          <div ref="courseRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card shadow="never" header="班级平均成绩对比">
          <div ref="classRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="never" header="各课程成绩雷达图">
          <div ref="radarRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="24">
        <el-card shadow="never" header="成绩特征相关性热力图">
          <div ref="corrRef" class="chart chart-lg"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { getScoreDistribution, getCourseAverage, getClassCompare, getRadar, getCorrelation } from '../api'

const distRef = ref()
const courseRef = ref()
const classRef = ref()
const radarRef = ref()
const corrRef = ref()
let charts = []

onMounted(async () => {
  const [dist, course, cls, radar, corr] = await Promise.all([
    getScoreDistribution(),
    getCourseAverage(),
    getClassCompare(),
    getRadar(),
    getCorrelation()
  ])

  charts.push(renderDistribution(distRef.value, dist.data))
  charts.push(renderCourse(courseRef.value, course.data))
  charts.push(renderClass(classRef.value, cls.data))
  charts.push(renderRadar(radarRef.value, radar.data))
  charts.push(renderHeatmap(corrRef.value, corr.data))
  window.addEventListener('resize', resizeAll)
})

function renderDistribution(el, data) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: data.map((d) => d.name), axisLabel: { interval: 0, rotate: 15 } },
    yAxis: { type: 'value', name: '人数' },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.value),
        barMaxWidth: 46,
        itemStyle: {
          borderRadius: [4, 4, 0, 0],
          color: (p) => ['#f56c6c', '#e6a23c', '#409eff', '#67c23a', '#8e44ad'][p.dataIndex]
        },
        label: { show: true, position: 'top' }
      }
    ]
  })
  return chart
}

function renderCourse(el, data) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['平均分', '及格率(%)'] },
    grid: { left: 40, right: 40, top: 40, bottom: 30 },
    xAxis: { type: 'category', data: data.map((d) => d.courseName), axisLabel: { interval: 0, rotate: 15 } },
    yAxis: [{ type: 'value', name: '平均分', min: 0, max: 100 }, { type: 'value', name: '及格率', min: 0, max: 100 }],
    series: [
      { name: '平均分', type: 'bar', data: data.map((d) => d.avg), barMaxWidth: 30, itemStyle: { color: '#409eff' } },
      { name: '及格率(%)', type: 'line', yAxisIndex: 1, data: data.map((d) => d.passRate), itemStyle: { color: '#67c23a' } }
    ]
  })
  return chart
}

function renderClass(el, data) {
  const chart = echarts.init(el)
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 40, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: data.map((d) => d.className), axisLabel: { interval: 0, rotate: 15 } },
    yAxis: { type: 'value', name: '平均分', min: 0, max: 100 },
    series: [
      {
        type: 'bar',
        data: data.map((d) => d.avg),
        barMaxWidth: 50,
        itemStyle: { color: '#67c23a', borderRadius: [4, 4, 0, 0] },
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
    series: [{ type: 'radar', data: [{ value: data.map((d) => d.avg), name: '各科平均分', areaStyle: { opacity: 0.3 } }] }]
  })
  return chart
}

function renderHeatmap(el, data) {
  const chart = echarts.init(el)
  const names = data.names
  const matrix = data.matrix
  const values = []
  for (let i = 0; i < names.length; i++) {
    for (let j = 0; j < names.length; j++) {
      values.push([j, i, matrix[i][j]])
    }
  }
  const max = Math.max(...matrix.flat())
  chart.setOption({
    tooltip: { position: 'top' },
    grid: { left: 80, right: 40, top: 20, bottom: 60 },
    xAxis: { type: 'category', data: names, splitArea: { show: true } },
    yAxis: { type: 'category', data: names, splitArea: { show: true } },
    visualMap: {
      min: 0,
      max: 1,
      calculable: true,
      orient: 'horizontal',
      left: 'center',
      bottom: 0,
      inRange: { color: ['#50a3ba', '#eac736', '#d94e5d'] }
    },
    series: [
      {
        type: 'heatmap',
        data: values,
        label: { show: true },
        emphasis: { itemStyle: { shadowBlur: 10, shadowColor: 'rgba(0,0,0,0.5)' } }
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
.chart {
  height: 320px;
}
.chart-lg {
  height: 340px;
}
</style>
