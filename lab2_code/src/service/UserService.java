package service;

import dao.*;
import model.AuditLog;
import model.AuditLog.OperationResult;
import model.Role;
import model.User;
import model.User.UserStatus;
import util.PasswordUtil;
import util.SessionManager;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 用户服务类
 * 提供用户管理的业务逻辑
 */
public class UserService {
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());
    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final UserRoleDAO userRoleDAO;
    private final AuditLogDAO auditLogDAO;
    private final RolePermissionDAO rolePermissionDAO;
    private final PermissionDAO permissionDAO;
    private final SessionManager sessionManager;

    public UserService() {
        this.userDAO = new UserDAO();
        this.roleDAO = new RoleDAO();
        this.userRoleDAO = new UserRoleDAO();
        this.auditLogDAO = new AuditLogDAO();
        this.sessionManager = SessionManager.getInstance();
        this.rolePermissionDAO = new RolePermissionDAO();
        this.permissionDAO = new PermissionDAO();
    }

    /**
     * 用户登录
     */
    public boolean login(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        
        if (!userOpt.isPresent()) {
            logAudit("LOGIN", username, "用户不存在", OperationResult.FAILURE);
            return false;
        }

        User user = userOpt.get();
        
        // 检查用户状态
        if (user.getStatus() != UserStatus.ACTIVE) {
            logAudit("LOGIN", username, "用户状态异常: " + user.getStatus(), OperationResult.DENIED);
            return false;
        }

        // 验证密码
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) {
            logAudit("LOGIN", username, "密码错误", OperationResult.FAILURE);
            return false;
        }

        // 登录成功
        sessionManager.login(user);
        userDAO.updateLastLoginTime(user.getId());

        // 加载用户角色及其权限
        List<Role> roles = roleDAO.findRolesByUserId(user.getId());
        for (Role role : roles) {
            // 查询该角色下所有权限ID并填充到角色对象
            java.util.List<Long> permissionIds = rolePermissionDAO.findPermissionIdsByRoleId(role.getId());
            for (Long pid : permissionIds) {
                permissionDAO.findById(pid).ifPresent(role::addPermission);
            }
            user.addRole(role);
        }
        
        logAudit("LOGIN", username, "登录成功", OperationResult.SUCCESS);
        LOGGER.info("用户登录成功: " + username);
        return true;
    }

    /**
     * 用户登出
     */
    public void logout() {
        User currentUser = sessionManager.getCurrentUser();
        if (currentUser != null) {
            logAudit("LOGOUT", currentUser.getUsername(), "登出成功", OperationResult.SUCCESS);
            sessionManager.logout();
            LOGGER.info("用户登出: " + currentUser.getUsername());
        }
    }

    /**
     * 创建用户
     */
    public User createUser(String username, String password, String email, String realName) {
        // 检查权限
        if (!hasPermission("USER_CREATE")) {
            logAudit("CREATE_USER", username, "权限不足", OperationResult.DENIED);
            return null;
        }

        // 检查用户名是否已存在
        if (userDAO.existsByUsername(username)) {
            logAudit("CREATE_USER", username, "用户名已存在", OperationResult.FAILURE);
            return null;
        }

        // 验证密码策略
        if (!PasswordUtil.validatePasswordPolicy(password)) {
            logAudit("CREATE_USER", username, "密码不符合策略", OperationResult.FAILURE);
            return null;
        }

        // 创建用户
        User user = new User(username, PasswordUtil.encryptPassword(password));
        user.setEmail(email);
        user.setRealName(realName);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userDAO.save(user);
        if (savedUser != null) {
            logAudit("CREATE_USER", username, "创建用户成功", OperationResult.SUCCESS);
            LOGGER.info("创建用户成功: " + username);
        }
        
        return savedUser;
    }

    /**
     * 更新用户信息
     */
    public boolean updateUser(Long userId, String email, String realName) {
        if (!hasPermission("USER_UPDATE")) {
            logAudit("UPDATE_USER", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        user.setEmail(email);
        user.setRealName(realName);

        boolean success = userDAO.update(user);
        if (success) {
            logAudit("UPDATE_USER", user.getUsername(), "更新用户信息", OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 修改密码
     */
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        
        // 验证旧密码
        if (!PasswordUtil.verifyPassword(oldPassword, user.getPassword())) {
            logAudit("CHANGE_PASSWORD", user.getUsername(), "旧密码错误", OperationResult.FAILURE);
            return false;
        }

        // 验证新密码策略
        if (!PasswordUtil.validatePasswordPolicy(newPassword)) {
            logAudit("CHANGE_PASSWORD", user.getUsername(), "新密码不符合策略", OperationResult.FAILURE);
            return false;
        }

        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        boolean success = userDAO.update(user);
        
        if (success) {
            logAudit("CHANGE_PASSWORD", user.getUsername(), "密码修改成功", OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 重置密码（管理员操作）
     */
    public boolean resetPassword(Long userId, String newPassword) {
        if (!hasPermission("USER_RESET_PASSWORD")) {
            logAudit("RESET_PASSWORD", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        user.setPassword(PasswordUtil.encryptPassword(newPassword));
        boolean success = userDAO.update(user);
        
        if (success) {
            logAudit("RESET_PASSWORD", user.getUsername(), "管理员重置密码", OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long userId) {
        if (!hasPermission("USER_DELETE")) {
            logAudit("DELETE_USER", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        
        // 先移除用户的所有角色
        userRoleDAO.removeAllRolesFromUser(userId);
        
        // 删除用户
        boolean success = userDAO.deleteById(userId);
        if (success) {
            logAudit("DELETE_USER", user.getUsername(), "删除用户", OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 锁定/解锁用户
     */
    public boolean lockUser(Long userId, boolean lock) {
        if (!hasPermission("USER_LOCK")) {
            logAudit("LOCK_USER", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        Optional<User> userOpt = userDAO.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }

        User user = userOpt.get();
        user.setStatus(lock ? UserStatus.LOCKED : UserStatus.ACTIVE);
        
        boolean success = userDAO.update(user);
        if (success) {
            String action = lock ? "锁定" : "解锁";
            logAudit("LOCK_USER", user.getUsername(), action + "用户", OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 为用户分配角色
     */
    public boolean assignRole(Long userId, Long roleId) {
        if (!hasPermission("USER_ASSIGN_ROLE")) {
            logAudit("ASSIGN_ROLE", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        boolean success = userRoleDAO.assignRoleToUser(userId, roleId);
        if (success) {
            logAudit("ASSIGN_ROLE", "userId=" + userId, "分配角色 roleId=" + roleId, OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 移除用户角色
     */
    public boolean removeRole(Long userId, Long roleId) {
        if (!hasPermission("USER_REMOVE_ROLE")) {
            logAudit("REMOVE_ROLE", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return false;
        }

        boolean success = userRoleDAO.removeRoleFromUser(userId, roleId);
        if (success) {
            logAudit("REMOVE_ROLE", "userId=" + userId, "移除角色 roleId=" + roleId, OperationResult.SUCCESS);
        }
        
        return success;
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        if (!hasPermission("USER_VIEW")) {
            logAudit("VIEW_USERS", "all", "权限不足", OperationResult.DENIED);
            return List.of();
        }
        
        return userDAO.findAll();
    }

    /**
     * 根据ID查询用户
     */
    public Optional<User> getUserById(Long userId) {
        return userDAO.findById(userId);
    }

    /**
     * 根据用户名查询用户
     */
    public Optional<User> getUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    /**
     * 获取用户的角色列表
     */
    public List<Role> getUserRoles(Long userId) {
        return roleDAO.findRolesByUserId(userId);
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

    /**
     * 记录审计日志
     */
    private void logAudit(String operation, String target, String detail, OperationResult result) {
        User currentUser = sessionManager.getCurrentUser();
        AuditLog log = new AuditLog();
        
        if (currentUser != null) {
            log.setUserId(currentUser.getId());
            log.setUsername(currentUser.getUsername());
        } else {
            log.setUserId(0L);
            log.setUsername("Anonymous");
        }
        
        log.setOperation(operation);
        log.setTarget(target);
        log.setDetail(detail);
        log.setResult(result);
        log.setIpAddress("127.0.0.1"); // 实际应用中应获取真实IP
        
        auditLogDAO.save(log);
    }
}
