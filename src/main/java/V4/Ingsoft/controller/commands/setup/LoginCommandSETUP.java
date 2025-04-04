package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.Guest; // Added missing import
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class LoginCommandSETUP extends AbstractCommand {

    private final Controller controller;

    public LoginCommandSETUP(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.LOGIN;

        this.hasBeenExecuted = false;
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

        Persona p = login(args[0], args[1]);
        controller.user = p;

        System.out.println(p);

        if (controller.user.getType() == PersonaType.CONFIGURATORE) { // ONLY A CONFIGURATOR CAN ACCESS SETUP
            ViewSE.println("Login successful (" + controller.user.getType() + ")");
            if (controller.user.isNew()) {
                ViewSE.println(
                        "First login detected, you are required to change your password using the 'changepsw [newpassword]' command to use the services");
            }
            this.hasBeenExecuted = true;
        } else {
            // Reset user to Guest if login failed or was not a configurator
            controller.user = new Guest();
            ViewSE.println("Login error or insufficient permissions for setup, please try again.");
        }
    }

    private Persona login(String username, String psw) {
        return controller.db.login(username, psw);
    }

}
