package strategy;

/**
 * 成绩生成策略接口
 * 使用策略模式定义不同的成绩生成算法
 */
@FunctionalInterface
public interface ScoreGenerationStrategy {
    /**
     * 生成成绩
     * @return 成绩值(0-100)
     */
    int generateScore();
}
