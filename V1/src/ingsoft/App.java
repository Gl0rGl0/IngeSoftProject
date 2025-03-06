package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.commands.*;
import ingsoft.commands.running.*;
import ingsoft.commands.setup.*;
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.util.AssertionControl;
import ingsoft.util.Date;
import ingsoft.util.Interpreter;
import ingsoft.util.TimeHelper;

import java.util.ArrayList;
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
        AssertionControl.logMessage("Tentativo di eseguire: " + prompt, 3, this.getClass().getSimpleName());
        interpreter.interpret(prompt, user);
    }

    public void interpreterSETUP(String prompt) {
        AssertionControl.logMessage("Tentativo di eseguire: " + prompt, 3, this.getClass().getSimpleName());
        setupInterpreter.interpret(prompt, user);
    }

    boolean skipSetupTesting = false;

    public boolean setupCompleted() {
        boolean out = setupInterpreter.haveAllBeenExecuted() || skipSetupTesting || !db.getNew();
        
        if(out)
            AssertionControl.logMessage("SetUp completato", 3, this.getClass().getSimpleName());
        
        return setupInterpreter.haveAllBeenExecuted() || skipSetupTesting || !db.getNew();
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public void addPrecludedDate(String d) {
        db.addPrecludedDate(new Date(d));
    }

    public ArrayList<Date> getDate() {
        return db.getPrecludedDates();
    }

    public void azioneDelGiorno() {
        db.refresher(this.date);
        // refresh--3 giorni al termine -> STATO.CLOSE

        switch (date.getGiorno()) {
            case 1 -> db.refreshPrecludedDate(this.date);
            //case 16 -> attivita16();
        }

    }

    // public void notPossibileCreareOrario() {
    //     possibileCreareOrario = false;
    // }

    // public void possibileCreareOrario() {
    //     possibileCreareOrario = true;
    // }
}
