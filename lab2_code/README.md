# RBAC权限管理系统

基于角色的访问控制(Role-Based Access Control)系统，使用Java实现完整的权限管理功能。

## 系统架构

### 分层架构
```
├── model/          # 实体层 - User, Role, Permission, Resource, AuditLog
├── dao/            # 数据访问层 - 使用JDBC实现数据持久化
├── service/        # 业务逻辑层 - UserService, RoleService, PermissionService, AuditService
├── ui/             # 表示层 - 命令行交互界面
├── util/           # 工具类 - 数据库连接、密码加密、会话管理
├── annotation/     # 注解 - 权限检查、审计日志
└── config/         # 配置文件
```

## 主要功能

### 1. 用户管理
- ✅ 创建用户（支持密码策略验证）
- ✅ 更新用户信息
- ✅ 删除用户
- ✅ 锁定/解锁用户
- ✅ 重置密码
- ✅ 为用户分配/移除角色
- ✅ 查看用户列表

### 2. 角色管理
- ✅ 创建角色（支持角色层级）
- ✅ 更新角色信息
- ✅ 删除角色
- ✅ 为角色分配/移除权限
- ✅ 查看角色列表
- ✅ 查看角色权限

### 3. 权限管理
- ✅ 创建权限（菜单权限、操作权限、资源权限、数据权限）
- ✅ 更新权限信息
- ✅ 删除权限
- ✅ 按类型查看权限
- ✅ 权限校验

### 4. 审计日志
- ✅ 记录所有操作
- ✅ 按用户查询日志
- ✅ 按操作类型查询
- ✅ 按结果查询
- ✅ 按时间范围查询
- ✅ 导出审计日志

### 5. 个人中心
- ✅ 修改密码
- ✅ 查看我的权限
- ✅ 查看我的操作记录

## 技术特性

### Java特性应用
- ✅ **泛型**: BaseDAO接口使用泛型实现通用CRUD操作
- ✅ **枚举**: UserStatus, RoleLevel, PermissionType, OperationResult
- ✅ **注解**: @RequiresPermission, @RequiresRole, @Auditable
- ✅ **Lambda表达式**: Stream API用于集合操作和权限检查
- ✅ **接口**: BaseDAO定义通用数据访问接口
- ✅ **继承和多态**: Service层继承和实现
- ✅ **Optional**: 避免空指针异常
- ✅ **内部类**: 枚举内部类

### 设计模式
- ✅ **单例模式**: DatabaseConnection, SessionManager
- ✅ **DAO模式**: 数据访问对象模式
- ✅ **分层架构**: Model-DAO-Service-UI
- ✅ **策略模式**: 权限验证策略

### 扩展功能
- ✅ **密码加密**: SHA-256 + 盐值
- ✅ **会话管理**: 登录状态维护
- ✅ **角色层级**: 支持角色级别管理
- ✅ **密码策略**: 最少8位，包含大小写字母和数字
- ✅ **审计追踪**: 完整的操作日志记录
- ✅ **数据导出**: 审计日志导出功能

## 数据库设计

### 表结构
1. **users** - 用户表
2. **roles** - 角色表
3. **permissions** - 权限表
4. **user_roles** - 用户角色关联表
5. **role_permissions** - 角色权限关联表
6. **audit_logs** - 审计日志表
7. **resources** - 资源表

## 默认数据

### 默认角色
- **SUPER_ADMIN**: 超级管理员，拥有所有权限
- **ADMIN**: 管理员，拥有大部分管理权限
- **USER**: 普通用户，基本查看权限

### 默认用户
- 用户名: `admin`
- 密码: `Admin123`
- 角色: SUPER_ADMIN

### 默认权限
- 用户管理权限: USER_VIEW, USER_CREATE, USER_UPDATE, USER_DELETE, etc.
- 角色管理权限: ROLE_VIEW, ROLE_CREATE, ROLE_UPDATE, ROLE_DELETE, etc.
- 权限管理权限: PERMISSION_VIEW, PERMISSION_CREATE, PERMISSION_UPDATE, PERMISSION_DELETE
- 审计日志权限: AUDIT_VIEW, AUDIT_EXPORT

## 项目结构

```
Java_lab2/
├── src/
│   ├── Main.java                   # 主程序入口
│   ├── model/                      # 实体类
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Permission.java
│   │   ├── Resource.java
│   │   └── AuditLog.java
│   ├── dao/                        # 数据访问层
│   │   ├── BaseDAO.java            # 通用DAO接口
│   │   ├── UserDAO.java
│   │   ├── RoleDAO.java
│   │   ├── PermissionDAO.java
│   │   ├── AuditLogDAO.java
│   │   ├── UserRoleDAO.java
│   │   └── RolePermissionDAO.java
│   ├── service/                    # 业务逻辑层
│   │   ├── UserService.java
│   │   ├── RoleService.java
│   │   ├── PermissionService.java
│   │   └── AuditService.java
│   ├── ui/                         # 用户界面
│   │   └── CommandLineInterface.java
│   ├── util/                       # 工具类
│   │   ├── DatabaseConnection.java
│   │   ├── DatabaseInitializer.java
│   │   ├── PasswordUtil.java
│   │   └── SessionManager.java
│   ├── annotation/                 # 注解
│   │   ├── RequiresPermission.java
│   │   ├── RequiresRole.java
│   │   └── Auditable.java
│   └── config/                     # 配置文件
│       └── database.properties
├── lib/                            # 第三方库
│   └── sqlite-jdbc-3.36.0.3.jar   # SQLite驱动
├── README.md                       # 项目说明
├── compile.sh                      # 编译脚本
├── run.sh                          # 运行脚本
└── test_login.sh                   # 测试登录脚本
```

## 依赖库

- **SQLite JDBC Driver**: org.sqlite.JDBC (3.36.0.3)
  - 用于SQLite数据库连接
  - 下载地址: https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/

## 编译和运行

### 1. 下载SQLite JDBC驱动
```bash
# 创建lib目录
mkdir -p lib

# 下载SQLite JDBC驱动
cd lib
curl -L -O https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.36.0.3/sqlite-jdbc-3.36.0.3.jar
cd ..
```

### 2. 编译
```bash
chmod +x compile.sh
./compile.sh
```

或手动编译:
```bash
javac -d bin -cp "lib/*" src/**/*.java src/*.java
```

### 3. 运行
```bash
chmod +x run.sh
./run.sh
```

或手动运行:
```bash
java -cp "bin:lib/*" Main
```

## 使用说明

### 1. 首次登录
- 使用默认管理员账号登录
- 用户名: `admin`
- 密码: `Admin123`

### 2. 创建用户
1. 进入"用户管理"菜单
2. 选择"创建用户"
3. 输入用户信息（密码必须符合策略）
4. 为新用户分配角色

### 3. 管理角色和权限
1. 进入"角色管理"菜单
2. 创建新角色或修改现有角色
3. 为角色分配权限
4. 通过用户管理将角色分配给用户

### 4. 查看审计日志
1. 进入"审计日志"菜单
2. 可按用户、操作、结果等条件查询
3. 支持导出日志记录

## 安全特性

1. **密码加密**: 使用SHA-256算法和随机盐值
2. **密码策略**: 最少8位，必须包含大小写字母和数字
3. **权限验证**: 所有操作都经过权限检查
4. **审计日志**: 记录所有操作，包括失败和拒绝的操作
5. **会话管理**: 自动会话超时（30分钟）

## 权限说明

### 菜单权限 (MENU)
控制用户能否访问某个菜单

### 操作权限 (OPERATION)
控制用户能否执行某个操作（如创建、删除）

### 资源权限 (RESOURCE)
控制用户能否访问某个资源

### 数据权限 (DATA)
控制用户能访问的数据范围

## 注意事项

1. 首次运行会自动创建数据库和初始化数据
2. 数据库文件: `rbac_system.db`
3. 超级管理员账号请妥善保管
4. 删除用户/角色前会自动清理关联关系
5. 审计日志不可修改和删除（保证审计完整性）

## 开发者

- Java版本: JDK 8+
- 数据库: SQLite
- 架构模式: 分层架构 + MVC

## 许可证

本项目仅用于学习和研究目的。
