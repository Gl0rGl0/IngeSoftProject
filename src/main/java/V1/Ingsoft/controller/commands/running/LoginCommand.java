package V1.Ingsoft.controller.commands.running;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.AbstractCommand;
import V1.Ingsoft.controller.item.persone.Persona;
import V1.Ingsoft.controller.item.persone.PersonaType;
import V1.Ingsoft.view.ViewSE;

public class LoginCommand extends AbstractCommand {

    private final Controller controller;

    public LoginCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGIN;
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

        controller.user = login(args[0], args[1]);

        if (controller.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Login effettuato con successo (" + controller.user.getType() + ")");
            if (controller.user.isNew()) {
                ViewSE.println(
                        "Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire dei servizi");
            }
        } else {
            ViewSE.println("Errore di login, riprova");
        }
    }

    private Persona login(String username, String psw) {
        return controller.db.login(username, psw);
    }

}
