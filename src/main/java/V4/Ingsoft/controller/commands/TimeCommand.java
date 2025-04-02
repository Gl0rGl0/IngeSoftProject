package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
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
                    controller.date = new Date(args[0]);
                    controller.dailyAction();
                }
                case "d" -> jumpDays(args[0], 1);
                case "m" -> jumpDays(args[0], 30);
                case "a" -> jumpDays(args[0], 365);
                case "n" -> controller.addPrecludedDate(args[0]); // Add precluded date
                case "l" -> ViewSE.println(controller.db.getPrecludedDates()); // List precluded dates
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
}
