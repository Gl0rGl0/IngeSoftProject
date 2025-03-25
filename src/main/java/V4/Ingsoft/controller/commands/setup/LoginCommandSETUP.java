package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class LoginCommandSETUP extends AbstractCommand {

    private final Controller controller;

    public LoginCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.LOGIN;

        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }
        if (controller.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }

        Persona p = login(args[0], args[1]);
        controller.user = p;

        if (controller.user.getType() == PersonaType.CONFIGURATORE) { // SOLO UN CONFIGURATORE PUÒ ACCEDERE AL SETUP
            ViewSE.println("Login effettuato con successo (" + controller.user.getType() + ")");
            if (controller.user.isNew()) {
                ViewSE.println(
                        "Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire dei servizi");
            }
            this.hasBeenExecuted = true;
        } else {
            if (controller.user.getType() != PersonaType.GUEST) {
                ViewSE.println("Errore di login, riprova.");
            } else {
                ViewSE.println("Solo i configuratori possono accedere, riprova.");
            }
        }
    }

    private Persona login(String username, String psw) {
        return controller.db.login(username, psw);
    }

}
