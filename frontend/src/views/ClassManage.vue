<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="按班级名称/专业搜索" clearable style="width: 240px" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="load">搜索</el-button>
      <div style="flex: 1"></div>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>新增班级</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="className" label="班级名称" min-width="200" />
      <el-table-column prop="grade" label="年级" width="110" />
      <el-table-column prop="major" label="专业" min-width="160" />
      <el-table-column prop="headTeacher" label="班主任" width="120" />
      <el-table-column prop="studentCount" label="学生人数" width="100" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑班级' : '新增班级'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="班级名称" prop="className"><el-input v-model="form.className" /></el-form-item>
        <el-form-item label="年级"><el-input v-model="form.grade" /></el-form-item>
        <el-form-item label="专业"><el-input v-model="form.major" /></el-form-item>
        <el-form-item label="班主任"><el-input v-model="form.headTeacher" /></el-form-item>
        <el-form-item label="学生人数"><el-input-number v-model="form.studentCount" :min="0" :max="200" /></el-form-item>
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
import { getClassPage, addClass, updateClass, deleteClass } from '../api'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ id: null, className: '', grade: '', major: '', headTeacher: '', studentCount: 0 })
const rules = {
  className: [{ required: true, message: '请输入班级名称', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getClassPage({ page: page.value, size: size.value, keyword: keyword.value })
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, { id: null, className: '', grade: '', major: '', headTeacher: '', studentCount: 0 })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  if (form.id) {
    await updateClass(form)
    ElMessage.success('修改成功')
  } else {
    await addClass(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除班级「${row.className}」吗？`, '提示', { type: 'warning' })
  await deleteClass(row.id)
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
