package ingsoft.commands;

import ingsoft.App;
import ingsoft.commands.setup.CommandListSETUP;
import ingsoft.util.ViewSE;

public class SetPersoneMaxCommand extends AbstractCommand {

    private final App app;
    public SetPersoneMaxCommand(App app) {
        this.app = app;
        super.commandInfo = CommandListSETUP.SETMAX;
        this.hasBeenExecuted = false;
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
            return;
        }

        this.hasBeenExecuted = true;        //DEVE ESSERE ESEGUITO ANCHE A SETUP
    }
}