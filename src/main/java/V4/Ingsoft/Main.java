package V4.Ingsoft;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.Initer;
import V4.Ingsoft.view.ViewSE;

//UTILIZZO TIPO
//init: Main -> test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
//controller: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-options] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...

// Usi controller.intepreter per dare direttamente i comandi anche prima
// dell'interazione con l'utente
// -> Guadagno: GUI in cui si manda la stringa direttamente all'controller senza
// passare dalla tastiera
public class Main {
    public static void main(String[] args) {

        Model model = new Model();
        Controller controller = new Controller(model);
        ViewSE view = new ViewSE(controller);

        controller.dailyAction();

        test(controller);

        controller.interpreter("login config1 pass1C");
        controller.interpreter("changepsw pass1C pass1C");

        view.run();
    }

    public static void test(Controller controller) {
        controller.skipSetupTesting = true;
        controller.interpreter("login ADMIN PASSWORD");

        Initer.initPersone(controller);
        Initer.initVisiteLuoghi(controller);
        Initer.initAvailability(controller);

        controller.interpreter("logout");
    }
}
