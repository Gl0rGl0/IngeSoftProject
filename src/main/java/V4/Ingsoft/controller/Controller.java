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

    public final Model db;
    
    // Initially the user is a Guest (not logged in)
    public String user;
    public Interpreter interpreter;
    public Date date = new Date(); // simply sets today's date

    private ScheduledExecutorService dailyTask;
    private ScheduledExecutorService virtualTimer;

    public Controller(Model db) {
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

        interpreter.interpret(prompt, getCurrentUser());
    }

    public void switchInterpreter(){
        // db.dbConfiguratoreHelper.removePersona("ADMIN"); //è il primo configuratore... lo lasciamo
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
        return interpreter.doneAll() || db.isInitialized();
    }

    public Persona getCurrentUser() {
        return db.findPersona(user);
    }

    // Flag indicating if today is the designated day (16th or first working day after)
    // for special monthly actions (add/remove/assign/generate plan).
    // Initialize to false, assuming it's not the 16th initially.
    public boolean isActionDay16 = false;

    public void dailyAction() {
        // Perform daily status updates first
        db.updateDailyStatuses(this.date);

        // Now, manage the flag for the 16th/first working day logic
        int currentDay = date.getDay();
        boolean isHolidayToday = this.date.holiday(); // Assumes Date.holiday() checks the current date

        if (currentDay == 15) {
            // Day before the 16th, reset the flag.
            isActionDay16 = false;
            // TODO: Consider adding logic here to "close" volunteer availability collection for the next month, as per spec.
        } else if (currentDay == 16) {
            // It's the 16th.
            if (!isHolidayToday) {
                // If not a holiday, today is the action day.
                performDay16Actions();
            } else {
                // If it's a holiday, keep the flag false, actions deferred.
                isActionDay16 = false;
            }
        } else if (!isActionDay16 && currentDay > 16) {
            // It's after the 16th, and the action day hasn't occurred yet (due to holidays).
            if (!isHolidayToday) {
                // If today is not a holiday, it becomes the action day.
                performDay16Actions();
            }
            // If it's still a holiday, isActionDay16 remains false.
        } else if (isActionDay16 && currentDay > 16) {
             // The action day (16th or later) has already passed. Reset the flag for subsequent days in the month.
             // This ensures the special commands are only allowed on that single designated day.
             isActionDay16 = false;
             // TODO: Consider adding logic here to "reopen" volunteer availability collection for month+2, as per spec.
        }
        // If currentDay < 15, the flag remains false.
    }

    /**
     * Performs the automatic actions designated for the 16th or the first working day after.
     * Sets the flag indicating that special commands are now allowed for this day.
     */
    private void performDay16Actions() {
        AssertionControl.logMessage("Performing Day 16 actions on: " + this.date, 3, getClass().getSimpleName());
        // Perform automatic tasks for the 16th
        db.refreshPrecludedDate(this.date); // Example task from original code

        // Set the flag to allow special commands for today
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
}
