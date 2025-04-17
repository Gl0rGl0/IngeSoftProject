package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

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
                    setDate(args[0]);
                    break;
                case "d": // Jump days - requires 1 arg
                case "m": // Jump months - requires 1 arg
                case "a": // Jump years - requires 1 arg
                    if (args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
                        AssertionControl.logMessage("Missing number argument for 'time -" + option + "'.", 2, CLASSNAME);
                        ViewSE.println("Usage: time -" + option + " <number>");
                        return;
                    }
                    int multiplier = switch (option) {
                        case "d" -> 1;
                        case "m" -> 30; // Approximation
                        case "a" -> 365; // Approximation
                        default -> 0; // Should not happen
                    };
                    jumpDays(args[0], multiplier);
                    break;
                case "n": // Add precluded date - requires 1 arg
                    if (args.length < 1 || args[0] == null || args[0].trim().isEmpty()) {
                        AssertionControl.logMessage("Missing date argument for 'time -n'.", 2, CLASSNAME);
                        ViewSE.println("Usage: time -n <dd/mm/yyyy>");
                        return;
                    }
                    addPrecluded(args[0]); // Pass only the date string
                    break;
                case "l": // List precluded dates - requires 0 args
                    if (args.length > 0) {
                        AssertionControl.logMessage("Unexpected arguments provided for 'time -l'.", 2, CLASSNAME);
                        ViewSE.println("Usage: time -l (no arguments needed)");
                        // Proceed to list anyway
                    }
                    listPrecluded();
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

    // Removed extra closing brace here

    private void setDate(String dateStr) {
        final String SUB_CLASSNAME = CLASSNAME + ".setDate";
        Date toSet;
        try {
            toSet = new Date(dateStr); // Constructor handles parsing
            if (controller.date == null) { // Initialize if null
                controller.date = toSet;
                AssertionControl.logMessage("Controller date initialized to: " + toSet, 4, SUB_CLASSNAME);
            } else {
                controller.date.localDate = toSet.localDate; // Update existing date object
                AssertionControl.logMessage("Date set to: " + toSet, 4, SUB_CLASSNAME);
            }
            ViewSE.println("Date set to: " + controller.date);
            controller.dailyAction(); // Trigger daily updates after setting date
        } catch (Exception e) {
            AssertionControl.logMessage("Error parsing date string '" + dateStr + "': " + e.getMessage(), 2, SUB_CLASSNAME);
            ViewSE.println("Error: Invalid date format. Please use dd/mm/yyyy.");
        }
    }


    private void jumpDays(String daysStr, int mult) {
        final String SUB_CLASSNAME = CLASSNAME + ".jumpDays";
        int daysToJump;
        try {
            daysToJump = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            AssertionControl.logMessage("Invalid number format for jumping days: " + daysStr, 2, SUB_CLASSNAME);
            ViewSE.println("Error: Invalid number '" + daysStr + "'. Please enter an integer.");
            return;
        }

        int g = daysToJump * mult;

        if (controller.date == null) {
            AssertionControl.logMessage("Cannot jump days, current date is null.", 1, SUB_CLASSNAME);
            ViewSE.println("Error: Current date not set.");
            return;
        }

        // Perform the update
        controller.date.update(g); // Assuming update handles the logic correctly

        // Trigger daily actions for each day skipped if moving forward
        Date today = controller.date;
        if (g > 0) {
            int i = 0;
            while (i < g)
                setDate(today.addDay(i++).toString());
            ViewSE.println("Successfully skipped forward " + g + " days. Current date: " + controller.date);
            AssertionControl.logMessage("Jumped forward " + g + " days. New date: " + controller.date, 4, SUB_CLASSNAME);
        } else if (g < 0) {
            // Don't trigger daily actions when going back in time
            ViewSE.println("Successfully went back " + (-g) + " days. Current date: " + controller.date);
            AssertionControl.logMessage("Jumped backward " + (-g) + " days. New date: " + controller.date, 4, SUB_CLASSNAME);
        } else {
            ViewSE.println("No days skipped. Current date: " + controller.date);
            AssertionControl.logMessage("Attempted to jump 0 days.", 4, SUB_CLASSNAME);
        }
    }

    private void addPrecluded(String dateStr) {
        final String SUB_CLASSNAME = CLASSNAME + ".addPrecluded";
        Date d;
        try {
            d = new Date(dateStr);
            if (controller.getDB() != null) {
                boolean added = controller.getDB().dbDatesHelper.addPrecludedDate(d); // Assuming returns boolean
                if (added) {
                    ViewSE.println("Date " + d + " added to precluded dates.");
                    AssertionControl.logMessage("Added precluded date: " + d, 4, SUB_CLASSNAME);
                } else {
                    ViewSE.println("Date " + d + " was already precluded or could not be added.");
                    AssertionControl.logMessage("Failed to add precluded date (already exists?): " + d, 3, SUB_CLASSNAME);
                }
            } else {
                AssertionControl.logMessage("DB or DBDatesHelper is null when adding precluded date.", 1, SUB_CLASSNAME);
                ViewSE.println("Internal error: Cannot access date storage.");
            }
        } catch (Exception e) {
            AssertionControl.logMessage("Error parsing date string '" + dateStr + "' for preclusion: " + e.getMessage(), 2, SUB_CLASSNAME);
            ViewSE.println("Error: Invalid date format. Please use dd/mm/yyyy.");
        }
    }

    private void listPrecluded() {
        final String SUB_CLASSNAME = CLASSNAME + ".listPrecluded";
        if (controller.getDB() != null) {
            ViewSE.println("Precluded Dates:");
            // Assuming getPrecludedDates returns a List or similar collection
            var precludedDates = controller.getDB().dbDatesHelper.getPrecludedDates();
            if (precludedDates == null || precludedDates.isEmpty()) {
                ViewSE.println("  (None)");
            } else {
                for (Date pd : precludedDates) {
                    ViewSE.println("  " + pd);
                }
            }
            AssertionControl.logMessage("Listed precluded dates.", 4, SUB_CLASSNAME);
        } else {
            AssertionControl.logMessage("DB or DBDatesHelper is null when listing precluded dates.", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error: Cannot access date storage.");
        }
    }
}
