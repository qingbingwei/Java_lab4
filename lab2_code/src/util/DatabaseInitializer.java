package util;

import dao.*;
import model.Permission;
import model.Permission.PermissionType;
import model.Role;
import model.Role.RoleLevel;
import model.User;
import service.PermissionService;

import java.sql.Connection;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据库初始化工具类
 * 创建表结构并初始化默认数据
 */
public class DatabaseInitializer {
    private static final Logger LOGGER = Logger.getLogger(DatabaseInitializer.class.getName());
    private final DatabaseConnection dbConnection;

    public DatabaseInitializer() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 初始化数据库
     */
    public boolean initialize() {
        try {
            LOGGER.info("开始初始化数据库...");
            
            // 创建表
            createTables();
            
            // 初始化默认数据
            initializeDefaultData();
            
            LOGGER.info("数据库初始化完成");
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "数据库初始化失败", e);
            return false;
        }
    }

    /**
     * 创建所有表
     */
    private void createTables() {
        createUsersTable();
        createRolesTable();
        createPermissionsTable();
        createUserRolesTable();
        createRolePermissionsTable();
        createAuditLogsTable();
        createResourcesTable();
    }

    /**
     * 创建用户表
     */
    private void createUsersTable() {
        String sql = "CREATE TABLE IF NOT EXISTS users (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "username VARCHAR(50) UNIQUE NOT NULL, " +
                     "password VARCHAR(255) NOT NULL, " +
                     "email VARCHAR(100), " +
                     "real_name VARCHAR(100), " +
                     "status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', " +
                     "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "last_login_time TIMESTAMP" +
                     ")";
        executeSQL(sql, "用户表");
    }

    /**
     * 创建角色表
     */
    private void createRolesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS roles (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name VARCHAR(50) UNIQUE NOT NULL, " +
                     "description VARCHAR(255), " +
                     "level VARCHAR(20) NOT NULL DEFAULT 'USER', " +
                     "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        executeSQL(sql, "角色表");
    }

    /**
     * 创建权限表
     */
    private void createPermissionsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS permissions (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "code VARCHAR(100) UNIQUE NOT NULL, " +
                     "name VARCHAR(100) NOT NULL, " +
                     "description VARCHAR(255), " +
                     "type VARCHAR(20) NOT NULL, " +
                     "resource_path VARCHAR(255), " +
                     "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        executeSQL(sql, "权限表");
    }

    /**
     * 创建用户角色关联表
     */
    private void createUserRolesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS user_roles (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "user_id INTEGER NOT NULL, " +
                     "role_id INTEGER NOT NULL, " +
                     "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE, " +
                     "UNIQUE(user_id, role_id)" +
                     ")";
        executeSQL(sql, "用户角色关联表");
    }

    /**
     * 创建角色权限关联表
     */
    private void createRolePermissionsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS role_permissions (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "role_id INTEGER NOT NULL, " +
                     "permission_id INTEGER NOT NULL, " +
                     "FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE, " +
                     "FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE, " +
                     "UNIQUE(role_id, permission_id)" +
                     ")";
        executeSQL(sql, "角色权限关联表");
    }

    /**
     * 创建审计日志表
     */
    private void createAuditLogsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS audit_logs (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "user_id INTEGER, " +
                     "username VARCHAR(50), " +
                     "operation VARCHAR(50) NOT NULL, " +
                     "target VARCHAR(100), " +
                     "detail VARCHAR(500), " +
                     "result VARCHAR(20) NOT NULL, " +
                     "ip_address VARCHAR(50), " +
                     "operation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                     ")";
        executeSQL(sql, "审计日志表");
        
        // 创建索引
        executeSQL("CREATE INDEX IF NOT EXISTS idx_audit_user ON audit_logs(user_id)", "审计日志用户索引");
        executeSQL("CREATE INDEX IF NOT EXISTS idx_audit_operation ON audit_logs(operation)", "审计日志操作索引");
        executeSQL("CREATE INDEX IF NOT EXISTS idx_audit_time ON audit_logs(operation_time)", "审计日志时间索引");
    }

    /**
     * 创建资源表
     */
    private void createResourcesTable() {
        String sql = "CREATE TABLE IF NOT EXISTS resources (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                     "name VARCHAR(100) NOT NULL, " +
                     "path VARCHAR(255) UNIQUE NOT NULL, " +
                     "type VARCHAR(20) NOT NULL, " +
                     "description VARCHAR(255), " +
                     "owner_id INTEGER, " +
                     "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                     "FOREIGN KEY (owner_id) REFERENCES users(id)" +
                     ")";
        executeSQL(sql, "资源表");
    }

    /**
     * 执行SQL语句
     */
    private void executeSQL(String sql, String tableName) {
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            LOGGER.info("创建成功: " + tableName);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "创建失败: " + tableName, e);
        }
    }

    /**
     * 初始化默认数据
     */
    private void initializeDefaultData() {
        // 初始化权限
        initializePermissions();
        
        // 初始化角色
        initializeRoles();
        
        // 初始化默认管理员用户
        initializeAdminUser();
    }

    /**
     * 判断是否需要执行初始化（是否为第一次启动）。
     *
     * 通过检查权限表中是否已经有数据来判断：
     *  - 若无数据，视为首次启动，需要初始化；
     *  - 若已有数据，说明之前已经初始化过，可跳过本次初始化。
     */
    public boolean needInitialize() {
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM permissions");
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }
        } catch (Exception e) {
            // 检查失败时，为了安全起见，默认认为需要初始化，同时记录日志
            LOGGER.log(Level.WARNING, "检查数据库是否已初始化失败，默认执行初始化", e);
            return true;
        }
        return true;
    }

    /**
     * 初始化权限
     */
    private void initializePermissions() {
        PermissionService permissionService = new PermissionService();
        permissionService.initializeDefaultPermissions();
        LOGGER.info("默认权限初始化完成");
    }

    /**
     * 初始化角色
     */
    private void initializeRoles() {
        RoleDAO roleDAO = new RoleDAO();
        RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();
        PermissionDAO permissionDAO = new PermissionDAO();

        // 创建超级管理员角色
        if (!roleDAO.existsByName("SUPER_ADMIN")) {
            Role superAdmin = new Role("SUPER_ADMIN", "超级管理员，拥有所有权限");
            superAdmin.setLevel(RoleLevel.SUPER_ADMIN);
            superAdmin = roleDAO.save(superAdmin);
            
            // 为超级管理员分配所有权限
            final Long superAdminId = superAdmin.getId();
            permissionDAO.findAll().forEach(permission -> 
                rolePermissionDAO.assignPermissionToRole(superAdminId, permission.getId())
            );
            LOGGER.info("创建超级管理员角色");
        }

        // 创建管理员角色
        if (!roleDAO.existsByName("ADMIN")) {
            Role admin = new Role("ADMIN", "管理员，拥有大部分管理权限");
            admin.setLevel(RoleLevel.ADMIN);
            admin = roleDAO.save(admin);
            
            // 为管理员分配常用权限
            assignPermissionsToRole(admin.getId(), new String[]{
                "USER_VIEW", "USER_CREATE", "USER_UPDATE", "USER_LOCK",
                "ROLE_VIEW", "ROLE_CREATE", "ROLE_UPDATE",
                "PERMISSION_VIEW", "AUDIT_VIEW"
            }, permissionDAO, rolePermissionDAO);
            LOGGER.info("创建管理员角色");
        }

        // 创建普通用户角色
        if (!roleDAO.existsByName("USER")) {
            Role user = new Role("USER", "普通用户，基本查看权限");
            user.setLevel(RoleLevel.USER);
            user = roleDAO.save(user);
            
            // 为普通用户分配基本权限
            assignPermissionsToRole(user.getId(), new String[]{
                "USER_VIEW", "ROLE_VIEW", "PERMISSION_VIEW"
            }, permissionDAO, rolePermissionDAO);
            LOGGER.info("创建普通用户角色");
        }
    }

    /**
     * 为角色分配权限
     */
    private void assignPermissionsToRole(Long roleId, String[] permissionCodes, 
                                        PermissionDAO permissionDAO, RolePermissionDAO rolePermissionDAO) {
        for (String code : permissionCodes) {
            permissionDAO.findByCode(code).ifPresent(permission ->
                rolePermissionDAO.assignPermissionToRole(roleId, permission.getId())
            );
        }
    }

    /**
     * 初始化默认管理员用户
     */
    private void initializeAdminUser() {
        UserDAO userDAO = new UserDAO();
        RoleDAO roleDAO = new RoleDAO();
        UserRoleDAO userRoleDAO = new UserRoleDAO();

        // 创建默认管理员账户
        if (!userDAO.existsByUsername("admin")) {
            User admin = new User("admin", PasswordUtil.encryptPassword("Admin123"));
            admin.setEmail("admin@system.com");
            admin.setRealName("系统管理员");
            admin.setStatus(User.UserStatus.ACTIVE);
            
            admin = userDAO.save(admin);
            
            // 分配超级管理员角色
            final Long adminId = admin.getId();
            roleDAO.findByName("SUPER_ADMIN").ifPresent(role ->
                userRoleDAO.assignRoleToUser(adminId, role.getId())
            );
            
            LOGGER.info("创建默认管理员用户: admin / Admin123");
        }
    }

    /**
     * 清空所有数据（谨慎使用）
     */
    public void clearAllData() {
        String[] tables = {"audit_logs", "role_permissions", "user_roles", 
                          "permissions", "roles", "users", "resources"};
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String table : tables) {
                stmt.execute("DELETE FROM " + table);
                LOGGER.info("清空表: " + table);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "清空数据失败", e);
        }
    }
}
