package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.commands.Command;
import ingsoft.commands.running.AddCommand;
import ingsoft.commands.running.ChangePswCommand;
import ingsoft.commands.running.CommandList;
import ingsoft.commands.running.ExitCommand;
import ingsoft.commands.running.HelpCommand;
import ingsoft.commands.running.LoginCommand;
import ingsoft.commands.running.LogoutCommand;
import ingsoft.commands.running.RemoveCommand;
import ingsoft.commands.running.TimeCommand;
import ingsoft.commands.setup.AddCommandSETUP;
import ingsoft.commands.setup.CommandListSETUP;
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.util.Date;
import ingsoft.util.Interpreter;
import ingsoft.util.TimeHelper;
import ingsoft.util.ViewSE;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate, scrivi 'help' per aiuto";
    private static final String MESSAGGIO_PRIMO_ACCESSO = "PRIMO ACCESSO ESEGUITOindicare l'ambito territoriale di appartenenza";
    private static final String AMBITO_TERRITORIALE = "Indicare l'ambito territoriale di appartenenza";
    private static final String AMBITO_SCELTO = "Ambito territoriale scelto correttamente: ";
    private static final String MESSAGGIO_MAX_PERSONE = "Impostare quante persone possono iscriversi ad un evento in una sola operazione da parte di un visitatore";
    private static final String MAX_PRENOTAZIONI_SCELTO = "Numero correttamente impostato: ";
    public boolean primoAccessoEffettuato = false;

    public String ambitoTerritoriale = new String();
    public int maxPrenotazioniPerPersona;

    public DBUtils db;
    // Inizialmente l'utente è un Guest (non loggato)
    public Persona user = new Guest();
    private Interpreter interpreter;
    private Interpreter setupInterpreter;
    public Date date = new Date(1,1,1);

    private final Map<String, Command> commandRegistry = new HashMap<>();
    private final Map<String, Command> setupCommandRegistry = new HashMap<>();
    private boolean setupCompleted;

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
        commandRegistry.put("add", new AddCommand(this, CommandList.ADD));
        commandRegistry.put("remove", new RemoveCommand(this, CommandList.REMOVE));
        commandRegistry.put("login", new LoginCommand(this, CommandList.LOGIN));
        commandRegistry.put("logout", new LogoutCommand(this, CommandList.LOGOUT));
        commandRegistry.put("help", new HelpCommand(this, CommandList.HELP));
        commandRegistry.put("changepsw", new ChangePswCommand(this, CommandList.CHANGEPSW));
        commandRegistry.put("time", new TimeCommand(this, CommandList.TIME));
        
        // Puoi aggiungere altri comandi
        commandRegistry.put("exit", new ExitCommand(CommandList.EXIT));
    }

    private void setupRegisterCommands() {
        // Passa l'istanza di App se i comandi hanno bisogno di accedere ad essa (praticamente tutti)
        setupCommandRegistry.put("add", new AddCommandSETUP(this, CommandListSETUP.ADD));
        setupCommandRegistry.put("login", new LoginCommand(this, CommandList.LOGIN));
        setupCommandRegistry.put("help", new HelpCommand(this, CommandList.HELP));
        setupCommandRegistry.put("changepsw", new ChangePswCommand(this, CommandList.CHANGEPSW));
        setupCommandRegistry.put("time", new TimeCommand(this, CommandList.TIME));
        setupCommandRegistry.put("done", new RemoveCommand(this, CommandList.REMOVE));
        
        // Puoi aggiungere altri comandi
        setupCommandRegistry.put("exit", new ExitCommand(CommandList.EXIT));
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

    public void sceltaAmbitoTerritoriale() {
        ViewSE.print(AMBITO_TERRITORIALE);
        
        String input;
        do {
            input = ViewSE.read();
        } while (input.length() <= 0 || !(input.matches("[\\p{L}\\p{N}]+"))); // gpt: controllo che contenga solo lettere anche accentate o numeri, da provare
        
        ambitoTerritoriale = input;
        
        ViewSE.print(AMBITO_SCELTO + input);
    }

    public void sceltaMaxPersone() {
        ViewSE.print(MESSAGGIO_MAX_PERSONE);
        
        int input;

        do {
            input = ViewSE.readInt();
        } while (input < 1);
        
        maxPrenotazioniPerPersona = input;
        
        ViewSE.print(MAX_PRENOTAZIONI_SCELTO + input);
    }

    public void sceltaLuoghi() {

    }
    
    public void primoAccesso() {
        primoAccessoEffettuato = false;

        ViewSE.print(MESSAGGIO_PRIMO_ACCESSO);

        sceltaAmbitoTerritoriale();

        sceltaMaxPersone();

        sceltaLuoghi();

        primoAccessoEffettuato = true;
    }

    /**
     * Avvia l'interprete dei comandi.
     * Viene prima verificato il login e poi viene eseguito un ciclo continuo
     * che attende l'immissione di un comando.
     */
    public void start() {
        ViewSE.print(MESSAGGIO_START);

        while(!setupCompleted){
            String input = ViewSE.read("\n(SETUP)" + user.getUsername() + "> ");
            setupInterpreter.interpret(input, user);
        }

        while (true) {
            String input = ViewSE.read("\n" + user.getUsername() + "> ");
            interpreter.interpret(input, user);
        }
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public void setUser(String username) {
        this.user = db.findUser(username);
        if (this.user == null) this.user = new Guest();
    }//INUTILE, TOREMOVE?

    public void addSpecialDate(String d, String comment) {
        db.addDateToDB(new Date(d + "-" + comment));
    }

    public HashSet<Date> getDate(){
        return db.getSpecialDates();
    }
}