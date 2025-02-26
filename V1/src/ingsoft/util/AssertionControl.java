package ingsoft.util;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Scanner;

public class AssertionControl {

    public final static String VERSION = "V1"; 
    private static final Scanner scanner = new Scanner(System.in);
        // Specifica il nome del file di log
    private static final String LOG_FILE = "log.txt";

    public static void print(Object out){
        ViewSE.println(out);
    }

    public static String read(String out){
        ViewSE.print(out);
        return scanner.nextLine();
    }

    // true se la condizione è falsa e la scrittura va a buon fine, oppure se la condizione è vera
    public static boolean verify(boolean condition, String message, int level, String path) {
        if(!condition) {
            return logMessage(message, level, path);
        }
        return true;
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
                fw.write(VERSION + "\t|\t" + type + "\t|\t" + path + "\t|\t" + LocalDateTime.now() + "\t|\t" + msg + System.lineSeparator());
        } catch (IOException e) {
            // In caso di errore, stampiamo lo stack trace
            // e.printStackTrace();
            print(e.getMessage());
            return false;
        }
        return true;
    }
}