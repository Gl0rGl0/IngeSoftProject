package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class HelpCommand extends AbstractCommand{

    private final App app;

    public HelpCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.HELP;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length > 0) {
            try {
                CommandList cl = CommandList.valueOf(args[0].toUpperCase());
                ViewSE.print(cl.toString());
                return;
            } catch (IllegalArgumentException ex) {
                // Il valore non corrisponde a nessun CommandList
            }
        }
        // Se non c'è alcun argomento o se il valueOf fallisce, mostra help in base al livello dell'utente
        ViewSE.print(super.commandInfo.getHelpMessage(app.getCurrentUser().getPriorita()));
    }

}
