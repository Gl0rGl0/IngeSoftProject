package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.StringUtils;

public class SetAmbitoCommandSETUP extends AbstractCommand {

    private final App app;

    public SetAmbitoCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.SETAMBITO;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando, richiesto almeno un nome. Utilizzo: \"Nome nome nome\"");
        }
        String[] a = StringUtils.joinQuotedArguments(args);
        app.db.setAmbito(a[0]);

        this.hasBeenExecuted = true;
    }
}
