<template>
  <el-card shadow="never">
    <el-tabs v-model="activeTab">
      <!-- 成绩预测 -->
      <el-tab-pane label="成绩预测（线性回归）" name="predict">
        <div class="ml-toolbar">
          <div v-if="predictInfo.r2 != null" class="model-info">
            <el-tag type="success">R² = {{ predictInfo.r2 }}</el-tag>
            <span class="formula">期末成绩 = {{ predictInfo.intercept }} + {{ predictInfo.w1 }}×平时 + {{ predictInfo.w2 }}×作业 + {{ predictInfo.w3 }}×出勤</span>
          </div>
          <el-button type="primary" size="small" @click="runPredict"><el-icon><Refresh /></el-icon>重新训练</el-button>
        </div>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-card shadow="never" header="损失函数收敛曲线">
              <div ref="lossRef" class="chart"></div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card shadow="never" header="预测值 vs 实际值">
              <div ref="scatterRef" class="chart"></div>
            </el-card>
          </el-col>
        </el-row>
      </el-tab-pane>

      <!-- 学生聚类 -->
      <el-tab-pane label="学生分层聚类（K-Means）" name="cluster">
        <div class="ml-toolbar">
          <div class="cluster-cards">
            <div v-for="c in clusterInfo.clusters" :key="c.id" class="cluster-card" :style="{ borderColor: clusterColors[c.id] }">
              <div class="cc-label" :style="{ color: clusterColors[c.id] }">{{ c.label }}</div>
              <div class="cc-count">{{ c.count }} 人</div>
              <div class="cc-avg">平均 {{ c.avgScore }} 分</div>
            </div>
          </div>
          <el-button type="primary" size="small" @click="runCluster"><el-icon><Refresh /></el-icon>重新聚类</el-button>
        </div>
        <el-card shadow="never" header="学生聚类散点图（平均成绩 vs 出勤率）">
          <div ref="clusterRef" class="chart"></div>
        </el-card>
      </el-tab-pane>

      <!-- 学业预警 -->
      <el-tab-pane label="学业预警" name="warning">
        <div class="ml-toolbar">
          <div class="warn-title">
            共识别出 <el-tag type="danger">{{ warningInfo.count }}</el-tag> 名需关注学生
          </div>
          <el-button type="primary" size="small" @click="runWarning"><el-icon><Refresh /></el-icon>重新分析</el-button>
        </div>
        <el-table :data="warningInfo.list" border stripe>
          <el-table-column prop="studentNo" label="学号" width="130" />
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="className" label="班级" min-width="180" />
          <el-table-column prop="avgScore" label="平均成绩" width="100" />
          <el-table-column label="风险等级" width="110">
            <template #default="{ row }">
              <el-tag :type="row.risk === '高风险' ? 'danger' : 'warning'">{{ row.risk }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="advice" label="建议" min-width="200" />
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { predict, cluster, warning } from '../api'

const activeTab = ref('predict')
const lossRef = ref()
const scatterRef = ref()
const clusterRef = ref()
const clusterColors = ['#67c23a', '#409eff', '#f56c6c']
const clusterLabels = ['优秀', '良好', '需关注']
let charts = []

const predictInfo = reactive({ r2: null, intercept: 0, w1: 0, w2: 0, w3: 0, loss: [], predictions: [] })
const clusterInfo = reactive({ clusters: [], students: [] })
const warningInfo = reactive({ count: 0, list: [] })

onMounted(async () => {
  await runPredict()
  window.addEventListener('resize', resizeAll)
})

async function runPredict() {
  const res = await predict()
  const d = res.data
  predictInfo.r2 = d.r2
  const t = d.theta
  predictInfo.intercept = round2(t[0])
  predictInfo.w1 = round2(t[1])
  predictInfo.w2 = round2(t[2])
  predictInfo.w3 = round2(t[3])
  predictInfo.loss = d.loss
  predictInfo.predictions = d.predictions
  renderLoss(lossRef.value, d.loss)
  renderScatter(scatterRef.value, d.predictions)
}

async function runCluster() {
  const res = await cluster()
  const d = res.data
  clusterInfo.clusters = d.clusters
  clusterInfo.students = d.students
  renderCluster(clusterRef.value, d.students)
}

async function runWarning() {
  const res = await warning()
  warningInfo.count = res.data.count
  warningInfo.list = res.data.list
}

function renderLoss(el, loss) {
  if (charts[0]) charts[0].dispose()
  const chart = echarts.init(el)
  charts[0] = chart
  chart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 30, bottom: 30 },
    xAxis: { type: 'category', data: loss.map((_, i) => i * 10), name: '迭代次数' },
    yAxis: { type: 'value', name: '损失(MSE)' },
    series: [{ type: 'line', data: loss, smooth: true, showSymbol: false, itemStyle: { color: '#409eff' } }]
  })
}

function renderScatter(el, predictions) {
  if (charts[1]) charts[1].dispose()
  const chart = echarts.init(el)
  charts[1] = chart
  const data = predictions.map((p) => [p.actual, p.predicted])
  const diag = [[40, 40], [100, 100]]
  chart.setOption({
    tooltip: {
      trigger: 'item',
      formatter: (p) => `实际: ${p.value[0]}<br/>预测: ${p.value[1]}`
    },
    grid: { left: 50, right: 30, top: 30, bottom: 40 },
    xAxis: { type: 'value', name: '实际成绩', min: 40, max: 100 },
    yAxis: { type: 'value', name: '预测成绩', min: 40, max: 100 },
    series: [
      {
        type: 'scatter',
        data,
        symbolSize: 12,
        itemStyle: { color: '#409eff', opacity: 0.8 }
      },
      {
        type: 'line',
        data: diag,
        showSymbol: false,
        lineStyle: { type: 'dashed', color: '#f56c6c' }
      }
    ]
  })
}

function renderCluster(el, students) {
  if (charts[2]) charts[2].dispose()
  const chart = echarts.init(el)
  charts[2] = chart
  const series = [0, 1, 2].map((c) => ({
    name: clusterLabels[c],
    type: 'scatter',
    data: students.filter((s) => s.cluster === c).map((s) => [s.avgScore, s.attendanceRate]),
    symbolSize: 13,
    itemStyle: { color: clusterColors[c], opacity: 0.85 }
  }))
  chart.setOption({
    tooltip: { trigger: 'item' },
    legend: { data: clusterLabels },
    grid: { left: 50, right: 30, top: 40, bottom: 40 },
    xAxis: { type: 'value', name: '平均成绩', min: 40, max: 100 },
    yAxis: { type: 'value', name: '出勤率(%)', min: 50, max: 100 },
    series
  })
}

function round2(v) {
  return Math.round(v * 100) / 100
}

function resizeAll() {
  charts.forEach((c) => c && c.resize())
}

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeAll)
  charts.forEach((c) => c && c.dispose())
})
</script>

<style scoped>
.ml-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.model-info {
  display: flex;
  align-items: center;
  gap: 12px;
}
.formula {
  font-size: 14px;
  color: #606266;
  font-family: Consolas, monospace;
}
.cluster-cards {
  display: flex;
  gap: 16px;
}
.cluster-card {
  border: 2px solid #ddd;
  border-radius: 8px;
  padding: 10px 20px;
  text-align: center;
  min-width: 110px;
}
.cc-label {
  font-size: 16px;
  font-weight: 600;
}
.cc-count {
  font-size: 20px;
  font-weight: 700;
  color: #303133;
}
.cc-avg {
  font-size: 12px;
  color: #909399;
}
.warn-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}
.chart {
  height: 340px;
}
</style>
