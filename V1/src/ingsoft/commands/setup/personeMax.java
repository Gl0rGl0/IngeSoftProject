package ingsoft.commands.setup;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class personeMax extends AbstractCommand {

    public personeMax(App app) {
        super(app, null);
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.print("Errore nell'utilizzo del prompt");
            return;
        }
        try {
            super.app.maxPrenotazioniPerPersona = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            //
        }
    }
}