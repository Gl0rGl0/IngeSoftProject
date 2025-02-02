package ingsoft.commands;

import ingsoft.App;
import ingsoft.util.ViewSE;

public class HelpCommand extends AbstractCommand{
    public HelpCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length > 0) {
            try {
                CommandList cl = CommandList.valueOf(args[0].toUpperCase());
                ViewSE.log(cl.toString());
                return;
            } catch (IllegalArgumentException ex) {
                // Il valore non corrisponde a nessun CommandList
            }
        }
        // Se non c'è alcun argomento o se il valueOf fallisce, mostra help in base al livello dell'utente
        ViewSE.log(super.commandInfo.getHelpMessage(app.getCurrentUser().type().getPriorita()));
    }

}
