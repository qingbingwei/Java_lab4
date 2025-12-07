package dao;

import model.AuditLog;
import model.AuditLog.OperationResult;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 审计日志数据访问对象
 */
public class AuditLogDAO implements BaseDAO<AuditLog, Long> {
    private static final Logger LOGGER = Logger.getLogger(AuditLogDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public AuditLogDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public AuditLog save(AuditLog log) {
        String sql = "INSERT INTO audit_logs (user_id, username, operation, target, detail, " +
                     "result, ip_address, operation_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setLong(1, log.getUserId() != null ? log.getUserId() : 0);
            stmt.setString(2, log.getUsername());
            stmt.setString(3, log.getOperation());
            stmt.setString(4, log.getTarget());
            stmt.setString(5, log.getDetail());
            stmt.setString(6, log.getResult() != null ? log.getResult().name() : OperationResult.SUCCESS.name());
            stmt.setString(7, log.getIpAddress());
            stmt.setTimestamp(8, Timestamp.valueOf(log.getOperationTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        log.setId(generatedKeys.getLong(1));
                        return log;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "保存审计日志失败", e);
        }
        return null;
    }

    @Override
    public boolean update(AuditLog log) {
        // 审计日志通常不允许修改
        LOGGER.warning("审计日志不应被修改");
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        // 审计日志通常不允许删除
        LOGGER.warning("审计日志不应被删除");
        return false;
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        String sql = "SELECT * FROM audit_logs WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractAuditLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找审计日志失败: ID=" + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<AuditLog> findAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs ORDER BY operation_time DESC LIMIT 1000";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(extractAuditLogFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询所有审计日志失败", e);
        }
        return logs;
    }

    /**
     * 根据用户ID查找审计日志
     */
    public List<AuditLog> findByUserId(Long userId, int limit) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE user_id=? ORDER BY operation_time DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractAuditLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询用户审计日志失败: userId=" + userId, e);
        }
        return logs;
    }

    /**
     * 根据操作类型查找审计日志
     */
    public List<AuditLog> findByOperation(String operation, int limit) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE operation=? ORDER BY operation_time DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, operation);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractAuditLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "按操作查询审计日志失败", e);
        }
        return logs;
    }

    /**
     * 根据时间范围查找审计日志
     */
    public List<AuditLog> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE operation_time BETWEEN ? AND ? " +
                     "ORDER BY operation_time DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(start));
            stmt.setTimestamp(2, Timestamp.valueOf(end));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractAuditLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "按时间范围查询审计日志失败", e);
        }
        return logs;
    }

    /**
     * 根据结果查找审计日志
     */
    public List<AuditLog> findByResult(OperationResult result, int limit) {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM audit_logs WHERE result=? ORDER BY operation_time DESC LIMIT ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, result.name());
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(extractAuditLogFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "按结果查询审计日志失败", e);
        }
        return logs;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM audit_logs";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计审计日志数量失败", e);
        }
        return 0;
    }

    /**
     * 从ResultSet提取AuditLog对象
     */
    private AuditLog extractAuditLogFromResultSet(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getLong("id"));
        log.setUserId(rs.getLong("user_id"));
        log.setUsername(rs.getString("username"));
        log.setOperation(rs.getString("operation"));
        log.setTarget(rs.getString("target"));
        log.setDetail(rs.getString("detail"));
        log.setResult(OperationResult.valueOf(rs.getString("result")));
        log.setIpAddress(rs.getString("ip_address"));
        
        Timestamp operationTime = rs.getTimestamp("operation_time");
        if (operationTime != null) {
            log.setOperationTime(operationTime.toLocalDateTime());
        }
        
        return log;
    }
}