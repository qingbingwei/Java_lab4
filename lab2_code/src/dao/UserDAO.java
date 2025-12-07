package dao;

import model.User;
import model.User.UserStatus;
import util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 用户数据访问对象
 * 实现用户相关的数据库操作
 */
public class UserDAO implements BaseDAO<User, Long> {
    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());
    private final DatabaseConnection dbConnection;

    public UserDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, email, real_name, status, create_time) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRealName());
            stmt.setString(5, user.getStatus().name());
            stmt.setTimestamp(6, Timestamp.valueOf(user.getCreateTime()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                        LOGGER.info("用户创建成功: " + user.getUsername());
                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "保存用户失败: " + user.getUsername(), e);
        }
        return null;
    }

    @Override
    public boolean update(User user) {
        String sql = "UPDATE users SET username=?, password=?, email=?, real_name=?, " +
                     "status=?, last_login_time=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getRealName());
            stmt.setString(5, user.getStatus().name());
            stmt.setTimestamp(6, user.getLastLoginTime() != null ? 
                              Timestamp.valueOf(user.getLastLoginTime()) : null);
            stmt.setLong(7, user.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("用户更新成功: " + user.getUsername());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新用户失败: " + user.getUsername(), e);
        }
        return false;
    }

    @Override
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM users WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                LOGGER.info("用户删除成功: ID=" + id);
                return true;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "删除用户失败: ID=" + id, e);
        }
        return false;
    }

    @Override
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找用户失败: ID=" + id, e);
        }
        return Optional.empty();
    }

    /**
     * 根据用户名查找用户
     */
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查找用户失败: " + username, e);
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY create_time DESC";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                users.add(extractUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "查询所有用户失败", e);
        }
        return users;
    }

    /**
     * 根据状态查找用户
     */
    public List<User> findByStatus(UserStatus status) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE status=? ORDER BY create_time DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "按状态查询用户失败", e);
        }
        return users;
    }

    @Override
    public boolean existsById(Long id) {
        return findById(id).isPresent();
    }

    /**
     * 检查用户名是否存在
     */
    public boolean existsByUsername(String username) {
        return findByUsername(username).isPresent();
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "统计用户数量失败", e);
        }
        return 0;
    }

    /**
     * 更新最后登录时间
     */
    public boolean updateLastLoginTime(Long userId) {
        String sql = "UPDATE users SET last_login_time=? WHERE id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setLong(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "更新登录时间失败", e);
        }
        return false;
    }

    /**
     * 从ResultSet提取User对象
     */
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setRealName(rs.getString("real_name"));
        user.setStatus(UserStatus.valueOf(rs.getString("status")));
        
        Timestamp createTime = rs.getTimestamp("create_time");
        if (createTime != null) {
            user.setCreateTime(createTime.toLocalDateTime());
        }
        
        Timestamp lastLoginTime = rs.getTimestamp("last_login_time");
        if (lastLoginTime != null) {
            user.setLastLoginTime(lastLoginTime.toLocalDateTime());
        }
        
        return user;
    }
}
