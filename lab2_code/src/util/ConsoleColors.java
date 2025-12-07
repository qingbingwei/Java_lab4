package util;

/**
 * 控制台颜色工具类
 * 仅在支持 ANSI 转义的终端下生效
 */
public final class ConsoleColors {
    public static final String RESET = "\u001B[0m";

    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";

    private ConsoleColors() {
    }

    public static String green(String message) {
        return GREEN + message + RESET;
    }

    public static String red(String message) {
        return RED + message + RESET;
    }
}
