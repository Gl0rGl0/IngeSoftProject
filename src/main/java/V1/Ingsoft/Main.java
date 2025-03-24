package V1.Ingsoft;

import java.util.ArrayList;
import java.util.Random;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.item.persone.Volontario;
import V1.Ingsoft.model.Model;
import V1.Ingsoft.util.Date;
import V1.Ingsoft.util.JsonStorage;
import V1.Ingsoft.util.StringUtils;
import V1.Ingsoft.view.ViewSE;

//UTILIZZO TIPO
//init: Main -> test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
//controller: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-options] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...
public class Main {

    static ArrayList<String[]> visiteList = JsonStorage.loadList("test/visiteList", String[].class);
    static ArrayList<String[]> luoghiList = JsonStorage.loadList("test/luoghiList", String[].class);

    public static void main(String[] args) {

        Model model = new Model();
        Controller controller = new Controller(model);
        ViewSE view = new ViewSE(controller);

        controller.dailyAction();

        // Mettere controllo in db se tutti i db.prop esistono (se non c'è già...)
        controller.skipSetupTesting = true; // !model.isNew();

        test(controller); // Prima di avviare il ciclo...

        // Usi controller.intepreter per dare direttamente i comandi anche prima
        // dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'controller senza
        // passare dalla tastiera

        controller.interpreter("login config1 pass1C");
        controller.interpreter("changepsw pass1C pass1C");

        view.run();
    }

    public static void test(Controller controller) {
        controller.interpreter("login ADMIN PASSWORD");

        initDBInterprete(controller);
        initAssign(controller);
        initAvailability(controller);

        controller.interpreter("logout");
    }

    public static void initDBInterprete(Controller controller) {
        visiteList.forEach(v -> controller.interpreter("add -t " + StringUtils.arrayToStringClean(v)));

        luoghiList.forEach(v -> controller.interpreter("add -L " + StringUtils.arrayToStringClean(v)));

        for (int i = 1; i <= 3; i++)
            controller.interpreter(String.format("add -c config%d pass%dC", i, i));

        for (int i = 1; i <= 5; i++)
            controller.interpreter(String.format("add -v volont%d pass%dV", i, i));

        for (int i = 1; i <= 3; i++)
            controller.interpreter(String.format("add -f fruit%d pass%dF", i, i));
    }

    public static void initAssign(Controller controller) {

        for (int i = 0; i < 13; i++)
            controller.interpreter("assign -L \"" + luoghiList.get(i)[0] + "\" \"" + visiteList.get(i)[0] + "\"");

        for (int i = 0; i < 6; i++)
            controller.interpreter("assign -L \"" + luoghiList.get(i)[0] + "\" \"" + visiteList.get(i + 13)[0] + "\"");

        for (String[] visita : visiteList) {
            int ra = r.nextInt(1, 5);
            String assignCommand = "assign -V \"" + visita[0] + "\" volont" + ra;
            controller.interpreter(assignCommand);
        }
    }

    public static Random r = new Random();

    public static void initAvailability(Controller controller) {
        int meseNum = controller.date.getMonth() + 3;
        if (controller.date.getDay() < 16)
            meseNum--;
        for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
            for (int i = 0; i < 15; i++)
                v.setAvailability(controller.date,
                        new Date(String.format("%d/%d/2025", r.nextInt(1, Date.monthLength(meseNum)), meseNum)));
            System.out.println(v.getNumAvailability());
        }
    }
}
