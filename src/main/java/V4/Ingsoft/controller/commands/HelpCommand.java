package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.view.ViewSE;

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
                AssertionControl.logMessage("Help null", 3, this.getClass().getSimpleName()); // Assuming "nullo" means null
            }
        }
        // If there is no argument or if valueOf fails, show help based on the
        // user's level
        ViewSE.println(super.commandInfo.getHelpMessage(controller.getCurrentUser().getPriority()));
    }

}
