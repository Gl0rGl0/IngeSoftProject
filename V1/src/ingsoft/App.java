package ingsoft;

import ingsoft.DB.DBUtils;
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
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.util.Date;
import ingsoft.util.Interpreter;
import ingsoft.util.TimeHelper;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate, scrivi 'help' per aiuto";
    // private static final String MESSAGGIO_PRIMO_ACCESSO = "PRIMO ACCESSO ESEGUITOindicare l'ambito territoriale di appartenenza";
    // private static final String AMBITO_TERRITORIALE = "Indicare l'ambito territoriale di appartenenza";
    // private static final String AMBITO_SCELTO = "Ambito territoriale scelto correttamente: ";
    // private static final String MESSAGGIO_MAX_PERSONE = "Impostare quante persone possono iscriversi ad un evento in una sola operazione da parte di un visitatore";
    // private static final String MAX_PRENOTAZIONI_SCELTO = "Numero correttamente impostato: ";

    public String ambitoTerritoriale = new String();
    public int maxPrenotazioniPerPersona;

    public DBUtils db;
    // Inizialmente l'utente è un Guest (non loggato)
    public Persona user = new Guest();
    private final Interpreter interpreter;
    private final Interpreter setupInterpreter;
    public Date date = new Date(1,1,1);

    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final Map<String, Command> setupCommandRegistry = new HashMap<>();
    private final Command TimeC = new TimeCommand(this);
    private final Command LogoutC = new LogoutCommand(this);
    private final Command ChangeC = new ChangePswCommand(this);
    private final Command ExitC = new ExitCommand();
    private final Command SetMaxC = new SetPersoneMaxCommand(this);     //COSI DA NON CREARE DOPPIONI INUTILI

    public App(DBUtils db) {
        this.db = db;
        // Registra i comandi nel costruttore
        initTimer();
        registerCommands();
        setupRegisterCommands();

        interpreter = new Interpreter(commandRegistry);
        setupInterpreter = new Interpreter(setupCommandRegistry);
    }

    private void initTimer(){
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        TimeHelper timeHelper = new TimeHelper(this);
        // Supponiamo che ogni secondo di tempo reale rappresenti un intervallo di aggiornamento
        scheduler.scheduleAtFixedRate(timeHelper, 0, 1, TimeUnit.SECONDS);
    }

    //Ogni nuovo comando va inserito nella mappa in modo da poterlo riconoscere e in CommandList per avere le informazioni
    private void registerCommands() {
        // Passa l'istanza di App se i comandi hanno bisogno di accedere ad essa (praticamente tutti)
        commandRegistry.put("add", new AddCommand(this));
        commandRegistry.put("list", new ListCommand(this));
        commandRegistry.put("login", new LoginCommand(this));
        commandRegistry.put("preclude", new PrecludeCommand(this));
        commandRegistry.put("remove", new RemoveCommand(this));
        commandRegistry.put("logout", LogoutC);
        commandRegistry.put("changepsw", ChangeC);
        commandRegistry.put("time", TimeC);
        commandRegistry.put("setmax", SetMaxC);
        commandRegistry.put("help", new HelpCommand(this, CommandList.HELP));
        
        // Puoi aggiungere altri comandi
        commandRegistry.put("exit", ExitC);
    }

    private void setupRegisterCommands() {
        // Passa l'istanza di App se i comandi hanno bisogno di accedere ad essa (praticamente tutti)
        setupCommandRegistry.put("add", new AddCommandSETUP(this));                 //DA ESEGUIRE
        setupCommandRegistry.put("done", new DoneCommandSETUP());                   //DA ESEGUIRE
        setupCommandRegistry.put("login", new LoginCommandSETUP(this));             //DA ESEGUIRE
        setupCommandRegistry.put("setambito", new SetAmbitoCommandSETUP(this));     //DA ESEGUIRE
        setupCommandRegistry.put("logout", LogoutC);
        setupCommandRegistry.put("changepsw", ChangeC);
        setupCommandRegistry.put("time", TimeC);
        setupCommandRegistry.put("setmax", SetMaxC);                                //DA ESEGUIRE
        setupCommandRegistry.put("help", new HelpCommand(this, CommandListSETUP.HELP));
        
        // Puoi aggiungere altri comandi
        setupCommandRegistry.put("exit", ExitC);
    }

    /**
     * Metodo interprete che analizza la stringa di comando immessa dall'utente,
     * ne estrae il comando, le opzioni (precedute dal carattere '-') e gli argomenti,
     * e poi esegue l'azione corrispondente.
     *
     * @param prompt la stringa di comando immessa
     */
    void interpreter(String prompt) {
        interpreter.interpret(prompt, user);
    }

    void intepreterSETUP(String prompt){
        setupInterpreter.interpret(prompt, user);
    }

    boolean skipSetupTesting = false;

    /**
     * Avvia l'interprete dei comandi.
     * Viene prima verificato il login e poi viene eseguito un ciclo continuo
     * che attende l'immissione di un comando.
     */
    public void start() {
        ViewSE.print(MESSAGGIO_START);

        while(!setupInterpreter.haveAllBeenExecuted() && !skipSetupTesting){     //DA TOGLIEREEEE!!!!
            String input = ViewSE.read("\n(SETUP)" + user.getUsername() + "> ");
            intepreterSETUP(input);
            //setupCompleted = setupCommandRegistry.values().stream().allMatch(action -> action.hasBeenExecuted()); //nuovo
            // controlla che ognuno dei comandi di setup sia stato eseguito, non ho trovato dove tu facessi il controllo, anche perché effettivamente non va
            // ma se lo fai più dentro secondo me è meglio qua
            //è dentro l'interprete, non andava perchè partiva con false & ... quindi non andava mai
            //Cosi tante ricorsioni non vanno bene (l'ho letto nelle slide di saetti, quindi lo dovremo comunque modificare nella seconda parte)
        }

        System.out.println("SETUP COMPLETATO");

        while (true) {
            String input = ViewSE.read("\n" + user.getUsername() + "> ");
            interpreter(input);
        }
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public void setUser(String username) {
        this.user = db.findUser(username);
        if (this.user == null) this.user = new Guest();
    }//INUTILE, TOREMOVE?

    public void addPrecludedDate(String d) {
        db.addPrecludedDate(new Date(d));
    }

    public ArrayList<Date> getDate(){
        return db.getPrecludedDates();
    }
}