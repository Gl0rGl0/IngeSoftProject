package V1.ingsoft.controller.commands;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.running.CommandList;
import V1.ingsoft.controller.item.persone.Guest;
import V1.ingsoft.controller.item.persone.PersonaType;
import V1.ingsoft.view.ViewSE;

public class LogoutCommand extends AbstractCommand {

    private final Controller app;

    public LogoutCommand(Controller app) {
        this.app = app;
        super.commandInfo = CommandList.LOGOUT;
    }

    @Override
    /**
     * Effettua il logout impostando l'utente corrente come Guest.
     */
    public void execute(String[] options, String[] args) {
        if (app.user.getType() == PersonaType.GUEST) {
            ViewSE.println("Nessun utente loggato, effettua il login prima.");
        } else {
            ViewSE.println("Logout effettuato con successo.");
            app.user = new Guest();
        }
    }
}