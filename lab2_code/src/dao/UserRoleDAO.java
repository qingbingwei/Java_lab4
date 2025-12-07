package dao;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用户角色关联DAO
 * 管理用户和角色之间的多对多关系
 */
public class UserRoleDAO {
    private static final Logger LOGGER = Logger.getLogger(UserRoleDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public UserRoleDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 为用户分配角色
     */
    public boolean assignRoleToUser(Long userId, Long roleId) {
        // 先检查是否已存在
        if (hasRole(userId, roleId)) {
            LOGGER.info("用户已拥有该角色: userId=" + userId + ", roleId=" + roleId);
            return true;
        }

        String sql = "INSERT INTO user_roles (user_id, role_id) VALUES (?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("角色分配成功: userId=" + userId + ", roleId=" + roleId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "分配角色失败", e);
        }
        return false;
    }

    /**
     * 移除用户的角色
     */
    public boolean removeRoleFromUser(Long userId, Long roleId) {
        String sql = "DELETE FROM user_roles WHERE user_id=? AND role_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("角色移除成功: userId=" + userId + ", roleId=" + roleId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "移除角色失败", e);
        }
        return false;
    }

    /**
     * 移除用户的所有角色
     */
    public boolean removeAllRolesFromUser(Long userId) {
        String sql = "DELETE FROM user_roles WHERE user_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.executeUpdate();
            LOGGER.info("已移除用户的所有角色: userId=" + userId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "移除用户所有角色失败", e);
        }
        return false;
    }

    /**
     * 检查用户是否拥有指定角色
     */
    public boolean hasRole(Long userId, Long roleId) {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE user_id=? AND role_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            stmt.setLong(2, roleId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "检查用户角色失败", e);
        }
        return false;
    }

    /**
     * 统计角色的用户数量
     */
    public long countUsersByRole(Long roleId) {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE role_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计角色用户数失败", e);
        }
        return 0;
    }

    /**
     * 统计用户的角色数量
     */
    public long countRolesByUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM user_roles WHERE user_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计用户角色数失败", e);
        }
        return 0;
    }
}
