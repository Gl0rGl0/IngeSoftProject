package ingsoft.commands;

import ingsoft.App;
import ingsoft.util.ViewSE;

public class ChangePswCommand extends AbstractCommand {

    public ChangePswCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    /**
     * Effettua il logout impostando l'utente corrente come Guest.
     */
    public void execute(String[] options, String[] args) {
        if(app.db.changePassword(app.user.getUsername(), args[0], app.user.type())){
            app.user.notNew();
            ViewSE.print("Password cambiata con successo!");
        }else{
            ViewSE.print("Errore nel cambio psw riprova...");
        }
    }
}