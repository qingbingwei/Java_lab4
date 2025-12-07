package ui;

/**
 * 菜单命令接口
 * 使用命令模式封装菜单操作
 */
@FunctionalInterface
public interface MenuCommand {
    /**
     * 执行命令
     */
    void execute();
}
