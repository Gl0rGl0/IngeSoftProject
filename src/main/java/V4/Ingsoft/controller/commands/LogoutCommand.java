package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.controller.item.persone.Guest;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class LogoutCommand extends AbstractCommand {

    private final Controller controller;

    public LogoutCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGOUT;
    }

    @Override
    /**
     * Effettua il logout impostando l'utente corrente come Guest.
     */
    public void execute(String[] options, String[] args) {
        if (controller.user.getType() == PersonaType.GUEST) {
            ViewSE.println("Nessun utente loggato, effettua il login prima.");
        } else {
            ViewSE.println("Logout effettuato con successo.");
            controller.user = new Guest();
        }
    }
}