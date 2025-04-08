package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

public class PrecludeCommand extends AbstractCommand {

    private final Controller controller;

    public PrecludeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.PRECLUDE;
    }

    @Override
    public void execute(String[] options, String[] args) {

        if (options.length < 1) {
            ViewSE.println("Error using the 'preclude' command");
            return;
        }

        // Internal check
        Date d;
        try {
            d = new Date(args[0]);
        } catch (Exception e) {
            AssertionControl.logMessage("Invalid date inserted", 2, CLASSNAME);
            return;
        }

        if (!Date.twoMonthsDifference(d, controller.date)) {
            ViewSE.println(
                    "It is not possible to add a date so far forward/backward in time, stick to the month after the next");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'a' ->
                addPrecludedDate(d);
            case 'r' ->
                removePrecludedDate(d);
            default ->
                ViewSE.println("Option not recognized for 'preclude'.");
        }
    }

    private void addPrecludedDate(Date d) {
        ViewSE.println("Executing: Adding precluded date");
        controller.db.addPrecludedDate(d); // adds a special date
    }

    private void removePrecludedDate(Date d) {
        ViewSE.println("Executing: Removing precluded date");
        controller.db.removePrecludedDate(d);
    }
}
