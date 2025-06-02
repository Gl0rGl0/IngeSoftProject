package V5.Ingsoft.controller.commands.setup;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.LoginCommand;
import V5.Ingsoft.controller.commands.setup.list.CommandListSETUP;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

public class LoginCommandSETUP extends LoginCommand {

    public LoginCommandSETUP(Controller controller) {
        super(controller);
        super.commandInfo = CommandListSETUP.LOGIN;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
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
        Payload<Persona> result = Model.getInstance().login(args[0], args[1]);
        if (result.getStatus() == Payload.Status.ERROR) {
            return super.handleLoginError(result, args);
        } else {
            return super.handleLoginSuccess(result);
        }

    }
}
