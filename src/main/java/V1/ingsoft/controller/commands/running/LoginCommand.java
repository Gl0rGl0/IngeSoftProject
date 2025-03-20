package V1.ingsoft.controller.commands.running;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.AbstractCommand;
import V1.ingsoft.controller.item.persone.Persona;
import V1.ingsoft.controller.item.persone.PersonaType;
import V1.ingsoft.view.ViewSE;

public class LoginCommand extends AbstractCommand {

    private final Controller app;

    public LoginCommand(Controller app) {
        this.app = app;
        super.commandInfo = CommandList.LOGIN;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }
        if (app.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }

        app.user = login(args[0], args[1]);

        if (app.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Login effettuato con successo (" + app.user.getType() + ")");
            if (app.user.isNew()) {
                ViewSE.println(
                        "Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire dei servizi");
            }
        } else {
            ViewSE.println("Errore di login, riprova");
        }
    }

    private Persona login(String username, String psw) {
        return app.db.login(username, psw);
    }

}
