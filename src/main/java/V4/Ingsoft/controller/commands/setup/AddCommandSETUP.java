package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.GPS;
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

    public void addLuogo(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);

        controller.db.addLuogo(a[0], a[1], new GPS(a[2]));
    }
}
