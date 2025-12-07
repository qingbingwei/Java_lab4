package util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据库连接工具类 - 单例模式
 * 负责数据库连接的创建和管理
 */
public class DatabaseConnection {
    private static final Logger LOGGER = Logger.getLogger(DatabaseConnection.class.getName());
    private static DatabaseConnection instance;
    private String url;
    private String username;
    private String password;
    private String driver;

    /**
     * 私有构造函数 - 单例模式
     */
    private DatabaseConnection() {
        loadConfiguration();
    }

    /**
     * 获取DatabaseConnection单例实例 - 双重检查锁定
     */
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            synchronized (DatabaseConnection.class) {
                if (instance == null) {
                    instance = new DatabaseConnection();
                }
            }
        }
        return instance;
    }

    /**
     * 加载数据库配置
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config/database.properties")) {
            if (input == null) {
                LOGGER.warning("未找到database.properties，使用默认配置");
                useDefaultConfiguration();
                return;
            }
            props.load(input);
            this.url = props.getProperty("db.url");
            this.username = props.getProperty("db.username");
            this.password = props.getProperty("db.password");
            this.driver = props.getProperty("db.driver");
            
            // 加载驱动
            Class.forName(driver);
            LOGGER.info("数据库配置加载成功");
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "加载数据库配置失败", e);
            useDefaultConfiguration();
        }
    }

    /**
     * 使用默认配置（SQLite）
     */
    private void useDefaultConfiguration() {
        this.driver = "org.sqlite.JDBC";
        this.url = "jdbc:sqlite:rbac_system.db";
        this.username = "";
        this.password = "";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "SQLite驱动加载失败", e);
        }
    }

    /**
     * 获取数据库连接
     * @return Connection对象
     * @throws SQLException SQL异常
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * 关闭数据库连接
     * @param connection 要关闭的连接
     */
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "关闭数据库连接失败", e);
            }
        }
    }

    /**
     * 测试数据库连接
     * @return 是否连接成功
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "数据库连接测试失败", e);
            return false;
        }
    }

    // Getters
    public String getUrl() {
        return url;
    }

    public String getDriver() {
        return driver;
    }
}
