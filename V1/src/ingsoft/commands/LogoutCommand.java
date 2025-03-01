package ingsoft.commands;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.running.CommandList;
import ingsoft.persone.Guest;
import ingsoft.persone.PersonaType;

public class LogoutCommand extends AbstractCommand {

    private final App app;

    public LogoutCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.LOGOUT;
    }

    @Override
    /**
     * Effettua il logout impostando l'utente corrente come Guest.
     */
    public void execute(String[] options, String[] args) {
        if (app.user.type() == PersonaType.GUEST) {
            ViewSE.println("Nessun utente loggato, effettua il login prima.");
        } else {
            ViewSE.println("Logout effettuato con successo.");
            app.user = new Guest();
        }
    }
}