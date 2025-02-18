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
        
        //Date.cheGiornoE(new Date("17/02/2025").localDate);

        //String[] d = LocalDateTime.now().toString().substring(0, 10).split("-"); //GIORNO IN CUI AVVII IL MAIN
        //String da = d[2] + "/" + d[1] + "/" + d[0];
        String da = "16/02/2025";

        DBUtils db = new DBUtils();
        //
        App app = new App(db);
        Test(app);

        //Usi app.intepreter per dare direttamente i comandi anche prima dell'interazione con l'utente
        // -> Guadagno: GUI in cui si manda la stringa direttamente all'app senza passare dalla tastiera
        app.interpreter("login config1 pass1C");    //da errore perchè non è finito il setup?
        //app.interpreter("time -s 12/02/2025");
        app.interpreter("time -s " + da);
        app.interpreter("time");

        //Inizia l'interazione con l'utente da tastiera

        // Volontario v = new Volontario("a", "a", "0");
        // v.setDisponibilita(app.date, new Date("16/03/2025"));
        // v.setDisponibilita(app.date, new Date("20/03/2025"));
        // v.setDisponibilita(app.date, new Date("24/03/2025"));
        // v.setDisponibilita(app.date, new Date("26/03/2025"));
        // v.setDisponibilita(app.date, new Date("28/03/2025"));
        // v.printDisponibilita();
        app.start();
    }


    public static void Test(App app){
        initPersone(app); //Inizializza il database in caso sia il primo accesso

        app.intepreterSETUP("login config1 pass1C");
        String prompt = " \"Parco di Pisogne\" \"Bellissimo parco brutto\" 12.34:11.1 Visita1 Visita2 Visita3 ";
        app.intepreterSETUP("add -L" + prompt);

        //System.out.println(app.db.getDBconfiguratori());
        //System.out.println(app.db.changePsw(app.db.getConfiguratoreFromDB("config1"), "pass1C"));
        //System.out.println(app.db.getDBconfiguratori());
        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        //System.out.println(app.login("config1", "pass1")); //CONFIGURATORE
        //System.out.println(app.login("config2", "pass2")); //CONFIGURATORE
        //System.out.println(app.login("config2", "123")); //ERRORE CREDENZIALI
        //System.out.println(app.login("nonesisto", "1234")); //ERRORE CREDENZIALI

        //System.out.println(app.getConfiguratoriListString());
        //System.out.println(app.db.removeConfiguratoreFromDB("config2"));
        //System.out.println(app.getConfiguratoriListString());
        //System.out.println(app.db.addConfiguratoreToDB(new Configuratore("config2", "pass2")));
        //System.out.println(app.getConfiguratoriListString());
    
        //System.out.println(app.getLuoghiList());
    }

    public static void initPersone(App app){
        //Ogni tipologia di persona ha i suoi metodi per aggiungere/rimuovere un elemento
        System.out.println(app.db.addConfiguratore("config1", "pass1C"));
        System.out.println(app.db.addConfiguratore(new Configuratore("config2", "pass2C", "1")));

        System.out.println(app.db.addFruitore("fruit1", "pass1F"));
        System.out.println(app.db.addFruitore("fruit2", "pass2F"));
        System.out.println(app.db.addFruitore(new Fruitore("fruit3", "pass3F", "1")));

        System.out.println(app.db.addVolontario("volont1", "pass1V"));
        System.out.println(app.db.addVolontario(new Volontario("volont2", "pass2V", "1")));
        System.out.println("\n\n\n---------------------------");
        //Testa se è il primo accesso per tutti i configuratori
        //app.db.getConfiguratori().forEach(c -> System.out.println(c.firstAccess()));
        //System.out.println(app.db.getConfiguratori());
    }
}