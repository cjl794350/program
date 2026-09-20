<template>
  <el-container class="layout">
    <el-aside width="220px" class="aside">
      <div class="logo">
        <el-icon :size="24" color="#409eff"><DataAnalysis /></el-icon>
        <span>教育数据分析系统</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#001529"
        text-color="#a6adb4"
        active-text-color="#ffffff"
        class="menu"
      >
        <el-menu-item v-for="item in menus" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">{{ $route.meta.title }}</div>
        <div class="header-right">
          <el-tag type="success" effect="dark" size="small">机器学习</el-tag>
          <el-dropdown @command="handleCommand">
            <span class="user">
              <el-icon><UserFilled /></el-icon>
              {{ nickname || '管理员' }}
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const nickname = localStorage.getItem('nickname')

const menus = [
  { path: '/dashboard', title: '数据大屏', icon: 'DataAnalysis' },
  { path: '/student', title: '学生管理', icon: 'User' },
  { path: '/teacher', title: '教师管理', icon: 'Avatar' },
  { path: '/course', title: '课程管理', icon: 'Notebook' },
  { path: '/class', title: '班级管理', icon: 'OfficeBuilding' },
  { path: '/score', title: '成绩管理', icon: 'EditPen' },
  { path: '/statistics', title: '统计分析', icon: 'Histogram' },
  { path: '/ml', title: '机器学习分析', icon: 'Cpu' }
]

function handleCommand(cmd) {
  if (cmd === 'logout') {
    localStorage.removeItem('token')
    localStorage.removeItem('nickname')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background: #001529;
}
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.menu {
  border-right: none;
}
.header {
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.user {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  color: #303133;
}
.main {
  background: #f0f2f5;
  padding: 16px;
  overflow: auto;
}
</style>
