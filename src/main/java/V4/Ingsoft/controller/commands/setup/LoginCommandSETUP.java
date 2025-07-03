package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Payload;
import V4.Ingsoft.util.Payload.Status;
import V4.Ingsoft.view.ViewSE;

public class LoginCommandSETUP extends AbstractCommand {


    public LoginCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.LOGIN;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 2) {
            ViewSE.println("Error in prompt usage");
            return;
        }
        if (controller.getCurrentUser().getType() != PersonaType.GUEST) {
            ViewSE.println("Already logged in, please log out if you want to change accounts");
            return;
        }

        Payload toUse = login(args[0], args[1]);
        controller.user = ((Persona) toUse.getData()).getUsername();

        if (toUse.getStatus() == Status.ERROR) {
            loginError(toUse);
        } else {
            loginSuccess();
        }

    }

    private Payload login(String username, String psw) {
        return controller.getDB().login(username, psw);
    }

    private void loginError(Payload toUse) {
        Persona p = (Persona) toUse.getData();
        if (p.getType() != PersonaType.GUEST) {
            ViewSE.println("Wrong password, please try again");
            AssertionControl.logMessage("Error password " + p.getUsername(), 3, CLASSNAME);
        } else {
            ViewSE.println("This Configuratore doesn't exist, please try again");
            AssertionControl.logMessage("No configuratore exist " + p.getUsername(), 3, CLASSNAME);
        }
    }

    private void loginSuccess() {
        ViewSE.println("[SETUP] Login successful (" + controller.getCurrentUser().getType() + ")");
        if (controller.getCurrentUser().isNew()) {
            ViewSE.println("First login detected, you are required to change your password using the 'changepsw [newpassword]' command to use the services");
        }
    }
}