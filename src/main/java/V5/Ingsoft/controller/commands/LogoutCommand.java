package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.CommandList;
import V5.Ingsoft.controller.item.persone.Guest;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;

public class LogoutCommand extends AbstractCommand {

    public LogoutCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGOUT;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (controller.getCurrentUser().getType() == PersonaType.GUEST) {
            return Payload.warn(
                "No user is logged in. Please log in first.",
                "Logout attempted with no user logged in.");
        }
        controller.user = Guest.getInstance().getUsername();
        return Payload.info(
            "Logout successful.",
            "User logged out successfully.");
    }
}
