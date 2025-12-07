package dao;

import model.Permission;
import model.Permission.PermissionType;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 权限数据访问对象
 */
public class PermissionDAO implements BaseDAO<Permission, Long> {
    private static final Logger LOGGER = Logger.getLogger(PermissionDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public PermissionDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Permission save(Permission permission) {
        String sql = "INSERT INTO permissions (code, name, description, type, resource_path, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, permission.getCode());
            stmt.setString(2, permission.getName());
            stmt.setString(3, permission.getDescription());
            stmt.setString(4, permission.getType().name());
            stmt.setString(5, permission.getResourcePath());
            stmt.setTimestamp(6, Timestamp.valueOf(permission.getCreateTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        permission.setId(generatedKeys.getLong(1));
                        LOGGER.info("权限创建成功: " + permission.getCode());
                        return permission;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "保存权限失败: " + permission.getCode(), e);
        }
        return null;
    }

    @Override
    public boolean update(Permission permission) {
        String sql = "UPDATE permissions SET code=?, name=?, description=?, type=?, resource_path=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, permission.getCode());
            stmt.setString(2, permission.getName());
            stmt.setString(3, permission.getDescription());
            stmt.setString(4, permission.getType().name());
            stmt.setString(5, permission.getResourcePath());
            stmt.setLong(6, permission.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("权限更新成功: " + permission.getCode());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新权限失败: " + permission.getCode(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM permissions WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("权限删除成功: ID=" + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "删除权限失败: ID=" + id, e);
        }
        return false;
    }

    @Override
    public Optional<Permission> findById(Long id) {
        String sql = "SELECT * FROM permissions WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractPermissionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找权限失败: ID=" + id, e);
        }
        return Optional.empty();
    }

    /**
     * 根据权限代码查找
     */
    public Optional<Permission> findByCode(String code) {
        String sql = "SELECT * FROM permissions WHERE code=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractPermissionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找权限失败: " + code, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Permission> findAll() {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions ORDER BY type, code";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                permissions.add(extractPermissionFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询所有权限失败", e);
        }
        return permissions;
    }

    /**
     * 根据类型查找权限
     */
    public List<Permission> findByType(PermissionType type) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT * FROM permissions WHERE type=? ORDER BY code";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, type.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(extractPermissionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "按类型查询权限失败", e);
        }
        return permissions;
    }

    /**
     * 查找角色的所有权限
     */
    public List<Permission> findPermissionsByRoleId(Long roleId) {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT p.* FROM permissions p " +
                     "INNER JOIN role_permissions rp ON p.id = rp.permission_id " +
                     "WHERE rp.role_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissions.add(extractPermissionFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询角色权限失败: roleId=" + roleId, e);
        }
        return permissions;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * 检查权限代码是否存在
     */
    public boolean existsByCode(String code) {
        return findByCode(code).isPresent();
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM permissions";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计权限数量失败", e);
        }
        return 0;
    }

    /**
     * 从ResultSet提取Permission对象
     */
    private Permission extractPermissionFromResultSet(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setId(rs.getLong("id"));
        permission.setCode(rs.getString("code"));
        permission.setName(rs.getString("name"));
        permission.setDescription(rs.getString("description"));
        permission.setType(PermissionType.valueOf(rs.getString("type")));
        permission.setResourcePath(rs.getString("resource_path"));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            permission.setCreateTime(createTime.toLocalDateTime());
        }
        
        return permission;
    }
}
