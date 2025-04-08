package V4.Ingsoft.view;

import java.util.Scanner;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.util.AssertionControl;

//Literally input/output management...
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

    // Instantiable class...
    private static final String MESSAGGIO_START = "Welcome to the Guided Tours management system, type 'help' for assistance";

    private final Controller controller;

    public ViewSE(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        println(MESSAGGIO_START);

        if(!controller.doneAll()){
            String ambito;
            do{
                ambito = ViewSE.read("Inserisci l'ambito territoriale per iniziare la fase di setup dell'applicazione");
            }while(ambito.isBlank());
            controller.db.setAmbito(ambito);
        }

        while (!controller.doneAll())
            controller.interpreter(read("\n[SETUP] " + controller.getCurrentUser().getUsername() + "> "));

        AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());
        ViewSE.println("SETUP COMPLETED");

        while (true)
            controller.interpreter(read("\n" + controller.getCurrentUser().getUsername() + "> "));
    }

    //VIEW OUT
    public static void error(String e){
        print(e);
    }
}
