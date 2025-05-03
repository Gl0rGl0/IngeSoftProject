package V5.Ingsoft.controller.commands.setup;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;

public class LoginCommandSETUP extends AbstractCommand {

    public LoginCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.LOGIN;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        if (args == null || args.length < 2) {
            return Payload.error(
                "Usage: login <username> <password>",
                "Missing credentials in LoginCommandSETUP"
            );
        }
        if (controller.getCurrentUser().getType() != PersonaType.GUEST) {
            return Payload.warn(
                "Already logged in. Please logout to switch accounts.",
                "LoginCommandSETUP: user already logged in"
            );
        }
        Payload result = controller.getDB().login(args[0], args[1]);
        if (result.getStatus() == Payload.Status.ERROR) {
            return Payload.error(
                result.getData(),
                result.getLogMessage()
            );
        }
        // success
        Persona p = (Persona) result.getData();
        controller.user = p.getUsername();
        String msg = "[SETUP] Login successful (" + p.getType() + ")";
        if (p.isNew()) {
            msg += "\nFirst login detected: please change your password using 'changepsw [newpassword]'";
        }
        this.hasBeenExecuted = true;
        return Payload.info(
            msg,
            "LoginCommandSETUP successful for user: " + p.getUsername()
        );
    }
}
