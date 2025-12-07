package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 成绩实体类
 * 包含平时成绩、期中成绩、实验成绩、期末成绩及综合成绩
 */
public class Score {
    private int regularScore;      // 平时成绩 (20%)
    private int midtermScore;      // 期中成绩 (20%)
    private int experimentScore;   // 实验成绩 (20%)
    private int finalExamScore;    // 期末成绩 (40%)
    private double finalScore;     // 综合成绩
    
    private LocalDateTime regularScoreTime;
    private LocalDateTime midtermScoreTime;
    private LocalDateTime experimentScoreTime;
    private LocalDateTime finalExamScoreTime;

    public Score() {
        this.regularScore = -1;
        this.midtermScore = -1;
        this.experimentScore = -1;
        this.finalExamScore = -1;
        this.finalScore = 0;
    }

    /**
     * 计算综合成绩
     * 综合成绩 = 平时成绩*0.2 + 期中成绩*0.2 + 实验成绩*0.2 + 期末成绩*0.4
     */
    public void calculateFinalScore() {
        if (regularScore >= 0 && midtermScore >= 0 && experimentScore >= 0 && finalExamScore >= 0) {
            this.finalScore = regularScore * 0.2 + midtermScore * 0.2 + 
                            experimentScore * 0.2 + finalExamScore * 0.4;
        }
    }

    public boolean isComplete() {
        return regularScore >= 0 && midtermScore >= 0 && 
               experimentScore >= 0 && finalExamScore >= 0;
    }

    // Setters with timestamp
    public void setRegularScore(int regularScore) {
        this.regularScore = regularScore;
        this.regularScoreTime = LocalDateTime.now();
    }

    public void setMidtermScore(int midtermScore) {
        this.midtermScore = midtermScore;
        this.midtermScoreTime = LocalDateTime.now();
    }

    public void setExperimentScore(int experimentScore) {
        this.experimentScore = experimentScore;
        this.experimentScoreTime = LocalDateTime.now();
    }

    public void setFinalExamScore(int finalExamScore) {
        this.finalExamScore = finalExamScore;
        this.finalExamScoreTime = LocalDateTime.now();
    }

    // Getters
    public int getRegularScore() { return regularScore; }
    public int getMidtermScore() { return midtermScore; }
    public int getExperimentScore() { return experimentScore; }
    public int getFinalExamScore() { return finalExamScore; }
    public double getFinalScore() { return finalScore; }
    
    public LocalDateTime getRegularScoreTime() { return regularScoreTime; }
    public LocalDateTime getMidtermScoreTime() { return midtermScoreTime; }
    public LocalDateTime getExperimentScoreTime() { return experimentScoreTime; }
    public LocalDateTime getFinalExamScoreTime() { return finalExamScoreTime; }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("平时: %d", regularScore >= 0 ? regularScore : 0));
        if (regularScoreTime != null) {
            sb.append(String.format(" (%s)", regularScoreTime.format(formatter)));
        }
        sb.append(String.format(", 期中: %d", midtermScore >= 0 ? midtermScore : 0));
        if (midtermScoreTime != null) {
            sb.append(String.format(" (%s)", midtermScoreTime.format(formatter)));
        }
        sb.append(String.format(", 实验: %d", experimentScore >= 0 ? experimentScore : 0));
        if (experimentScoreTime != null) {
            sb.append(String.format(" (%s)", experimentScoreTime.format(formatter)));
        }
        sb.append(String.format(", 期末: %d", finalExamScore >= 0 ? finalExamScore : 0));
        if (finalExamScoreTime != null) {
            sb.append(String.format(" (%s)", finalExamScoreTime.format(formatter)));
        }
        sb.append(String.format(", 综合: %.2f", finalScore));
        return sb.toString();
    }
}
