package model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 用户实体类
 * 代表系统中的用户，包含用户基本信息和角色关联
 */
public class User {
    private Long id;
    private String username;
    private String password; // 加密后的密码
    private String email;
    private String realName;
    private UserStatus status; // 使用枚举表示用户状态
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;
    private Set<Role> roles; // 用户拥有的角色集合

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        ACTIVE("激活"),
        LOCKED("锁定"),
        DISABLED("禁用");

        private final String description;

        UserStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public User() {
        this.roles = new HashSet<>();
        this.status = UserStatus.ACTIVE;
        this.createTime = LocalDateTime.now();
    }

    public User(String username, String password) {
        this();
        this.username = username;
        this.password = password;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(LocalDateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * 为用户添加角色
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    /**
     * 移除用户角色
     */
    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean hasRole(String roleName) {
        return roles.stream().anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * 获取用户所有权限（通过角色）
     */
    public Set<Permission> getAllPermissions() {
        Set<Permission> permissions = new HashSet<>();
        roles.forEach(role -> permissions.addAll(role.getPermissions()));
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", realName='" + realName + '\'' +
                ", status=" + status +
                ", roles=" + roles.size() +
                '}';
    }
}
