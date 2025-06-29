package V5.Ingsoft.view;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;

import java.util.Objects;
import java.util.Scanner;

//Literally input/output management...
public class ViewSE implements Runnable {
    private static final Scanner scanner = new Scanner(System.in);
    private static final String MESSAGGIO_START = "Welcome to the Guided Tours management system, type 'help' for assistance";
    public final Controller controller;

    public ViewSE() {
        insertAmbito();

        this.controller = new Controller();
    }

    public ViewSE(String ambito) {
        if (!Objects.requireNonNull(Model.getInstance().appSettings).isAmbitoSet()) {
            Model.getInstance().setAmbito(ambito);
        }

        this.controller = new Controller();
    }

    public static void payloadOut(Payload<?> out) {
        if(out.getCommand() == null){
            println(out.getData());
            return;
        }

        if (out.getCommand().equals(CommandList.EXIT)) {
            println("Program terminated. Goodbye!");
            ((Runnable) out.getData()).run();
        } else {
            println(out.getData());
        }
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

        while (!controller.isSetupCompleted())
            payloadOut(controller.interpreter(read("\n[SETUP] " + controller.getCurrentUser().getUsername() + "> ")));

        AssertionControl.logMessage("Setup completed", Payload.Status.INFO, this.getClass().getSimpleName());

        new Thread(() -> {
            while (true) {
                payloadOut(controller.interpreter(read("\n" + controller.getCurrentUser().getUsername() + "> ")));
            }
        }).start();
    }

    private void insertAmbito(){
        while (!Objects.requireNonNull(Model.getInstance().appSettings).isAmbitoSet()) {
            String ambito;
            do {
                ambito = ViewSE.read("Inserisci l'ambito territoriale per inizializzare l'applicazione: ");
            } while (ambito.isBlank());
            Model.getInstance().setAmbito(ambito);
        }
    }
}
