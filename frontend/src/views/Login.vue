<template>
  <div class="login-page">
    <div class="login-box">
      <div class="login-title">
        <el-icon :size="30" color="#409eff"><DataAnalysis /></el-icon>
        <h2>教育大数据分析与可视化系统</h2>
        <p>Educational Big Data Analysis & Visualization System</p>
      </div>
      <el-form :model="form" :rules="rules" ref="formRef" @keyup.enter="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" size="large">
            <template #prefix><el-icon><User /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" size="large" show-password>
            <template #prefix><el-icon><Lock /></el-icon></template>
          </el-input>
        </el-form-item>
        <el-button type="primary" size="large" style="width: 100%" :loading="loading" @click="handleLogin">
          登 录
        </el-button>
      </el-form>
      <div class="login-tip">默认账号：admin ／ 密码：admin123</div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api'

const router = useRouter()
const formRef = ref()
const loading = ref(false)
const form = reactive({ username: 'admin', password: 'admin123' })
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  await formRef.value.validate()
  loading.value = true
  try {
    const res = await login(form)
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('nickname', res.data.nickname)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
}
.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
}
.login-title {
  text-align: center;
  margin-bottom: 30px;
}
.login-title h2 {
  font-size: 20px;
  color: #303133;
  margin: 12px 0 6px;
}
.login-title p {
  font-size: 12px;
  color: #909399;
}
.login-tip {
  margin-top: 16px;
  text-align: center;
  font-size: 12px;
  color: #c0c4cc;
}
</style>
