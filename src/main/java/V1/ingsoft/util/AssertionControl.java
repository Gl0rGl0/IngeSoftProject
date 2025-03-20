package V1.ingsoft.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

import V1.ingsoft.view.ViewSE;

public class AssertionControl {

    public final static String VERSION = "V1";
    private static final Scanner scanner = new Scanner(System.in);
    // Specifica il name del file di log
    private static final String LOG_FILE = "log.log";

    public static void print(Object out) {
        ViewSE.println(out);
    }

    public static String read(String out) {
        ViewSE.println(out);
        return scanner.nextLine();
    }

    /**
     * Aggiunge il messaggio passato al file di log in modalità append.
     * Se il file non esiste, viene creato.
     * level [1-4] ERROR/WARNING/INFO/DEBUG
     *
     * @param msg il messaggio da loggare
     * @return true se la scrittura va a buon fine
     */
    public static boolean logMessage(String msg, int level, String path) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            String type = switch (level) {
                case 1 -> "ERROR";
                case 2 -> "WARNING";
                case 3 -> "INFO";
                default -> "DEBUG";
            };
            String out = String.format("%4s | %8s | %15s | %-29s | %s", VERSION, type, path, LocalDateTime.now(), msg);
            fw.write(out + System.lineSeparator());
        } catch (IOException e) {
            // In caso di errore, stampiamo lo stack trace
            // e.printStackTrace();
            print(e.getMessage());
            return false;
        }
        return true;
    }
}