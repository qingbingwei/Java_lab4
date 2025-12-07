package dao;

import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 角色权限关联DAO
 * 管理角色和权限之间的多对多关系
 */
public class RolePermissionDAO {
    private static final Logger LOGGER = Logger.getLogger(RolePermissionDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public RolePermissionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    /**
     * 为角色分配权限
     */
    public boolean assignPermissionToRole(Long roleId, Long permissionId) {
        // 先检查是否已存在
        if (hasPermission(roleId, permissionId)) {
            LOGGER.info("角色已拥有该权限: roleId=" + roleId + ", permissionId=" + permissionId);
            return true;
        }

        String sql = "INSERT INTO role_permissions (role_id, permission_id) VALUES (?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.setLong(2, permissionId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("权限分配成功: roleId=" + roleId + ", permissionId=" + permissionId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "分配权限失败", e);
        }
        return false;
    }

    /**
     * 移除角色的权限
     */
    public boolean removePermissionFromRole(Long roleId, Long permissionId) {
        String sql = "DELETE FROM role_permissions WHERE role_id=? AND permission_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.setLong(2, permissionId);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("权限移除成功: roleId=" + roleId + ", permissionId=" + permissionId);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "移除权限失败", e);
        }
        return false;
    }

    /**
     * 移除角色的所有权限
     */
    public boolean removeAllPermissionsFromRole(Long roleId) {
        String sql = "DELETE FROM role_permissions WHERE role_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.executeUpdate();
            LOGGER.info("已移除角色的所有权限: roleId=" + roleId);
            return true;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "移除角色所有权限失败", e);
        }
        return false;
    }

    /**
     * 检查角色是否拥有指定权限
     */
    public boolean hasPermission(Long roleId, Long permissionId) {
        String sql = "SELECT COUNT(*) FROM role_permissions WHERE role_id=? AND permission_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            stmt.setLong(2, permissionId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "检查角色权限失败", e);
        }
        return false;
    }

    /**
     * 统计权限分配给了多少个角色
     */
    public long countRolesByPermission(Long permissionId) {
        String sql = "SELECT COUNT(*) FROM role_permissions WHERE permission_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, permissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计权限角色数失败", e);
        }
        return 0;
    }

    /**
     * 统计角色的权限数量
     */
    public long countPermissionsByRole(Long roleId) {
        String sql = "SELECT COUNT(*) FROM role_permissions WHERE role_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计角色权限数失败", e);
        }
        return 0;
    }

    /**
     * 根据角色ID查询该角色拥有的所有权限ID
     */
    public java.util.List<Long> findPermissionIdsByRoleId(Long roleId) {
        java.util.List<Long> permissionIds = new java.util.ArrayList<>();
        String sql = "SELECT permission_id FROM role_permissions WHERE role_id=?";

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roleId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissionIds.add(rs.getLong("permission_id"));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询角色权限ID失败", e);
        }
        return permissionIds;
    }
}
