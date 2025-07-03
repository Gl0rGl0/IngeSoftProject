package V2.Ingsoft.controller.commands.running;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.controller.commands.AbstractCommand;
import V2.Ingsoft.controller.item.persone.Fruitore;
import V2.Ingsoft.controller.item.persone.Persona;
import V2.Ingsoft.controller.item.persone.PersonaType;
import V2.Ingsoft.util.AssertionControl;
import V2.Ingsoft.util.Payload;
import V2.Ingsoft.util.Payload.Status;
import V2.Ingsoft.view.ViewSE;

public class LoginCommand extends AbstractCommand {


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
        if (controller.getCurrentUser().getType() != PersonaType.GUEST) {
            ViewSE.println("Already logged in, please log out if you want to change accounts");
            return;
        }

        Payload toUse = login(args[0], args[1]);
        AssertionControl.logMessage(toUse, 0, "CLASSNAME");

        if (toUse.getStatus() == Status.ERROR) {
            loginError(toUse, args);
        } else {
            loginSuccess(toUse);
        }
    }

    private Payload login(String username, String psw) {
        return controller.getDB().login(username, psw);
    }

    private void loginError(Payload toUse, String[] args) {
        Persona p = (Persona) toUse.getData();
        if (p.getType() != PersonaType.GUEST) {
            ViewSE.println("Wrong password, please try again");
            AssertionControl.logMessage("Error password " + p.getUsername(), 3, CLASSNAME);
            return;
        }

        if (args.length < 3) {
            ViewSE.println("This user doesn't exist, if you want to create an account please put the password 2 times");
            AssertionControl.logMessage("Wrong registration setup " + p.getUsername(), 3, CLASSNAME);
            return;
        }

        if (args[2].equals(args[1])) {
            try {
                Fruitore f = new Fruitore(args);
                f.setAsNotNew();
                controller.getDB().dbFruitoreHelper.addFruitore(f);
                controller.user = f.getUsername();
            } catch (Exception e) {
                AssertionControl.logMessage(e.getMessage(), 2, CLASSNAME);
                return;
            }
        }

        ViewSE.println("Login successful (" + controller.getCurrentUser().getType() + ") " + controller.getCurrentUser().getUsername());
        AssertionControl.logMessage("Login successful (" + controller.getCurrentUser().getType() + ") " + controller.getCurrentUser().getUsername(), 3, CLASSNAME);
    }

    private void loginSuccess(Payload toUse) {
        controller.user = ((Persona) toUse.getData()).getUsername();
        ViewSE.println("Login successful (" + controller.getCurrentUser().getType() + ") " + controller.getCurrentUser().getUsername());
        if (controller.getCurrentUser().isNew()) {
            ViewSE.println(
                    "First login detected, you are required to change your password using the 'changepsw [newpassword] [newpassword]' command to use the services");
        }
    }

}
