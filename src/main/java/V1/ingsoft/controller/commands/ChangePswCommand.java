package V1.ingsoft.controller.commands;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.running.CommandList;
import V1.ingsoft.view.ViewSE;

public class ChangePswCommand extends AbstractCommand {

    private final Controller app;

    public ChangePswCommand(Controller app) {
        this.app = app;
        super.commandInfo = CommandList.CHANGEPSW;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }

        if (app.db.changePassword(app.user.getUsername(), args[0], app.user.getType())) {
            app.user.setAsNotNew();
            ViewSE.println("Password cambiata con successo!");
            this.hasBeenExecuted = true;
        } else {
            ViewSE.println("Errore nel cambio psw riprova...");
        }
    }
}