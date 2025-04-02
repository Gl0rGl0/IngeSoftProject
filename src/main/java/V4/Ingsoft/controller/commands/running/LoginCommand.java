package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class LoginCommand extends AbstractCommand {

    private final Controller controller;

    public LoginCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGIN;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.println("Error in prompt usage");
            return;
        }
        if (controller.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Already logged in, please log out if you want to change accounts");
            return;
        }

        controller.user = login(args[0], args[1]);

        if (controller.user.getType() != PersonaType.GUEST) {
            ViewSE.println("Login successful (" + controller.user.getType() + ")");
            if (controller.user.isNew()) {
                ViewSE.println(
                        "First login detected, you are required to change your password using the 'changepsw [newpassword]' command to use the services");
            }
        } else {
            ViewSE.println("Login error, please try again");
        }
    }

    private Persona login(String username, String psw) {
        return controller.db.login(username, psw);
    }

}
