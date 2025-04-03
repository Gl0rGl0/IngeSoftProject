package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
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

    public void addLuogo(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);

        if(a.length < 3){
            AssertionControl.logMessage("Comando 'add' errato", 3, getClass().getSimpleName());
            return;
        }

        if(a[0].trim() == ""){
            AssertionControl.logMessage("You can't add an empty title", 3, getClass().getSimpleName());
            return;
        }

        if(a[2].trim() == ""){
            AssertionControl.logMessage("You can't add an empty position", 3, getClass().getSimpleName());
            return;
        }

        controller.db.addLuogo(a[0], a[1], a[2]);
    }
}
