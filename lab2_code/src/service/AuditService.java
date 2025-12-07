package service;

import dao.AuditLogDAO;
import model.AuditLog;
import model.AuditLog.OperationResult;
import model.User;
import util.SessionManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * 审计服务类
 * 提供审计日志管理和查询功能
 */
public class AuditService {
    private static final Logger LOGGER = Logger.getLogger(AuditService.class.getName());
    private final AuditLogDAO auditLogDAO;
    private final SessionManager sessionManager;

    public AuditService() {
        this.auditLogDAO = new AuditLogDAO();
        this.sessionManager = SessionManager.getInstance();
    }

    /**
     * 记录操作日志
     */
    public void logOperation(String operation, String target, String detail, OperationResult result) {
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

    /**
     * 查询所有审计日志
     */
    public List<AuditLog> getAllLogs() {
        if (!hasPermission("AUDIT_VIEW")) {
            logOperation("VIEW_AUDIT", "all", "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return auditLogDAO.findAll();
    }

    /**
     * 根据用户查询审计日志
     */
    public List<AuditLog> getLogsByUserId(Long userId, int limit) {
        if (!hasPermission("AUDIT_VIEW")) {
            logOperation("VIEW_AUDIT", "userId=" + userId, "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return auditLogDAO.findByUserId(userId, limit);
    }

    /**
     * 根据操作类型查询审计日志
     */
    public List<AuditLog> getLogsByOperation(String operation, int limit) {
        if (!hasPermission("AUDIT_VIEW")) {
            logOperation("VIEW_AUDIT", "operation=" + operation, "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return auditLogDAO.findByOperation(operation, limit);
    }

    /**
     * 根据时间范围查询审计日志
     */
    public List<AuditLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        if (!hasPermission("AUDIT_VIEW")) {
            logOperation("VIEW_AUDIT", "timeRange", "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return auditLogDAO.findByTimeRange(start, end);
    }

    /**
     * 根据结果查询审计日志
     */
    public List<AuditLog> getLogsByResult(OperationResult result, int limit) {
        if (!hasPermission("AUDIT_VIEW")) {
            logOperation("VIEW_AUDIT", "result=" + result, "权限不足", OperationResult.DENIED);
            return List.of();
        }

        return auditLogDAO.findByResult(result, limit);
    }

    /**
     * 导出审计日志
     */
    public String exportLogs(List<AuditLog> logs) {
        if (!hasPermission("AUDIT_EXPORT")) {
            logOperation("EXPORT_AUDIT", "logs", "权限不足", OperationResult.DENIED);
            return null;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("审计日志导出\n");
        sb.append("导出时间: ").append(LocalDateTime.now()).append("\n");
        sb.append("记录数量: ").append(logs.size()).append("\n");
        sb.append("=".repeat(80)).append("\n\n");

        sb.append(String.format("%-5s %-15s %-20s %-20s %-30s %-10s %-20s%n",
                "ID", "用户名", "操作", "目标", "详情", "结果", "时间"));
        sb.append("-".repeat(130)).append("\n");

        for (AuditLog log : logs) {
            sb.append(String.format("%-5d %-15s %-20s %-20s %-30s %-10s %-20s%n",
                    log.getId(),
                    log.getUsername(),
                    log.getOperation(),
                    log.getTarget(),
                    log.getDetail(),
                    log.getResult(),
                    log.getOperationTime()));
        }

        logOperation("EXPORT_AUDIT", "logs", "导出" + logs.size() + "条记录", OperationResult.SUCCESS);
        return sb.toString();
    }

    /**
     * 统计审计日志数量
     */
    public long getLogCount() {
        return auditLogDAO.count();
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
