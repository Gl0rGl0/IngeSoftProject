package V4.Ingsoft.controller;

import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.*;
import V4.Ingsoft.view.ViewSE;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;

public class Controller {
    public static final int SECONDIVIRTUALI_PS = 120; // 12 real minutes are 1 day in the simulation -> 1rs : 120vs =
    // 12rmin : 24hv (Note: This comment explains the virtual time ratio)

    private Model db;

    // Initially the user is a Guest (not logged in)
    public String user;
    public Interpreter interpreter;
    public Date date = new Date(); // simply sets today's date
    // Flag indicating if today is the designated day (16th or first working day after)
    // for special monthly actions (add/remove/assign/generate plan).
    // Initialize to false, assuming it's not the 16th initially.
    public boolean isActionDay16 = false;
    private boolean collectionStatus;

    public Controller(Model db) {
        this.db = db;

        initVirtualTime();
        initDailyScheduler();

        interpreter = new SetupInterpreter(this);
    }

    private void initDailyScheduler() {
        ScheduledExecutorService dailyTask = newSingleThreadScheduledExecutor();
        dailyTask.scheduleAtFixedRate(this::dailyAction, 0, 60 * 60 * 24 / SECONDIVIRTUALI_PS, TimeUnit.SECONDS);
        // EVERY VIRTUAL DAY (every 12min)
    }

    private void initVirtualTime() {
        ScheduledExecutorService virtualTimer = newSingleThreadScheduledExecutor();
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

        interpreter.interpret(prompt, getCurrentUser());
    }

    public void switchInterpreter() {
        // db.dbConfiguratoreHelper.removePersona("ADMIN"); //è il primo configuratore... lo lasciamo
        this.interpreter = new RunningInterpreter(this);
        ViewSE.println("SETUP COMPLETED");
    }

    public boolean setupCompleted() {
        boolean out = interpreter.doneAll();

        if (out)
            AssertionControl.logMessage("Setup completed", 3, this.getClass().getSimpleName());

        return out;
    }

    public boolean haveAllBeenExecuted() {
        return interpreter.haveAllBeenExecuted();
    }

    public boolean doneAll() {
        return interpreter.doneAll() && db.isInitialized();
    }

    public Persona getCurrentUser() {
        return db.findPersona(user);
    }

    public void dailyAction() {
        // Perform daily status updates first
        db.updateDailyStatuses(this.date);

        // Now, manage the flag for the 16th/first working day logic
        int currentDay = date.getDay();
        boolean isHolidayToday = this.date.holiday(); // Assumes Date.holiday() checks the current date

        if(currentDay > 18 || currentDay <= 15){
            isActionDay16 = false;
            return;
        }

        if(isHolidayToday){
            isActionDay16 = false;
            return;
        }

        if(currentDay == 16){
            performDay16Actions();
            return;
        }

        Date yesterday = date.clone().minusDays(1);
        if(yesterday.holiday()){
            performDay16Actions();
        }else{
            isActionDay16 = false;
        }
    }

    /**
     * Performs the automatic actions designated for the 16th or the first working day after.
     * Sets the flag indicating that special commands are now allowed for this day.
     */
    private void performDay16Actions() {
        AssertionControl.logMessage("Performing Day 16 actions on: " + this.date, 3, getClass().getSimpleName());
        db.dbDatesHelper.refreshPrecludedDate(this.date); // Example task from original code

        isActionDay16 = true;
    }

    public void close() {
        db.closeAll();
        ViewSE.println("Program terminated. Goodbye!");
        System.exit(0);
    }

    public Model getDB() {
        return db;
    }

    public void openCollection() {
        collectionStatus = true;
        getDB().dbVolontarioHelper.resetAllAvailability();
    }

    public void closeCollection() {
        collectionStatus = false;
    }

    public boolean isVolunteerCollectionOpen() {
        return collectionStatus;
    }

    public void setDB(Model instance) {
        this.db = instance;
    }
}
