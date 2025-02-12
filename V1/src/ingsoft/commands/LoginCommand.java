package ingsoft.commands;

import ingsoft.App;
import ingsoft.persone.PersonaType;
import ingsoft.util.ViewSE;

public class LoginCommand extends AbstractCommand{

    public LoginCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
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
        PersonaType tipo = login(args[0], args[1]);
        if (tipo != PersonaType.GUEST && tipo != PersonaType.ERROR) {
            ViewSE.print("Login effettuato con successo (" + tipo + ")");
            if(app.user.firstAccess()){
                changePsw();
            }
        } else {
            ViewSE.print("Errore di login, riprova");
        }
    }

    private PersonaType login(String username, String psw) {
        if (app.db.loginCheckConfiguratore(username, psw)) {
            app.user = app.db.getConfiguratoreFromDB(username);
            return PersonaType.CONFIGURATORE;
        }

        app.setUser(username);
        
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
