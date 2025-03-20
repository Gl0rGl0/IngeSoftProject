package V1.ingsoft;

import java.util.ArrayList;
import java.util.Random;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.item.persone.Volontario;
import V1.ingsoft.model.Model;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.JsonStorage;
import V1.ingsoft.util.StringUtils;
import V1.ingsoft.view.ViewSE;

//UTILIZZO TIPO
//init: Main -> Test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
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

        // if (controller.skipSetupTesting)
        Test(controller); // Prima di avviare il ciclo...
        // System.out.println("CONFIGURATORI:" + controller.db.getConfiguratori());
        // System.out.println("VOLONTARI:" + controller.db.getVolontari());
        // System.out.println("FRUITORI:" + controller.db.getFruitori());
        // System.out.println("LUOGHI:" + controller.db.getLuoghi());
        // System.out.println("TIPIVISITA:" + controller.db.getTipi());

        // initAvailability(controller);

        // Usi controller.intepreter per dare direttamente i comandi anche prima
        // dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'controller senza
        // passare dalla tastiera

        controller.interpreter("login config1 pass1C");
        controller.interpreter("changepsw pass1C pass1C");

        view.run();
    }

    public static void Test(Controller controller) {
        controller.interpreter("login ADMIN PASSWORD");

        initDBInterprete(controller);
        initAssign(controller);
        initAvailability(controller);

        controller.interpreter("logout");
    }

    public static void initDBInterprete(Controller controller) {
        String addVisita = "add -t ";
        visiteList.forEach(v -> controller.interpreter(addVisita + StringUtils.arrayToStringClean(v)));

        String addLuogo = "add -L ";
        luoghiList.forEach(v -> controller.interpreter(addLuogo + StringUtils.arrayToStringClean(v)));

        String addCofig = "add -c ";
        String configuratore1 = "config1 pass1C";
        String configuratore2 = "config2 pass2C";

        controller.interpreter(addCofig + configuratore1);
        controller.interpreter(addCofig + configuratore2);

        String addVolont = "add -v ";
        String volontario1 = "volont1 pass1V";
        String volontario2 = "volont2 pass2V";
        String volontario3 = "volont3 pass3V";
        String volontario4 = "volont4 pass4V";
        String volontario5 = "volont5 pass5V";

        controller.interpreter(addVolont + volontario1);
        controller.interpreter(addVolont + volontario2);
        controller.interpreter(addVolont + volontario3);
        controller.interpreter(addVolont + volontario4);
        controller.interpreter(addVolont + volontario5);

        String addFruit = "add -f ";
        String fruitore1 = "fruit1 pass1F";
        String fruitore2 = "fruit2 pass2F";
        String fruitore3 = "fruit3 pass3F";

        controller.interpreter(addFruit + fruitore1);
        controller.interpreter(addFruit + fruitore2);
        controller.interpreter(addFruit + fruitore3);
    }

    public static void initAssign(Controller controller) {

        for (int i = 0; i < 13; i++)
            controller.interpreter("assign -L \"" + luoghiList.get(i)[0] + "\" \"" + visiteList.get(i)[0] + "\"");

        for (int i = 0; i < 6; i++)
            controller.interpreter("assign -L \"" + luoghiList.get(i)[0] + "\" \"" + visiteList.get(i + 13)[0] + "\"");

        Random r = new Random();
        for (String[] visita : visiteList) {
            int ra = r.nextInt(1, 5);
            String assignCommand = "assign -V \"" + visita[0] + "\" volont" + ra;
            controller.interpreter(assignCommand);
        }
    }

    public static void initAvailability(Controller controller) {
        Random r = new Random();
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
