package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Fruitore;
import ingsoft.persone.Volontario;

//UTILIZZO TIPO
//init: Main -> Test -> Comandi lanciati da interpeter in anticipo -> start dell'app
//app: MessaggioBenvenuto -> while(Interpreter)
//Interpreter: [comando] [-opzioni] [argomenti], ogni comando è passato al suo Command(>Abstract|Interface) dove viene gestito
//DB: Crea 5 sotto helper che gestiscono ognuno una parte di dati, Configuratori,ecc
//Classi di Utilita: letteralmente classi di utilita, ora, data, interazione con esterno ecc...
public class Main {

    public static void main(String[] args) {

        // String[] d = LocalDateTime.now().toString().substring(0, 10).split("-");
        // //GIORNO IN CUI AVVII IL MAIN
        // String da = d[2] + "/" + d[1] + "/" + d[0];

        DBUtils db = new DBUtils();
        App app = new App(db);

        app.interpreter("login config1 pass1C");
        Test(app); // Prima di avviare il ciclo...

        // Usi app.intepreter per dare direttamente i comandi anche prima
        // dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'app senza
        // passare dalla tastiera
        // app.interpreter("login config1 pass1C");
        // app.interpreter("time -s 12/02/2025");
        // app.interpreter("time");

        app.start();
        //app.interpreter("preclude -a 1/4");
        //app.interpreter("assign -v volont1 \"Visita Test\"");
    }

    public static void Test(App app) {
        initPersone(app); // Inizializza il database in caso sia il primo accesso

        app.skipSetupTesting = true;

        //app.interpreter("login config1 pass1C");
        //app.interpreter("changepsw pass1C");
        initLuoghioVisiteInterprete(app);

        // app.interpreter("preclude -r 14/05");
        // System.out.println(app.db.dbDatesHelper.getPrecludedDates());
        // app.intepreterSETUP("login config1 pass1C");
        // String prompt = " \"Parco di Pisogne\" \"Bellissimo parco brutto\"
        // 12.34:11.1";
        // app.intepreterSETUP("login config1 pass1C");
        // app.intepreterSETUP("add -L" + prompt);
        // app.interpreter("remove -L \"Parco di Pisogne\"");
        // System.out.println(app.db.getDBconfiguratori());
        // System.out.println(app.db.changePsw(app.db.getConfiguratoreFromDB("config1"),
        // "pass1C"));
        // System.out.println(app.db.getDBconfiguratori());
        // System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        // System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        // System.out.println(app.login("config2", "pass2")); //CONFIGURATORE
        // System.out.println(app.login("config2", "123")); //ERRORE CREDENZIALI
        // System.out.println(app.login("nonesisto", "1234")); //ERRORE CREDENZIALI
        // System.out.println(app.getConfiguratoriListString());
        // System.out.println(app.db.removeConfiguratoreFromDB("config2"));
        // System.out.println(app.getConfiguratoriListString());
        // System.out.println(app.db.addConfiguratoreToDB(new Configuratore("config2",
        // "pass2")));
        // System.out.println(app.getConfiguratoriListString());
        // System.out.println(app.getLuoghiList());
        // Volontario v = new Volontario("a", "a", "0");
        // v.setDisponibilita(app.date, new Date("16/03/2025"));
        // v.setDisponibilita(app.date, new Date("20/03/2025"));
        // v.setDisponibilita(app.date, new Date("24/03/2025"));
        // v.setDisponibilita(app.date, new Date("26/03/2025"));
        // v.setDisponibilita(app.date, new Date("28/03/2025"));
        // v.printDisponibilita();
    }

    public static void initPersone(App app) {
        // Ogni tipologia di persona ha i suoi metodi per aggiungere/rimuovere un
        // elemento
        System.out.println(app.db.addConfiguratore("config1", "pass1C"));
        System.out.println(app.db.addConfiguratore(new Configuratore("config2", "pass2C", "1")));

        System.out.println(app.db.addFruitore("fruit1", "pass1F"));
        System.out.println(app.db.addFruitore("fruit2", "pass2F"));
        System.out.println(app.db.addFruitore(new Fruitore("fruit3", "pass3F", "1")));

        System.out.println(app.db.addVolontario("volont1", "pass1V"));
        System.out.println(app.db.addVolontario(new Volontario("volont2", "pass2V", "1")));
        System.out.println(app.db.addVolontario(new Volontario("volont3", "pass3V", "1")));
        System.out.println("\n\n\n---------------------------");
        // Testa se è il primo accesso per tutti i configuratori
        // app.db.getConfiguratori().forEach(c -> System.out.println(c.firstAccess()));
        // System.out.println(app.db.getConfiguratori());
    }

    public static void initLuoghioVisiteInterprete(App app) {
        app.interpreter("add -t \"Visita Test\" \"Descrizione Test\" 12.1:33.3 27/02 14/09 14:00 90 true 10 30 LuMaMe");
        app.interpreter(
                "add -t \"Alla scoperta della cascata\" \"Un percorso guidato per scoprire le meraviglie naturali del parco.\" 10.8:39.31 15/05 21/12 9:00 90 false 10 20 SaDo");
        app.interpreter(
                "add -t \"Passeggiata naturalistica\" \"Una camminata rilassante immersa nella natura.\" 1.1:3.3 9/06 29/08 16:30 30 true 5 15 MeVeSa");
        app.interpreter(
                "add -t \"Alla scoperta del Moretto\" \"Una visita guidata alla scoperta delle opere del grande maestro rinascimentale.\" 45.539:10.220 15/01 28/09 14:00 120 true 8 30 LuDo");

        // Uso setup perchè nella V1/V2 non si possono aggiungere i luoghi dopo il setup
        app.interpreterSETUP(
                "add -L \"Parco Grotta Cascata del Varone\" \"Un incantevole parco naturale con una spettacolare cascata sotterranea.\" 45.885:10.821");
        app.interpreterSETUP(
                "add -L \"Pinacoteca Tosio-Martinengo di Brescia\" \"Sede museale ospitata nel palazzo Martinengo da Barco che espone opere pittoriche dal Trecento all'Ottocento.\" 45.539:10.220");
        app.interpreterSETUP("add -L \"Parco di Pisogne\" \"Bellissimo parco brutto\" 12.34:11.1");
    }
}
