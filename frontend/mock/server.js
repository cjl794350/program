/**
 * 演示用 Mock 数据服务（无需数据库即可预览前端）
 * 使用方式: node mock/server.js   (默认端口 8080，与后端一致)
 * 前端开发服务器会把 /api 代理到本服务
 */
const http = require('http')

// ---------- 可复现随机数 ----------
let seed = 20240919
function rnd() {
  seed = (seed * 1103515245 + 12345) & 0x7fffffff
  return seed / 0x7fffffff
}
function ri(a, b) {
  return Math.floor(rnd() * (b - a + 1)) + a
}
function r2(a, b) {
  return rnd() * (b - a) + a
}
function clamp(v, a, b) {
  return v < a ? a : v > b ? b : v
}
function round2(v) {
  return Math.round(v * 100) / 100
}

// ---------- 生成数据 ----------
const classList = [
  { id: 1, className: '计算机科学与技术2401班', grade: '2024级', major: '计算机科学与技术', headTeacher: '王建国', studentCount: 10 },
  { id: 2, className: '计算机科学与技术2402班', grade: '2024级', major: '计算机科学与技术', headTeacher: '李秀兰', studentCount: 10 },
  { id: 3, className: '软件工程2401班', grade: '2024级', major: '软件工程', headTeacher: '张伟', studentCount: 10 }
]
const teacherList = [
  { id: 1, teacherNo: 'T001', name: '陈志强', gender: '男', title: '教授', department: '计算机学院' },
  { id: 2, teacherNo: 'T002', name: '刘敏', gender: '女', title: '副教授', department: '计算机学院' },
  { id: 3, teacherNo: 'T003', name: '赵磊', gender: '男', title: '讲师', department: '计算机学院' },
  { id: 4, teacherNo: 'T004', name: '孙丽', gender: '女', title: '副教授', department: '计算机学院' },
  { id: 5, teacherNo: 'T005', name: '周涛', gender: '男', title: '讲师', department: '计算机学院' },
  { id: 6, teacherNo: 'T006', name: '吴静', gender: '女', title: '教授', department: '计算机学院' }
]
const courseList = [
  { id: 1, courseNo: 'C001', courseName: 'Java程序设计', credit: 4, teacherName: '陈志强', semester: '2024-2025-1', courseType: '必修' },
  { id: 2, courseNo: 'C002', courseName: '数据结构', credit: 4, teacherName: '刘敏', semester: '2024-2025-1', courseType: '必修' },
  { id: 3, courseNo: 'C003', courseName: '数据库原理', credit: 3, teacherName: '赵磊', semester: '2024-2025-1', courseType: '必修' },
  { id: 4, courseNo: 'C004', courseName: 'Web前端开发', credit: 3, teacherName: '孙丽', semester: '2024-2025-1', courseType: '必修' },
  { id: 5, courseNo: 'C005', courseName: '计算机网络', credit: 3, teacherName: '周涛', semester: '2024-2025-1', courseType: '必修' },
  { id: 6, courseNo: 'C006', courseName: '操作系统', credit: 4, teacherName: '吴静', semester: '2024-2025-1', courseType: '必修' }
]

const names = ['张伟', '王芳', '李娜', '刘洋', '陈静', '杨帆', '赵敏', '黄磊', '周杰', '吴倩',
  '孙悦', '徐鹏', '胡婷', '朱婷', '高翔', '林峰', '何雪', '郭涛', '马丽', '罗成',
  '梁静', '宋佳', '郑浩', '谢娜', '韩雪', '唐磊', '冯军', '于洋', '董洁', '萧然']
const genders = ['男', '女', '女', '男', '女', '男', '女', '男', '男', '女', '女', '男', '女', '女', '男', '男', '女', '男', '女', '男', '女', '女', '男', '女', '女', '男', '男', '男', '女', '男']

const studentList = names.map((n, i) => ({
  id: i + 1,
  studentNo: '2024010' + (Math.floor(i / 10) + 1) + String((i % 10) + 1).padStart(2, '0'),
  name: n,
  gender: genders[i],
  age: ri(18, 21),
  classId: Math.floor(i / 10) + 1,
  phone: '138' + String(10000000 + ri(0, 9999999)),
  email: n + '@stu.edu.cn',
  enrollmentYear: '2024'
}))

function levelOf(i) {
  const r = (i * 7 + 3) % 10
  if (r === 0) return 0
  if (r >= 1 && r <= 4) return 1
  if (r >= 5 && r <= 7) return 2
  return 3
}
const scoreList = []
let sid = 1
for (let s = 0; s < studentList.length; s++) {
  const lv = levelOf(s)
  const base = [92, 82, 72, 60][lv]
  for (let c = 1; c <= 6; c++) {
    const ability = base + (rnd() * 14 - 7)
    const regular = Math.round(clamp(ability + (rnd() * 8 - 4), 55, 100))
    const homework = Math.round(clamp(ability + (rnd() * 8 - 4), 55, 100))
    const attendance = Math.round(clamp(20 - (3 - lv) - rnd() * 2, 10, 20))
    const final = Math.round(clamp(ability + (rnd() * 20 - 10), 40, 100))
    const total = round2(regular * 0.3 + homework * 0.2 + final * 0.5)
    scoreList.push({ id: sid++, studentId: s + 1, courseId: c, regularScore: round2(regular), homeworkScore: round2(homework), attendance, finalScore: round2(final), totalScore: total, semester: '2024-2025-1' })
  }
}

// ---------- 统计 ----------
function avg(arr) {
  return arr.length ? arr.reduce((a, b) => a + b, 0) / arr.length : 0
}
function groupByStudent() {
  const m = {}
  scoreList.forEach((s) => {
    ;(m[s.studentId] = m[s.studentId] || []).push(s)
  })
  return m
}
function overview() {
  const totals = scoreList.map((s) => s.totalScore)
  const pass = totals.filter((t) => t >= 60).length
  const excel = totals.filter((t) => t >= 90).length
  return {
    studentCount: studentList.length,
    teacherCount: teacherList.length,
    courseCount: courseList.length,
    classCount: classList.length,
    scoreCount: scoreList.length,
    avgScore: round2(avg(totals)),
    passRate: round2((pass / totals.length) * 100),
    excellentRate: round2((excel / totals.length) * 100)
  }
}
function scoreDistribution() {
  const byStudent = groupByStudent()
  const names = ['不及格(<60)', '及格(60-69)', '中等(70-79)', '良好(80-89)', '优秀(≥90)']
  const counts = [0, 0, 0, 0, 0]
  Object.values(byStudent).forEach((ts) => {
    const a = avg(ts.map((s) => s.totalScore))
    let idx = a < 60 ? 0 : a < 70 ? 1 : a < 80 ? 2 : a < 90 ? 3 : 4
    counts[idx]++
  })
  return names.map((name, i) => ({ name, value: counts[i] }))
}
function courseAverage() {
  const m = {}
  scoreList.forEach((s) => {
    ;(m[s.courseId] = m[s.courseId] || []).push(s.totalScore)
  })
  return courseList.map((c) => {
    const ts = m[c.id]
    return {
      courseName: c.courseName,
      avg: round2(avg(ts)),
      passRate: round2((ts.filter((t) => t >= 60).length / ts.length) * 100),
      max: round2(Math.max(...ts)),
      min: round2(Math.min(...ts)),
      count: ts.length
    }
  })
}
function classCompare() {
  const m = {}
  scoreList.forEach((s) => {
    const cid = studentList.find((st) => st.id === s.studentId).classId
    ;(m[cid] = m[cid] || []).push(s.totalScore)
  })
  return classList.map((c) => ({ className: c.className, avg: round2(avg(m[c.id])) }))
}
function radar() {
  return courseAverage().map((c) => ({ courseName: c.courseName, avg: c.avg }))
}
function pearson(a, b) {
  const ma = avg(a)
  const mb = avg(b)
  let cov = 0
  let va = 0
  let vb = 0
  for (let i = 0; i < a.length; i++) {
    cov += (a[i] - ma) * (b[i] - mb)
    va += (a[i] - ma) * (a[i] - ma)
    vb += (b[i] - mb) * (b[i] - mb)
  }
  return va === 0 || vb === 0 ? 0 : cov / Math.sqrt(va * vb)
}
function correlation() {
  const names = ['平时成绩', '作业成绩', '出勤', '期末成绩']
  const cols = scoreList.map((s) => [s.regularScore, s.homeworkScore, s.attendance, s.finalScore])
  const matrix = names.map((_, i) => names.map((_, j) => round2(pearson(cols.map((c) => c[i]), cols.map((c) => c[j])))))
  return { names, matrix }
}

// ---------- 机器学习 ----------
function linearRegression(X, y, lr, epochs) {
  const n = X.length
  const f = X[0].length
  const mean = new Array(f).fill(0)
  const std = new Array(f).fill(0)
  for (let j = 0; j < f; j++) {
    mean[j] = avg(X.map((r) => r[j]))
    std[j] = Math.sqrt(X.reduce((a, r) => a + (r[j] - mean[j]) * (r[j] - mean[j]), 0) / n)
    if (std[j] < 1e-9) std[j] = 1
  }
  const Xn = X.map((r) => r.map((v, j) => (v - mean[j]) / std[j]))
  const theta = new Array(f + 1).fill(0)
  const losses = []
  for (let iter = 0; iter < epochs; iter++) {
    const grad = new Array(f + 1).fill(0)
    let loss = 0
    for (let i = 0; i < n; i++) {
      let h = theta[0]
      for (let j = 0; j < f; j++) h += theta[j + 1] * Xn[i][j]
      const err = h - y[i]
      loss += err * err
      grad[0] += err
      for (let j = 0; j < f; j++) grad[j + 1] += err * Xn[i][j]
    }
    for (let j = 0; j <= f; j++) theta[j] -= (lr / n) * grad[j]
    if (iter % 10 === 0) losses.push(round2(loss / n))
  }
  function predict(x) {
    let h = theta[0]
    for (let j = 0; j < x.length; j++) h += theta[j + 1] * (x[j] - mean[j]) / std[j]
    return h
  }
  let ssRes = 0
  let ssTot = 0
  const ym = avg(y)
  for (let i = 0; i < n; i++) {
    ssRes += (y[i] - predict(X[i])) * (y[i] - predict(X[i]))
    ssTot += (y[i] - ym) * (y[i] - ym)
  }
  return { theta, losses, predict, r2: ssTot === 0 ? 0 : 1 - ssRes / ssTot }
}
function predictAnalysis() {
  const X = scoreList.map((s) => [s.regularScore, s.homeworkScore, s.attendance])
  const y = scoreList.map((s) => s.finalScore)
  const lr = linearRegression(X, y, 0.01, 2000)
  const byStudent = groupByStudent()
  const predictions = Object.entries(byStudent).map(([sid, ts]) => {
    const c = ts.length
    const avgFeat = [
      avg(ts.map((s) => s.regularScore)),
      avg(ts.map((s) => s.homeworkScore)),
      avg(ts.map((s) => s.attendance))
    ]
    const st = studentList.find((s) => s.id === Number(sid))
    return {
      name: st.name,
      actual: round2(avg(ts.map((s) => s.finalScore))),
      predicted: round2(lr.predict(avgFeat))
    }
  })
  return {
    theta: lr.theta,
    r2: round2(lr.r2),
    featureNames: ['平时成绩', '作业成绩', '出勤次数'],
    loss: lr.losses,
    predictions
  }
}
function kmeans(X, k, maxIter) {
  const n = X.length
  const f = X[0].length
  const centers = []
  for (let c = 0; c < k; c++) centers.push(X[Math.floor((c * n) / k)].slice())
  const labels = new Array(n).fill(0)
  let iterations = 0
  for (let iter = 0; iter < maxIter; iter++) {
    iterations = iter + 1
    let changed = false
    for (let i = 0; i < n; i++) {
      let best = 0
      let bestD = Infinity
      for (let c = 0; c < k; c++) {
        let d = 0
        for (let j = 0; j < f; j++) d += (X[i][j] - centers[c][j]) * (X[i][j] - centers[c][j])
        if (d < bestD) {
          bestD = d
          best = c
        }
      }
      if (labels[i] !== best) {
        labels[i] = best
        changed = true
      }
    }
    const sum = Array.from({ length: k }, () => new Array(f).fill(0))
    const cnt = new Array(k).fill(0)
    for (let i = 0; i < n; i++) {
      cnt[labels[i]]++
      for (let j = 0; j < f; j++) sum[labels[i]][j] += X[i][j]
    }
    for (let c = 0; c < k; c++) for (let j = 0; j < f; j++) centers[c][j] = cnt[c] > 0 ? sum[c][j] / cnt[c] : centers[c][j]
    if (!changed) break
  }
  return { labels, centers, iterations }
}
function clusterAnalysis() {
  const byStudent = groupByStudent()
  const ids = Object.keys(byStudent)
  const X = ids.map((sid) => {
    const ts = byStudent[sid]
    return [
      avg(ts.map((s) => s.totalScore)),
      avg(ts.map((s) => s.attendance)) / 20,
      avg(ts.map((s) => s.homeworkScore))
    ]
  })
  // z-score 归一化
  const f = 3
  const mean = new Array(f).fill(0)
  const std = new Array(f).fill(0)
  for (let j = 0; j < f; j++) {
    mean[j] = avg(X.map((r) => r[j]))
    std[j] = Math.sqrt(X.reduce((a, r) => a + (r[j] - mean[j]) * (r[j] - mean[j]), 0) / X.length) || 1
  }
  const Xn = X.map((r) => r.map((v, j) => (v - mean[j]) / std[j]))
  const cr = kmeans(Xn, 3, 100)
  const clusterAvg = [0, 0, 0]
  const clusterCnt = [0, 0, 0]
  ids.forEach((_, i) => {
    clusterAvg[cr.labels[i]] += X[i][0]
    clusterCnt[cr.labels[i]]++
  })
  const order = [0, 1, 2].sort((a, b) => clusterAvg[b] / clusterCnt[b] - clusterAvg[a] / clusterCnt[a])
  const rankOf = [0, 0, 0]
  order.forEach((orig, r) => (rankOf[orig] = r))
  const levelNames = ['优秀', '良好', '需关注']
  const students = ids.map((sid, i) => {
    const st = studentList.find((s) => s.id === Number(sid))
    return { name: st.name, avgScore: round2(X[i][0]), attendanceRate: round2(X[i][1] * 100), cluster: rankOf[cr.labels[i]] }
  })
  const clusters = order.map((orig, r) => ({
    id: r,
    label: levelNames[r],
    count: clusterCnt[orig],
    avgScore: round2(clusterAvg[orig] / clusterCnt[orig])
  }))
  return { iterations: cr.iterations, clusters, students }
}
function warningAnalysis() {
  const clusterData = clusterAnalysis()
  const byStudent = groupByStudent()
  const warnings = Object.entries(byStudent)
    .map(([sid, ts]) => {
      const a = avg(ts.map((s) => s.totalScore))
      const st = studentList.find((s) => s.id === Number(sid))
      const cs = clusterData.students.find((x) => x.name === st.name)
      return {
        studentNo: st.studentNo,
        name: st.name,
        className: classList.find((c) => c.id === st.classId).className,
        avgScore: round2(a),
        cluster: cs ? cs.cluster : 2,
        risk: a < 60 ? '高风险' : '中风险',
        advice: a < 60 ? '建议重点关注，安排学业帮扶' : '成绩偏低，建议加强辅导'
      }
    })
    .filter((w) => w.avgScore < 70)
    .sort((a, b) => a.avgScore - b.avgScore)
  return { count: warnings.length, list: warnings }
}

// ---------- HTTP 服务 ----------
function ok(res, data) {
  res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8', 'Access-Control-Allow-Origin': '*' })
  res.end(JSON.stringify({ code: 200, message: '操作成功', data }))
}
function paginate(list, page, size, keyword, keys) {
  let filtered = list
  if (keyword) {
    filtered = list.filter((it) => keys.some((k) => String(it[k]).includes(keyword)))
  }
  const total = filtered.length
  const start = (page - 1) * size
  return { records: filtered.slice(start, start + size), total }
}

const server = http.createServer((req, res) => {
  const url = new URL(req.url, 'http://localhost')
  const path = url.pathname
  const method = req.method
  const params = Object.fromEntries(url.searchParams)

  let body = ''
  req.on('data', (c) => (body += c))
  req.on('end', () => {
    try {
      // 登录
      if (path === '/api/auth/login' && method === 'POST') {
        return ok(res, { token: 'mock-token-' + Date.now(), username: 'admin', nickname: '系统管理员', role: 'ADMIN' })
      }
      // 学生
      if (path === '/api/student/page') return ok(res, paginate(studentList, +params.page || 1, +params.size || 10, params.keyword, ['name', 'studentNo']))
      if (path === '/api/student/list') return ok(res, studentList)
      // 教师
      if (path === '/api/teacher/page') return ok(res, paginate(teacherList, +params.page || 1, +params.size || 10, params.keyword, ['name', 'teacherNo']))
      if (path === '/api/teacher/list') return ok(res, teacherList)
      // 课程
      if (path === '/api/course/page') return ok(res, paginate(courseList, +params.page || 1, +params.size || 10, params.keyword, ['courseName', 'courseNo']))
      if (path === '/api/course/list') return ok(res, courseList)
      // 班级
      if (path === '/api/class/page') return ok(res, paginate(classList, +params.page || 1, +params.size || 10, params.keyword, ['className', 'major']))
      if (path === '/api/class/list') return ok(res, classList)
      // 成绩
      if (path === '/api/score/page') {
        let filtered = scoreList
        if (params.studentId) filtered = filtered.filter((s) => s.studentId === +params.studentId)
        if (params.courseId) filtered = filtered.filter((s) => s.courseId === +params.courseId)
        const total = filtered.length
        const page = +params.page || 1
        const size = +params.size || 10
        const records = filtered.slice((page - 1) * size, page * size).map((s) => ({
          ...s,
          studentName: studentList.find((st) => st.id === s.studentId).name,
          courseName: courseList.find((c) => c.id === s.courseId).courseName
        }))
        return ok(res, { records, total })
      }
      // 统计
      if (path === '/api/statistics/overview') return ok(res, overview())
      if (path === '/api/statistics/scoreDistribution') return ok(res, scoreDistribution())
      if (path === '/api/statistics/courseAverage') return ok(res, courseAverage())
      if (path === '/api/statistics/classCompare') return ok(res, classCompare())
      if (path === '/api/statistics/radar') return ok(res, radar())
      if (path === '/api/statistics/correlation') return ok(res, correlation())
      // 机器学习
      if (path === '/api/analysis/predict') return ok(res, predictAnalysis())
      if (path === '/api/analysis/cluster') return ok(res, clusterAnalysis())
      if (path === '/api/analysis/warning') return ok(res, warningAnalysis())
      // 未匹配
      ok(res, { message: 'not found: ' + path })
    } catch (e) {
      res.writeHead(200, { 'Content-Type': 'application/json; charset=utf-8' })
      res.end(JSON.stringify({ code: 500, message: e.message, data: null }))
    }
  })
})

server.listen(8080, () => {
  console.log('Mock 数据服务已启动: http://localhost:8080')
})
