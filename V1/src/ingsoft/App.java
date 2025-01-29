package ingsoft;

import ingsoft.luoghi.Luogo;
import ingsoft.persone.Configuratore;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.util.DBUtils;
import ingsoft.util.FunctionList;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class App {
    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate";
    private static final String MESSAGGIO_LOGIN_FIRST_CONFIGURATORE = "Esegui il primo login da Configuratore";
    private static final String MESSAGGIO_MENU = """
            1. Stampa un messaggio
            2. Esegui un'operazione
            3. Mostra il tempo corrente
            4. Log Out
            0. Esci
            """;
    private static final String MESSAGGIO_CHIUSURA = "Programma terminato. Arrivederci!";

    DBUtils db = new DBUtils();
    private Persona user;

    public App(){
    }

    public PersonaType login(String user, String psw){
        if (db.loginCheckConfiguratore(user, psw)) {
            return PersonaType.CONFIGURATORE;
        }

        // if (db.loginCheckFruitore(user,psw)){
        //     ViewSE.log("BBBB");
        //     return PersonaType.FRUITORE;
        // }
        return PersonaType.ERROR;
    }

    public PersonaType login(){
        final int MAX_TENTATIVI = 5;
        int tentativiRimasti = MAX_TENTATIVI;

        while (tentativiRimasti > 0) {
            ViewSE.log("Inserisci username: ");
            String user = ViewSE.read();
            ViewSE.log("Inserisci password: ");
            String psw = ViewSE.read();

            PersonaType log = login(user, psw);
            if (log != PersonaType.ERROR) {
                ViewSE.log("Login riuscito! Ciao " + user + " (" + log + ")");
                this.user = db.getConfiguratoreFromDB(user);
                return log;
            } else {
                tentativiRimasti--;
                ViewSE.log("Login fallito. Tentativi rimasti: " + tentativiRimasti);
            }
        }
        ViewSE.log("Hai esaurito i tentativi. Accesso negato.");
        return PersonaType.ERROR;
    }

    public String getConfiguratoriListString(){
        StringBuilder out = new StringBuilder();
        for (Configuratore c : db.getDBconfiguratori()) {
            out.append(c.getUsername()).append("\n");
        }
        return out.toString();
    }

    public ArrayList<Configuratore> getConfiguratoriList(){
        ArrayList<Configuratore> out = new ArrayList<>();
        for (Configuratore configuratore : db.getDBconfiguratori()) {
            out.add(new Configuratore(configuratore.getUsername(), "*****"));
        }
        return out;
    }

    public ArrayList<Luogo> getLuoghiList(){
        return db.getDBLuoghi();
    }

    private boolean running = true;
    public void start() {
        ViewSE.log(MESSAGGIO_START);

        // ViewSE.log(MESSAGGIO_LOGIN_FIRST_CONFIGURATORE);
        // if (login() != PersonaType.CONFIGURATORE){
        //     ViewSE.log("Primo accesso necessario un configuratore!");
        //     return;
        // }

        running = !running; //invertiFlusso
        while (running) {
            if(user == null)
                login();

            // Mostra il menu
            ViewSE.log(MESSAGGIO_MENU);

            // Legge l'input dell'utente
            ViewSE.log("Inserisci la tua scelta: ");
            int scelta;
            try {
                scelta = Integer.parseInt(ViewSE.read());
            } catch (NumberFormatException e) {
                ViewSE.log("Input non valido. Inserisci un numero.");
                continue;
            }

            switch(user.type()){
                case CONFIGURATORE -> switchCONFIGURATORE(scelta);
                case FRUITORE -> switchFRUITORE(scelta);
                case VOLONTARIO -> switchVOLONTARIO(scelta);
            }
        }

        while(!running){
            interpreter(ViewSE.read());
        }
    }

    private void switchCONFIGURATORE(int s){
        switch (s) {
            case 1 -> stampaMessaggio();
            case 2 -> eseguiOperazione();
            case 3 -> mostraTempoCorrente();
            case 4 -> logout();
            case 0 -> terminaProgramma();
            default -> ViewSE.log("Scelta non valida. Riprova.");
        }
    }

    private void logout(){
        ViewSE.log("Log out effettuato, riaccedere...");
        user = null;
    }

    private void switchFRUITORE(int s){}

    private void switchVOLONTARIO(int s){}

    private void stampaMessaggio() {
        ViewSE.log("Hai selezionato: Stampa un messaggio!");
    }

    private void eseguiOperazione() {
        ViewSE.log("Hai selezionato: Esegui un'operazione!");
    }

    private void mostraTempoCorrente() {
        ViewSE.log("Tempo corrente: " + java.time.LocalTime.now());
    }

    private void terminaProgramma() {
        ViewSE.log(MESSAGGIO_CHIUSURA);
        running = false;
    }



    private void interpreter(String prompt) {
        ArrayList<String> args = new ArrayList<>(Arrays.asList(prompt.split(" ")));
        if (args.isEmpty()) {
            ViewSE.log("Errore: nessun comando fornito.");
            return;
        }
    
        String cmd = args.remove(0); // Usa remove(0) per ottenere e rimuovere il primo elemento
    
        Set<Character> opzioni = new HashSet<>(); // Usa HashSet per opzioni univoche
        ArrayList<String> elementi = new ArrayList<>();
    
        for (String string : args) {
            if (string.startsWith("-") && string.length() > 1) {
                opzioni.add(string.charAt(1));
            } else {
                elementi.add(string);
            }
        }
        
        char[] option = new char[opzioni.size()];
        int i = 0;
        for (char c : opzioni) {
            option[i++] = c;
        }
    
        switch (cmd) {
            case "add" -> {
                ViewSE.log("ADD");
                add(option, elementi);
            }
            case "remove" -> {
                ViewSE.log("REMOVE");
                remove(option, elementi);
            }
            case "help" -> ViewSE.log(FunctionList.HELP);
            default -> ViewSE.log("\"" + cmd + "\" non è riconosciuto come comando interno");
        }
    }

    private void add(char[] opzioni, ArrayList<String> args) {
        if(opzioni.length < 1){
            ViewSE.log("Errore nell'utilizzo del prompt: " + FunctionList.ADD);
            return;
        }
        switch(opzioni[0]){
            case 'c' -> ViewSE.log("CONFIGURATORE");
            case 'f' -> ViewSE.log("FRUITORE");
            case 'v' -> ViewSE.log("VOLONTARIO");
            case 'V' -> ViewSE.log("VISITA");
            case 'L' -> ViewSE.log("LUOGO");
            default -> ViewSE.log("Errore nell'utilizzo del prompt: " + FunctionList.ADD);
        }
    }

    private void remove(char[] opzioni, ArrayList<String> args){
        if(opzioni.length < 1){
            ViewSE.log("Errore nell'utilizzo del prompt: " + FunctionList.REMOVE);
            return;
        }
        switch(opzioni[0]){
            case 'c' -> ViewSE.log("CONFIGURATORE");
            case 'f' -> ViewSE.log("FRUITORE");
            case 'v' -> ViewSE.log("VOLONTARIO");
            case 'V' -> ViewSE.log("VISITA");
            case 'L' -> ViewSE.log("LUOGO");
            default -> ViewSE.log("Errore nell'utilizzo del prompt: " + FunctionList.REMOVE);
        }
    }

}
