<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="按姓名/工号搜索" clearable style="width: 240px" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="load">搜索</el-button>
      <div style="flex: 1"></div>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>新增教师</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="teacherNo" label="工号" width="120" />
      <el-table-column prop="name" label="姓名" width="120" />
      <el-table-column prop="gender" label="性别" width="80" />
      <el-table-column prop="title" label="职称" width="120" />
      <el-table-column prop="department" label="所属学院" min-width="200" />
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

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑教师' : '新增教师'" width="480px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="90px">
        <el-form-item label="工号" prop="teacherNo"><el-input v-model="form.teacherNo" /></el-form-item>
        <el-form-item label="姓名" prop="name"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="form.title" style="width: 100%">
            <el-option v-for="t in ['教授', '副教授', '讲师', '助教']" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属学院"><el-input v-model="form.department" /></el-form-item>
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
import { getTeacherPage, addTeacher, updateTeacher, deleteTeacher } from '../api'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({ id: null, teacherNo: '', name: '', gender: '男', title: '讲师', department: '' })
const rules = {
  teacherNo: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

async function load() {
  loading.value = true
  try {
    const res = await getTeacherPage({ page: page.value, size: size.value, keyword: keyword.value })
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, { id: null, teacherNo: '', name: '', gender: '男', title: '讲师', department: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  if (form.id) {
    await updateTeacher(form)
    ElMessage.success('修改成功')
  } else {
    await addTeacher(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除教师「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteTeacher(row.id)
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
