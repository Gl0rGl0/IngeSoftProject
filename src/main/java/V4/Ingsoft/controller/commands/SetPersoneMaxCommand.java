package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.view.ViewSE;

public class SetPersoneMaxCommand extends AbstractCommand {

    private final Controller controller;

    public SetPersoneMaxCommand(Controller controller, boolean hasBeenExecuted) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETMAX;
        this.hasBeenExecuted = hasBeenExecuted;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Error in prompt usage");
            return;
        }
        try {
            controller.maxPrenotazioniPerPersona = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            ViewSE.println("Invalid value.");
            return;
        }
        this.hasBeenExecuted = true; // MUST ALSO BE EXECUTED DURING SETUP
    }
}
