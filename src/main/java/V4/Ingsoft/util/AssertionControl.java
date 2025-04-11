package V4.Ingsoft.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

import V4.Ingsoft.view.ViewSE;

public class AssertionControl {

    public final static String VERSION = "V4";
    private static final Scanner scanner = new Scanner(System.in);
    // Specifica il name del file di log
    private static final String LOG_FILE = JsonStorage.BASE_PATH + "log.log"; 

    public static void print(Object out) {
        ViewSE.println(out);
    }

    public static String read(String out) {
        ViewSE.println(out);
        return scanner.nextLine();
    }

    /**
     * Appends the passed message to the log file in append mode.
     * If the file does not exist, it is created.
     * level [1-4] ERROR/WARNING/INFO/DEBUG
     *
     * @param msg the message to log
     * @return true if the write is successful
     */
    public static boolean logMessage(Object o, int level, String path) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String type = switch (level) {
                case 0 -> "!DANGER!";
                case 1 -> "ERROR";
                case 2 -> "WARNING";
                case 3 -> "INFO";
                default -> "DEBUG";
            };
            String out = String.format("%4s | %8s | %20s | %-29s | %s", VERSION, type, path, LocalDateTime.now(), o.toString());
            fw.write(out + System.lineSeparator());
        } catch (IOException e) {
            // In case of error, print the stack trace (commented out)
            // e.printStackTrace();
            print(e.getMessage());
            return false;
        }
        return true;
    }
}
