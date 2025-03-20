package V1.ingsoft.controller.commands.setup;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.AbstractCommand;
import V1.ingsoft.util.StringUtils;
import V1.ingsoft.view.ViewSE;

public class SetAmbitoCommandSETUP extends AbstractCommand {

    private final Controller controller;

    public SetAmbitoCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.SETAMBITO;
        this.hasBeenExecuted = false;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param options le options (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando, richiesto almeno un name. Utilizzo: \"Nome name name\"");
        }
        String[] a = StringUtils.joinQuotedArguments(args);
        controller.db.setAmbito(a[0]);

        this.hasBeenExecuted = true;
    }
}
