package V1.ingsoft.commands.running;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.persone.Persona;
import V1.ingsoft.persone.PersonaType;

public class LoginCommand extends AbstractCommand {

    private final App app;

    public LoginCommand(App app) {
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
                        "Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire di servizi");
            }
        } else {
            ViewSE.println("Errore di login, riprova");
        }
    }

    private Persona login(String userName, String psw) {
        return app.db.login(userName, psw);
    }

}
