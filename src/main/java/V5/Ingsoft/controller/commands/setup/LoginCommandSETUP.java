package V5.Ingsoft.controller.commands.setup;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.LoginCommand;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;

public class LoginCommandSETUP extends AbstractCommand {

    public LoginCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.LOGIN;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        new LoginCommand(controller).execute(options, args);
        if (args == null || args.length < 2) {
            return Payload.error(
                "Usage: login <username> <password>",
                "Missing credentials in LoginCommandSETUP");
        }
        if (controller.getCurrentUser().getType() != PersonaType.GUEST) {
            return Payload.warn(
                "Already logged in. Please logout to switch accounts.",
                "LoginCommandSETUP: user already logged in");
        }
        Payload<Persona> result = controller.getDB().login(args[0], args[1]);
        if (result.getStatus() == Payload.Status.ERROR) {
            return result;
        }

        //login success
        Persona p = result.getData();
        controller.user = p.getUsername();
        String msg = "Login successful as " + p.getUsername() + " (" + p.getType() + ")";
        if (p.isNew())
            return Payload.warn(msg, "First login for " + p.getUsername());
        return Payload.info(msg, "Login succeeded for " + p.getUsername());
    }
}
