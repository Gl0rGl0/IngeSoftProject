package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Guest;
import ingsoft.persone.Volontario;
import ingsoft.util.ViewSE;
import java.util.ArrayList;

//UTILIZZO TIPO
//init: Main -> Test -> Comandi lanciati da interpeter in anticipo -> start dell'controller
//controller: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-opzioni] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...
public class Main {

    public static void main(String[] args) throws Exception{

        DBUtils model = new DBUtils();
        App controller = new App(model);
        ViewSE view = new ViewSE(controller);

        Test(controller); // Prima di avviare il ciclo...

        // Usi controller.intepreter per dare direttamente i comandi anche prima
        // dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'controller senza
        // passare dalla tastiera

        view.run();
    }

    public static void Test(App controller) {
        initDBInterprete(controller); // Inizializza il database in caso sia il primo accesso

        // controller.interpreter("preclude -r 14/05");
        // ViewSE.println(controller.db.dbDatesHelper.getPrecludedDates());
        // controller.intepreterSETUP("login config1 pass1C");
        // String prompt = " \"Parco di Pisogne\" \"Bellissimo parco brutto\"
        // 12.34:11.1";
        // controller.intepreterSETUP("login config1 pass1C");
        // controller.intepreterSETUP("add -L" + prompt);
        // controller.interpreter("remove -L \"Parco di Pisogne\"");
        // ViewSE.println(controller.db.getDBconfiguratori());
        // ViewSE.println(controller.db.changePsw(controller.db.getConfiguratoreFromDB("config1"),
        // "pass1C"));
        // ViewSE.println(controller.db.getDBconfiguratori());
        // ViewSE.println(controller.login("config1", "pass1")); //CONFIGURATORE
        // ViewSE.println(controller.login("config1", "pass1")); //CONFIGURATORE
        // ViewSE.println(controller.login("config2", "pass2")); //CONFIGURATORE
        // ViewSE.println(controller.login("config2", "123")); //ERRORE CREDENZIALI
        // ViewSE.println(controller.login("nonesisto", "1234")); //ERRORE
        // CREDENZIALI
        // ViewSE.println(controller.getConfiguratoriListString());
        // ViewSE.println(controller.db.removeConfiguratoreFromDB("config2"));
        // ViewSE.println(controller.getConfiguratoriListString());
        // ViewSE.println(controller.db.addConfiguratoreToDB(new
        // Configuratore("config2",
        // "pass2")));
        // ViewSE.println(controller.getConfiguratoriListString());
        // ViewSE.println(controller.getLuoghiList());
        // Volontario v = new Volontario("a", "a", "0");
        // v.setDisponibilita(controller.date, new Date("16/03/2025"));
        // v.setDisponibilita(controller.date, new Date("20/03/2025"));
        // v.setDisponibilita(controller.date, new Date("24/03/2025"));
        // v.setDisponibilita(controller.date, new Date("26/03/2025"));
        // v.setDisponibilita(controller.date, new Date("28/03/2025"));
        // v.printDisponibilita();
    }

    public static void initPersone(App controller) {
        // Ogni tipologia di persona ha i suoi metodi per aggiungere/rimuovere un
        // elemento
        ArrayList<Boolean> out = new ArrayList<>();
        out.add(controller.db.addConfiguratore("config1", "pass1C"));
        out.add(controller.db.addConfiguratore(new Configuratore("config2", "pass2C", "1")));

        out.add(controller.db.addFruitore("fruit1", "pass1F"));
        out.add(controller.db.addFruitore("fruit2", "pass2F"));
        out.add(controller.db.addFruitore(new Fruitore("fruit3", "pass3F", "1")));

        out.add(controller.db.addVolontario("volont1", "pass1V"));
        out.add(controller.db.addVolontario(new Volontario("volont2", "pass2V", "1")));
        out.add(controller.db.addVolontario(new Volontario("volont3", "pass3V", "1")));
        ViewSE.println(out + "\n---------------------------");
    }

    public static void initDBInterprete(App controller) {
        controller.user = new Configuratore("", "", "0");

        String addVisita = "add -t ";
        String visitaT1 = "\"Alla scoperta della cascata\" \"Un percorso guidato per scoprire le meraviglie naturali del parco.\" 10.8:39.31 15/05 21/12 9:00 90 false 10 20 SaDo";
        String visitaT2 = "\"Passeggiata naturalistica\" \"Una camminata rilassante immersa nella natura.\" 1.1:3.3 9/06 29/08 16:30 30 true 5 15 MeVeSa";
        String visitaT3 = "\"Alla scoperta del Moretto\" \"Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.\" 45.539:10.220 15/01 28/09 14:00 120 true 8 30 LuDo";
        String visitaTT = "\"Visita Test\" \"Descrizione Test\" 12.1:33.3 27/02 14/09 14:00 90 true 10 30 LuMaMe";
        
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
        
        controller.interpreter(addVolont + volontario1);
        controller.interpreter(addVolont + volontario2);

        String addFruit = "add -v ";
        String fruitore1 = "fruit1 pass1F";
        String fruitore2 = "fruit2 pass2F";
        String fruitore3 = "fruit3 pass3F";
        
        controller.interpreter(addFruit + fruitore1);
        controller.interpreter(addFruit + fruitore2);
        controller.interpreter(addFruit + fruitore3);

        controller.user = new Guest();
    }
}
