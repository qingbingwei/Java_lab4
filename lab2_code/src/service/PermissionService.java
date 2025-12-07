package service;

import dao.PermissionDAO;
import model.AuditLog.OperationResult;
import model.Permission;
import model.Permission.PermissionType;
import model.User;
import util.SessionManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 权限服务类
 * 提供权限管理的业务逻辑
 */
public class PermissionService {
    private static final Logger LOGGER = Logger.getLogger(PermissionService.class.getName());
    private final PermissionDAO permissionDAO;
    private final AuditService auditService;
    private final SessionManager sessionManager;

    public PermissionService() {
        this.permissionDAO = new PermissionDAO();
        this.auditService = new AuditService();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * 创建权限
     */
    public Permission createPermission(String code, String name, PermissionType type, String description) {
        if (!hasPermission("PERMISSION_CREATE")) {
            auditService.logOperation("CREATE_PERMISSION", code, "权限不足", OperationResult.DENIED);
            return null;
        }

        // 检查权限代码是否已存在
        if (permissionDAO.existsByCode(code)) {
            auditService.logOperation("CREATE_PERMISSION", code, "权限代码已存在", OperationResult.FAILURE);
            return null;
        }

        Permission permission = new Permission(code, name, type);
        permission.setDescription(description);

        Permission savedPermission = permissionDAO.save(permission);
        if (savedPermission != null) {
            auditService.logOperation("CREATE_PERMISSION", code, "创建权限成功", OperationResult.SUCCESS);
            LOGGER.info("创建权限成功: " + code);
        }

        return savedPermission;
    }

    /**
     * 更新权限
     */
    public boolean updatePermission(Long permissionId, String code, String name, PermissionType type, String description) {
        if (!hasPermission("PERMISSION_UPDATE")) {
            auditService.logOperation("UPDATE_PERMISSION", "permissionId=" + permissionId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<Permission> permissionOpt = permissionDAO.findById(permissionId);
        if (!permissionOpt.isPresent()) {
            return false;
        }

        Permission permission = permissionOpt.get();
        permission.setCode(code);
        permission.setName(name);
        permission.setType(type);
        permission.setDescription(description);

        boolean success = permissionDAO.update(permission);
        if (success) {
            auditService.logOperation("UPDATE_PERMISSION", code, "更新权限信息", OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 删除权限
     */
    public boolean deletePermission(Long permissionId) {
        if (!hasPermission("PERMISSION_DELETE")) {
            auditService.logOperation("DELETE_PERMISSION", "permissionId=" + permissionId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<Permission> permissionOpt = permissionDAO.findById(permissionId);
        if (!permissionOpt.isPresent()) {
            return false;
        }

        Permission permission = permissionOpt.get();

        // 删除权限
        boolean success = permissionDAO.deleteById(permissionId);
        if (success) {
            auditService.logOperation("DELETE_PERMISSION", permission.getCode(), "删除权限", OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 查询所有权限
     */
    public List<Permission> getAllPermissions() {
        if (!hasPermission("PERMISSION_VIEW")) {
            auditService.logOperation("VIEW_PERMISSIONS", "all", "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return permissionDAO.findAll();
    }

    /**
     * 根据ID查询权限
     */
    public Optional<Permission> getPermissionById(Long permissionId) {
        return permissionDAO.findById(permissionId);
    }

    /**
     * 根据代码查询权限
     */
    public Optional<Permission> getPermissionByCode(String code) {
        return permissionDAO.findByCode(code);
    }

    /**
     * 根据类型查询权限
     */
    public List<Permission> getPermissionsByType(PermissionType type) {
        return permissionDAO.findByType(type);
    }

    /**
     * 检查当前用户是否有指定权限
     */
    public boolean checkPermission(String permissionCode) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // 超级管理员拥有所有权限
        if (currentUser.hasRole("SUPER_ADMIN")) {
            return true;
        }

        boolean hasPermission = currentUser.getAllPermissions().stream()
                .anyMatch(p -> p.getCode().equals(permissionCode));

        // 记录权限检查结果
        OperationResult result = hasPermission ? OperationResult.SUCCESS : OperationResult.DENIED;
        auditService.logOperation("CHECK_PERMISSION", permissionCode, 
                "权限检查: " + (hasPermission ? "通过" : "拒绝"), result);

        return hasPermission;
    }

    /**
     * 检查当前用户是否有指定权限（内部使用，不记录日志）
     */
    private boolean hasPermission(String permissionCode) {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser == null) {
            return false;
        }

        // 超级管理员拥有所有权限
        if (currentUser.hasRole("SUPER_ADMIN")) {
            return true;
        }

        return currentUser.getAllPermissions().stream()
                .anyMatch(p -> p.getCode().equals(permissionCode));
    }

    /**
     * 初始化默认权限
     */
    public void initializeDefaultPermissions() {
        // 用户管理权限
        createDefaultPermission("USER_VIEW", "查看用户", PermissionType.MENU, "查看用户列表和信息");
        createDefaultPermission("USER_CREATE", "创建用户", PermissionType.OPERATION, "创建新用户");
        createDefaultPermission("USER_UPDATE", "更新用户", PermissionType.OPERATION, "更新用户信息");
        createDefaultPermission("USER_DELETE", "删除用户", PermissionType.OPERATION, "删除用户");
        createDefaultPermission("USER_LOCK", "锁定用户", PermissionType.OPERATION, "锁定或解锁用户");
        createDefaultPermission("USER_RESET_PASSWORD", "重置密码", PermissionType.OPERATION, "重置用户密码");
        createDefaultPermission("USER_ASSIGN_ROLE", "分配角色", PermissionType.OPERATION, "为用户分配角色");
        createDefaultPermission("USER_REMOVE_ROLE", "移除角色", PermissionType.OPERATION, "移除用户角色");

        // 角色管理权限
        createDefaultPermission("ROLE_VIEW", "查看角色", PermissionType.MENU, "查看角色列表和信息");
        createDefaultPermission("ROLE_CREATE", "创建角色", PermissionType.OPERATION, "创建新角色");
        createDefaultPermission("ROLE_UPDATE", "更新角色", PermissionType.OPERATION, "更新角色信息");
        createDefaultPermission("ROLE_DELETE", "删除角色", PermissionType.OPERATION, "删除角色");
        createDefaultPermission("ROLE_ASSIGN_PERMISSION", "分配权限", PermissionType.OPERATION, "为角色分配权限");
        createDefaultPermission("ROLE_REMOVE_PERMISSION", "移除权限", PermissionType.OPERATION, "移除角色权限");

        // 权限管理权限
        createDefaultPermission("PERMISSION_VIEW", "查看权限", PermissionType.MENU, "查看权限列表和信息");
        createDefaultPermission("PERMISSION_CREATE", "创建权限", PermissionType.OPERATION, "创建新权限");
        createDefaultPermission("PERMISSION_UPDATE", "更新权限", PermissionType.OPERATION, "更新权限信息");
        createDefaultPermission("PERMISSION_DELETE", "删除权限", PermissionType.OPERATION, "删除权限");

        // 审计日志权限
        createDefaultPermission("AUDIT_VIEW", "查看审计日志", PermissionType.MENU, "查看系统审计日志");
        createDefaultPermission("AUDIT_EXPORT", "导出审计日志", PermissionType.OPERATION, "导出审计日志数据");

        LOGGER.info("默认权限初始化完成");
    }

    /**
     * 创建默认权限（不检查权限）
     */
    private void createDefaultPermission(String code, String name, PermissionType type, String description) {
        if (!permissionDAO.existsByCode(code)) {
            Permission permission = new Permission(code, name, type);
            permission.setDescription(description);
            permissionDAO.save(permission);
        }
    }
}
