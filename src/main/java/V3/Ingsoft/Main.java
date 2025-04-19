package V3.Ingsoft;

import java.time.Month;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.model.Model;
import V3.Ingsoft.util.Date;
import V3.Ingsoft.view.ViewSE;

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

        view.run();
    }
}
