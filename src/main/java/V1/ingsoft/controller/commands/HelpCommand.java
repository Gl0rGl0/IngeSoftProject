package V1.ingsoft.controller.commands;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.running.CommandList;
import V1.ingsoft.view.ViewSE;

public class HelpCommand extends AbstractCommand {

    private final Controller app;

    public HelpCommand(Controller app, ListInterface commandInfo) {
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
            }
        }
        // Se non c'è alcun argomento o se il valueOf fallisce, mostra help in base al
        // livello dell'utente
        ViewSE.println(super.commandInfo.getHelpMessage(app.getCurrentUser().getPriority()));
    }

}
