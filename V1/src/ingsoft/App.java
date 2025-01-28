package ingsoft;

import ingsoft.luoghi.Luogo;
import ingsoft.persone.Configuratore;
import ingsoft.persone.PersonaType;
import ingsoft.util.DBUtils;
import java.util.ArrayList;
import java.util.Scanner;

public class App {
    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate";
    private static final String MESSAGGIO_LOGIN = "Esegui il primo login da Configuratore";
    private static final String MESSAGGIO_MENU = """
            1. Stampa un messaggio
            2. Esegui un'operazione
            3. Mostra il tempo corrente
            0. Esci
            """;
    private static final String MESSAGGIO_CHIUSURA = "Programma terminato. Arrivederci!";

    DBUtils db = new DBUtils();
    private Scanner scanner;

    public App(){
        scanner = new Scanner(System.in);
    }

    public PersonaType login(String user, String psw){
        if (db.checkConfiguratore(user, psw)) {
            return PersonaType.CONFIGURATORE;
        }

        if (db.checkFruitore(user,psw)){
            return PersonaType.FRUITORE;
        }
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
        System.out.println(MESSAGGIO_START);

        PersonaType status = initLogin();
        if (status == PersonaType.ERROR)
            return;

        while (running) {
            // Mostra il menu
            System.out.println(MESSAGGIO_MENU);

            // Legge l'input dell'utente
            System.out.print("Inserisci la tua scelta: ");
            int scelta;
            try {
                scelta = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Input non valido. Inserisci un numero.");
                continue;
            }

            switch(status){
                case CONFIGURATORE -> switchCONFIGURATORE(scelta);
                case FRUITORE -> switchFRUITORE(scelta);
                case VOLONTARIO -> switchVOLONTARIO(scelta);
            }
            
        }

        scanner.close(); // Chiude lo scanner solo alla fine
    }

    private void switchCONFIGURATORE(int s){
        switch (s) {
            case 1 -> stampaMessaggio();
            case 2 -> eseguiOperazione();
            case 3 -> mostraTempoCorrente();
            case 0 -> terminaProgramma();
            default -> System.out.println("Scelta non valida. Riprova.");
        }
    }

    private void switchFRUITORE(int s){
        
    }

    private void switchVOLONTARIO(int s){
        
    }

    private PersonaType initLogin() {
        final int MAX_TENTATIVI = 5;
        int tentativiRimasti = MAX_TENTATIVI;

        while (tentativiRimasti > 0) {
            System.out.print("Inserisci username: ");
            String user = scanner.nextLine();
            System.out.print("Inserisci password: ");
            String psw = scanner.nextLine();

            if (login(user, psw) != PersonaType.ERROR) {
                System.out.println("Login riuscito! Ciao " + user + " (" +(login(user, psw) + ")"));
                return login(user, psw);
            } else {
                tentativiRimasti--;
                System.out.println("Login fallito. Tentativi rimasti: " + tentativiRimasti);
            }
        }
        System.out.println("Hai esaurito i tentativi. Accesso negato.");
        return PersonaType.ERROR;
    }

    private void stampaMessaggio() {
        System.out.println("Hai selezionato: Stampa un messaggio!");
    }

    private void eseguiOperazione() {
        System.out.println("Hai selezionato: Esegui un'operazione!");
        // Logica per eseguire l'operazione
    }

    private void mostraTempoCorrente() {
        System.out.println("Tempo corrente: " + java.time.LocalTime.now());
    }

    private void terminaProgramma() {
        System.out.println(MESSAGGIO_CHIUSURA);
        running = false;
    }
}
