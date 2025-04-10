package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class RemoveCommand extends AbstractCommand {

    private final Controller controller;
    private final String ERROR_16 = "Action only possible on the 16th of the month!";

    public RemoveCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.REMOVE;
    }

    @Override
    /**
     * Implementation of the "remove" command.
     *
     * @param options the options (e.g., -c for configurator)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        // For example, we expect the first argument to be the option (e.g., "c" for configurator)
        if (options.length < 1) {
            ViewSE.println("Error using the 'remove' command");
            return;
        }
        char option = options[0].charAt(0);

        String[] ar = StringUtils.joinQuotedArguments(args);

        switch (option) {
            case 'c' -> removeConfiguratore(ar);
            case 'f' -> removeFruitore(ar); // No time restriction for Fruitore
            case 'v' -> { // Volontario removal restricted to action day
                if (controller.isActionDay16)
                    removeVolontario(ar);
                else
                    ViewSE.println(ERROR_16); // Correctly show error if not action day
            }
            case 't' -> { // TipoVisita removal restricted to action day
                if (controller.isActionDay16)
                    removeTipoVisita(ar);
                else
                    ViewSE.println(ERROR_16); // Correctly show error if not action day
            }
            case 'L' -> { // Luogo removal restricted to action day
                if (controller.isActionDay16)
                    removeLuogo(ar);
                else
                    ViewSE.println(ERROR_16); // Correctly show error if not action day
            }
            default -> ViewSE.println("Option not recognized for 'remove'.");
        }
    }

    private void removeConfiguratore(String[] args) {
        ViewSE.println("Executing: Removing configurator");
        controller.db.dbConfiguratoreHelper.removePersona(args[0]);
    }

    private void removeFruitore(String[] args) {
        ViewSE.println("Executing: Removing fruitore (user/visitor)");
        controller.db.removeFruitore(args[0]);
    }

    // Note: The explicit checks within these methods are now redundant
    // as the main execute method already performs the check based on isActionDay16.
    // However, keeping them provides defense-in-depth.
    private void removeVolontario(String[] args) {
        if (!controller.isActionDay16) { // Check again for safety, though execute() should prevent this call
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Attempted to remove volunteer outside action day: " + args[0],
                    1,
                    CLASSNAME);
             ViewSE.println(ERROR_16);
            return;
        }

        ViewSE.println("Executing: Removing volunteer");
        if (!controller.db.removeVolontario(args[0])) {
             ViewSE.println("Failed to remove volunteer: " + args[0]);
        } else {
             ViewSE.println("Volunteer removed: " + args[0]);
        }
    }

    private void removeTipoVisita(String[] args) {
         if (!controller.isActionDay16) { // Check again for safety
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Attempted to remove visit type outside action day: " + args[0],
                    1,
                    CLASSNAME);
             ViewSE.println(ERROR_16);
            return;
        }

        ViewSE.println("Executing: Removing visit type");
        if (!controller.db.removeTipoVisita(args[0])) {
             ViewSE.println("Failed to remove visit type: " + args[0]);
        } else {
             ViewSE.println("Visit type removed: " + args[0]);
        }
    }

    private void removeLuogo(String[] args) {
         if (!controller.isActionDay16) { // Check again for safety
             AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "| Attempted to remove place outside action day: " + args[0],
                    1,
                    CLASSNAME);
             ViewSE.println(ERROR_16);
            return;
        }

        ViewSE.println("Executing: Removing place");
        if (!controller.db.removeLuogo(args[0])) {
             ViewSE.println("Failed to remove place: " + args[0]);
        } else {
             ViewSE.println("Place removed: " + args[0]);
        }
    }
}
