package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
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
        Date toOperate = new Date(args[0]);
        if (toOperate.toString().equals("00/00/00")) {
            ViewSE.println("Error entering the date, please double-check");
            return;
        }

        if (!Date.twoMonthsDifference(toOperate, controller.date)) {
            ViewSE.println(
                    "It is not possible to add a date so far forward/backward in time, stick to the month after the next");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'a' ->
                addPrecludedDate(args);
            case 'r' ->
                removePrecludedDate(args);
            default ->
                ViewSE.println("Option not recognized for 'preclude'.");
        }
    }

    private void addPrecludedDate(String[] args) {
        ViewSE.println("Executing: Adding precluded date");
        controller.db.addPrecludedDate(new Date(args[0])); // adds a special date
    }

    private void removePrecludedDate(String[] args) {
        ViewSE.println("Executing: Removing precluded date");
        controller.db.removePrecludedDate(new Date(args[0]));
    }
}
