# 学生成绩管理系统 - SpringBoot全栈版

## 项目概述

本项目是一个基于 SpringBoot + Vue3 的学生成绩管理系统，采用前后端分离架构，后端提供完整的RESTful API接口，前端使用Vue3 + Element Plus构建现代化管理界面。

## 技术栈

- **核心框架**: Spring Boot 3.2.0
- **ORM框架**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0+
- **连接池**: Druid 1.2.20
- **API文档**: Knife4j 4.3.0 (OpenAPI 3.0)
- **Excel处理**: EasyExcel 3.3.3
- **工具库**: Hutool 5.8.23
- **构建工具**: Maven 3.8+
- **JDK版本**: Java 17+

## 项目结构

```
src/main/java/com/example/studentscore/
├── StudentScoreApplication.java    # 启动类
├── common/                         # 通用类
│   ├── PageResult.java            # 分页结果
│   ├── Result.java                # 统一响应结果
│   └── ResultCode.java            # 响应状态码
├── config/                         # 配置类
│   ├── CorsConfig.java            # 跨域配置
│   ├── Knife4jConfig.java         # API文档配置
│   ├── MybatisPlusConfig.java     # MyBatis-Plus配置
│   └── WebMvcConfig.java          # Web配置
├── controller/                     # 控制器
│   ├── CourseController.java
│   ├── EnrollmentController.java
│   ├── ExcelController.java
│   ├── FileController.java
│   ├── ScoreController.java
│   ├── StatisticsController.java
│   ├── StudentController.java
│   ├── TeacherController.java
│   └── TeachingClassController.java
├── dto/                            # 数据传输对象
├── entity/                         # 实体类
├── exception/                      # 异常处理
├── excel/                          # Excel模型
├── mapper/                         # MyBatis映射
├── query/                          # 查询对象
├── service/                        # 业务服务
│   └── impl/                      # 服务实现
└── vo/                            # 视图对象

src/main/resources/
├── application.yml                # 应用配置
├── db/
│   ├── schema.sql                # 数据库表结构
│   └── data.sql                  # 初始化数据
└── mapper/                        # MyBatis XML
```

## 快速开始

### 1. 环境要求

- JDK 17+
- Maven 3.8+
- MySQL 8.0+

### 2. 数据库配置

1. 创建MySQL数据库：
```sql
CREATE DATABASE student_score_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```bash
mysql -u root -p student_score_db < src/main/resources/db/schema.sql
mysql -u root -p student_score_db < src/main/resources/db/data.sql
```

3. 修改 `application.yml` 中的数据库连接配置：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/student_score_db
    username: your_username
    password: your_password
```

### 3. 启动项目

```bash
# 编译项目
mvn clean compile

# 运行项目
mvn spring-boot:run

# 或者打包后运行
mvn clean package
java -jar target/student-score-system-1.0.0.jar
```

### 4. 访问接口

- 后端服务地址: http://localhost:8080/api
- API文档地址: http://localhost:8080/api/doc.html
- Druid监控: http://localhost:8080/api/druid

## API接口说明

### 学生管理 `/api/students`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 分页查询学生 |
| GET | /list | 查询所有学生 |
| GET | /{id} | 根据ID查询学生 |
| GET | /studentId/{studentId} | 根据学号查询 |
| POST | / | 新增学生 |
| PUT | /{id} | 更新学生 |
| DELETE | /{id} | 删除学生 |
| GET | /{id}/scores | 查询学生成绩详情 |

### 教师管理 `/api/teachers`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 分页查询教师 |
| GET | /list | 查询所有教师 |
| GET | /{id} | 根据ID查询教师 |
| POST | / | 新增教师 |
| PUT | /{id} | 更新教师 |
| DELETE | /{id} | 删除教师 |
| GET | /{id}/classes | 查询教师的教学班 |

### 课程管理 `/api/courses`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 分页查询课程 |
| GET | /list | 查询所有课程 |
| GET | /{id} | 根据ID查询课程 |
| POST | / | 新增课程 |
| PUT | /{id} | 更新课程 |
| DELETE | /{id} | 删除课程 |

### 教学班管理 `/api/teaching-classes`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 分页查询教学班 |
| GET | /list | 查询所有教学班 |
| GET | /{id} | 根据ID查询教学班 |
| POST | / | 新增教学班 |
| PUT | /{id} | 更新教学班 |
| DELETE | /{id} | 删除教学班 |
| GET | /{id}/students | 查询教学班学生 |

### 成绩管理 `/api/scores`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /page | 分页查询成绩 |
| GET | /{id} | 根据ID查询成绩 |
| POST | / | 录入成绩 |
| PUT | /{id} | 更新成绩 |
| DELETE | /{id} | 删除成绩 |
| POST | /batch | 批量录入成绩 |
| GET | /student/{studentId} | 查询学生成绩 |
| GET | /ranking | 成绩排名 |
| GET | /statistics/course/{courseId} | 课程统计 |

### 选课管理 `/api/enrollments`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /enroll | 学生选课 |
| POST | /drop | 学生退课 |
| GET | /student/{studentDbId} | 查询学生选课 |
| GET | /class/{teachingClassDbId} | 查询教学班学生 |

### 统计分析 `/api/statistics`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /overview | 系统概览 |
| GET | /score-distribution | 成绩分布 |
| GET | /course-average | 课程平均分 |
| GET | /score-trend | 成绩趋势 |
| GET | /class-comparison | 班级对比 |

### Excel导入导出 `/api/excel`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /students/export | 导出学生 |
| POST | /students/import | 导入学生 |
| GET | /students/template | 学生模板 |
| GET | /scores/export | 导出成绩 |
| POST | /scores/import | 导入成绩 |
| GET | /scores/template | 成绩模板 |
| GET | /teachers/export | 导出教师 |
| POST | /teachers/import | 导入教师 |
| GET | /teachers/template | 教师模板 |
| GET | /courses/export | 导出课程 |
| POST | /courses/import | 导入课程 |
| GET | /courses/template | 课程模板 |

### 文件管理 `/api/files`

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /upload | 上传单个文件 |
| POST | /upload/batch | 批量上传文件 |
| GET | /download/{fileId} | 下载文件 |
| DELETE | /{fileId} | 删除文件 |
| GET | /{fileId} | 获取文件信息 |
| GET | /list | 获取文件列表 |

## 统一响应格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1699999999999
}
```

## 成绩计算规则

综合成绩 = 平时成绩 × 20% + 期中成绩 × 20% + 实验成绩 × 20% + 期末成绩 × 40%

## 配置说明

### 文件上传配置

```yaml
file:
  upload:
    path: ./uploads          # 上传目录
    max-size: 10485760       # 最大10MB
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx
```

### 跨域配置

默认允许所有来源的跨域请求，如需限制请修改 `CorsConfig.java`。

## 前后端联调

1. 确保后端服务运行在 http://localhost:8080
2. 前端代理配置（vite.config.js）：
```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

## 常见问题

### Q: 启动报数据库连接失败？
A: 检查MySQL服务是否启动，数据库是否创建，用户名密码是否正确。

### Q: API文档无法访问？
A: 确认访问地址为 http://localhost:8080/api/doc.html

### Q: 跨域问题？
A: 已配置全局CORS，如仍有问题检查请求头是否正确。

## 前端项目

前端代码位于 `frontend/` 目录，基于 Vue3 + Vite + Element Plus 构建。

### 前端技术栈

- **核心框架**: Vue 3.4.0
- **构建工具**: Vite 5.0.0
- **UI组件库**: Element Plus 2.5.0
- **状态管理**: Pinia 2.1.7
- **路由管理**: Vue Router 4.2.5
- **HTTP客户端**: Axios 1.6.2
- **图表库**: ECharts 5.4.3
- **CSS预处理**: SCSS

### 前端目录结构

```
frontend/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── main.js                 # 入口文件
    ├── App.vue                 # 根组件
    ├── api/                    # API接口
    │   └── index.js           # 所有API封装
    ├── assets/                 # 静态资源
    │   └── styles/            # 全局样式
    ├── layouts/                # 布局组件
    │   └── MainLayout.vue     # 主布局
    ├── router/                 # 路由配置
    │   └── index.js
    ├── stores/                 # 状态管理
    │   └── system.js
    ├── utils/                  # 工具函数
    │   ├── index.js
    │   └── request.js         # Axios封装
    └── views/                  # 页面视图
        ├── Dashboard.vue       # 首页概览
        ├── students/           # 学生管理
        ├── teachers/           # 教师管理
        ├── courses/            # 课程管理
        ├── classes/            # 教学班管理
        ├── scores/             # 成绩管理
        ├── enrollment/         # 选课管理
        └── statistics/         # 统计分析
```

### 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 开发模式运行
npm run dev

# 生产构建（输出到 src/main/resources/static）
npm run build
```

### 前端功能模块

1. **首页概览**: 显示学生、教师、课程等统计数据
2. **学生管理**: 学生信息的增删改查、Excel导入导出
3. **教师管理**: 教师信息管理
4. **课程管理**: 课程信息管理
5. **教学班管理**: 教学班创建、学生名单管理
6. **成绩管理**: 成绩录入、查询、排名
7. **选课管理**: 学生选课/退课
8. **统计分析**: 成绩分布、课程平均分、班级对比等可视化图表

## IDEA运行指南

### 导入项目

1. 打开IntelliJ IDEA
2. 选择 `File` -> `Open`
3. 选择 `system_by_springboot` 文件夹
4. 等待Maven依赖下载完成

### 运行后端

1. 找到 `StudentScoreApplication.java` 
   - 路径: `src/main/java/com/example/studentscore/StudentScoreApplication.java`
2. 右键点击 -> `Run 'StudentScoreApplication'`
3. 或者点击类中 `main` 方法旁边的绿色运行按钮

### 运行前端

在IDEA终端中执行：
```bash
cd frontend
npm install
npm run dev
```

## License

MIT License
