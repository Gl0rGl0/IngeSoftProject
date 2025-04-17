package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.view.ViewSE;

public class ChangePswCommand extends AbstractCommand {


    public ChangePswCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.CHANGEPSW;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1) {
            ViewSE.println("Error in prompt usage");
            return;
        }

        if (controller.getDB().changePassword(controller.user, args[0])) {
            ViewSE.println("Password changed successfully!");
            this.hasBeenExecuted = true;
        } else {
            ViewSE.println("Error changing password, please try again...");
        }
    }
}
