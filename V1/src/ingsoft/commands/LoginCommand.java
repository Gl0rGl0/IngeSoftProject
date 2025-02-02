package ingsoft.commands;

import ingsoft.App;
import ingsoft.persone.Guest;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;

public class LoginCommand extends AbstractCommand{

    public LoginCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.log("Errore nell'utilizzo del prompt: " + CommandList.LOGIN);
            return;
        }
        if (app.user.type() != PersonaType.GUEST) {
            ViewSE.log("Accesso già effettuato, effettua il logout se vuoi cambiare account");
            return;
        }
        PersonaType tipo = login(args[0], args[1]);
        if (tipo != PersonaType.GUEST && tipo != PersonaType.ERROR) {
            ViewSE.log("Login effettuato con successo (" + tipo + ")");
            if(app.user.firstAccess()){
                changePsw();
            }
        } else {
            ViewSE.log("Errore di login, riprova");
        }
    }

    private PersonaType login(String username, String psw) {
        if (app.db.loginCheckConfiguratore(username, psw)) {
            app.user = app.db.getConfiguratoreFromDB(username);
            return PersonaType.CONFIGURATORE;
        }

        app.user = app.db.cercaInDB(username);
        if (app.user == null) app.user = new Guest();
        // Potrebbero essere aggiunti ulteriori casi per altri tipi di utente
        return PersonaType.ERROR;
    }

    private void changePsw(){
        String user = app.user.getUsername();
        String newPsw;
        do { 
            newPsw = ViewSE.read("Questo e' il tuo primo accesso, cambia la psw inserendo quella nuova: ");
        } while (app.db.changePsw(app.db.cercaInDB(user), newPsw));
    }
}
