package V2.Ingsoft.controller.commands;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.controller.commands.running.CommandList;
import V2.Ingsoft.controller.item.persone.Guest;
import V2.Ingsoft.controller.item.persone.PersonaType;
import V2.Ingsoft.view.ViewSE;

public class LogoutCommand extends AbstractCommand {


    public LogoutCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGOUT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller.getCurrentUser().getType() == PersonaType.GUEST) {
            ViewSE.println("No user logged in, please log in first.");
        } else {
            ViewSE.println("Logout successful.");
            controller.user = Guest.getInstance().getUsername();
        }
    }
}
