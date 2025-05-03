package V5.Ingsoft.util;

import V5.Ingsoft.view.ViewSE;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class AssertionControl {
    public final static String VERSION = "V5";
    private static final String LOG_FILE = JsonStorage.BASE_PATH + "log.log";

    /**
     * Prints a message to the standard view.
     */
    public static void print(Object out) {
        ViewSE.println(out);
    }

    /**
     * Logs a message with a given level and origin class name.
     * @param message   the message to log
     * @param level     log level (ERROR, WARN, INFO, DEBUG)
     * @param className origin class simple name
     * @return true if written successfully
     */
    public static boolean logMessage(String message, Payload.Level level, String className) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String type = switch (level) {
                case ERROR -> "ERROR";
                case WARN  -> "WARNING";
                case INFO  -> "INFO";
                case DEBUG -> "DEBUG";
            };
            String out = String.format("%4s | %7s | %-20s | %s | %s",
                VERSION,
                type,
                className,
                LocalDateTime.now(),
                message
            );
            fw.write(out + System.lineSeparator());
        } catch (IOException e) {
            print("Log error: " + e.getMessage());
            return false;
        }
        return true;
    }
}
