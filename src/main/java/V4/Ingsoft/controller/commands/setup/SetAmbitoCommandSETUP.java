package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class SetAmbitoCommandSETUP extends AbstractCommand {

    private final Controller controller;

    public SetAmbitoCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETAMBITO;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementation of the "setambito" command. // Corrected command name
     *
     * @param options the options (e.g., -c for configurator) - Note: Options seem unused here.
     * @param args    any additional arguments (the territorial scope name)
     */
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            // Updated the error message to reflect the actual command usage
            ViewSE.println("Error using the command, at least one name is required. Usage: setambito \"Scope Name\"");
            return;
        }
        String[] a = StringUtils.joinQuotedArguments(args);

        if(a[0].isBlank()) {
            AssertionControl.logMessage("Ambito non può essere stringa vuota", 2, CLASSNAME);
            ViewSE.println("La stringa setambito non può essere vuota");
            return;
        }

        controller.db.setAmbito(a[0]);

        this.hasBeenExecuted = true;
    }
}
