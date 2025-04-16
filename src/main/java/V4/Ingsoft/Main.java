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
        Model model;

        if (args.length > 0)
            model = Model.getInstance(args[0]);
        else {
            model = Model.getInstance();
        }

        Controller controller = new Controller(model);
        ViewSE view = new ViewSE(controller);

        init(controller);

        controller.interpreter("login config1 pass1C");
        // controller.interpreter("changepsw pass1C pass1C");

        view.run();
    }

    public static void init(Controller controller) {
        controller.interpreter("login ADMIN PASSWORD");

        //Initer.setupPhase(controller);
        Initer.initPersone(controller);
        Initer.initVisiteLuoghi(controller);
        controller.interpreter("done");
        Initer.initAvailability(controller);

        controller.interpreter("logout");
    }
}
