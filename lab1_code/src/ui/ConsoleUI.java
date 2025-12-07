package ui;

import model.*;
import service.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 控制台用户界面
 */
public class ConsoleUI {
    private Scanner scanner;
    private DataManager dataManager;
    private ScoreService scoreService;
    private StatisticsService statisticsService;
    private Menu mainMenu;

    public ConsoleUI() {
        scanner = new Scanner(System.in);
        dataManager = DataManager.getInstance();
        scoreService = new ScoreService();
        statisticsService = new StatisticsService();
        initializeMenu();
    }

    /**
     * 初始化主菜单
     */
    private void initializeMenu() {
        mainMenu = new Menu("学生成绩管理系统");
        
        mainMenu.addItem("1", "初始化数据(生成学生、教师、课程、教学班)", this::initializeData);
        mainMenu.addItem("2", "学生选课", this::enrollStudents);
        mainMenu.addItem("3", "生成平时成绩", this::generateRegularScores);
        mainMenu.addItem("4", "生成期中成绩", this::generateMidtermScores);
        mainMenu.addItem("5", "生成实验成绩", this::generateExperimentScores);
        mainMenu.addItem("6", "生成期末成绩并计算综合成绩", this::generateFinalScores);
        mainMenu.addItem("7", "显示教学班成绩", this::displayClassScores);
        mainMenu.addItem("8", "查询学生成绩", this::queryStudentScores);
        mainMenu.addItem("9", "成绩统计分析", this::statisticsAnalysis);
        mainMenu.addItem("10", "显示所有学生排名", this::displayStudentRankings);
        mainMenu.addItem("0", "退出系统", () -> {});
    }

    /**
     * 运行系统
     */
    public void run() {
        System.out.println("\n欢迎使用学生成绩管理系统！");
        
        while (true) {
            mainMenu.display();
            System.out.print("\n请选择功能 (输入编号): ");
            String choice = scanner.nextLine().trim();
            
            if ("0".equals(choice)) {
                System.out.println("\n感谢使用，再见！");
                break;
            }
            
            if (!mainMenu.executeItem(choice)) {
                System.out.println("无效的选择，请重试！");
            }
            
            System.out.println("\n按回车键继续...");
            scanner.nextLine();
        }
    }

    /**
     * 1. 初始化数据
     */
    private void initializeData() {
        dataManager.initializeData();
    }

    /**
     * 2. 学生选课
     */
    private void enrollStudents() {
        if (!checkInitialized()) return;
        dataManager.enrollStudents();
    }

    /**
     * 3. 生成平时成绩
     */
    private void generateRegularScores() {
        if (!checkInitialized()) return;
        
        TeachingClass selectedClass = selectTeachingClass();
        if (selectedClass == null) return;
        
        scoreService.generateRegularScores(selectedClass);
        System.out.println("✓ 已生成教学班 " + selectedClass.getClassId() + 
                         " 的平时成绩！");
    }

    /**
     * 4. 生成期中成绩
     */
    private void generateMidtermScores() {
        if (!checkInitialized()) return;
        
        TeachingClass selectedClass = selectTeachingClass();
        if (selectedClass == null) return;
        
        scoreService.generateMidtermScores(selectedClass);
        System.out.println("✓ 已生成教学班 " + selectedClass.getClassId() + 
                         " 的期中成绩！");
    }

    /**
     * 5. 生成实验成绩
     */
    private void generateExperimentScores() {
        if (!checkInitialized()) return;
        
        TeachingClass selectedClass = selectTeachingClass();
        if (selectedClass == null) return;
        
        scoreService.generateExperimentScores(selectedClass);
        System.out.println("✓ 已生成教学班 " + selectedClass.getClassId() + 
                         " 的实验成绩！");
    }

    /**
     * 6. 生成期末成绩
     */
    private void generateFinalScores() {
        if (!checkInitialized()) return;
        
        System.out.println("\n选择操作:");
        System.out.println("  [1] 为单个教学班生成期末成绩");
        System.out.println("  [2] 为所有教学班生成全部成绩");
        System.out.print("请选择: ");
        String choice = scanner.nextLine().trim();
        
        if ("1".equals(choice)) {
            TeachingClass selectedClass = selectTeachingClass();
            if (selectedClass == null) return;
            
            scoreService.generateFinalExamScores(selectedClass);
            System.out.println("\n✓ 已生成教学班 " + selectedClass.getClassId() + 
                             " 的期末成绩并计算综合成绩！");
        } else if ("2".equals(choice)) {
            scoreService.generateAllScores(dataManager.getTeachingClasses());
            System.out.println("\n✓ 已为所有教学班生成全部成绩！");
        } else {
            System.out.println("\n无效的选择！");
        }
    }

    /**
     * 7. 显示教学班成绩
     */
    private void displayClassScores() {
        if (!checkInitialized()) return;
        
        TeachingClass selectedClass = selectTeachingClass();
        if (selectedClass == null) return;
        
        System.out.println("\n选择排序方式:");
        System.out.println("  [1] 按学号排序");
        System.out.println("  [2] 按成绩升序");
        System.out.println("  [3] 按成绩降序");
        System.out.print("请选择: ");
        String choice = scanner.nextLine().trim();
        
        List<Student> students;
        switch (choice) {
            case "1":
                students = statisticsService.sortByStudentId(selectedClass);
                break;
            case "2":
                students = statisticsService.sortByScore(selectedClass, true);
                break;
            case "3":
                students = statisticsService.sortByScore(selectedClass, false);
                break;
            default:
                students = selectedClass.getStudents();
        }
        
        displayClassScoreTable(selectedClass, students);
    }

    /**
     * 8. 查询学生成绩
     */
    private void queryStudentScores() {
        if (!checkInitialized()) return;
        
        System.out.print("\n请输入学号或姓名: ");
        String searchKey = scanner.nextLine().trim();
        
        Student student = statisticsService.findStudent(dataManager.getStudents(), searchKey);
        
        if (student == null) {
            System.out.println("未找到该学生！");
            return;
        }
        
        displayStudentAllScores(student);
    }

    /**
     * 9. 成绩统计分析
     */
    private void statisticsAnalysis() {
        if (!checkInitialized()) return;
        
        System.out.println("\n选择统计类型:");
        System.out.println("  [1] 单个教学班成绩分布");
        System.out.println("  [2] 所有学生总成绩分布");
        System.out.print("请选择: ");
        String choice = scanner.nextLine().trim();
        
        if ("1".equals(choice)) {
            TeachingClass selectedClass = selectTeachingClass();
            if (selectedClass == null) return;
            
            Map<StatisticsService.ScoreRange, Long> distribution = 
                    statisticsService.getScoreDistribution(selectedClass);
            
            System.out.println("\n" + selectedClass.getClassId() + " - " + 
                             selectedClass.getCourse().getCourseName() + " 成绩分布:");
            displayScoreDistribution(distribution, selectedClass.getCurrentSize());
            
        } else if ("2".equals(choice)) {
            Map<StatisticsService.ScoreRange, Long> distribution = 
                    statisticsService.getTotalScoreDistribution(dataManager.getStudents());
            
            System.out.println("\n所有学生平均成绩分布:");
            displayScoreDistribution(distribution, dataManager.getStudents().size());
        } else {
            System.out.println("\n无效的选择！");
        }
    }

    /**
     * 10. 显示所有学生排名
     */
    private void displayStudentRankings() {
        if (!checkInitialized()) return;
        
        // 按总成绩降序排序
        List<Student> students = dataManager.getStudents().stream()
                .filter(s -> !s.getScores().isEmpty())  // 过滤掉没有成绩的学生
                .sorted(Comparator.comparingDouble(Student::getTotalScore).reversed())
                .collect(java.util.stream.Collectors.toList());
        
        if (students.isEmpty()) {
            System.out.println("\n暂无学生成绩数据！");
            return;
        }
        
        System.out.println("\n所有学生成绩排名 (按总成绩降序):");
        System.out.println("=".repeat(100));
        System.out.printf("%-6s %-10s %-8s %-10s %-10s\n",
                "排名", "学号", "姓名", "平均成绩", "总成绩");
        System.out.println("-".repeat(100));
        
        int rank = 1;
        for (Student student : students) {
            System.out.printf("%-6d %-10s %-8s %-10.2f %-10.2f\n",
                    rank++,
                    student.getStudentId(),
                    student.getName(),
                    student.getAverageScore(),
                    student.getTotalScore());
        }
        System.out.println("=".repeat(100));
    }

    // ========== 辅助方法 ==========

    private boolean checkInitialized() {
        if (!dataManager.isInitialized()) {
            System.out.println("请先初始化数据！");
            return false;
        }
        return true;
    }

    private TeachingClass selectTeachingClass() {
        List<TeachingClass> classes = dataManager.getTeachingClasses();
        
        System.out.println("\n可用的教学班:");
        for (int i = 0; i < classes.size(); i++) {
            TeachingClass tc = classes.get(i);
            System.out.printf("  [%d] %s - %s (教师: %s, 人数: %d)\n",
                    i + 1, tc.getClassId(), tc.getCourse().getCourseName(),
                    tc.getTeacher().getName(), tc.getCurrentSize());
        }
        
        System.out.print("请选择教学班编号 (1-" + classes.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice >= 1 && choice <= classes.size()) {
                return classes.get(choice - 1);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
        
        System.out.println("无效的选择！");
        return null;
    }

    private void displayClassScoreTable(TeachingClass teachingClass, List<Student> students) {
        System.out.println("\n教学班: " + teachingClass.getClassId() + 
                         " - " + teachingClass.getCourse().getCourseName());
        System.out.println("教师: " + teachingClass.getTeacher().getName() + 
                         " | 学期: " + teachingClass.getSemester());
        System.out.println("=".repeat(120));
        System.out.printf("%-10s %-8s %-8s %-8s %-8s %-8s %-10s\n",
                "学号", "姓名", "平时", "期中", "实验", "期末", "综合成绩");
        System.out.println("-".repeat(120));
        
        for (Student student : students) {
            Score score = student.getScore(teachingClass);
            if (score != null) {
                System.out.printf("%-10s %-8s %-8d %-8d %-8d %-8d %-10.2f\n",
                        student.getStudentId(),
                        student.getName(),
                        score.getRegularScore() >= 0 ? score.getRegularScore() : 0,
                        score.getMidtermScore() >= 0 ? score.getMidtermScore() : 0,
                        score.getExperimentScore() >= 0 ? score.getExperimentScore() : 0,
                        score.getFinalExamScore() >= 0 ? score.getFinalExamScore() : 0,
                        score.getFinalScore());
            }
        }
        System.out.println("=".repeat(120));
    }

    private void displayStudentAllScores(Student student) {
        System.out.println("\n学生信息: " + student);
        System.out.println("=".repeat(120));
        System.out.printf("%-15s %-10s %-8s %-8s %-8s %-8s %-10s\n",
                "课程", "教学班", "平时", "期中", "实验", "期末", "综合成绩");
        System.out.println("-".repeat(120));
        
        double totalScore = 0;
        int count = 0;
        
        for (Map.Entry<TeachingClass, Score> entry : student.getScores().entrySet()) {
            TeachingClass tc = entry.getKey();
            Score score = entry.getValue();
            
            System.out.printf("%-15s %-10s %-8d %-8d %-8d %-8d %-10.2f\n",
                    tc.getCourse().getCourseName(),
                    tc.getClassId(),
                    score.getRegularScore() >= 0 ? score.getRegularScore() : 0,
                    score.getMidtermScore() >= 0 ? score.getMidtermScore() : 0,
                    score.getExperimentScore() >= 0 ? score.getExperimentScore() : 0,
                    score.getFinalExamScore() >= 0 ? score.getFinalExamScore() : 0,
                    score.getFinalScore());
            
            totalScore += score.getFinalScore();
            count++;
        }
        
        System.out.println("=".repeat(120));
        System.out.printf("总成绩: %.2f | 平均成绩: %.2f\n", 
                totalScore, count > 0 ? totalScore / count : 0);
    }

    private void displayScoreDistribution(Map<StatisticsService.ScoreRange, Long> distribution, 
                                          int total) {
        System.out.println("\n" + "=".repeat(60));
        System.out.printf("%-10s %-10s %-10s %-20s\n", "分数段", "人数", "百分比", "分布图");
        System.out.println("-".repeat(60));
        
        for (Map.Entry<StatisticsService.ScoreRange, Long> entry : distribution.entrySet()) {
            StatisticsService.ScoreRange range = entry.getKey();
            long count = entry.getValue();
            double percentage = total > 0 ? (count * 100.0 / total) : 0;
            
            // 生成柱状图
            int barLength = (int) (percentage / 2); // 每2%一个星号
            String bar = "*".repeat(Math.max(0, barLength));
            
            System.out.printf("%-10s %-10d %-9.2f%% %s\n",
                    range.getName(), count, percentage, bar);
        }
        
        System.out.println("=".repeat(60));
        System.out.println("总人数: " + total);
    }
}
