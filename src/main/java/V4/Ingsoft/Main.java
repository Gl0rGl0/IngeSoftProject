package V4.Ingsoft;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.Initer;
import V4.Ingsoft.view.ViewSE;

//USAGE TYPE
//init: Main -> test -> Commands launched by interpreter in advance -> start of the controller
//controller: WelcomeMessage -> while(Interpreter)
//Interpreter: [command] [-options] [arguments], each command is passed to its Command(>Abstract|Interface) where it is handled
//DB: Creates 5 sub-helpers that each manage a part of the data, Configurators, etc.
//Utility Classes: literally utility classes, time, date, external interaction, etc...

// Use controller.interpreter to give commands directly even before
// user interaction
// -> Benefit: GUI where the string is sent directly to the controller without
// going through the keyboard
public class Main {
    public static void main(String[] args) {
        Model model = Model.getInstance();
        Controller controller = new Controller(model);
        ViewSE view = new ViewSE(controller);

        controller.interpreter("time -s 20/4/2025");

        if (args.length > 0 && args[0] != null && args[0].startsWith("-"))
            switch (args[0].charAt(1)) {
                case 'S' -> initSetup(controller);
                case 'I' -> initPeoplePlaces(controller);
                case 'A' -> initAvailability(controller);
                case 'D' -> initDimostrazione(controller);
                case 'R' -> resetAll(controller);
            
                default -> {} // -B o vuoto
            }
        
        //initDimostrazione(controller);
        //System.out.println();

        initSetup(controller);
        System.out.println();
        view.run();
    }

    private static void resetAll(Controller controller) {
        controller.getDB().clearAll();
        Model.deleteInstance();
        controller.setDB(Model.getInstance());
    }

    private static void initDimostrazione(Controller controller) {
        resetAll(controller);
        Initer.dimostrazione(controller);
    }

    private static void initAvailability(Controller controller) {
        initPeoplePlaces(controller);

        Initer.initAvailability(controller);
    }

    private static void initPeoplePlaces(Controller controller) {
        initSetup(controller);

        Initer.initPersone(controller);
        Initer.initVisiteLuoghi(controller);
    }

    private static void initSetup(Controller controller) {
        resetAll(controller);
        Initer.setupPhase(controller);
    }
}
