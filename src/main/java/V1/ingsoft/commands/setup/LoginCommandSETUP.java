package V1.ingsoft.commands.setup;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.persone.Persona;
import V1.ingsoft.persone.PersonaType;

public class LoginCommandSETUP extends AbstractCommand {

    private final App app;

    public LoginCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.LOGIN;

        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.println("Errore nell'utilizzo del prompt");
            return;
        }
        if (app.user.type() != PersonaType.GUEST) {
            ViewSE.println("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }

        Persona p = login(args[0], args[1]);
        app.user = p;

        if (app.user.type() == PersonaType.CONFIGURATORE) { // SOLO UN CONFIGURATORE PUÒ ACCEDERE AL SETUP
            ViewSE.println("Login effettuato con successo (" + app.user.type() + ")");
            if (app.user.firstAccess()) {
                ViewSE.println(
                        "Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire di servizi");
            }
            this.hasBeenExecuted = true;
        } else {
            if (app.user.type() != PersonaType.GUEST) {
                ViewSE.println("Errore di login, riprova.");
            } else {
                ViewSE.println("Solo i configuratori possono accedere, riprova.");
            }
        }
    }

    private Persona login(String username, String psw) {
        return app.db.login(username, psw);
    }

}
