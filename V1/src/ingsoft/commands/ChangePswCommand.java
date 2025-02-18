package ingsoft.commands;

import ingsoft.App;
import ingsoft.commands.running.CommandList;
import ingsoft.util.ViewSE;

public class ChangePswCommand extends AbstractCommand {

    private final App app;
    public ChangePswCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.CHANGEPSW;

        this.hasBeenExecuted = true;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.print("Errore nell'utilizzo del prompt");
            return;
        }

        if(app.db.changePassword(app.user.getUsername(), args[0], app.user.type())){
            app.user.notNew();
            ViewSE.print("Password cambiata con successo!");
            this.hasBeenExecuted = true;
        }else{
            ViewSE.print("Errore nel cambio psw riprova...");
        }
    }
}