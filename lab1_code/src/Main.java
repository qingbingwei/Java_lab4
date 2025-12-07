import ui.ConsoleUI;

/**
 * 学生成绩管理系统 - 主程序入口
 * 
 * 系统功能:
 * 1. 数据初始化 - 生成学生、教师、课程、教学班等基础数据
 * 2. 学生选课 - 使用策略模式实现灵活的选课策略
 * 3. 成绩管理 - 分阶段生成平时、期中、实验、期末成绩
 * 4. 成绩查询 - 支持按学号、姓名查询学生成绩
 * 5. 成绩统计 - 提供多维度的成绩统计分析
 * 6. 成绩排名 - 支持按学号、成绩等多种方式排序
 * 
 * 设计特点:
 * - 使用单例模式管理全局数据
 * - 使用工厂模式生成数据对象
 * - 使用策略模式实现成绩生成和选课策略
 * - 使用命令模式封装菜单操作
 * - 充分利用Lambda表达式、Stream API、泛型等Java特性
 * - 使用枚举类型管理常量数据
 * - 良好的分层架构: model-service-ui
 * 
 * @author Student Grade Management System
 * @version 1.0
 */
public class Main {
    public static void main(String[] args) {
        // 启动控制台用户界面
        ConsoleUI ui = new ConsoleUI();
        ui.run();
    }
}