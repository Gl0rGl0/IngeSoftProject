package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;

public class LoginCommand extends AbstractCommand {
    private static final String CLASSNAME = LoginCommand.class.getSimpleName();

    public LoginCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGIN;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        if (args == null || args.length < 2)
            return Payload.error(
                "Usage: login <username> <password>",
                CLASSNAME + ": missing credentials");

        if (controller.getCurrentUser().getType() != PersonaType.GUEST)
            return Payload.warn(
                "Already logged in, please log out first",
                CLASSNAME + ": login attempted while already logged in");

        Payload result = controller.getDB().login(args[0], args[1]);
        // log the raw result if desired
        // AssertionControl.logMessage(result.getLogMessage(), result.getLevel(), CLASSNAME);

        if (result.getStatus() == Payload.Status.ERROR)
            return handleLoginError(result, args);

        return handleLoginSuccess(result);
    }

    private Payload handleLoginError(Payload result, String[] args) {
        Persona p = (Persona) result.getData();
        if (p.getType() != PersonaType.GUEST) {
            return Payload.warn(
                "Wrong password, please try again",
                CLASSNAME + ": invalid password for user " + p.getUsername());
        }
        // user does not exist → allow registration if a second password is provided
        if (args.length < 3) {
            return Payload.warn(
                "User not found. To register, provide the password twice",
                CLASSNAME + ": registration password missing for new user " + args[0]);
        }
        if (args[2].equals(args[1])) {
            try {
                Fruitore f = new Fruitore(args);
                f.setAsNotNew();
                controller.getDB().dbFruitoreHelper.addFruitore(f);
                controller.user = f.getUsername();
                return Payload.info(
                    "Account created and logged in as " + f.getUsername(),
                    CLASSNAME + ": new account registered for " + f.getUsername());
            } catch (Exception e) {
                return Payload.error(
                    "Error during registration: " + e.getMessage(),
                    CLASSNAME + ": exception creating new user: " + e.getMessage());
            }
        }
        return Payload.error(
            "Registration failed: passwords do not match",
            CLASSNAME + ": password mismatch during registration for " + args[0]);
    }

    private Payload handleLoginSuccess(Payload result) {
        Persona p = (Persona) result.getData();
        controller.user = p.getUsername();
        String msg = "Login successful as " + p.getUsername() + " (" + p.getType() + ")";
        if (p.isNew()) {
            msg += ". First login detected, please change your password with 'changepsw [newpassword]'";
            return Payload.warn(
                msg,
                CLASSNAME + ": first login for " + p.getUsername());
        }
        return Payload.info(
            msg,
            CLASSNAME + ": login succeeded for " + p.getUsername());
    }
}
