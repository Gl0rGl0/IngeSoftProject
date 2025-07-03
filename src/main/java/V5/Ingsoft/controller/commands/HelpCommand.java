package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.util.Payload;

public class HelpCommand extends AbstractCommand {

    public HelpCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.HELP;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        String helpMsg;
        if (args != null && args.length > 0) {
            try {
                CommandList cl = CommandList.valueOf(args[0].toUpperCase());

                helpMsg = cl.toString();
                //helpMsg = CommandList.valueOf("HELP").getHelpMessage(controller.getCurrentUser().getPriority());

                return Payload.info(
                        helpMsg,
                        "Displayed help for command: " + args[0]);
            } catch (IllegalArgumentException ex) {
                return Payload.warn(
                        "Unknown command '" + args[0] + "'. Showing general help.",
                        "HelpCommand: invalid argument '" + args[0] + "'");
            }
        }
        // no args or invalid → general help
        helpMsg = super.commandInfo.getHelpMessage(controller.getCurrentUser().getPriority());
        return Payload.info(
                helpMsg,
                "Displayed general help.");
    }
}
