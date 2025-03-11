package V1.ingsoft.commands;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.running.CommandList;
import V1.ingsoft.persone.Guest;
import V1.ingsoft.persone.PersonaType;

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