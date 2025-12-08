package com.example.studentscore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.File;
import java.net.URL;

/**
 * 数据源配置
 * 动态确定数据库路径，确保数据库始终创建在项目目录下
 */
@Slf4j
@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        // 获取项目根目录
        String projectRoot = getProjectRootPath();
        File dataDir = new File(projectRoot, "data");

        // 确保data目录存在
        if (!dataDir.exists()) {
            boolean created = dataDir.mkdirs();
            log.info("创建数据目录: {} - {}", dataDir.getAbsolutePath(), created ? "成功" : "失败");
        }

        // 数据库文件路径
        File dbFile = new File(dataDir, "student_score.db");
        String jdbcUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();

        log.info("========================================");
        log.info("数据源配置");
        log.info("项目根目录: {}", projectRoot);
        log.info("数据目录: {}", dataDir.getAbsolutePath());
        log.info("数据库文件: {}", dbFile.getAbsolutePath());
        log.info("JDBC URL: {}", jdbcUrl);
        log.info("========================================");

        // 配置HikariCP
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        config.setMinimumIdle(1);
        config.setConnectionTestQuery("SELECT 1");

        return new HikariDataSource(config);
    }

    /**
     * 获取项目根目录
     * 优先级：
     * 1. 通过classpath定位（target/classes或build/classes）
     * 2. 检查system_by_springboot子目录
     * 3. 检查当前目录是否包含src
     * 4. 使用user.dir
     */
    private String getProjectRootPath() {
        try {
            // 方法1：通过classpath资源定位
            ClassPathResource resource = new ClassPathResource("application.yml");
            if (resource.exists()) {
                URL url = resource.getURL();
                String path = url.getPath();

                // 处理file:前缀和URL编码
                if (path.startsWith("file:")) {
                    path = path.substring(5);
                }

                // 从 .../target/classes/application.yml 回退到项目根
                if (path.contains("/target/classes/")) {
                    String root = path.substring(0, path.indexOf("/target/classes/"));
                    log.debug("通过target/classes定位项目根目录: {}", root);
                    return root;
                }
                // 从 .../build/classes/application.yml 回退到项目根
                if (path.contains("/build/classes/")) {
                    String root = path.substring(0, path.indexOf("/build/classes/"));
                    log.debug("通过build/classes定位项目根目录: {}", root);
                    return root;
                }
            }
        } catch (Exception e) {
            log.debug("通过classpath定位失败: {}", e.getMessage());
        }

        // 方法2：检查user.dir是否指向父目录
        String userDir = System.getProperty("user.dir");
        File systemDir = new File(userDir, "system_by_springboot");
        if (systemDir.exists() && systemDir.isDirectory()) {
            log.debug("检测到system_by_springboot子目录，使用: {}", systemDir.getAbsolutePath());
            return systemDir.getAbsolutePath();
        }

        // 方法3：检查user.dir是否已经是项目目录
        File srcDir = new File(userDir, "src");
        if (srcDir.exists() && srcDir.isDirectory()) {
            log.debug("当前目录包含src，使用user.dir: {}", userDir);
            return userDir;
        }

        // 方法4：回退到user.dir
        log.debug("使用默认user.dir: {}", userDir);
        return userDir;
    }
}
