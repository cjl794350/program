<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-select v-model="studentId" placeholder="按学生筛选" clearable filterable style="width: 180px" @change="load">
        <el-option v-for="s in studentList" :key="s.id" :label="s.name" :value="s.id" />
      </el-select>
      <el-select v-model="courseId" placeholder="按课程筛选" clearable filterable style="width: 180px" @change="load">
        <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
      </el-select>
      <div style="flex: 1"></div>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>录入成绩</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="studentName" label="学生" width="100" />
      <el-table-column prop="courseName" label="课程" min-width="140" />
      <el-table-column prop="regularScore" label="平时成绩" width="90" />
      <el-table-column prop="homeworkScore" label="作业成绩" width="90" />
      <el-table-column prop="attendance" label="出勤次数" width="90" />
      <el-table-column prop="finalScore" label="期末成绩" width="90" />
      <el-table-column label="总评成绩" width="90">
        <template #default="{ row }">
          <el-tag :type="row.totalScore >= 90 ? 'success' : row.totalScore >= 60 ? 'primary' : 'danger'">
            {{ row.totalScore }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="semester" label="学期" width="120" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
          <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      style="margin-top: 16px; justify-content: flex-end"
      background
      layout="total, prev, pager, next, sizes"
      :total="total"
      v-model:current-page="page"
      v-model:page-size="size"
      :page-sizes="[10, 20, 50, 100]"
      @current-change="load"
      @size-change="load"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑成绩' : '录入成绩'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" filterable style="width: 100%">
            <el-option v-for="s in studentList" :key="s.id" :label="s.name" :value="s.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="form.courseId" filterable style="width: 100%">
            <el-option v-for="c in courseList" :key="c.id" :label="c.courseName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="平时成绩"><el-input-number v-model="form.regularScore" :min="0" :max="100" /></el-form-item>
        <el-form-item label="作业成绩"><el-input-number v-model="form.homeworkScore" :min="0" :max="100" /></el-form-item>
        <el-form-item label="出勤次数"><el-input-number v-model="form.attendance" :min="0" :max="20" /></el-form-item>
        <el-form-item label="期末成绩"><el-input-number v-model="form.finalScore" :min="0" :max="100" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getScorePage, getStudentList, getCourseList, addScore, updateScore, deleteScore } from '../api'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const studentId = ref(null)
const courseId = ref(null)
const loading = ref(false)
const studentList = ref([])
const courseList = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: null, studentId: null, courseId: null, regularScore: 80, homeworkScore: 80, attendance: 18, finalScore: 80, semester: '2024-2025-1'
})
const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  courseId: [{ required: true, message: '请选择课程', trigger: 'change' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getScorePage({
      page: page.value,
      size: size.value,
      studentId: studentId.value,
      courseId: courseId.value
    })
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, {
    id: null, studentId: null, courseId: null, regularScore: 80, homeworkScore: 80, attendance: 18, finalScore: 80, semester: '2024-2025-1'
  })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  if (form.id) {
    await updateScore(form)
    ElMessage.success('修改成功')
  } else {
    await addScore(form)
    ElMessage.success('录入成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该成绩记录吗？', '提示', { type: 'warning' })
  await deleteScore(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  const [s, c] = await Promise.all([getStudentList(), getCourseList()])
  studentList.value = s.data
  courseList.value = c.data
  load()
})
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}
</style>
