package V1.Ingsoft.Controller.commands;

import V1.Ingsoft.Controller.Controller;
import V1.Ingsoft.Controller.commands.running.CommandList;
import V1.Ingsoft.util.AssertionControl;
import V1.Ingsoft.view.ViewSE;

public class HelpCommand extends AbstractCommand {

    private final Controller controller;

    public HelpCommand(Controller controller, ListInterface commandInfo) {
        this.controller = controller;
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
                AssertionControl.logMessage("Help nullo", 3, this.getClass().getSimpleName());
            }
        }
        // Se non c'è alcun argomento o se il valueOf fallisce, mostra help in base al
        // livello dell'utente
        ViewSE.println(super.commandInfo.getHelpMessage(controller.getCurrentUser().getPriority()));
    }

}
