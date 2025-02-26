package ingsoft.util;

import ingsoft.App;
import java.util.Scanner;

//Letteralmente gestione input/output...
public class ViewSE implements Runnable{
    // public final static String version = "V1"; 
        
        private static final Scanner scanner = new Scanner(System.in);
    //     // Specifica il nome del file di log
    //     private static final String LOG_FILE = "log.txt";
        
        public static void print(Object out){
            System.out.print(out);
        }

        public static void println(Object out){
            System.out.println(out);
        }

        public static void printIf(boolean condition, Object out) {
            if(condition) print(out);
        }
    
        public static String read(String out){
            ViewSE.print(out);
            return scanner.nextLine();
        }
        
    //     /**
    //      * Aggiunge il messaggio passato al file di log in modalità append.
    //      * Se il file non esiste, viene creato.
    //      * level [1-4] ERROR/WARNING/INFO/DEBUG
    //      *
    //      * @param msg il messaggio da loggare
    //      */
    //     public static void log(String msg, int level, String path) {
    //         try (FileWriter fw = new FileWriter(LOG_FILE, true)) {
    //             String type = switch (level) {
    //                 case 1 -> "ERROR";
    //                 case 2 -> "WARNING";
    //                 case 3 -> "INFO";
    //                 default -> "DEBUG";
    //             };
    //             fw.write(version + " | " + type + " | " + path + " | " + LocalDateTime.now() + " | " + msg + System.lineSeparator());
    //     } catch (IOException e) {
    //         // In caso di errore, stampiamo lo stack trace
    //         // e.printStackTrace();
    //         print(e.getMessage());
    //     }
    // }

    //Classe istanziabile...
    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate, scrivi 'help' per aiuto";

    private final App app;
    public ViewSE(App app){
        this.app = app;
    }

    @Override
    public void run() {
        print(MESSAGGIO_START);

        while (!app.setupCompleted()) {
            app.interpreterSETUP(read("\n[SETUP] " + app.getCurrentUser().getUsername() + "> "));
        }

        AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());
        ViewSE.print("SETUP COMPLETATO");

        while(true)
            app.interpreter(read("\n" + app.getCurrentUser().getUsername() + "> "));
    }
}