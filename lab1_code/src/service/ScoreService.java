package service;

import model.*;
import strategy.*;
import java.util.*;

/**
 * 成绩服务类
 * 负责成绩的生成和管理
 */
public class ScoreService {
    
    /**
     * 为教学班的所有学生生成平时成绩
     */
    public void generateRegularScores(TeachingClass teachingClass) {
        ScoreGenerationStrategy strategy = RandomScoreStrategy.regularStrategy();
        for (Student student : teachingClass.getStudents()) {
            Score score = student.getScore(teachingClass);
            if (score == null) {
                score = new Score();
                student.setScore(teachingClass, score);
            }
            score.setRegularScore(strategy.generateScore());
        }
    }

    /**
     * 为教学班的所有学生生成期中成绩
     */
    public void generateMidtermScores(TeachingClass teachingClass) {
        ScoreGenerationStrategy strategy = RandomScoreStrategy.midtermStrategy();
        for (Student student : teachingClass.getStudents()) {
            Score score = student.getScore(teachingClass);
            if (score == null) {
                score = new Score();
                student.setScore(teachingClass, score);
            }
            score.setMidtermScore(strategy.generateScore());
        }
    }

    /**
     * 为教学班的所有学生生成实验成绩
     */
    public void generateExperimentScores(TeachingClass teachingClass) {
        ScoreGenerationStrategy strategy = RandomScoreStrategy.experimentStrategy();
        for (Student student : teachingClass.getStudents()) {
            Score score = student.getScore(teachingClass);
            if (score == null) {
                score = new Score();
                student.setScore(teachingClass, score);
            }
            score.setExperimentScore(strategy.generateScore());
        }
    }

    /**
     * 为教学班的所有学生生成期末成绩
     */
    public void generateFinalExamScores(TeachingClass teachingClass) {
        ScoreGenerationStrategy strategy = RandomScoreStrategy.finalExamStrategy();
        for (Student student : teachingClass.getStudents()) {
            Score score = student.getScore(teachingClass);
            if (score == null) {
                score = new Score();
                student.setScore(teachingClass, score);
            }
            score.setFinalExamScore(strategy.generateScore());
            score.calculateFinalScore();
        }
    }

    /**
     * 为所有教学班生成所有成绩
     */
    public void generateAllScores(List<TeachingClass> teachingClasses) {
        for (TeachingClass tc : teachingClasses) {
            generateRegularScores(tc);
            generateMidtermScores(tc);
            generateExperimentScores(tc);
            generateFinalExamScores(tc);
        }
    }

    /**
     * 计算所有学生的综合成绩
     */
    public void calculateAllFinalScores(List<Student> students) {
        for (Student student : students) {
            for (Score score : student.getScores().values()) {
                score.calculateFinalScore();
            }
        }
    }
}
