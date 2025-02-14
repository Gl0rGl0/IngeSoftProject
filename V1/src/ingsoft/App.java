package ingsoft;

import ingsoft.DB.DBUtils;
import ingsoft.commands.AddCommand;
import ingsoft.commands.ChangePswCommand;
import ingsoft.commands.Command;
import ingsoft.commands.CommandList;
import ingsoft.commands.ExitCommand;
import ingsoft.commands.HelpCommand;
import ingsoft.commands.LoginCommand;
import ingsoft.commands.LogoutCommand;
import ingsoft.commands.RemoveCommand;
import ingsoft.commands.TimeCommand;
import ingsoft.persone.Guest;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.util.Date;
import ingsoft.util.TimeHelper;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    private static final String MESSAGGIO_START = "Benvenuto nel sistema di gestione di Visite Guidate, scrivi 'help' per aiuto";

    public DBUtils db = new DBUtils();
    // Inizialmente l'utente è un Guest (non loggato)
    public Persona user = new Guest();
    public Date date = new Date(1,1,1);

    private final Map<String, Command> commandRegistry = new HashMap<>();

    public App() {
        // Registra i comandi nel costruttore
        initTimer();
        registerCommands();
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
        commandRegistry.put("logout", new LogoutCommand(this, CommandList.LOGOUT)); //implementare
        commandRegistry.put("help", new HelpCommand(this, CommandList.HELP));
        commandRegistry.put("changepsw", new ChangePswCommand(this, CommandList.CHANGEPSW));
        commandRegistry.put("time", new TimeCommand(this, CommandList.TIME));
        

        // Puoi aggiungere altri comandi
        commandRegistry.put("exit", new ExitCommand(CommandList.EXIT)); //implementare
    }

    /**
     * Metodo interprete che analizza la stringa di comando immessa dall'utente,
     * ne estrae il comando, le opzioni (precedute dal carattere '-') e gli argomenti,
     * e poi esegue l'azione corrispondente.
     *
     * @param prompt la stringa di comando immessa
     */
    void interpreter(String prompt) {
        String[] tokens = prompt.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isEmpty()) {
            ViewSE.print("Errore: nessun comando fornito.");
            ViewSE.log("V1 ERRORE NESSUN COMANDO", "GRAVE");
            return;
        }
        String cmd = tokens[0];
    
        // Liste per opzioni e argomenti
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> argsList = new ArrayList<>();
    
        // Analizza i token rimanenti: se iniziano con '-' li considera opzioni, altrimenti argomenti.
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("-") && token.length() > 1) {
                optionsList.add(token.substring(1));
            } else {
                argsList.add(token);
            }
        }

        //RIMPIAZZA new String[0] (java11+) senza creare un array inutilmente
        String[] options = optionsList.toArray(String[]::new);
        String[] args = argsList.toArray(String[]::new); 
    
        Command command = commandRegistry.get(cmd);
        if (command != null) {
            // Controllo del permesso: confronta il livello dell'utente con quello richiesto dal comando.
            int userPerm = user.type().getPriorita();
            //Se l'utente non dispone dei permessi viene rifiutata la query subito
            if (userPerm < command.getRequiredPermission()) {
                ViewSE.print("Non hai i permessi necessari per eseguire il comando '" + cmd + "'.");
                return;
            }
            //se l'utente deve cambiare la psw viene messo in uno stato intermedio ma gli permette di eseguire i comandi con priorita guest
            if(user.firstAccess() && command.getRequiredPermission() > PersonaType.CAMBIOPSW.getPriorita()){ //Priorita guest = 0
                ViewSE.print("Non hai i permessi necessari per eseguire il comando '" + cmd + "' finche' non viene cambiata la password con 'changepsw [nuovapsw]'.");
                return;
            }
            command.execute(options, args); //OGNI comando ha execute dato che è una discendenza abstract+interface da implementare
        } else {
            ViewSE.print("\"" + cmd + "\" non è riconosciuto come comando interno.");
        }
    }        

    /**
     * Avvia l'interprete dei comandi.
     * Viene prima verificato il login e poi viene eseguito un ciclo continuo
     * che attende l'immissione di un comando.
     */
    public void start() {
        ViewSE.print(MESSAGGIO_START);

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

    public void addSpecialDate(String d, String comment) {
        db.addDateToDB(new Date(d + "-" + comment));
    }

    public HashSet<Date> getDate(){
        return db.getSpecialDates();
    }
}