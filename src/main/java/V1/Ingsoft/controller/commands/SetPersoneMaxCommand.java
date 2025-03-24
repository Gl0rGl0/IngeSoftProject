package V1.Ingsoft.controller.commands;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.setup.CommandListSETUP;
import V1.Ingsoft.view.ViewSE;

public class SetPersoneMaxCommand extends AbstractCommand {

    private final Controller controller;

    public SetPersoneMaxCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETMAX;
        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }
        try {
            controller.maxPrenotazioniPerPersona = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return;
        }

        this.hasBeenExecuted = true; // DEVE ESSERE ESEGUITO ANCHE A SETUP
    }
}