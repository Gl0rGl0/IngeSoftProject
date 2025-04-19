package V3.Ingsoft.view;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.model.Model;
import V3.Ingsoft.util.AssertionControl;

import java.util.Scanner;

//Literally input/output management...
public class ViewSE implements Runnable {
    private static final Scanner scanner = new Scanner(System.in);
    // Instantiable class...
    private static final String MESSAGGIO_START = "Welcome to the Guided Tours management system, type 'help' for assistance";
    private final Controller controller;

    public ViewSE(Controller controller) {
        this.controller = controller;
    }

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

    //VIEW OUT
    public static void error(String e) {
        println(e);
    }

    @Override
    public void run() {
        println(MESSAGGIO_START);

        controller.getDB();
        if (!Model.appSettings.isAmbitoSet()) {
            String ambito;
            do {
                ambito = ViewSE.read("Inserisci l'ambito territoriale per iniziare la fase di setup dell'applicazione: ");
            } while (ambito.isBlank());
            Model.setAmbito(ambito);
        }

        while (!controller.doneAll())
            controller.interpreter(read("\n[SETUP] " + controller.getCurrentUser().getUsername() + "> "));

        AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());

        while (true)
            controller.interpreter(read("\n" + controller.getCurrentUser().getUsername() + "> "));
    }
}
