import request from './request'

// 认证
export const login = (data) => request.post('/auth/login', data)

// 学生
export const getStudentPage = (params) => request.get('/student/page', { params })
export const getStudentList = () => request.get('/student/list')
export const addStudent = (data) => request.post('/student', data)
export const updateStudent = (data) => request.put('/student', data)
export const deleteStudent = (id) => request.delete('/student/' + id)

// 教师
export const getTeacherPage = (params) => request.get('/teacher/page', { params })
export const getTeacherList = () => request.get('/teacher/list')
export const addTeacher = (data) => request.post('/teacher', data)
export const updateTeacher = (data) => request.put('/teacher', data)
export const deleteTeacher = (id) => request.delete('/teacher/' + id)

// 课程
export const getCoursePage = (params) => request.get('/course/page', { params })
export const getCourseList = () => request.get('/course/list')
export const addCourse = (data) => request.post('/course', data)
export const updateCourse = (data) => request.put('/course', data)
export const deleteCourse = (id) => request.delete('/course/' + id)

// 班级
export const getClassPage = (params) => request.get('/class/page', { params })
export const getClassList = () => request.get('/class/list')
export const addClass = (data) => request.post('/class', data)
export const updateClass = (data) => request.put('/class', data)
export const deleteClass = (id) => request.delete('/class/' + id)

// 成绩
export const getScorePage = (params) => request.get('/score/page', { params })
export const addScore = (data) => request.post('/score', data)
export const updateScore = (data) => request.put('/score', data)
export const deleteScore = (id) => request.delete('/score/' + id)

// 统计
export const getOverview = () => request.get('/statistics/overview')
export const getScoreDistribution = () => request.get('/statistics/scoreDistribution')
export const getCourseAverage = () => request.get('/statistics/courseAverage')
export const getClassCompare = () => request.get('/statistics/classCompare')
export const getRadar = () => request.get('/statistics/radar')
export const getCorrelation = () => request.get('/statistics/correlation')

// 机器学习
export const predict = () => request.post('/analysis/predict')
export const cluster = () => request.post('/analysis/cluster')
export const warning = () => request.get('/analysis/warning')
