package V5.Ingsoft;

import java.util.Arrays;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.model.seeder.Initer;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload.Status;
import V5.Ingsoft.view.ViewSE;

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
    public static void main(String[] args) throws Exception {
        AssertionControl.logMessage("INITED WITH: " + Arrays.toString(args), Status.DEBUG, "main");
        ViewSE view;

        args = new String[]{"-d"};

        if(args.length > 0 && args[0] != null && args[0].matches("[a-zA-Z]+")){
            view = new ViewSE(args[0]);
        }else{
            view = new ViewSE();
        }

        Controller c = view.controller;

        if (args.length > 0 && args[0] != null){
            for(String a : args)
                if(a.equals("-d"))
                    initDimostrazione(c);
        }

        view.run();
    }

    private static void initDimostrazione(Controller controller) throws Exception {
        Model.getInstance().clearAll();
        Initer.dimostrazione(controller);
    }
}
