package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.persone.Persona;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;

public class LoginCommandSETUP extends AbstractCommand{

    private final App app;
    public LoginCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.LOGIN;

        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.print("Errore nell'utilizzo del prompt");
            return;
        }
        if (app.user.type() != PersonaType.GUEST) {
            ViewSE.print("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }
        
        Persona p = login(args[0], args[1]);
        app.user = p;

        if (app.user.type() == PersonaType.CONFIGURATORE) { //SOLO UN CONFIGURATORE PUÒ ACCEDERE AL SETUP
            ViewSE.print("Login effettuato con successo (" + app.user.type() + ")");
            if(app.user.firstAccess()){
                ViewSE.print("Effettuato il primo accesso, e' richiesto di cambiare la psw con il comando 'changepsw [nuovapsw]' per usufruire di servizi");
            }
            this.hasBeenExecuted = true;
        } else {
            if(app.user.type() != PersonaType.GUEST){
                ViewSE.print("Errore di login, riprova.");
            }else{
                ViewSE.print("Solo i configuratori possono accedere, riprova.");
            }
        }
    }

    private Persona login(String username, String psw) {
        return app.db.login(username, psw);
    }

}
