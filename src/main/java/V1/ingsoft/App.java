package V1.ingsoft;

import V1.ingsoft.DB.DBUtils;
import V1.ingsoft.persone.Guest;
import V1.ingsoft.persone.Persona;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.Interpreter;
import V1.ingsoft.util.RunningInterpreter;
import V1.ingsoft.util.SetupInterpreter;
import V1.ingsoft.util.TimeHelper;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class App {

    public static final int SECONDIVIRTUALI_PS = 120; // 12minuti reali sono 1gg nella simulazione -> 1rs : 120vs =
    // 12rmin : 24hv
    public int maxPrenotazioniPerPersona;

    public DBUtils db;
    // Inizialmente l'utente è un Guest (non loggato)
    public Persona user = new Guest();
    private final Interpreter interpreter;
    private final Interpreter setupInterpreter;
    public Date date = new Date(); // mette oggi semplicemente

    private ScheduledExecutorService dailyTask;
    private ScheduledExecutorService virtualTimer;

    public App(DBUtils db) {
        this.db = db;
        // Registra i comandi nel costruttore
        initVirtualTime();
        initDailyScheduler();

        interpreter = new RunningInterpreter(this);
        setupInterpreter = new SetupInterpreter(this);
    }

    private void initDailyScheduler() {
        dailyTask = Executors.newSingleThreadScheduledExecutor();
        dailyTask.scheduleAtFixedRate(() -> dailyAction(), 0, 60 * 60 * 24 / SECONDIVIRTUALI_PS, TimeUnit.SECONDS);
        // OGNI GIORNO VIRTUALE (ogni 12min)
    }

    private void initVirtualTime() {
        virtualTimer = Executors.newSingleThreadScheduledExecutor();
        TimeHelper timeHelper = new TimeHelper(this); // (runnable)
        // Supponiamo che ogni secondo di tempo reale rappresenti un intervallo di
        // aggiornamento
        virtualTimer.scheduleAtFixedRate(timeHelper, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Metodo interprete che analizza la stringa di comando immessa dall'utente,
     * ne estrae il comando, le options (precedute dal carattere '-') e gli
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
        boolean out = setupInterpreter.haveAllBeenExecuted() || skipSetupTesting || !db.isNew();

        if (out)
            AssertionControl.logMessage("SetUp completato", 3, this.getClass().getSimpleName());

        return out;
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public void addPrecludedDate(String d) {
        db.addPrecludedDate(new Date(d));
    }

    public boolean canExecute16thAction = true;

    public void dailyAction() {
        // db.refresher(this.date);

        switch (date.getDay()) {
            case 16 -> azioniDel16(true);
            case 17, 18 -> azioniDel16(!canExecute16thAction && !this.date.holiday());
            default -> canExecute16thAction = false;
        }

        canExecute16thAction = true;
    }

    private void azioniDel16(boolean eseguo) {
        if (!eseguo)
            return;

        db.refreshPrecludedDate(this.date);
        canExecute16thAction = true;
        // SI PUO USARE QUESTA VARIABILE PER PERMETTERE CERTI COMANDI SOLO IL 16 (o il
        // primo giorno feriale)
    }

    public void close() {
        db.closeAll();
        ViewSE.println("Programma terminato. Arrivederci!");
        System.exit(0);
    }
}
