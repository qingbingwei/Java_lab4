package model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 资源实体类
 * 代表系统中的受保护资源，如文件、API、数据等
 */
public class Resource {
    private Long id;
    private String name;
    private String path; // 资源路径
    private ResourceType type;
    private String description;
    private Long ownerId; // 资源所有者ID
    private LocalDateTime createTime;

    /**
     * 资源类型枚举
     */
    public enum ResourceType {
        FILE("文件"),
        API("接口"),
        DATA("数据"),
        MODULE("模块"),
        SYSTEM("系统");

        private final String description;

        ResourceType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public Resource() {
        this.createTime = LocalDateTime.now();
    }

    public Resource(String name, String path, ResourceType type) {
        this();
        this.name = name;
        this.path = path;
        this.type = type;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
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
        Resource resource = (Resource) o;
        return Objects.equals(id, resource.id) && Objects.equals(path, resource.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }

    @Override
    public String toString() {
        return "Resource{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", type=" + type +
                ", ownerId=" + ownerId +
                '}';
    }
}
