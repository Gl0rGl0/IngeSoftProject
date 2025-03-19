package V1.ingsoft;

import java.util.Random;

import V1.ingsoft.DB.DBUtils;
import V1.ingsoft.persone.Configuratore;
import V1.ingsoft.persone.Guest;
import V1.ingsoft.persone.Volontario;
import V1.ingsoft.util.Date;

//UTILIZZO TIPO
//init: Main -> Test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
//controller: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-options] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...
public class Main {

    public static void main(String[] args) {

        DBUtils model = new DBUtils();
        App controller = new App(model);
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

    public static void Test(App controller) {
        controller.interpreter("login ADMIN PASSWORD");

        initDBInterprete(controller);
        initAssign(controller);

        controller.interpreter("logout");
    }

    public static void initDBInterprete(App controller) {
        String addVisita = "add -t ";
        String visitaT1 = "\"Alla scoperta della cascata\" \"Un percorso guidato per scoprire le meraviglie naturali del parco.\" 10.8:39.31 15/05/25 21/12/25 9:00 90 false 10 20 SaDo";
        String visitaT2 = "\"Passeggiata naturalistica\" \"Una camminata rilassante immersa nella natura.\" 1.1:3.3 9/06/25 29/08/25 16:30 30 true 5 15 MeVeSa";
        String visitaT3 = "\"Alla scoperta del Moretto\" \"Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.\" 45.539:10.220 15/01/25 28/09/25 14:00 120 true 8 30 LuDo";
        String visitaTT = "\"Visita Test\" \"Descrizione Test\" 12.1:33.3 27/02/25 14/09/25 14:00 90 true 10 30 LuMaMe";

        controller.interpreter(addVisita + visitaT1);
        controller.interpreter(addVisita + visitaT3);
        controller.interpreter(addVisita + visitaT2);
        controller.interpreter(addVisita + visitaTT);

        // Uso setup perchè nella V1/V2 non si possono aggiungere i luoghi dopo il setup
        String addLuogo = "add -L ";
        String luogoT1 = "\"Parco Grotta Cascata del Varone\" \"Un incantevole parco naturale con una spettacolare cascata sotterranea.\" 45.885:10.821";
        String luogoT2 = "\"Pinacoteca Tosio-Martinengo di Brescia\" \"Sede museale ospitata nel palazzo Martinengo da Barco che espone opere pittoriche dal Trecento all'Ottocento.\" 45.539:10.220";
        String luogoTT = "\"Luogo Test\" \"Descrizione luogo test\" 12.34:56.78";

        controller.interpreterSETUP(addLuogo + luogoT1);
        controller.interpreterSETUP(addLuogo + luogoT2);
        controller.interpreterSETUP(addLuogo + luogoTT);

        String addCofig = "add -c ";
        String configuratore1 = "config1 pass1C";
        String configuratore2 = "config2 pass2C";

        controller.interpreter(addCofig + configuratore1);
        controller.interpreter(addCofig + configuratore2);

        String addVolont = "add -v ";
        String volontario1 = "volont1 pass1V";
        String volontario2 = "volont2 pass2V";
        String volontario3 = "volont3 pass3V";

        controller.interpreter(addVolont + volontario1);
        controller.interpreter(addVolont + volontario2);
        controller.interpreter(addVolont + volontario3);

        String addFruit = "add -f ";
        String fruitore1 = "fruit1 pass1F";
        String fruitore2 = "fruit2 pass2F";
        String fruitore3 = "fruit3 pass3F";

        controller.interpreter(addFruit + fruitore1);
        controller.interpreter(addFruit + fruitore2);
        controller.interpreter(addFruit + fruitore3);
    }

    public static void initAssign(App controller) {
        String lv1 = "\"Parco Grotta Cascata del Varone\"" + " " + "\"Passeggiata naturalistica\"";
        String lv2 = "\"Pinacoteca Tosio-Martinengo di Brescia\"" + " " + "\"Alla scoperta del Moretto\"";
        String lv3 = "\"Luogo Test\"" + " " + "\"Visita Test\"";
        String assignLV = "assign -L ";

        controller.interpreter(assignLV + lv1);
        controller.interpreter(assignLV + lv2);
        controller.interpreter(assignLV + lv3);

        String tv1 = "\"Passeggiata naturalistica\"" + " " + "volont1";
        String tv2 = "\"Alla scoperta del Moretto\"" + " " + "volont2";
        String tv3 = "\"Visita Test\"" + " " + "volont3";
        String assignTV = "assign -V ";

        controller.interpreter(assignTV + tv1);
        controller.interpreter(assignTV + tv2);
        controller.interpreter(assignTV + tv3);
    }

    public static void initAvailability(App controller) {
        Random r = new Random();
        for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
            for (int i = 0; i < 15; i++)
                v.setAvailability(controller.date, new Date(String.format("%d/%d/2025", r.nextInt(1, 28), 5)));
        }

        // for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
        // for (boolean b : v.getAvailability())
        // ;
        // }
    }
}
