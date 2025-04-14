package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AddCommandSETUP extends AbstractCommand {
    private final Controller controller;

    public AddCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.ADD;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementation of the "add" command.
     *
     * @param options the options (only -L for places)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        // For example, we expect the first argument to be the option (must be 'L')
        if (options.length < 1) {
            ViewSE.println("Error using the 'add' command");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'L' -> addLuogo(args);
            default -> {
                ViewSE.println("Option not recognized for 'add' during SETUP.");
                return;
            }
        }

        this.hasBeenExecuted = true;
    }

    private void addLuogo(String[] args) {

        String[] a = StringUtils.joinQuotedArguments(args);

        // Removed the incorrect isActionDay16 check for SETUP phase

        Luogo l;
        try {
            l = new Luogo(a);
        } catch (Exception e) {
            AssertionControl.logMessage(e.getMessage(), 2, CLASSNAME);
            return;
        }

        if (a.length > 2 && controller.db.dbLuoghiHelper.addLuogo(l)) {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Added place: " + a[0], 3, CLASSNAME);
        } else {
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Place not added: " + a[0], 3, CLASSNAME);
        }
    }
}
