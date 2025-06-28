package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

public class ChangePswCommand extends AbstractCommand {

    public ChangePswCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.CHANGEPSW;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (args == null || args.length < 2) {
            return Payload.error(
                    "Usage: changepsw <newpassword> <newpassword>",
                    "Missing password argument in ChangePswCommand");
        }
        if(!args[0].equals(args[1])){
            return Payload.error(
                "The passwords aren't equal, please try again...",
                "Password inequality failed for user: " + controller.user);
        }
        if (Model.getInstance().changePassword(controller.user, args[0])) {
            this.hasBeenExecuted = true;
            return Payload.info(
                    "Password changed successfully!",
                    "Password changed for user: " + controller.user);
        }
        return Payload.error(
                "Error changing password, please try again...",
                "Password change failed for user: " + controller.user);
    }
}
