package ingsoft.commands;

import ingsoft.App;
import ingsoft.persone.Guest;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;

public class LogoutCommand extends AbstractCommand {

    public LogoutCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    /**
     * Effettua il logout impostando l'utente corrente come Guest.
     */
    public void execute(String[] options, String[] args) {
        if (app.user.type() == PersonaType.GUEST) {
            ViewSE.print("Nessun utente loggato, effettua il login prima.");
        } else {
            ViewSE.print("Logout effettuato con successo.");
            app.user = new Guest();
        }
    }
}