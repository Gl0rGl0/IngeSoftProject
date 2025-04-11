package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

public class TimeCommand extends AbstractCommand {
    Controller controller;

    public TimeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1 && options.length == 0) {
            ViewSE.println(controller.date);
            return;
        }
        
        if (options.length > 0 && args.length > 0) {
            switch (options[0]) {
                case "s" -> {
                    Date toSet;
                    try {
                        toSet = new Date(args[0]);
                    } catch (Exception e) {
                        AssertionControl.logMessage("Error while parsing date: " + e.getMessage(), 2, CLASSNAME);
                        return;
                    }
                    AssertionControl.logMessage("Date set: " + toSet, 2, CLASSNAME);
                    controller.date = toSet;
                    controller.dailyAction();
                }
                case "d" -> jumpDays(args[0], 1);
                case "m" -> jumpDays(args[0], 30);
                case "a" -> jumpDays(args[0], 365);
                case "n" -> addPrecluded(args);
                case "l" -> ViewSE.println(controller.db.dbDatesHelper.getPrecludedDates()); // List precluded dates
                default -> ViewSE.println("Option not recognized for the 'time' command.");
            }
        } else {
            ViewSE.println(controller.date);
        }
    }

    private void jumpDays(String d, int mult) {
        int g;
        try {
            g = Integer.parseInt(d) * mult;
        } catch (NumberFormatException e) {
            ViewSE.println("Incorrect prompt usage");
            return;
        }

        controller.date.update(g);
        if (g > 0) {
            ViewSE.println("Successfully skipped " + g + " days.");
        } else if (g < 0) { // Handle negative case explicitly for clarity
            ViewSE.println("Successfully went back " + (-g) + " days.");
        }
        // If g is 0, no message is printed, which seems reasonable.
    }

    private void addPrecluded(String[] args){
        Date d;
        try {
            d = new Date(args[0]);
        } catch (Exception e) {
            return;
        }
        controller.db.dbDatesHelper.addPrecludedDate(d);
    }
}
