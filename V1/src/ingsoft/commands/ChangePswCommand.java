package ingsoft.commands;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.running.CommandList;

public class ChangePswCommand extends AbstractCommand {

    private final App app;

    public ChangePswCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.CHANGEPSW;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }

        if (app.db.changePassword(app.user.getUsername(), args[0], app.user.type())) {
            app.user.notNew();
            ViewSE.println("Password cambiata con successo!");
            this.hasBeenExecuted = true;
        } else {
            ViewSE.println("Errore nel cambio psw riprova...");
        }
    }
}