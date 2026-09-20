# 基于教育大数据与机器学习的教育数据可视化系统

本项目是一个前后端分离的 Java Web 实训项目，将教育大数据分析、机器学习与数据可视化相结合，实现了学生成绩预测（线性回归）、学生学业分层（K-Means 聚类）与学业预警等功能。

## 一、技术栈

| 层次 | 技术 |
|------|------|
| 后端 | Spring Boot 2.7、MyBatis-Plus 3.5、MySQL 8、JWT |
| 前端 | Vue 3、Vite、Element Plus、ECharts 5、Axios |
| 机器学习 | 纯 Java 手写：多元线性回归（梯度下降）、K-Means 聚类 |
| 开发工具 | IDEA、Maven、Node.js、Navicat |

## 二、目录结构

```
教育数据分析可视化系统/
├── backend/          # 后端 Spring Boot 工程
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/edu/analysis/
│       │   ├── controller/   # 控制层
│       │   ├── service/      # 业务层（含统计分析、机器学习分析）
│       │   ├── mapper/       # 数据访问层
│       │   ├── entity/       # 实体类
│       │   ├── ml/           # 机器学习算法（线性回归、K-Means）
│       │   ├── config/       # 配置（CORS、JWT、MyBatis-Plus）
│       │   ├── common/       # 公共类（统一返回、异常处理）
│       │   └── util/         # 工具类（JWT）
│       └── resources/application.yml
├── frontend/         # 前端 Vue3 工程
│   ├── package.json
│   ├── vite.config.js
│   ├── mock/server.js # 演示用 Mock 数据服务（无需数据库即可预览前端）
│   └── src/
│       ├── api/       # 接口封装
│       ├── router/    # 路由
│       ├── layout/    # 布局（侧边栏+顶部）
│       └── views/     # 页面（登录、数据大屏、管理、统计、机器学习）
└── sql/
    ├── schema.sql     # 数据库结构脚本（DDL）
    └── data.sql       # 种子数据脚本（DML）
```

## 三、环境要求

- JDK 1.8 及以上
- Maven 3.6+
- MySQL 5.7 / 8.0
- Node.js 16+（含 npm）
- IDEA（推荐）

## 四、运行步骤

### 1. 初始化数据库

使用 Navicat 或 MySQL 命令行依次执行：

```sql
-- 方式一：命令行
mysql -uroot -p < sql/schema.sql
mysql -uroot -p < sql/data.sql

-- 方式二：在 Navicat 中打开 schema.sql 和 data.sql，依次运行
```

执行完成后会自动创建数据库 `edu_analysis` 及 7 张数据表，并插入 30 名学生、6 门课程、6 名教师、180 条成绩记录等种子数据。

### 2. 修改数据库连接配置

打开 `backend/src/main/resources/application.yml`，修改数据库账号密码：

```yaml
spring:
  datasource:
    username: root      # 改为你的 MySQL 用户名
    password: root      # 改为你的 MySQL 密码
```

### 3. 启动后端

在 IDEA 中打开 `backend` 目录（作为 Maven 工程导入），等待依赖下载完成后，运行 `EduAnalysisApplication` 主类。

启动成功后会输出：

```
教育大数据分析与可视化系统 后端启动成功!
API 地址: http://localhost:8080/api
```

### 4. 启动前端

在 `frontend` 目录下打开终端，执行：

```bash
npm install       # 首次运行需安装依赖
npm run dev       # 启动开发服务器
```

浏览器访问 `http://localhost:5173`，使用默认账号登录：

```
账号：admin
密码：admin123
```

> 前端开发服务器已配置代理，`/api` 请求会自动转发到后端 `http://localhost:8080`。

## 五、无需数据库快速预览（可选）

如果暂时没有 MySQL 环境，可以只启动前端和内置的 Mock 数据服务：

```bash
# 终端 1：启动 Mock 数据服务（端口 8080）
cd frontend
node mock/server.js

# 终端 2：启动前端
npm run dev
```

访问 `http://localhost:5173`，登录时任意输入账号密码即可进入（Mock 服务不校验）。

## 六、主要功能

1. **基础数据管理**：学生、教师、课程、班级、成绩的增删改查、分页与条件搜索。
2. **数据统计分析**：成绩分布、各科平均与及格率、班级对比、雷达图、相关性热力图。
3. **机器学习分析**：
   - 期末成绩预测（多元线性回归，拟合优度 R²≈0.77）
   - 学生学业分层（K-Means 聚类，划分优秀/良好/需关注）
   - 学业预警（自动识别需关注学生并给出帮扶建议）
4. **数据大屏**：集中展示核心指标与多维度图表。

## 七、主要接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 登录（返回 JWT） |
| GET/POST/PUT/DELETE | /api/student | 学生管理 |
| GET/POST/PUT/DELETE | /api/teacher | 教师管理 |
| GET/POST/PUT/DELETE | /api/course | 课程管理 |
| GET/POST/PUT/DELETE | /api/class | 班级管理 |
| GET/POST/PUT/DELETE | /api/score | 成绩管理 |
| GET | /api/statistics/* | 统计分析（overview、scoreDistribution 等） |
| POST | /api/analysis/predict | 成绩预测 |
| POST | /api/analysis/cluster | 学生聚类 |
| GET | /api/analysis/warning | 学业预警 |

## 八、常见问题

- **端口被占用**：修改 `application.yml` 的 `server.port` 和 `vite.config.js` 的代理 target。
- **数据库连接失败**：确认 MySQL 已启动、账号密码正确、已执行 SQL 脚本。
- **依赖下载慢**：为 Maven 配置阿里云镜像仓库；npm 可设置国内镜像 `npm config set registry https://registry.npmmirror.com`。
