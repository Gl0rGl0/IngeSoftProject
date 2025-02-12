package ingsoft.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

//Letteralmente gestione input/output...
public class ViewSE {
    
    private static final Scanner scanner = new Scanner(System.in);
    // Specifica il nome del file di log
    private static final String LOG_FILE = "log.txt";
    
    public static void print(Object out){
        System.out.println(out);
    }

    public static String read(String out){
        System.out.print(out);
        return scanner.nextLine();
    }

    public static String read(){
        return scanner.nextLine();
    }
    
    /**
     * Aggiunge il messaggio passato al file di log in modalità append.
     * Se il file non esiste, viene creato.
     *
     * @param msg il messaggio da loggare
     */
    public static void log(String msg, String type) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
            fw.write(type + " | " + msg + System.lineSeparator());
        } catch (IOException e) {
            // In caso di errore, stampiamo lo stack trace
            // e.printStackTrace();
            print(e.getMessage());
        }
    }
}