package V1.Ingsoft.controller.commands;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.running.CommandList;
import V1.Ingsoft.util.AssertionControl;
import V1.Ingsoft.util.Date;
import V1.Ingsoft.view.ViewSE;

public class TimeCommand extends AbstractCommand {
    // Made final
    private static final String CLASSNAME = TimeCommand.class.getSimpleName(); // Added for logging

    public TimeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public void execute(String[] options, String[] args) {
        // Basic null checks
        if (controller == null) {
            System.err.println("FATAL: Controller is null in TimeCommand");
            return;
        }
        if (options == null || args == null) {
            AssertionControl.logMessage("Options or args array is null in TimeCommand.execute", 1, CLASSNAME);
            ViewSE.println("Internal error: Invalid command structure.");
            return;
        }

        // Case 1: No options, no args -> Print current date
        if (options.length == 0 && args.length == 0) {
            if (controller.date != null) {
                ViewSE.println("Current date: " + controller.date);
            } else {
                AssertionControl.logMessage("Controller date is null when trying to display time.", 1, CLASSNAME);
                ViewSE.println("Error: Current date not available.");
            }
            return;
        }

        // Case 2: Option provided
        if (options.length > 0 && options[0] != null && !options[0].isEmpty()) {
            String option = options[0];
            switch (option) {
                case "s": // Set date - requires 1 arg
                    if (args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
                        AssertionControl.logMessage("Missing date argument for 'time -s'.", 2, CLASSNAME);
                        ViewSE.println("Usage: time -s <dd/mm/yyyy>");
                        return;
                    }
                    try {
                        controller.date = new Date(args[0]);
                        controller.dailyAction();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case "d":
                    int d = Integer.parseInt(args[0]);
                    for(int i = 0; i < d; i++){
                        controller.date.addDay(1);
                        controller.dailyAction();
                    }
                    
                    ViewSE.println("Current date: " + controller.date);
                    break;
                case "m":
                    int m = Integer.parseInt(args[0]);
                    for(int i = 0; i < m; i++){
                        controller.date.addMonth(1);
                        controller.dailyAction();
                    }
                    ViewSE.println("Current date: " + controller.date);
                    break;
                default:
                    AssertionControl.logMessage("Unrecognized option for 'time': " + option, 2, CLASSNAME);
                    ViewSE.println("Option '-" + option + "' not recognized for the 'time' command.");
                    break;
            }
        } else {
            // Handle cases like option present but empty, or args present without option
            AssertionControl.logMessage("Invalid combination of options/arguments for 'time'.", 2, CLASSNAME);
            ViewSE.println("Incorrect usage of the 'time' command. Use 'time' to view, or 'time -<option> [args]'.");
        }
    }
}
