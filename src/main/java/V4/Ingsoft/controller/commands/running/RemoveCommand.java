package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class RemoveCommand extends AbstractCommand {

    private final Controller controller;
    private final String CLASSNAME = this.getClass().getSimpleName();
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
            case 'f' -> removeFruitore(ar);
            case 'v' -> {
                if (!controller.canExecute16thAction)
                    removeVolontario(ar);
                else
                    ViewSE.println(ERROR_16);
            }
            case 't' -> {
                if (!controller.canExecute16thAction)
                    removeTipoVisita(ar);
                else
                    ViewSE.println(ERROR_16);
            }
            case 'L' -> {
                if (!controller.canExecute16thAction)
                    removeLuogo(ar);
                else
                    ViewSE.println(ERROR_16);
            }
            default -> ViewSE.println("Option not recognized for 'remove'.");
        }
    }

    private void removeConfiguratore(String[] args) {
        ViewSE.println("Executing: Removing configurator");
        controller.db.removeConfiguratore(args[0]);
    }

    private void removeFruitore(String[] args) {
        ViewSE.println("Executing: Removing fruitore (user/visitor)");
        controller.db.removeFruitore(args[0]);
    }

    private void removeVolontario(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Cannot remove a volunteer if it's not the 16th of the month: " + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Executing: Removing volunteer");
        controller.db.removeVolontario(args[0]);
    }

    private void removeTipoVisita(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Cannot remove a visit type if it's not the 16th of the month: " + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Executing: Removing visit type");
        controller.db.removeTipoVisita(args[0]);
    }

    private void removeLuogo(String[] args) {
        if (!controller.canExecute16thAction) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername()
                            + "|  + Cannot remove a place if it's not the 16th of the month: "
                            + args[0],
                    1,
                    CLASSNAME);
            return;
        }

        ViewSE.println("Executing: Removing place");
        controller.db.removeLuogo(args[0]);
    }
}
