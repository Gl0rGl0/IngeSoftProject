package V1.Ingsoft.controller.commands;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.running.CommandList;
import V1.Ingsoft.view.ViewSE;

public class ChangePswCommand extends AbstractCommand {

    private final Controller controller;

    public ChangePswCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.CHANGEPSW;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }

        if (controller.db.changePassword(controller.user.getUsername(), args[0], controller.user.getType())) {
            controller.user.setAsNotNew();
            ViewSE.println("Password cambiata con successo!");
            this.hasBeenExecuted = true;
        } else {
            ViewSE.println("Errore nel cambio psw riprova...");
        }
    }
}