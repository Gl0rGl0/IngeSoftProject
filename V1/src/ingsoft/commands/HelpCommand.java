package ingsoft.commands;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.running.CommandList;

public class HelpCommand extends AbstractCommand {

    private final App app;

    public HelpCommand(App app, ListInterface commandInfo) {
        this.app = app;
        super.commandInfo = commandInfo;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length > 0) {
            try {
                CommandList cl = CommandList.valueOf(args[0].toUpperCase());
                ViewSE.println(cl.toString());
                return;
            } catch (IllegalArgumentException ex) {
                // Il valore non corrisponde a nessun CommandList
            }
        }
        // Se non c'è alcun argomento o se il valueOf fallisce, mostra help in base al
        // livello dell'utente
        ViewSE.println(super.commandInfo.getHelpMessage(app.getCurrentUser().getPriorita()));
    }

}
