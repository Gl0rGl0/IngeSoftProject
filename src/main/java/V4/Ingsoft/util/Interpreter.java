package V4.Ingsoft.util;

import java.util.ArrayList;
import java.util.HashMap;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AssignCommand;
import V4.Ingsoft.controller.commands.ChangePswCommand;
import V4.Ingsoft.controller.commands.Command;
import V4.Ingsoft.controller.commands.ExitCommand;
import V4.Ingsoft.controller.commands.LogoutCommand;
import V4.Ingsoft.controller.commands.SetPersoneMaxCommand;
import V4.Ingsoft.controller.commands.TimeCommand;
import V4.Ingsoft.controller.commands.setup.DoneCommandSETUP;
import V4.Ingsoft.controller.item.persone.Persona;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public abstract class Interpreter {
    // Map of commands passed during setup
    protected HashMap<String, Command> commandRegistry = new HashMap<>();

    // Constructor that receives the command map
    public Interpreter(Controller controller) {
        commandRegistry.put("time", new TimeCommand(controller));
        commandRegistry.put("logout", new LogoutCommand(controller));
        commandRegistry.put("changepsw", new ChangePswCommand(controller));
        commandRegistry.put("assign", new AssignCommand(controller));
        commandRegistry.put("setmax", new SetPersoneMaxCommand(controller, true));
        commandRegistry.put("exit", new ExitCommand(controller));
    }

    /**
     * Interprets the prompt provided by the user and, based on the command map,
     * executes the corresponding action.
     *
     * @param prompt      the entered command string
     * @param currentUser the current user (necessary for permission checks)
     */
    public void interpret(String prompt, Persona currentUser) {
        if(prompt == null || prompt.isEmpty()) { // Use isEmpty() for clarity
            AssertionControl.logMessage("ERROR NO COMMAND", 2, this.getClass().getSimpleName());
            ViewSE.println("Error: no command provided.");
            return;
        }

        String[] tokens = prompt.trim().split("\\s+");

        if (tokens.length == 0 || tokens[0].isEmpty()) {
            AssertionControl.logMessage("ERROR NO COMMAND", 2, this.getClass().getSimpleName());
            ViewSE.println("Error: no command provided.");
            return;
        }

        String cmd = tokens[0];
        ArrayList<String> optionsList = new ArrayList<>();
        ArrayList<String> argsList = new ArrayList<>();

        // Separate options (tokens starting with '-') and arguments
        for (int i = 1; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("-") && token.length() > 1) {
                optionsList.add(token.substring(1));
            } else {
                argsList.add(token);
            }
        }

        String[] options = optionsList.toArray(String[]::new);
        String[] args = argsList.toArray(String[]::new);

        Command command = commandRegistry.get(cmd);
        if (command != null) {
            int userPriority = currentUser.getType().getPriority();
            // Check permissions
            if (!command.canBeExecutedBy(userPriority)) {
                ViewSE.println("You do not have the necessary permissions to execute the command '" + cmd + "'.");
                return;
            }
            // If the user is in "firstAccess" state, limit executable commands
            if (currentUser.isNew() &&
                    !command.canBeExecutedBy(PersonaType.CAMBIOPSW.getPriority())) {
                ViewSE.println("You do not have the necessary permissions to execute the command '" + cmd +
                        "' until the password is changed using 'changepsw [newpassword]'.");
                return;
            }
            // Execute the command
            command.execute(options, args);
        } else {
            ViewSE.println("\"" + cmd + "\" is not recognized as an internal command.");
        }
    }

    public boolean haveAllBeenExecuted() {
        boolean out = true;
        for (Command c : commandRegistry.values()) {
            if(c instanceof DoneCommandSETUP){
                continue;
            }
            out &= c.hasBeenExecuted();
            if (!out)
                return out;
        }
        return out; // Just one false is enough to return false as the result
    }

    public void printHaveAllBeenExecuted() {
        for (Command c : commandRegistry.values()) {
            System.out.println(c.toString() + " " + c.hasBeenExecuted());
        }
    }

    public boolean doneAll(){
        if(!(this instanceof SetupInterpreter))
            return true;
        return haveAllBeenExecuted() && commandRegistry.get("done").hasBeenExecuted();
    }
}
