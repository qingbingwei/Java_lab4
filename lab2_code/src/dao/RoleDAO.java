package dao;

import model.Role;
import model.Role.RoleLevel;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 角色数据访问对象
 */
public class RoleDAO implements BaseDAO<Role, Long> {
    private static final Logger LOGGER = Logger.getLogger(RoleDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public RoleDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public Role save(Role role) {
        String sql = "INSERT INTO roles (name, description, level, create_time) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setString(3, role.getLevel().name());
            stmt.setTimestamp(4, Timestamp.valueOf(role.getCreateTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        role.setId(generatedKeys.getLong(1));
                        LOGGER.info("角色创建成功: " + role.getName());
                        return role;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "保存角色失败: " + role.getName(), e);
        }
        return null;
    }

    @Override
    public boolean update(Role role) {
        String sql = "UPDATE roles SET name=?, description=?, level=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, role.getName());
            stmt.setString(2, role.getDescription());
            stmt.setString(3, role.getLevel().name());
            stmt.setLong(4, role.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("角色更新成功: " + role.getName());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新角色失败: " + role.getName(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM roles WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("角色删除成功: ID=" + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "删除角色失败: ID=" + id, e);
        }
        return false;
    }

    @Override
    public Optional<Role> findById(Long id) {
        String sql = "SELECT * FROM roles WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractRoleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找角色失败: ID=" + id, e);
        }
        return Optional.empty();
    }

    /**
     * 根据角色名查找
     */
    public Optional<Role> findByName(String name) {
        String sql = "SELECT * FROM roles WHERE name=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractRoleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找角色失败: " + name, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles ORDER BY level, name";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                roles.add(extractRoleFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询所有角色失败", e);
        }
        return roles;
    }

    /**
     * 查找用户的所有角色
     */
    public List<Role> findRolesByUserId(Long userId) {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT r.* FROM roles r " +
                     "INNER JOIN user_roles ur ON r.id = ur.role_id " +
                     "WHERE ur.user_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    roles.add(extractRoleFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询用户角色失败: userId=" + userId, e);
        }
        return roles;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * 检查角色名是否存在
     */
    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM roles";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计角色数量失败", e);
        }
        return 0;
    }

    /**
     * 从ResultSet提取Role对象
     */
    private Role extractRoleFromResultSet(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getLong("id"));
        role.setName(rs.getString("name"));
        role.setDescription(rs.getString("description"));
        role.setLevel(RoleLevel.valueOf(rs.getString("level")));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            role.setCreateTime(createTime.toLocalDateTime());
        }
        
        return role;
    }
}
