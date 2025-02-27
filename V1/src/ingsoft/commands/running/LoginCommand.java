package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;

public class LoginCommand extends AbstractCommand{

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
        if (app.user.type() != PersonaType.GUEST) {
            ViewSE.println("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }

        app.user = login(args[0], args[1]);

        if (app.user.type() != PersonaType.GUEST) {
            ViewSE.println("Login effettuato con successo (" + app.user.type() + ")");
            if(app.user.firstAccess()){
                ViewSE.println("Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire di servizi");
            }
        } else {
            ViewSE.println("Errore di login, riprova");
        }
    }

    private Persona login(String username, String psw) {
        return app.db.login(username, psw);
    }

}
