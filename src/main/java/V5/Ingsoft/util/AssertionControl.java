package V5.Ingsoft.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public final class AssertionControl {
    public final static String VERSION = "V5";
    private static final String BASE_PATH = "log/";
    private static final String LOG_FILE = BASE_PATH + "log.log";
    private static final String LOG_PAYLOAD = BASE_PATH + "payload.log";

    static {
        if (!new File(BASE_PATH).exists()) {
            new File(BASE_PATH).mkdirs();
        }
    }

    /**
     * Prints a message to the standard view.
     */
    private static void print(Object out) {
        System.out.println(out);
    }

    /**
     * Logs a message with a given level and origin class name.
     *
     * @param message   the message to log
     * @param level     log level (ERROR, WARN, INFO, DEBUG)
     * @param className origin class simple name
     * @return true if written successfully
     */
    synchronized public static boolean logMessage(String message, Payload.Status level, String className) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String type = switch (level) {
                case ERROR -> "ERROR";
                case WARN -> "WARNING";
                case INFO -> "INFO";
                case DEBUG -> "DEBUG";
            };
            String out = String.format("%4s | %7s | %-30s | %29s | %s",
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

    synchronized public static void logPayload(Payload<?> p) {
        if (p == null) return;
        try (FileWriter fw = new FileWriter(LOG_PAYLOAD, true)) {
            fw.write(p + "\n");
        } catch (IOException e) {
            print("Log error: " + e.getMessage());
        }
    }
}
