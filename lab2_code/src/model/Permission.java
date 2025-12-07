package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 权限实体类
 * 代表系统中的权限，包括菜单权限、操作权限和资源权限
 */
public class Permission {
    private Long id;
    private String code; // 权限编码，如 USER_CREATE, USER_DELETE
    private String name; // 权限名称
    private String description;
    private PermissionType type; // 权限类型
    private String resourcePath; // 资源路径（可选）
    private LocalDateTime createTime;

    /**
     * 权限类型枚举
     */
    public enum PermissionType {
        MENU("菜单权限"),      // 访问某个菜单的权限
        OPERATION("操作权限"),  // 执行某个操作的权限
        RESOURCE("资源权限"),   // 访问某个资源的权限
        DATA("数据权限");       // 数据级别的权限

        private final String description;

        PermissionType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Permission() {
        this.createTime = LocalDateTime.now();
    }

    public Permission(String code, String name, PermissionType type) {
        this();
        this.code = code;
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public PermissionType getType() {
        return type;
    }

    public void setType(PermissionType type) {
        this.type = type;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, code);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", resourcePath='" + resourcePath + '\'' +
                '}';
    }
}
