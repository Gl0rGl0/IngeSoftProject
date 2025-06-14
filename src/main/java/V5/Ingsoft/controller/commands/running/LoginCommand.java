package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

public class LoginCommand extends AbstractCommand {
    private static final String CLASSNAME = LoginCommand.class.getSimpleName();

    public LoginCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LOGIN;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (args == null || args.length < 2)
            return Payload.error(
                    "Usage: login <username> <password>",
                    CLASSNAME + ": missing credentials");

        if (controller.getCurrentUser().getType() != PersonaType.GUEST)
            return Payload.warn(
                    "Already logged in, please log out first",
                    CLASSNAME + ": login attempted while already logged in");

        Payload<Persona> result = Model.getInstance().login(args[0], args[1]);

        if (result.getStatus() == Payload.Status.ERROR)
            return handleLoginError(result, args);

        return handleLoginSuccess(result);
    }

    protected Payload<String> handleLoginError(Payload<Persona> result, String[] args) {
        Persona p = result.getData();
        if (p.getType() != PersonaType.GUEST) {
            return Payload.warn(
                    "Wrong password, please try again",
                    "Invalid password for user " + p.getUsername());
        }
        // user does not exist → allow registration if a second password is provided
        if (args.length < 3) {
            return Payload.error(
                    "User not found",
                    "Registration password missing for new user " + args[0]);
        }
        if (args[2].equals(args[1])) {
            try {
                Fruitore f = new Fruitore(args);
                f.setAsNotNew();
                Model.getInstance().dbFruitoreHelper.addFruitore(f);
                controller.user = f.getUsername();
                return Payload.info(
                        "Account created and logged in as " + f.getUsername(),
                        "New account registered for " + f.getUsername());
            } catch (Exception e) {
                return Payload.error(
                        "Error during registration: " + e.getMessage(),
                        "Exception creating new user: " + e.getMessage());
            }
        }
        return Payload.error(
                "Registration failed: passwords do not match",
                "Password mismatch during registration for " + args[0]);
    }

    protected Payload<String> handleLoginSuccess(Payload<Persona> result) {
        Persona p = result.getData();
        controller.user = p.getUsername();
        String msg = "Login successful as " + p.getUsername() + " (" + p.getType() + ")";
        if (p.isNew())
            return Payload.warn(msg, "First login for " + p.getUsername());
        return Payload.info(msg, "Login succeeded for " + p.getUsername());
    }
}
