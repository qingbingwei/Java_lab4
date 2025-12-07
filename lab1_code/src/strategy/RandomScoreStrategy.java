package strategy;

import java.util.Random;

/**
 * 随机成绩生成策略
 * 生成符合正态分布的成绩
 */
public class RandomScoreStrategy implements ScoreGenerationStrategy {
    private static final Random random = new Random();
    private final int mean;      // 平均分
    private final int stdDev;    // 标准差

    public RandomScoreStrategy(int mean, int stdDev) {
        this.mean = mean;
        this.stdDev = stdDev;
    }

    @Override
    public int generateScore() {
        // 使用正态分布生成成绩
        int score = (int) (random.nextGaussian() * stdDev + mean);
        // 限制在0-100范围内
        return Math.max(0, Math.min(100, score));
    }

    /**
     * 创建平时成绩策略 (平均分75, 标准差10)
     */
    public static RandomScoreStrategy regularStrategy() {
        return new RandomScoreStrategy(75, 10);
    }

    /**
     * 创建期中成绩策略 (平均分70, 标准差12)
     */
    public static RandomScoreStrategy midtermStrategy() {
        return new RandomScoreStrategy(70, 12);
    }

    /**
     * 创建实验成绩策略 (平均分80, 标准差8)
     */
    public static RandomScoreStrategy experimentStrategy() {
        return new RandomScoreStrategy(80, 8);
    }

    /**
     * 创建期末成绩策略 (平均分72, 标准差15)
     */
    public static RandomScoreStrategy finalExamStrategy() {
        return new RandomScoreStrategy(72, 15);
    }
}
