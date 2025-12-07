package model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 角色实体类
 * 代表系统中的角色，连接用户和权限
 */
public class Role {
    private Long id;
    private String name;
    private String description;
    private RoleLevel level; // 角色级别，用于层级控制
    private LocalDateTime createTime;
    private Set<Permission> permissions; // 角色拥有的权限集合

    /**
     * 角色级别枚举 - 用于实现角色层级管理
     */
    public enum RoleLevel {
        SUPER_ADMIN(1, "超级管理员"),
        ADMIN(2, "管理员"),
        MANAGER(3, "经理"),
        USER(4, "普通用户"),
        GUEST(5, "访客");

        private final int level;
        private final String description;

        RoleLevel(int level, String description) {
            this.level = level;
            this.description = description;
        }

        public int getLevel() {
            return level;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 检查当前角色是否高于目标角色
         */
        public boolean isHigherThan(RoleLevel target) {
            return this.level < target.level;
        }
    }

    public Role() {
        this.permissions = new HashSet<>();
        this.createTime = LocalDateTime.now();
        this.level = RoleLevel.USER;
    }

    public Role(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoleLevel getLevel() {
        return level;
    }

    public void setLevel(RoleLevel level) {
        this.level = level;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    /**
     * 为角色添加权限
     */
    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    /**
     * 移除角色权限
     */
    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    /**
     * 检查角色是否拥有指定权限
     */
    public boolean hasPermission(String permissionCode) {
        return permissions.stream()
                .anyMatch(permission -> permission.getCode().equals(permissionCode));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", level=" + level +
                ", permissions=" + permissions.size() +
                '}';
    }
}
