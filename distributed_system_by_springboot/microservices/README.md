# 学生成绩管理系统 - 分布式微服务架构

## 项目概述

本项目将单体 Spring Boot 学生成绩管理系统改造为分布式微服务架构，保持原有功能不变，所有前端 API 调用正常运行。

## 技术栈

- **Spring Boot**: 3.2.0
- **Spring Cloud**: 2023.0.0
- **Spring Cloud Gateway**: API 网关
- **OpenFeign**: 服务间调用
- **MyBatis-Plus**: 3.5.5 ORM框架
- **SQLite**: 数据库
- **Knife4j**: 4.3.0 API文档
- **EasyExcel**: 3.3.3 Excel处理
- **Hutool**: 5.8.23 工具库

## 微服务架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         Frontend (Vue3)                          │
│                          http://localhost:5173                   │
└─────────────────────────────────────────────────────────────────┘
                                   │
                                   ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Gateway (Port: 8080)                          │
│                    路由转发、请求过滤                             │
└─────────────────────────────────────────────────────────────────┘
                                   │
          ┌────────────────────────┼────────────────────────┐
          │           │            │            │           │
          ▼           ▼            ▼            ▼           ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│ Auth Service │ │Student Service│ │Teacher Service│ │Course Service│
│  Port: 8081  │ │  Port: 8082   │ │  Port: 8083   │ │  Port: 8084  │
└──────────────┘ └──────────────┘ └──────────────┘ └──────────────┘
          │           │            │            │           │
          ▼           ▼            ▼            ▼           ▼
┌──────────────┐ ┌──────────────┐ ┌──────────────┐
│Score Service │ │Stats Service │ │ File Service │
│  Port: 8085  │ │  Port: 8086  │ │  Port: 8087  │
└──────────────┘ └──────────────┘ └──────────────┘
```

## 服务列表

| 服务名称 | 端口 | 描述 | API路径 |
|---------|------|------|---------|
| gateway | 8080 | API网关，路由转发 | - |
| auth-service | 8081 | 认证服务，登录登出、用户管理 | /api/auth/** |
| student-service | 8082 | 学生服务，学生信息CRUD | /api/students/** |
| teacher-service | 8083 | 教师服务，教师信息CRUD | /api/teachers/** |
| course-service | 8084 | 课程服务，课程、教学班、选课管理 | /api/courses/**, /api/teaching-classes/**, /api/enrollments/** |
| score-service | 8085 | 成绩服务，成绩录入、查询、统计 | /api/scores/** |
| statistics-service | 8086 | 统计服务，数据概览、统计分析 | /api/statistics/** |
| file-service | 8087 | 文件服务，文件上传下载、Excel导入导出 | /api/files/**, /api/excel/** |

## 目录结构

```
microservices/
├── pom.xml                      # 父POM
├── common/                      # 公共模块
│   └── src/main/java/com/example/common/
│       ├── entity/              # 实体类
│       ├── dto/                 # 数据传输对象
│       ├── vo/                  # 视图对象
│       ├── query/               # 查询对象
│       ├── result/              # 统一响应
│       ├── exception/           # 全局异常
│       └── utils/               # 工具类
├── gateway/                     # 网关服务
├── auth-service/                # 认证服务
├── student-service/             # 学生服务
├── teacher-service/             # 教师服务
├── course-service/              # 课程服务
├── score-service/               # 成绩服务
├── statistics-service/          # 统计服务
└── file-service/                # 文件服务
```

## 启动顺序

1. **确保数据库存在**: `data/student_score.db`
2. **按以下顺序启动服务**:

```bash
# 1. 首先启动网关
cd microservices/gateway
mvn spring-boot:run

# 2. 启动认证服务
cd microservices/auth-service
mvn spring-boot:run

# 3. 启动业务服务（可并行启动）
cd microservices/student-service && mvn spring-boot:run
cd microservices/teacher-service && mvn spring-boot:run
cd microservices/course-service && mvn spring-boot:run
cd microservices/score-service && mvn spring-boot:run
cd microservices/statistics-service && mvn spring-boot:run
cd microservices/file-service && mvn spring-boot:run
```

## 一键启动（Windows）

创建 `start-all.bat` 文件：

```batch
@echo off
echo Starting Microservices...

cd microservices

start "Gateway" cmd /k "cd gateway && mvn spring-boot:run"
timeout /t 5

start "Auth Service" cmd /k "cd auth-service && mvn spring-boot:run"
start "Student Service" cmd /k "cd student-service && mvn spring-boot:run"
start "Teacher Service" cmd /k "cd teacher-service && mvn spring-boot:run"
start "Course Service" cmd /k "cd course-service && mvn spring-boot:run"
start "Score Service" cmd /k "cd score-service && mvn spring-boot:run"
start "Statistics Service" cmd /k "cd statistics-service && mvn spring-boot:run"
start "File Service" cmd /k "cd file-service && mvn spring-boot:run"

echo All services starting...
```

## API 文档

每个服务都有独立的 Knife4j API 文档：

- **Gateway**: http://localhost:8080 (仅路由)
- **Auth Service**: http://localhost:8081/doc.html
- **Student Service**: http://localhost:8082/doc.html
- **Teacher Service**: http://localhost:8083/doc.html
- **Course Service**: http://localhost:8084/doc.html
- **Score Service**: http://localhost:8085/doc.html
- **Statistics Service**: http://localhost:8086/doc.html
- **File Service**: http://localhost:8087/doc.html

## 网关路由配置

所有 `/api/**` 请求通过网关转发到对应服务：

| 前端请求 | 网关路由 | 目标服务 |
|---------|----------|---------|
| /api/auth/** | StripPrefix=1 | localhost:8081/auth/** |
| /api/students/** | StripPrefix=1 | localhost:8082/students/** |
| /api/teachers/** | StripPrefix=1 | localhost:8083/teachers/** |
| /api/courses/** | StripPrefix=1 | localhost:8084/courses/** |
| /api/teaching-classes/** | StripPrefix=1 | localhost:8084/teaching-classes/** |
| /api/enrollments/** | StripPrefix=1 | localhost:8084/enrollments/** |
| /api/scores/** | StripPrefix=1 | localhost:8085/scores/** |
| /api/statistics/** | StripPrefix=1 | localhost:8086/statistics/** |
| /api/files/** | StripPrefix=1 | localhost:8087/files/** |
| /api/excel/** | StripPrefix=1 | localhost:8087/excel/** |

## 服务间通信

使用 OpenFeign 进行服务间调用：

- **student-service** → **score-service**: 获取学生成绩详情

## 数据库

所有服务共享同一个 SQLite 数据库文件：

```
data/student_score.db
```

每个服务通过相对路径访问数据库：`jdbc:sqlite:../data/student_score.db`

## 前端配置

前端通过网关（8080端口）访问所有 API，无需修改现有配置：

```javascript
// vite.config.js
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

## 构建与部署

### 构建所有服务

```bash
cd microservices
mvn clean package -DskipTests
```

### 运行 JAR 包

```bash
java -jar gateway/target/gateway-1.0.0.jar
java -jar auth-service/target/auth-service-1.0.0.jar
java -jar student-service/target/student-service-1.0.0.jar
# ... 其他服务
```

## 功能列表

### 认证服务 (auth-service)
- 用户登录/登出
- 用户信息管理（CRUD）
- 密码修改

### 学生服务 (student-service)
- 学生信息 CRUD
- 学生成绩详情查询（通过 Feign 调用 score-service）
- 学生选课信息

### 教师服务 (teacher-service)
- 教师信息 CRUD
- 教学班关联查询

### 课程服务 (course-service)
- 课程信息 CRUD
- 教学班管理
- 选课管理

### 成绩服务 (score-service)
- 成绩录入与修改
- 成绩查询
- 班级排名
- 学生成绩详情

### 统计服务 (statistics-service)
- 系统概览（学生、教师、课程、班级数量）
- 课程统计（平均分、及格率、优秀率）
- 班级统计（详细统计信息）
- 成绩分布分析

### 文件服务 (file-service)
- 文件上传/下载/删除
- 学生数据导入/导出
- 教师数据导入/导出
- 课程数据导入/导出
- 成绩数据导入/导出
- Excel 模板下载

## 注意事项

1. **启动顺序**: 建议先启动 gateway，再启动其他服务
2. **端口占用**: 确保 8080-8087 端口未被占用
3. **数据库路径**: 各服务使用相对路径访问数据库，确保从正确目录启动
4. **前端兼容**: 所有 API 路径与原单体架构保持一致，前端无需修改

## 许可证

MIT License
