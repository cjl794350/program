<template>
  <el-card shadow="never">
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="按姓名/学号搜索" clearable style="width: 240px" @keyup.enter="load">
        <template #prefix><el-icon><Search /></el-icon></template>
      </el-input>
      <el-button type="primary" @click="load"><el-icon><Search /></el-icon>搜索</el-button>
      <div style="flex: 1"></div>
      <el-button type="primary" @click="openAdd"><el-icon><Plus /></el-icon>新增学生</el-button>
    </div>

    <el-table :data="records" border stripe v-loading="loading">
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="name" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="70" />
      <el-table-column prop="age" label="年龄" width="70" />
      <el-table-column label="班级" min-width="160">
        <template #default="{ row }">{{ className(row.classId) }}</template>
      </el-table-column>
      <el-table-column prop="phone" label="联系电话" width="130" />
      <el-table-column prop="email" label="邮箱" min-width="160" />
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
      :page-sizes="[10, 20, 50]"
      @current-change="load"
      @size-change="load"
    />

    <el-dialog v-model="dialogVisible" :title="form.id ? '编辑学生' : '新增学生'" width="520px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="学号" prop="studentNo">
          <el-input v-model="form.studentNo" />
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="form.gender">
            <el-radio label="男">男</el-radio>
            <el-radio label="女">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="年龄">
          <el-input-number v-model="form.age" :min="15" :max="30" />
        </el-form-item>
        <el-form-item label="班级">
          <el-select v-model="form.classId" placeholder="请选择班级" style="width: 100%">
            <el-option v-for="c in classList" :key="c.id" :label="c.className" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
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
import { getStudentPage, getClassList, addStudent, updateStudent, deleteStudent } from '../api'

const records = ref([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const classList = ref([])
const dialogVisible = ref(false)
const formRef = ref()
const form = reactive({
  id: null, studentNo: '', name: '', gender: '男', age: 20, classId: null, phone: '', email: '', enrollmentYear: '2024'
})
const rules = {
  studentNo: [{ required: true, message: '请输入学号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

function className(id) {
  const c = classList.value.find((x) => x.id === id)
  return c ? c.className : '-'
}

async function load() {
  loading.value = true
  try {
    const res = await getStudentPage({ page: page.value, size: size.value, keyword: keyword.value })
    records.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function openAdd() {
  Object.assign(form, { id: null, studentNo: '', name: '', gender: '男', age: 20, classId: null, phone: '', email: '' })
  dialogVisible.value = true
}

function openEdit(row) {
  Object.assign(form, row)
  dialogVisible.value = true
}

async function handleSave() {
  await formRef.value.validate()
  if (form.id) {
    await updateStudent(form)
    ElMessage.success('修改成功')
  } else {
    await addStudent(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  load()
}

async function handleDelete(row) {
  await ElMessageBox.confirm(`确定删除学生「${row.name}」吗？`, '提示', { type: 'warning' })
  await deleteStudent(row.id)
  ElMessage.success('删除成功')
  load()
}

onMounted(async () => {
  const res = await getClassList()
  classList.value = res.data
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
