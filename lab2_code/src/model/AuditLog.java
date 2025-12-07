package model;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 * 用于记录系统中的重要操作，实现权限审计功能
 */
public class AuditLog {
    private Long id;
    private Long userId;
    private String username;
    private String operation; // 操作类型
    private String target; // 操作目标
    private String detail; // 操作详情
    private OperationResult result; // 操作结果
    private String ipAddress; // IP地址
    private LocalDateTime operationTime;

    /**
     * 操作结果枚举
     */
    public enum OperationResult {
        SUCCESS("成功"),
        FAILURE("失败"),
        DENIED("拒绝");

        private final String description;

        OperationResult(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public AuditLog() {
        this.operationTime = LocalDateTime.now();
    }

    public AuditLog(Long userId, String username, String operation, String target) {
        this();
        this.userId = userId;
        this.username = username;
        this.operation = operation;
        this.target = target;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public OperationResult getResult() {
        return result;
    }

    public void setResult(OperationResult result) {
        this.result = result;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public LocalDateTime getOperationTime() {
        return operationTime;
    }

    public void setOperationTime(LocalDateTime operationTime) {
        this.operationTime = operationTime;
    }

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", operation='" + operation + '\'' +
                ", target='" + target + '\'' +
                ", result=" + result +
                ", operationTime=" + operationTime +
                '}';
    }
}
