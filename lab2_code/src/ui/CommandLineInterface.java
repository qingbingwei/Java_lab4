package ui;

import model.*;
import service.*;
import util.SessionManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * 命令行界面类
 * 提供友好的菜单导航和操作交互
 */
public class CommandLineInterface {
    private final Scanner scanner;
    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final AuditService auditService;
    private final SessionManager sessionManager;
    private boolean running;

    public CommandLineInterface() {
        this.scanner = new Scanner(System.in);
        this.userService = new UserService();
        this.roleService = new RoleService();
        this.permissionService = new PermissionService();
        this.auditService = new AuditService();
        this.sessionManager = SessionManager.getInstance();
        this.running = true;
    }

    /**
     * 启动命令行界面
     */
    public void start() {
        printWelcomeBanner();
        
        while (running) {
            if (!sessionManager.isLoggedIn()) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
        
        scanner.close();
        printGoodbyeMessage();
    }

    /**
     * 打印欢迎横幅
     */
    private void printWelcomeBanner() {
        clearScreen();
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                                                              ║");
        System.out.println("║        RBAC 权限管理系统 (Role-Based Access Control)           ║");
        System.out.println("║                                                              ║");
        System.out.println("║                      版本 1.0.0                               ║");
        System.out.println("║                                                              ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * 显示登录菜单
     */
    private void showLoginMenu() {
        printSectionHeader("登录界面");
        System.out.println("1. 登录");
        System.out.println("0. 退出系统");
        printSeparator();
        
        String choice = readInput("请选择");
        
        // 空输入直接返回，重新显示菜单
        if (choice.isEmpty()) {
            return;
        }
        
        switch (choice) {
            case "1":
                handleLogin();
                break;
            case "0":
                running = false;
                break;
            default:
                printError("无效的选择，请重新输入");
                pause();
        }
    }

    /**
     * 处理登录
     */
    private void handleLogin() {
        clearScreen();
        printSectionHeader("用户登录");
        
        String username = readInput("用户名");
        if (username.trim().isEmpty()) {
            printError("用户名不能为空");
            pause();
            return;
        }
        
        String password = readPassword("密码");
        if (password.trim().isEmpty()) {
            printError("密码不能为空");
            pause();
            return;
        }
        
        System.out.println("\n正在验证...");
        if (userService.login(username, password)) {
            printSuccess("登录成功！欢迎, " + username);
            pause();
        } else {
            printError("登录失败！用户名或密码错误，或账户已被锁定");
            pause();
        }
    }

    /**
     * 显示主菜单
     */
    private void showMainMenu() {
        clearScreen();
        User currentUser = sessionManager.getCurrentUser();
        
        printSectionHeader("主菜单");
        System.out.println("当前用户: " + currentUser.getUsername() + 
                         " (" + currentUser.getRealName() + ")");
        System.out.println("角色: " + getRolesDisplay(currentUser));
        printSeparator();
        
        System.out.println("1. 用户管理");
        System.out.println("2. 角色管理");
        System.out.println("3. 权限管理");
        System.out.println("4. 审计日志");
        System.out.println("5. 个人中心");
        System.out.println("0. 登出");
        printSeparator();
        
        String choice = readInput("请选择");
        
        // 空输入直接返回，重新显示菜单
        if (choice.isEmpty()) {
            return;
        }
        
        switch (choice) {
            case "1":
                showUserManagementMenu();
                break;
            case "2":
                showRoleManagementMenu();
                break;
            case "3":
                showPermissionManagementMenu();
                break;
            case "4":
                showAuditLogMenu();
                break;
            case "5":
                showPersonalCenterMenu();
                break;
            case "0":
                handleLogout();
                break;
            default:
                printError("无效的选择");
                pause();
        }
    }

    /**
     * 用户管理菜单
     */
    private void showUserManagementMenu() {
        clearScreen();
        printSectionHeader("用户管理");
        
        System.out.println("1. 查看所有用户");
        System.out.println("2. 创建用户");
        System.out.println("3. 更新用户信息");
        System.out.println("4. 删除用户");
        System.out.println("5. 锁定/解锁用户");
        System.out.println("6. 重置用户密码");
        System.out.println("7. 为用户分配角色");
        System.out.println("8. 移除用户角色");
        System.out.println("0. 返回上级菜单");
        printSeparator();
        
        String choice = readInput("请选择");
        
        // 空输入直接返回，重新显示菜单
        if (choice.isEmpty()) {
            showUserManagementMenu();
            return;
        }
        
        switch (choice) {
            case "1":
                viewAllUsers();
                break;
            case "2":
                createUser();
                break;
            case "3":
                updateUser();
                break;
            case "4":
                deleteUser();
                break;
            case "5":
                lockUnlockUser();
                break;
            case "6":
                resetUserPassword();
                break;
            case "7":
                assignRoleToUser();
                break;
            case "8":
                removeRoleFromUser();
                break;
            case "0":
                return;
            default:
                printError("无效的选择");
                pause();
                showUserManagementMenu();
        }
    }

    /**
     * 查看所有用户
     */
    private void viewAllUsers() {
        clearScreen();
        printSectionHeader("用户列表");
        
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            printWarning("没有用户数据或权限不足");
        } else {
            System.out.printf("%-5s %-15s %-20s %-15s %-10s %-20s%n",
                    "ID", "用户名", "真实姓名", "邮箱", "状态", "创建时间");
            printSeparator();
            
            for (User user : users) {
                System.out.printf("%-5d %-15s %-20s %-15s %-10s %-20s%n",
                        user.getId(),
                        user.getUsername(),
                        user.getRealName() != null ? user.getRealName() : "N/A",
                        user.getEmail() != null ? user.getEmail() : "N/A",
                        user.getStatus(),
                        formatDateTime(user.getCreateTime()));
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 创建用户
     */
    private void createUser() {
        clearScreen();
        printSectionHeader("创建用户");
        
        String username = readInput("用户名");
        String password = readPassword("密码 (至少8位，包含大小写字母和数字)");
        String email = readInput("邮箱");
        String realName = readInput("真实姓名");
        
        User user = userService.createUser(username, password, email, realName);
        if (user != null) {
            printSuccess("创建用户 [" + username + "] 成功");
        } else {
            printError("创建用户失败！可能是用户名已存在或权限不足");
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 更新用户信息
     */
    private void updateUser() {
        clearScreen();
        printSectionHeader("更新用户信息");
        
        String userInput = readInput("请输入用户ID或用户名");
        Optional<User> userOpt = getUserByNameOrId(userInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else {
            User user = userOpt.get();
            System.out.println("当前用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            String email = readInput("新邮箱 (回车跳过)");
            if (email.trim().isEmpty()) {
                email = user.getEmail();
            }
            
            String realName = readInput("新真实姓名 (回车跳过)");
            if (realName.trim().isEmpty()) {
                realName = user.getRealName();
            }
            
            if (userService.updateUser(user.getId(), email, realName)) {
                printSuccess("更新用户信息成功");
            } else {
                printError("更新用户信息失败！可能是权限不足");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 删除用户
     */
    private void deleteUser() {
        clearScreen();
        printSectionHeader("删除用户");
        
        String userInput = readInput("请输入要删除的用户ID或用户名");
        Optional<User> userOpt = getUserByNameOrId(userInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else {
            User user = userOpt.get();
            String confirm = readInput("确认删除用户 [" + user.getUsername() + "] (ID: " + user.getId() + ")? (yes/no)");
            
            if ("yes".equalsIgnoreCase(confirm)) {
                if (userService.deleteUser(user.getId())) {
                    printSuccess("删除用户成功");
                } else {
                    printError("删除用户失败！可能是权限不足");
                }
            } else {
                printWarning("已取消删除操作");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 锁定/解锁用户
     */
    private void lockUnlockUser() {
        clearScreen();
        printSectionHeader("锁定/解锁用户");
        
        String userInput = readInput("请输入用户ID或用户名");
        Optional<User> userOpt = getUserByNameOrId(userInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else {
            User user = userOpt.get();
            System.out.println("当前用户: " + user.getUsername() + " (ID: " + user.getId() + ") 状态: " + user.getStatus());
            
            String action = readInput("锁定(lock) 或 解锁(unlock)");
            boolean lock = "lock".equalsIgnoreCase(action);
            
            if (userService.lockUser(user.getId(), lock)) {
                printSuccess((lock ? "锁定" : "解锁") + "用户成功");
            } else {
                printError("操作失败！可能是权限不足");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 重置用户密码
     */
    private void resetUserPassword() {
        clearScreen();
        printSectionHeader("重置用户密码");
        
        String userInput = readInput("请输入用户ID或用户名");
        Optional<User> userOpt = getUserByNameOrId(userInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else {
            User user = userOpt.get();
            System.out.println("用户: " + user.getUsername() + " (ID: " + user.getId() + ")");
            
            String newPassword = readPassword("新密码 (至少8位，包含大小写字母和数字)");
            
            if (userService.resetPassword(user.getId(), newPassword)) {
                printSuccess("重置密码成功");
            } else {
                printError("重置密码失败！可能是密码不符合策略或权限不足");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 为用户分配角色
     */
    private void assignRoleToUser() {
        clearScreen();
        printSectionHeader("为用户分配角色");
        
        String userInput = readInput("用户ID或用户名");
        String roleInput = readInput("角色ID或角色名称 (如: ADMIN, USER)");
        
        Optional<User> userOpt = getUserByNameOrId(userInput);
        Optional<Role> roleOpt = getRoleByNameOrId(roleInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else if (!roleOpt.isPresent()) {
            printError("角色不存在");
        } else {
            User user = userOpt.get();
            Role role = roleOpt.get();
            System.out.println("为用户 [" + user.getUsername() + "] 分配角色 [" + role.getName() + "]");
            
            if (userService.assignRole(user.getId(), role.getId())) {
                printSuccess("分配角色成功");
            } else {
                printError("分配角色失败！可能是权限不足");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 移除用户角色
     */
    private void removeRoleFromUser() {
        clearScreen();
        printSectionHeader("移除用户角色");
        
        String userInput = readInput("用户ID或用户名");
        String roleInput = readInput("角色ID或角色名称");
        
        Optional<User> userOpt = getUserByNameOrId(userInput);
        Optional<Role> roleOpt = getRoleByNameOrId(roleInput);
        
        if (!userOpt.isPresent()) {
            printError("用户不存在");
        } else if (!roleOpt.isPresent()) {
            printError("角色不存在");
        } else {
            User user = userOpt.get();
            Role role = roleOpt.get();
            System.out.println("从用户 [" + user.getUsername() + "] 移除角色 [" + role.getName() + "]");
            
            if (userService.removeRole(user.getId(), role.getId())) {
                printSuccess("移除角色成功");
            } else {
                printError("移除角色失败！可能是权限不足");
            }
        }
        pause();
        showUserManagementMenu();
    }

    /**
     * 角色管理菜单
     */
    private void showRoleManagementMenu() {
        clearScreen();
        printSectionHeader("角色管理");
        
        System.out.println("1. 查看所有角色");
        System.out.println("2. 创建角色");
        System.out.println("3. 更新角色");
        System.out.println("4. 删除角色");
        System.out.println("5. 为角色分配权限");
        System.out.println("6. 移除角色权限");
        System.out.println("7. 查看角色权限");
        System.out.println("0. 返回上级菜单");
        printSeparator();
        
        String choice = readInput("请选择");
        
        switch (choice) {
            case "1":
                viewAllRoles();
                break;
            case "2":
                createRole();
                break;
            case "3":
                updateRole();
                break;
            case "4":
                deleteRole();
                break;
            case "5":
                assignPermissionToRole();
                break;
            case "6":
                removePermissionFromRole();
                break;
            case "7":
                viewRolePermissions();
                break;
            case "0":
                return;
            default:
                printError("无效的选择");
                pause();
                showRoleManagementMenu();
        }
    }

    /**
     * 查看所有角色
     */
    private void viewAllRoles() {
        clearScreen();
        printSectionHeader("角色列表");
        
        List<Role> roles = roleService.getAllRoles();
        if (roles.isEmpty()) {
            printWarning("没有角色数据或权限不足");
        } else {
            System.out.printf("%-5s %-20s %-15s %-30s%n",
                    "ID", "角色名", "级别", "描述");
            printSeparator();
            
            for (Role role : roles) {
                System.out.printf("%-5d %-20s %-15s %-30s%n",
                        role.getId(),
                        role.getName(),
                        role.getLevel(),
                        role.getDescription() != null ? role.getDescription() : "N/A");
            }
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 创建角色
     */
    private void createRole() {
        clearScreen();
        printSectionHeader("创建角色");
        
        String name = readInput("角色名");
        String description = readInput("描述");
        
        System.out.println("\n可选级别:");
        for (Role.RoleLevel level : Role.RoleLevel.values()) {
            System.out.println(level.ordinal() + ". " + level + " - " + level.getDescription());
        }
        
        String levelStr = readInput("级别序号 (默认3-USER)");
        Role.RoleLevel level = Role.RoleLevel.USER;
        try {
            if (!levelStr.trim().isEmpty()) {
                int levelIndex = Integer.parseInt(levelStr);
                level = Role.RoleLevel.values()[levelIndex];
            }
        } catch (Exception e) {
            printWarning("使用默认级别: USER");
        }
        
        Role role = roleService.createRole(name, description, level);
        if (role != null) {
            printSuccess("创建角色 [" + name + "] 成功");
        } else {
            printError("创建角色失败！可能是角色名已存在或权限不足");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 更新角色
     */
    private void updateRole() {
        clearScreen();
        printSectionHeader("更新角色");
        
        String roleIdStr = readInput("角色ID");
        try {
            Long roleId = Long.parseLong(roleIdStr);
            Optional<Role> roleOpt = roleService.getRoleById(roleId);
            
            if (!roleOpt.isPresent()) {
                printError("角色不存在");
            } else {
                Role role = roleOpt.get();
                System.out.println("当前角色: " + role.getName());
                
                String name = readInput("新角色名 (回车跳过)");
                if (name.trim().isEmpty()) name = role.getName();
                
                String description = readInput("新描述 (回车跳过)");
                if (description.trim().isEmpty()) description = role.getDescription();
                
                Role.RoleLevel level = role.getLevel();
                System.out.println("\n可选级别:");
                for (Role.RoleLevel l : Role.RoleLevel.values()) {
                    System.out.println(l.ordinal() + ". " + l);
                }
                String levelStr = readInput("新级别序号 (回车跳过)");
                if (!levelStr.trim().isEmpty()) {
                    try {
                        level = Role.RoleLevel.values()[Integer.parseInt(levelStr)];
                    } catch (Exception e) {
                        printWarning("使用原级别");
                    }
                }
                
                if (roleService.updateRole(roleId, name, description, level)) {
                    printSuccess("更新角色成功");
                } else {
                    printError("更新角色失败！可能是权限不足");
                }
            }
        } catch (NumberFormatException e) {
            printError("无效的角色ID");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 删除角色
     */
    private void deleteRole() {
        clearScreen();
        printSectionHeader("删除角色");
        
        String roleIdStr = readInput("要删除的角色ID");
        try {
            Long roleId = Long.parseLong(roleIdStr);
            Optional<Role> roleOpt = roleService.getRoleById(roleId);
            
            if (!roleOpt.isPresent()) {
                printError("角色不存在");
            } else {
                Role role = roleOpt.get();
                String confirm = readInput("确认删除角色 [" + role.getName() + "]? (yes/no)");
                
                if ("yes".equalsIgnoreCase(confirm)) {
                    if (roleService.deleteRole(roleId)) {
                        printSuccess("删除角色成功");
                    } else {
                        printError("删除角色失败！可能是权限不足");
                    }
                } else {
                    printWarning("已取消删除操作");
                }
            }
        } catch (NumberFormatException e) {
            printError("无效的角色ID");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 为角色分配权限
     */
    private void assignPermissionToRole() {
        clearScreen();
        printSectionHeader("为角色分配权限");
        
        String roleIdStr = readInput("角色ID");
        String permissionIdStr = readInput("权限ID");
        
        try {
            Long roleId = Long.parseLong(roleIdStr);
            Long permissionId = Long.parseLong(permissionIdStr);
            
            if (roleService.assignPermission(roleId, permissionId)) {
                printSuccess("分配权限成功");
            } else {
                printError("分配权限失败！可能是权限不足");
            }
        } catch (NumberFormatException e) {
            printError("无效的ID");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 移除角色权限
     */
    private void removePermissionFromRole() {
        clearScreen();
        printSectionHeader("移除角色权限");
        
        String roleIdStr = readInput("角色ID");
        String permissionIdStr = readInput("权限ID");
        
        try {
            Long roleId = Long.parseLong(roleIdStr);
            Long permissionId = Long.parseLong(permissionIdStr);
            
            if (roleService.removePermission(roleId, permissionId)) {
                printSuccess("移除权限成功");
            } else {
                printError("移除权限失败！可能是权限不足");
            }
        } catch (NumberFormatException e) {
            printError("无效的ID");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 查看角色权限
     */
    private void viewRolePermissions() {
        clearScreen();
        printSectionHeader("查看角色权限");
        
        String roleIdStr = readInput("角色ID");
        try {
            Long roleId = Long.parseLong(roleIdStr);
            Optional<Role> roleOpt = roleService.getRoleById(roleId);
            
            if (!roleOpt.isPresent()) {
                printError("角色不存在");
            } else {
                Role role = roleOpt.get();
                System.out.println("\n角色: " + role.getName());
                printSeparator();
                
                List<Permission> permissions = roleService.getRolePermissions(roleId);
                if (permissions.isEmpty()) {
                    printWarning("该角色没有权限");
                } else {
                    System.out.printf("%-5s %-25s %-20s %-15s%n",
                            "ID", "权限代码", "权限名", "类型");
                    printSeparator();
                    
                    for (Permission permission : permissions) {
                        System.out.printf("%-5d %-25s %-20s %-15s%n",
                                permission.getId(),
                                permission.getCode(),
                                permission.getName(),
                                permission.getType());
                    }
                }
            }
        } catch (NumberFormatException e) {
            printError("无效的角色ID");
        }
        pause();
        showRoleManagementMenu();
    }

    /**
     * 权限管理菜单
     */
    private void showPermissionManagementMenu() {
        clearScreen();
        printSectionHeader("权限管理");
        
        System.out.println("1. 查看所有权限");
        System.out.println("2. 创建权限");
        System.out.println("3. 更新权限");
        System.out.println("4. 删除权限");
        System.out.println("5. 按类型查看权限");
        System.out.println("0. 返回上级菜单");
        printSeparator();
        
        String choice = readInput("请选择");
        
        switch (choice) {
            case "1":
                viewAllPermissions();
                break;
            case "2":
                createPermission();
                break;
            case "3":
                updatePermission();
                break;
            case "4":
                deletePermission();
                break;
            case "5":
                viewPermissionsByType();
                break;
            case "0":
                return;
            default:
                printError("无效的选择");
                pause();
                showPermissionManagementMenu();
        }
    }

    /**
     * 查看所有权限
     */
    private void viewAllPermissions() {
        clearScreen();
        printSectionHeader("权限列表");
        
        List<Permission> permissions = permissionService.getAllPermissions();
        if (permissions.isEmpty()) {
            printWarning("没有权限数据或权限不足");
        } else {
            System.out.printf("%-5s %-25s %-20s %-15s %-30s%n",
                    "ID", "权限代码", "权限名", "类型", "描述");
            printSeparator();
            
            for (Permission permission : permissions) {
                System.out.printf("%-5d %-25s %-20s %-15s %-30s%n",
                        permission.getId(),
                        permission.getCode(),
                        permission.getName(),
                        permission.getType(),
                        permission.getDescription() != null ? permission.getDescription() : "N/A");
            }
        }
        pause();
        showPermissionManagementMenu();
    }

    /**
     * 创建权限
     */
    private void createPermission() {
        clearScreen();
        printSectionHeader("创建权限");
        
        String code = readInput("权限代码 (如: USER_CREATE)");
        String name = readInput("权限名");
        String description = readInput("描述");
        
        System.out.println("\n权限类型:");
        for (Permission.PermissionType type : Permission.PermissionType.values()) {
            System.out.println(type.ordinal() + ". " + type + " - " + type.getDescription());
        }
        
        String typeStr = readInput("类型序号 (默认1-OPERATION)");
        Permission.PermissionType type = Permission.PermissionType.OPERATION;
        try {
            if (!typeStr.trim().isEmpty()) {
                int typeIndex = Integer.parseInt(typeStr);
                type = Permission.PermissionType.values()[typeIndex];
            }
        } catch (Exception e) {
            printWarning("使用默认类型: OPERATION");
        }
        
        Permission permission = permissionService.createPermission(code, name, type, description);
        if (permission != null) {
            printSuccess("创建权限 [" + code + "] 成功");
        } else {
            printError("创建权限失败！可能是权限代码已存在或权限不足");
        }
        pause();
        showPermissionManagementMenu();
    }

    /**
     * 更新权限
     */
    private void updatePermission() {
        clearScreen();
        printSectionHeader("更新权限");
        
        String permissionIdStr = readInput("权限ID");
        try {
            Long permissionId = Long.parseLong(permissionIdStr);
            Optional<Permission> permissionOpt = permissionService.getPermissionById(permissionId);
            
            if (!permissionOpt.isPresent()) {
                printError("权限不存在");
            } else {
                Permission permission = permissionOpt.get();
                System.out.println("当前权限: " + permission.getCode());
                
                String code = readInput("新权限代码 (回车跳过)");
                if (code.trim().isEmpty()) code = permission.getCode();
                
                String name = readInput("新权限名 (回车跳过)");
                if (name.trim().isEmpty()) name = permission.getName();
                
                String description = readInput("新描述 (回车跳过)");
                if (description.trim().isEmpty()) description = permission.getDescription();
                
                Permission.PermissionType type = permission.getType();
                System.out.println("\n权限类型:");
                for (Permission.PermissionType t : Permission.PermissionType.values()) {
                    System.out.println(t.ordinal() + ". " + t);
                }
                String typeStr = readInput("新类型序号 (回车跳过)");
                if (!typeStr.trim().isEmpty()) {
                    try {
                        type = Permission.PermissionType.values()[Integer.parseInt(typeStr)];
                    } catch (Exception e) {
                        printWarning("使用原类型");
                    }
                }
                
                if (permissionService.updatePermission(permissionId, code, name, type, description)) {
                    printSuccess("更新权限成功");
                } else {
                    printError("更新权限失败！可能是权限不足");
                }
            }
        } catch (NumberFormatException e) {
            printError("无效的权限ID");
        }
        pause();
        showPermissionManagementMenu();
    }

    /**
     * 删除权限
     */
    private void deletePermission() {
        clearScreen();
        printSectionHeader("删除权限");
        
        String permissionIdStr = readInput("要删除的权限ID");
        try {
            Long permissionId = Long.parseLong(permissionIdStr);
            Optional<Permission> permissionOpt = permissionService.getPermissionById(permissionId);
            
            if (!permissionOpt.isPresent()) {
                printError("权限不存在");
            } else {
                Permission permission = permissionOpt.get();
                String confirm = readInput("确认删除权限 [" + permission.getCode() + "]? (yes/no)");
                
                if ("yes".equalsIgnoreCase(confirm)) {
                    if (permissionService.deletePermission(permissionId)) {
                        printSuccess("删除权限成功");
                    } else {
                        printError("删除权限失败！可能是权限不足");
                    }
                } else {
                    printWarning("已取消删除操作");
                }
            }
        } catch (NumberFormatException e) {
            printError("无效的权限ID");
        }
        pause();
        showPermissionManagementMenu();
    }

    /**
     * 按类型查看权限
     */
    private void viewPermissionsByType() {
        clearScreen();
        printSectionHeader("按类型查看权限");
        
        System.out.println("权限类型:");
        for (Permission.PermissionType type : Permission.PermissionType.values()) {
            System.out.println(type.ordinal() + ". " + type + " - " + type.getDescription());
        }
        
        String typeStr = readInput("类型序号");
        try {
            int typeIndex = Integer.parseInt(typeStr);
            Permission.PermissionType type = Permission.PermissionType.values()[typeIndex];
            
            List<Permission> permissions = permissionService.getPermissionsByType(type);
            System.out.println("\n类型: " + type);
            printSeparator();
            
            if (permissions.isEmpty()) {
                printWarning("该类型没有权限");
            } else {
                System.out.printf("%-5s %-25s %-20s %-30s%n",
                        "ID", "权限代码", "权限名", "描述");
                printSeparator();
                
                for (Permission permission : permissions) {
                    System.out.printf("%-5d %-25s %-20s %-30s%n",
                            permission.getId(),
                            permission.getCode(),
                            permission.getName(),
                            permission.getDescription() != null ? permission.getDescription() : "N/A");
                }
            }
        } catch (Exception e) {
            printError("无效的类型序号");
        }
        pause();
        showPermissionManagementMenu();
    }

    /**
     * 审计日志菜单
     */
    private void showAuditLogMenu() {
        clearScreen();
        printSectionHeader("审计日志");
        
        System.out.println("1. 查看所有日志");
        System.out.println("2. 按用户查看日志");
        System.out.println("3. 按操作查看日志");
        System.out.println("4. 按结果查看日志");
        System.out.println("5. 导出日志");
        System.out.println("0. 返回上级菜单");
        printSeparator();
        
        String choice = readInput("请选择");
        
        switch (choice) {
            case "1":
                viewAllAuditLogs();
                break;
            case "2":
                viewAuditLogsByUser();
                break;
            case "3":
                viewAuditLogsByOperation();
                break;
            case "4":
                viewAuditLogsByResult();
                break;
            case "5":
                exportAuditLogs();
                break;
            case "0":
                return;
            default:
                printError("无效的选择");
                pause();
                showAuditLogMenu();
        }
    }

    /**
     * 查看所有审计日志
     */
    private void viewAllAuditLogs() {
        clearScreen();
        printSectionHeader("审计日志列表");
        
        List<AuditLog> logs = auditService.getAllLogs();
        displayAuditLogs(logs);
        
        pause();
        showAuditLogMenu();
    }

    /**
     * 按用户查看审计日志
     */
    private void viewAuditLogsByUser() {
        clearScreen();
        printSectionHeader("按用户查看日志");
        
        String userIdStr = readInput("用户ID");
        String limitStr = readInput("数量限制 (默认100)");
        
        try {
            Long userId = Long.parseLong(userIdStr);
            int limit = limitStr.trim().isEmpty() ? 100 : Integer.parseInt(limitStr);
            
            List<AuditLog> logs = auditService.getLogsByUserId(userId, limit);
            displayAuditLogs(logs);
        } catch (NumberFormatException e) {
            printError("无效的输入");
        }
        
        pause();
        showAuditLogMenu();
    }

    /**
     * 按操作查看审计日志
     */
    private void viewAuditLogsByOperation() {
        clearScreen();
        printSectionHeader("按操作查看日志");
        
        String operation = readInput("操作类型 (如: LOGIN, CREATE_USER)");
        String limitStr = readInput("数量限制 (默认100)");
        
        try {
            int limit = limitStr.trim().isEmpty() ? 100 : Integer.parseInt(limitStr);
            List<AuditLog> logs = auditService.getLogsByOperation(operation, limit);
            displayAuditLogs(logs);
        } catch (NumberFormatException e) {
            printError("无效的数量");
        }
        
        pause();
        showAuditLogMenu();
    }

    /**
     * 按结果查看审计日志
     */
    private void viewAuditLogsByResult() {
        clearScreen();
        printSectionHeader("按结果查看日志");
        
        System.out.println("结果类型:");
        for (AuditLog.OperationResult result : AuditLog.OperationResult.values()) {
            System.out.println(result.ordinal() + ". " + result);
        }
        
        String resultStr = readInput("结果序号");
        String limitStr = readInput("数量限制 (默认100)");
        
        try {
            AuditLog.OperationResult result = AuditLog.OperationResult.values()[Integer.parseInt(resultStr)];
            int limit = limitStr.trim().isEmpty() ? 100 : Integer.parseInt(limitStr);
            
            List<AuditLog> logs = auditService.getLogsByResult(result, limit);
            displayAuditLogs(logs);
        } catch (Exception e) {
            printError("无效的输入");
        }
        
        pause();
        showAuditLogMenu();
    }

    /**
     * 导出审计日志
     */
    private void exportAuditLogs() {
        clearScreen();
        printSectionHeader("导出审计日志");
        
        List<AuditLog> logs = auditService.getAllLogs();
        String export = auditService.exportLogs(logs);
        
        if (export != null) {
            System.out.println(export);
            printSuccess("日志导出成功");
        } else {
            printError("导出失败！可能是权限不足");
        }
        
        pause();
        showAuditLogMenu();
    }

    /**
     * 显示审计日志
     */
    private void displayAuditLogs(List<AuditLog> logs) {
        if (logs.isEmpty()) {
            printWarning("没有日志记录或权限不足");
        } else {
            System.out.printf("%-5s %-15s %-20s %-15s %-25s %-10s%n",
                    "ID", "用户名", "操作", "目标", "详情", "结果");
            printSeparator();
            
            for (AuditLog log : logs) {
                System.out.printf("%-5d %-15s %-20s %-15s %-25s %-10s%n",
                        log.getId(),
                        log.getUsername(),
                        log.getOperation(),
                        log.getTarget(),
                        truncateString(log.getDetail(), 25),
                        log.getResult());
            }
        }
    }

    /**
     * 个人中心菜单
     */
    private void showPersonalCenterMenu() {
        clearScreen();
        User currentUser = sessionManager.getCurrentUser();
        
        printSectionHeader("个人中心");
        System.out.println("用户名: " + currentUser.getUsername());
        System.out.println("真实姓名: " + (currentUser.getRealName() != null ? currentUser.getRealName() : "未设置"));
        System.out.println("邮箱: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "未设置"));
        System.out.println("状态: " + currentUser.getStatus());
        System.out.println("角色: " + getRolesDisplay(currentUser));
        printSeparator();
        
        System.out.println("1. 修改密码");
        System.out.println("2. 查看我的权限");
        System.out.println("3. 查看我的操作日志");
        System.out.println("0. 返回上级菜单");
        printSeparator();
        
        String choice = readInput("请选择");
        
        switch (choice) {
            case "1":
                changePassword();
                break;
            case "2":
                viewMyPermissions();
                break;
            case "3":
                viewMyAuditLogs();
                break;
            case "0":
                return;
            default:
                printError("无效的选择");
                pause();
                showPersonalCenterMenu();
        }
    }

    /**
     * 修改密码
     */
    private void changePassword() {
        clearScreen();
        printSectionHeader("修改密码");
        
        User currentUser = sessionManager.getCurrentUser();
        
        String oldPassword = readPassword("当前密码");
        String newPassword = readPassword("新密码 (至少8位，包含大小写字母和数字)");
        String confirmPassword = readPassword("确认新密码");
        
        if (!newPassword.equals(confirmPassword)) {
            printError("两次输入的密码不一致");
        } else if (userService.changePassword(currentUser.getId(), oldPassword, newPassword)) {
            printSuccess("密码修改成功");
        } else {
            printError("密码修改失败！可能是当前密码错误或新密码不符合策略");
        }
        
        pause();
        showPersonalCenterMenu();
    }

    /**
     * 查看我的权限
     */
    private void viewMyPermissions() {
        clearScreen();
        printSectionHeader("我的权限");
        
        User currentUser = sessionManager.getCurrentUser();
        List<Permission> permissions = currentUser.getAllPermissions().stream().toList();
        
        if (permissions.isEmpty()) {
            printWarning("您还没有任何权限");
        } else {
            System.out.printf("%-25s %-20s %-15s%n", "权限代码", "权限名", "类型");
            printSeparator();
            
            for (Permission permission : permissions) {
                System.out.printf("%-25s %-20s %-15s%n",
                        permission.getCode(),
                        permission.getName(),
                        permission.getType());
            }
        }
        
        pause();
        showPersonalCenterMenu();
    }

    /**
     * 查看我的操作日志
     */
    private void viewMyAuditLogs() {
        clearScreen();
        printSectionHeader("我的操作日志");
        
        User currentUser = sessionManager.getCurrentUser();
        List<AuditLog> logs = auditService.getLogsByUserId(currentUser.getId(), 50);
        
        displayAuditLogs(logs);
        
        pause();
        showPersonalCenterMenu();
    }

    /**
     * 处理登出
     */
    private void handleLogout() {
        userService.logout();
        printSuccess("已登出");
        pause();
    }

    // ==================== 辅助方法 ====================

    /**
     * 清屏
     */
    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * 打印分区标题
     */
    private void printSectionHeader(String title) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  " + title);
        System.out.println("=".repeat(60));
    }

    /**
     * 打印分隔线
     */
    private void printSeparator() {
        System.out.println("-".repeat(60));
    }

    /**
     * 读取输入
     */
    private String readInput(String prompt) {
        System.out.print(prompt + ": ");
        // 统一使用 nextLine 读取本行剩余内容，避免与 pause() 冲突
        String input = scanner.nextLine();
        return input.trim();
    }

    /**
     * 读取密码（简化版，实际应隐藏输入）
     */
    private String readPassword(String prompt) {
        System.out.print(prompt + ": ");
        String input = scanner.nextLine();
        return input.trim();
    }

    /**
     * 暂停等待用户按键
     */
    private void pause() {
        System.out.print("\n按回车键继续...");
        try {
            // 直接消费下一行输入，保证只需按一次回车即可继续
            scanner.nextLine();
        } catch (Exception e) {
            // 忽略异常
        }
        clearScreen();
    }

    /**
     * 打印成功消息
     */
    private void printSuccess(String message) {
        System.out.println("\n✓ [成功] " + message);
    }

    /**
     * 打印错误消息
     */
    private void printError(String message) {
        System.out.println("\n• [提示] " + message);
    }

    /**
     * 打印警告消息
     */
    private void printWarning(String message) {
        System.out.println("\n• [注意] " + message);
    }

    /**
     * 打印再见消息
     */
    private void printGoodbyeMessage() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  感谢使用RBAC权限管理系统！");
        System.out.println("  再见！");
        System.out.println("=".repeat(60) + "\n");
    }

    /**
     * 获取角色显示字符串
     */
    private String getRolesDisplay(User user) {
        if (user.getRoles().isEmpty()) {
            return "无";
        }
        return user.getRoles().stream()
                .map(Role::getName)
                .reduce((a, b) -> a + ", " + b)
                .orElse("无");
    }

    /**
     * 格式化日期时间
     */
    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "N/A";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return dateTime.format(formatter);
    }

    /**
     * 截断字符串
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) {
            return "N/A";
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * 通过用户名或ID获取用户
     */
    private Optional<User> getUserByNameOrId(String input) {
        // 优先按用户名查找
        Optional<User> userByName = userService.getUserByUsername(input);
        if (userByName.isPresent()) {
            return userByName;
        }
        
        // 用户名找不到，尝试作为ID解析
        try {
            Long userId = Long.parseLong(input);
            return userService.getUserById(userId);
        } catch (NumberFormatException e) {
            // 既不是有效用户名也不是有效ID
            return Optional.empty();
        }
    }

    /**
     * 通过角色名或ID获取角色
     */
    private Optional<Role> getRoleByNameOrId(String input) {
        // 优先按角色名查找
        Optional<Role> roleByName = roleService.getRoleByName(input);
        if (roleByName.isPresent()) {
            return roleByName;
        }
        
        // 角色名找不到，尝试作为ID解析
        try {
            Long roleId = Long.parseLong(input);
            return roleService.getRoleById(roleId);
        } catch (NumberFormatException e) {
            // 既不是有效角色名也不是有效ID
            return Optional.empty();
        }
    }

    /**
     * 通过权限名或ID获取权限
     */
    private Optional<Permission> getPermissionByNameOrId(String input) {
        // 优先按权限代码查找
        Optional<Permission> permByCode = permissionService.getPermissionByCode(input);
        if (permByCode.isPresent()) {
            return permByCode;
        }
        
        // 权限代码找不到，尝试作为ID解析
        try {
            Long permId = Long.parseLong(input);
            return permissionService.getPermissionById(permId);
        } catch (NumberFormatException e) {
            // 既不是有效权限代码也不是有效ID
            return Optional.empty();
        }
    }
}
