package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.commands.AssignCommand;
import ingsoft.commands.ChangePswCommand;
import ingsoft.commands.Command;
import ingsoft.commands.ExitCommand;
import ingsoft.commands.HelpCommand;
import ingsoft.commands.LogoutCommand;
import ingsoft.commands.SetPersoneMaxCommand;
import ingsoft.commands.TimeCommand;
import ingsoft.commands.running.AddCommand;
import ingsoft.commands.running.CommandList;
import ingsoft.commands.running.ListCommand;
import ingsoft.commands.running.LoginCommand;
import ingsoft.commands.running.PrecludeCommand;
import ingsoft.commands.running.RemoveCommand;
import ingsoft.commands.setup.AddCommandSETUP;
import ingsoft.commands.setup.CommandListSETUP;
import ingsoft.commands.setup.DoneCommandSETUP;
import ingsoft.commands.setup.LoginCommandSETUP;
import ingsoft.commands.setup.SetAmbitoCommandSETUP;
import ingsoft.luoghi.TipoVisita;
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.persone.Volontario;
import ingsoft.util.Date;
import ingsoft.util.Interpreter;
import ingsoft.util.TimeHelper;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static final int SECONDIVIRTUALI_PS = 120; // 12minuti reali sono 1gg nella simulazione -> 1rs : 120vs =
    // 12rmin : 24hv
    // private static final String MESSAGGIO_PRIMO_ACCESSO = "PRIMO ACCESSO
    // ESEGUITOindicare l'ambito territoriale di appartenenza";
    // private static final String AMBITO_TERRITORIALE = "Indicare l'ambito
    // territoriale di appartenenza";
    // private static final String AMBITO_SCELTO = "Ambito territoriale scelto
    // correttamente: ";
    // private static final String MESSAGGIO_MAX_PERSONE = "Impostare quante persone
    // possono iscriversi ad un evento in una sola operazione da parte di un
    // visitatore";
    // private static final String MAX_PRENOTAZIONI_SCELTO = "Numero correttamente
    // impostato: ";

    public String ambitoTerritoriale = new String();
    public int maxPrenotazioniPerPersona;

    public DBUtils db;
    // Inizialmente l'utente è un Guest (non loggato)
    public Persona user = new Guest();
    private final Interpreter interpreter;
    private final Interpreter setupInterpreter;
    public Date date = new Date(); // mette oggi semplicemente

    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final Map<String, Command> setupCommandRegistry = new HashMap<>();
    private final Command TimeC = new TimeCommand(this);
    private final Command LogoutC = new LogoutCommand(this);
    private final Command ChangeC = new ChangePswCommand(this);
    private final Command AssignC = new AssignCommand(this);
    private final Command ExitC = new ExitCommand();
    private final Command SetMaxC = new SetPersoneMaxCommand(this); // COSI DA NON CREARE DOPPIONI INUTILI

    public App(DBUtils db) {
        this.db = db;
        // Registra i comandi nel costruttore
        initVirtualTime();
        initDailyScheduler();
        registerCommands();
        setupRegisterCommands();

        interpreter = new Interpreter(commandRegistry);
        setupInterpreter = new Interpreter(setupCommandRegistry);
    }

    private void initDailyScheduler() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            azioneDelGiorno();
        }, 1, 60 * 60 * 24 / SECONDIVIRTUALI_PS, TimeUnit.SECONDS); // OGNI GIORNO VIRTUALE (ogni 12min) = 60 * 12
    }

    private void initVirtualTime() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        TimeHelper timeHelper = new TimeHelper(this);
        // Supponiamo che ogni secondo di tempo reale rappresenti un intervallo di
        // aggiornamento
        scheduler.scheduleAtFixedRate(timeHelper, 0, 1, TimeUnit.SECONDS);
    }

    // Ogni nuovo comando va inserito nella mappa in modo da poterlo riconoscere e
    // in CommandList per avere le informazioni
    private void registerCommands() {
        // Passa l'istanza di App se i comandi hanno bisogno di accedere ad essa
        // (praticamente tutti)
        commandRegistry.put("add", new AddCommand(this));
        commandRegistry.put("list", new ListCommand(this));
        commandRegistry.put("login", new LoginCommand(this));
        commandRegistry.put("preclude", new PrecludeCommand(this));
        commandRegistry.put("remove", new RemoveCommand(this));
        commandRegistry.put("logout", LogoutC);
        commandRegistry.put("changepsw", ChangeC);
        commandRegistry.put("time", TimeC);
        commandRegistry.put("assign", AssignC);
        commandRegistry.put("setmax", SetMaxC);
        commandRegistry.put("help", new HelpCommand(this, CommandList.HELP));

        // Puoi aggiungere altri comandi
        commandRegistry.put("exit", ExitC);
    }

    private void setupRegisterCommands() {
        setupCommandRegistry.put("add", new AddCommandSETUP(this)); // DA ESEGUIRE
        setupCommandRegistry.put("done", new DoneCommandSETUP()); // DA ESEGUIRE
        setupCommandRegistry.put("login", new LoginCommandSETUP(this)); // DA ESEGUIRE
        setupCommandRegistry.put("setambito", new SetAmbitoCommandSETUP(this)); // DA ESEGUIRE
        setupCommandRegistry.put("logout", LogoutC);
        setupCommandRegistry.put("changepsw", ChangeC);
        setupCommandRegistry.put("time", TimeC);
        setupCommandRegistry.put("setmax", SetMaxC); // DA ESEGUIRE
        setupCommandRegistry.put("help", new HelpCommand(this, CommandListSETUP.HELP));

        // Puoi aggiungere altri comandi
        setupCommandRegistry.put("exit", ExitC);
    }

    /**
     * Metodo interprete che analizza la stringa di comando immessa dall'utente,
     * ne estrae il comando, le opzioni (precedute dal carattere '-') e gli
     * argomenti, e poi esegue l'azione corrispondente.
     *
     * @param prompt la stringa di comando immessa
     */
    public void interpreter(String prompt) {
        interpreter.interpret(prompt, user);
    }

    public void interpreterSETUP(String prompt) {
        setupInterpreter.interpret(prompt, user);
    }

    boolean skipSetupTesting = false;

    public boolean setupCompleted(){
        return setupInterpreter.haveAllBeenExecuted() || skipSetupTesting;
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public void setUser(String username) {
        this.user = db.findUser(username);
        if (this.user == null) {
            this.user = new Guest();
        }
    }// INUTILE, TOREMOVE?

    public void addPrecludedDate(String d) {
        db.addPrecludedDate(new Date(d));
    }

    public ArrayList<Date> getDate() {
        return db.getPrecludedDates();
    }

    public void azioneDelGiorno() {

        //refresh--3 giorni al termine -> STATO.CLOSE

        switch (date.getGiorno()) {
            case 1 -> db.refreshPrecludedDate(this.date);
            //case 16 -> makePianoVisite();
        }
        
    }

    public void makePianoVisite() {
        int currentMonth = date.getMese(); // mese attuale (1-based)
        // Per test: se il mese attuale è 11, allora il mese prossimo (secondo la logica) diventa 1, altrimenti currentMonth + 2.
        int meseProssimo = (currentMonth == 11) ? 1 : currentMonth + 2;

        // Creiamo una data di riferimento (ad esempio il 16 del mese prossimo)
        Date d = new Date("16/" + meseProssimo);

        // Recupera e ordina i volontari in base al numero di disponibilità (minimo in testa)
        ArrayList<Volontario> volontariSorted = db.getVolontari();
        volontariSorted.sort(Comparator.comparingInt(Volontario::getNumDisponibilita));

        // Recupera le date precluse
        ArrayList<Date> precludedDates = db.dbDatesHelper.getPrecludedDates();

        // Ottieni la lunghezza del mese relativo alla data d (presumibilmente in giorni)
        int daysInMonth = Date.lunghezzaMese(d);

        // Itera per ogni giorno del mese prossimo (1-based)
        for (int i = 1; i <= daysInMonth; i++) {
            boolean presente = false;

            // Verifica se il giorno "i" è precluso
            for (Date dat : precludedDates) {
                if (dat.getGiorno() == i && dat.getMese() == meseProssimo) {
                    presente = true;
                    break;
                }
            }
            if (presente) {
                continue;
            }

            // Crea una data da controllare per il giorno i del mese prossimo
            Date toCheck = new Date(i + "/" + meseProssimo);

            // Per ogni volontario ordinato
            for (Volontario v : volontariSorted) {
                // Supponiamo che la disponibilità modificabile sia nella riga 1 (array 0-based: index 1) e che
                // il giorno nel calendario corrisponda a index = (giorno - 1)
                if (!v.disponibilita[i]) {
                    // Se il volontario non è disponibile per questo giorno, passa al prossimo volontario
                    continue;
                }

                // Recupera l'elenco degli UID dei tipi di visita assegnati al volontario
                ArrayList<String> uidVisiteV = v.getTipiVisiteUID();
                ViewSE.print("UID tipi di visita per " + v.getUsername() + ": " + uidVisiteV);

                // Per ciascun tipo di visita assegnato
                for (String s : uidVisiteV) {
                    TipoVisita tv = db.getTipiByUID(s);
                    if (tv == null) {
                        continue;
                    }

                    // Verifica se il giorno da controllare è tra quelli programmabili per questo tipo di visita
                    // Supponiamo che tv.getGiorni() restituisca un Collection (ad es. ArrayList<Integer> o ArrayList<String>)
                    // e che toCheck.cheGiornoE() restituisca un valore compatibile per il confronto.
                    
                    boolean isProgrammable = tv.giorni.contains(toCheck.cheGiornoE());
                    ViewSE.print("Verifica per " + tv.getTitolo() + ": " + isProgrammable);

                    ViewSE.printIf(isProgrammable, v.getUsername() + " può lavorare il " + i + "/" + meseProssimo
                    + " per il tipo di visita " + tv.getTitolo());
                    // Qui puoi creare l'istanza della visita o registrarla nel sistema
                }
            }

        }
    }

    public void makeOrarioVecchio() {       //È la versione incollata su gpt, il risultato è quello sopra (ovviamente non funziona)
          //boh fai orario?
        //non so se serva un comando che venga lanciato dal configuratore o possa essere svolto in autonomia...
        //Verranno create tutte le visite massimizzando le disponibilità dei volontari

        int currentMonth = date.getMese();
        // int meseProssimo = (currentMonth == 12) ? 1 : currentMonth + 1;      //QUESTO È GIUSTO, SOTTO PER TEST
        int meseProssimo = (currentMonth == 11) ? 1 : currentMonth + 2;
        Date d = new Date("16/" + meseProssimo);

        ArrayList<Volontario> volontariSorted = db.getVolontari();
        volontariSorted.sort(Comparator.comparingInt(Volontario::getNumDisponibilita));

        ArrayList<Date> precludedDates = db.dbDatesHelper.getPrecludedDates();
        boolean presente;
        for (int i = 1; i < Date.lunghezzaMese(d); i++) {
            presente = false;

            for (Date dat : precludedDates) {
                if (dat.getGiorno() == i && dat.getMese() == meseProssimo) {
                    presente = true;
                    break;
                }
            }
            if (presente) {
                continue;
            }
            //Date toCheck = new Date(i + "/" + meseProssimo);
            for (Volontario v : volontariSorted) {
                if (v.disponibilita[i] == false) {
                    break;
                }
                ArrayList<String> uidVisiteV = v.getTipiVisiteUID();
                ViewSE.println(uidVisiteV);
                for (String s : uidVisiteV) {
                    TipoVisita tv = db.getTipiByUID(s);
                    if (tv == null) {
                        continue;
                    }
                    ViewSE.println("PD");
                    //ViewSE.println(tv.giorni.contains(toCheck.cheGiornoE()));
                    //if (tv.giorni.contains(toCheck.cheGiornoE())) {
                    //    ViewSE.println(v + " puo lavorare il " + i + "/" + meseProssimo);
                    //}
                }
                //ViewSE.println(v.getUsername() + " puo lavorare il " + i + "/" + meseProssimo);
            }
        }
    }

}
