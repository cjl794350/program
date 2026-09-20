import { createRouter, createWebHistory } from 'vue-router'
import Layout from '../layout/Layout.vue'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/Dashboard.vue'),
        meta: { title: '数据大屏', icon: 'DataAnalysis' }
      },
      {
        path: 'student',
        name: 'Student',
        component: () => import('../views/StudentManage.vue'),
        meta: { title: '学生管理', icon: 'User' }
      },
      {
        path: 'teacher',
        name: 'Teacher',
        component: () => import('../views/TeacherManage.vue'),
        meta: { title: '教师管理', icon: 'Avatar' }
      },
      {
        path: 'course',
        name: 'Course',
        component: () => import('../views/CourseManage.vue'),
        meta: { title: '课程管理', icon: 'Notebook' }
      },
      {
        path: 'class',
        name: 'Class',
        component: () => import('../views/ClassManage.vue'),
        meta: { title: '班级管理', icon: 'OfficeBuilding' }
      },
      {
        path: 'score',
        name: 'Score',
        component: () => import('../views/ScoreManage.vue'),
        meta: { title: '成绩管理', icon: 'EditPen' }
      },
      {
        path: 'statistics',
        name: 'Statistics',
        component: () => import('../views/Statistics.vue'),
        meta: { title: '统计分析', icon: 'Histogram' }
      },
      {
        path: 'ml',
        name: 'ML',
        component: () => import('../views/MLAnalysis.vue'),
        meta: { title: '机器学习分析', icon: 'Cpu' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
