import ui.CommandLineInterface;
import util.AnsiColorConsoleHandler;
import util.ConsoleColors;
import util.DatabaseConnection;
import util.DatabaseInitializer;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * RBAC权限管理系统主程序入口
 * 
 * 系统功能:
 * 1. 用户管理 - 创建、更新、删除、锁定用户
 * 2. 角色管理 - 创建、更新、删除角色，分配权限
 * 3. 权限管理 - 创建、更新、删除权限
 * 4. 权限校验 - 基于角色的权限验证
 * 5. 审计日志 - 记录所有操作并支持查询导出
 * 
 * 特色功能:
 * - 密码加密存储(SHA-256)
 * - 会话管理
 * - 角色层级管理
 * - 完整的审计追踪
 * - 友好的命令行界面
 * 
 * 默认管理员账号: admin / Admin123
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            // 配置全局日志输出颜色
            configureLoggingWithColor();

            // 初始化系统
            System.out.println(ConsoleColors.green("正在初始化系统..."));
            
            // 测试数据库连接
            DatabaseConnection dbConnection = DatabaseConnection.getInstance();
            if (!dbConnection.testConnection()) {
                System.err.println(ConsoleColors.red("数据库连接失败！请检查配置。"));
                return;
            }
            System.out.println(ConsoleColors.green("✓ 数据库连接成功"));

            // 初始化数据库表和默认数据
            DatabaseInitializer initializer = new DatabaseInitializer();
            if (initializer.needInitialize()) {
                if (!initializer.initialize()) {
                    System.err.println(ConsoleColors.red("数据库初始化失败！"));
                    return;
                }
                System.out.println(ConsoleColors.green("✓ 数据库初始化成功"));
            } else {
                System.out.println(ConsoleColors.green("检测到数据库已初始化，跳过初始化步骤"));
            }

            // 启动命令行界面
            System.out.println(ConsoleColors.green("✓ 系统初始化完成"));
            System.out.println("\n按回车键开始...");
            System.in.read();

            CommandLineInterface cli = new CommandLineInterface();
            cli.start();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "系统启动失败", e);
            System.err.println("系统启动失败: " + e.getMessage());
        }
    }

    /**
     * 将全局 java.util.logging 控制台输出替换为带 ANSI 颜色的 Handler。
     */
    private static void configureLoggingWithColor() {
        Logger rootLogger = LogManager.getLogManager().getLogger("");
        if (rootLogger == null) {
            return;
        }

        // 移除默认的 ConsoleHandler
        for (Handler handler : rootLogger.getHandlers()) {
            if (handler instanceof ConsoleHandler) {
                rootLogger.removeHandler(handler);
            }
        }

        // 添加自定义的带颜色的 Handler
        AnsiColorConsoleHandler colorHandler = new AnsiColorConsoleHandler();
        colorHandler.setLevel(Level.ALL);
        rootLogger.addHandler(colorHandler);
        rootLogger.setLevel(Level.ALL);
    }
}