package util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * 为 java.util.logging 输出增加 ANSI 颜色的控制台 Handler。
 * INFO 及以下使用绿色，WARNING/SEVERE 使用红色，其它保持默认。
 */
public class AnsiColorConsoleHandler extends ConsoleHandler {

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) {
            return;
        }

        String message = getFormatter().format(record);
        Level level = record.getLevel();

        String colored;
        if (level.intValue() >= Level.WARNING.intValue()) {
            // WARNING、SEVERE 等走红色
            colored = ConsoleColors.red(message);
        } else {
            // INFO、CONFIG、FINE 等走绿色
            colored = ConsoleColors.green(message);
        }

        // 直接写到标准错误输出（保持 IDEA 的日志窗口行为）
        System.err.print(colored);
    }
}
