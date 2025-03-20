package V1.ingsoft.view;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.util.AssertionControl;

import java.util.Scanner;

//Letteralmente gestione input/output...
public class ViewSE implements Runnable {
    private static final Scanner scanner = new Scanner(System.in);

    public static void print(Object out) {
        System.out.print(out);
    }

    public static void println(Object out) {
        System.out.println(out);
    }

    public static String read(String out) {
        ViewSE.print(out);
        return scanner.nextLine();
    }

    // Classe istanziabile...
    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate, scrivi 'help' per aiuto";

    private final Controller app;

    public ViewSE(Controller app) {
        this.app = app;
    }

    @Override
    public void run() {
        println(MESSAGGIO_START);

        while (!app.setupCompleted()) {
            app.interpreterSETUP(read("\n[SETUP] " + app.getCurrentUser().getUsername() + "> "));
        }

        AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());
        ViewSE.println("SETUP COMPLETATO");

        while (true)
            app.interpreter(read("\n" + app.getCurrentUser().getUsername() + "> "));
    }
}