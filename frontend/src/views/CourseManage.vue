<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="按课程名称/编号搜索" clearable style="width: 240px" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="load">搜索</el-button>
      <div style="flex: 1"></div>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>新增课程</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="courseNo" label="课程编号" width="110" />
      <el-table-column prop="courseName" label="课程名称" min-width="160" />
      <el-table-column prop="credit" label="学分" width="80" />
      <el-table-column prop="teacherName" label="任课教师" width="120" />
      <el-table-column prop="semester" label="开课学期" width="130" />
      <el-table-column prop="courseType" label="类型" width="90" />
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
      layout="total, prev, pager, next"
      :total="total"
      v-model:current-page="page"
      v-model:page-size="size"
      @current-change="load"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑课程' : '新增课程'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="课程编号" prop="courseNo"><el-input v-model="form.courseNo" /></el-form-item>
        <el-form-item label="课程名称" prop="courseName"><el-input v-model="form.courseName" /></el-form-item>
        <el-form-item label="学分"><el-input-number v-model="form.credit" :min="0.5" :max="10" :step="0.5" /></el-form-item>
        <el-form-item label="任课教师"><el-input v-model="form.teacherName" /></el-form-item>
        <el-form-item label="开课学期"><el-input v-model="form.semester" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.courseType" style="width: 100%">
            <el-option label="必修" value="必修" />
            <el-option label="选修" value="选修" />
          </el-select>
        </el-form-item>
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
import { getCoursePage, addCourse, updateCourse, deleteCourse } from '../api'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ id: null, courseNo: '', courseName: '', credit: 3, teacherName: '', semester: '2024-2025-1', courseType: '必修' })
const rules = {
  courseNo: [{ required: true, message: '请输入课程编号', trigger: 'blur' }],
  courseName: [{ required: true, message: '请输入课程名称', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getCoursePage({ page: page.value, size: size.value, keyword: keyword.value })
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, { id: null, courseNo: '', courseName: '', credit: 3, teacherName: '', semester: '2024-2025-1', courseType: '必修' })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  if (form.id) {
    await updateCourse(form)
    ElMessage.success('修改成功')
  } else {
    await addCourse(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除课程「${row.courseName}」吗？`, '提示', { type: 'warning' })
  await deleteCourse(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(load)
</script>

<style scoped>
.toolbar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  align-items: center;
}
</style>
