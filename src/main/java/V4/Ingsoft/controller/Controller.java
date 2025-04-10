package V4.Ingsoft.controller;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import V4.Ingsoft.controller.item.persone.Guest;
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.util.Interpreter;
import V4.Ingsoft.util.RunningInterpreter;
import V4.Ingsoft.util.SetupInterpreter;
import V4.Ingsoft.util.TimeHelper;
import V4.Ingsoft.view.ViewSE;

public class Controller {
    public static final int SECONDIVIRTUALI_PS = 120; // 12 real minutes are 1 day in the simulation -> 1rs : 120vs =
    // 12rmin : 24hv (Note: This comment explains the virtual time ratio)
    public int maxPrenotazioniPerPersona; // Max bookings per person

    public final Model db;
    // Initially the user is a Guest (not logged in)
    public Persona user;
    public Interpreter interpreter;
    public Date date = new Date(); // simply sets today's date

    private ScheduledExecutorService dailyTask;
    private ScheduledExecutorService virtualTimer;

    public Controller(Model db) {
        try {
            this.user = new Guest();
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage(), 1, getClass().getSimpleName());
        }
        this.db = db;

        initVirtualTime();
        initDailyScheduler();

        interpreter = new SetupInterpreter(this);
    }

    private void initDailyScheduler() {
        dailyTask = Executors.newSingleThreadScheduledExecutor();
        dailyTask.scheduleAtFixedRate(() -> dailyAction(), 0, 60 * 60 * 24 / SECONDIVIRTUALI_PS, TimeUnit.SECONDS);
        // EVERY VIRTUAL DAY (every 12min)
    }

    private void initVirtualTime() {
        virtualTimer = Executors.newSingleThreadScheduledExecutor();
        TimeHelper timeHelper = new TimeHelper(this); // (runnable)
        // Assume that each real second represents an update interval
        virtualTimer.scheduleAtFixedRate(timeHelper, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Interpreter method that analyzes the command string entered by the user,
     * extracts the command, options (preceded by the '-' character), and
     * arguments, and then executes the corresponding action.
     *
     * @param prompt the entered command string
     */
    public void interpreter(String prompt) {
        AssertionControl.logMessage("Attempting to execute: " + prompt, 3, this.getClass().getSimpleName());

        interpreter.interpret(prompt, user);
    }

    public void switchInterpreter(){
        this.interpreter = new RunningInterpreter(this);
    }

    public boolean setupCompleted() {
        boolean out = interpreter.doneAll();

        if (out)
            AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());

        return out;
    }

    public boolean haveAllBeenExecuted(){
        return interpreter.haveAllBeenExecuted();
    }

    public boolean doneAll(){
        return interpreter.doneAll();
    }

    public Persona getCurrentUser() {
        return this.user;
    }

    public boolean canExecute16thAction = true;

    public void dailyAction() {
        db.refresher(this.date);

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
        // THIS VARIABLE CAN BE USED TO ALLOW CERTAIN COMMANDS ONLY ON THE 16TH (or the
        // first working day)
    }

    public void close() {
        db.closeAll();
        ViewSE.println("Program terminated. Goodbye!");
        System.exit(0);
    }
}
