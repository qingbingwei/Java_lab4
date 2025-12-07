package service;

import dao.*;
import model.AuditLog.OperationResult;
import model.Permission;
import model.Role;
import model.User;
import util.SessionManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * 角色服务类
 * 提供角色管理的业务逻辑
 */
public class RoleService {
    private static final Logger LOGGER = Logger.getLogger(RoleService.class.getName());
    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;
    private final RolePermissionDAO rolePermissionDAO;
    private final AuditService auditService;
    private final SessionManager sessionManager;

    public RoleService() {
        this.roleDAO = new RoleDAO();
        this.permissionDAO = new PermissionDAO();
        this.rolePermissionDAO = new RolePermissionDAO();
        this.auditService = new AuditService();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * 创建角色
     */
    public Role createRole(String name, String description, Role.RoleLevel level) {
        if (!hasPermission("ROLE_CREATE")) {
            auditService.logOperation("CREATE_ROLE", name, "权限不足", OperationResult.DENIED);
            return null;
        }

        // 检查角色名是否已存在
        if (roleDAO.existsByName(name)) {
            auditService.logOperation("CREATE_ROLE", name, "角色名已存在", OperationResult.FAILURE);
            return null;
        }

        Role role = new Role(name, description);
        role.setLevel(level);

        Role savedRole = roleDAO.save(role);
        if (savedRole != null) {
            auditService.logOperation("CREATE_ROLE", name, "创建角色成功", OperationResult.SUCCESS);
            LOGGER.info("创建角色成功: " + name);
        }

        return savedRole;
    }

    /**
     * 更新角色
     */
    public boolean updateRole(Long roleId, String name, String description, Role.RoleLevel level) {
        if (!hasPermission("ROLE_UPDATE")) {
            auditService.logOperation("UPDATE_ROLE", "roleId=" + roleId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<Role> roleOpt = roleDAO.findById(roleId);
        if (!roleOpt.isPresent()) {
            return false;
        }

        Role role = roleOpt.get();
        role.setName(name);
        role.setDescription(description);
        role.setLevel(level);

        boolean success = roleDAO.update(role);
        if (success) {
            auditService.logOperation("UPDATE_ROLE", name, "更新角色信息", OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 删除角色
     */
    public boolean deleteRole(Long roleId) {
        if (!hasPermission("ROLE_DELETE")) {
            auditService.logOperation("DELETE_ROLE", "roleId=" + roleId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<Role> roleOpt = roleDAO.findById(roleId);
        if (!roleOpt.isPresent()) {
            return false;
        }

        Role role = roleOpt.get();

        // 先移除角色的所有权限
        rolePermissionDAO.removeAllPermissionsFromRole(roleId);

        // 删除角色
        boolean success = roleDAO.deleteById(roleId);
        if (success) {
            auditService.logOperation("DELETE_ROLE", role.getName(), "删除角色", OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 为角色分配权限
     */
    public boolean assignPermission(Long roleId, Long permissionId) {
        if (!hasPermission("ROLE_ASSIGN_PERMISSION")) {
            auditService.logOperation("ASSIGN_PERMISSION", "roleId=" + roleId, "权限不足", OperationResult.DENIED);
            return false;
        }

        boolean success = rolePermissionDAO.assignPermissionToRole(roleId, permissionId);
        if (success) {
            auditService.logOperation("ASSIGN_PERMISSION", "roleId=" + roleId, 
                    "分配权限 permissionId=" + permissionId, OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 移除角色权限
     */
    public boolean removePermission(Long roleId, Long permissionId) {
        if (!hasPermission("ROLE_REMOVE_PERMISSION")) {
            auditService.logOperation("REMOVE_PERMISSION", "roleId=" + roleId, "权限不足", OperationResult.DENIED);
            return false;
        }

        boolean success = rolePermissionDAO.removePermissionFromRole(roleId, permissionId);
        if (success) {
            auditService.logOperation("REMOVE_PERMISSION", "roleId=" + roleId,
                    "移除权限 permissionId=" + permissionId, OperationResult.SUCCESS);
        }

        return success;
    }

    /**
     * 查询所有角色
     */
    public List<Role> getAllRoles() {
        if (!hasPermission("ROLE_VIEW")) {
            auditService.logOperation("VIEW_ROLES", "all", "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return roleDAO.findAll();
    }

    /**
     * 根据ID查询角色
     */
    public Optional<Role> getRoleById(Long roleId) {
        return roleDAO.findById(roleId);
    }

    /**
     * 根据名称查询角色
     */
    public Optional<Role> getRoleByName(String name) {
        return roleDAO.findByName(name);
    }

    /**
     * 获取角色的权限列表
     */
    public List<Permission> getRolePermissions(Long roleId) {
        return permissionDAO.findPermissionsByRoleId(roleId);
    }

    /**
     * 获取角色的用户数量
     */
    public long getUserCountByRole(Long roleId) {
        UserRoleDAO userRoleDAO = new UserRoleDAO();
        return userRoleDAO.countUsersByRole(roleId);
    }

    /**
     * 批量分配权限
     */
    public boolean batchAssignPermissions(Long roleId, List<Long> permissionIds) {
        if (!hasPermission("ROLE_ASSIGN_PERMISSION")) {
            auditService.logOperation("BATCH_ASSIGN_PERMISSION", "roleId=" + roleId, "权限不足", OperationResult.DENIED);
            return false;
        }

        boolean allSuccess = true;
        for (Long permissionId : permissionIds) {
            if (!rolePermissionDAO.assignPermissionToRole(roleId, permissionId)) {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            auditService.logOperation("BATCH_ASSIGN_PERMISSION", "roleId=" + roleId,
                    "批量分配" + permissionIds.size() + "个权限", OperationResult.SUCCESS);
        }

        return allSuccess;
    }

    /**
     * 检查当前用户是否有指定权限
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
}
