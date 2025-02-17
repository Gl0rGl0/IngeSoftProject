package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class SetPersoneMaxCommandSETUP extends AbstractCommand {

    private final App app;
    public SetPersoneMaxCommandSETUP(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.SETMAX;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.print("Errore nell'utilizzo del prompt");
            return;
        }
        try {
            app.maxPrenotazioniPerPersona = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            //
        }
    }
}