package V5.Ingsoft.util.interpreter;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.HelpCommand;
import V5.Ingsoft.controller.commands.SetPersoneMaxCommand;
import V5.Ingsoft.controller.commands.list.CommandListSETUP;
import V5.Ingsoft.controller.commands.setup.DoneCommandSETUP;
import V5.Ingsoft.controller.commands.setup.LoginCommandSETUP;
import V5.Ingsoft.controller.item.interfaces.Command;

public class SetupInterpreter extends Interpreter {

    public SetupInterpreter(Controller controller) {
        super(controller);
        //commandRegistry.put("add", new AddCommandSETUP(controller)); // TO BE EXECUTED
        commandRegistry.put("done", new DoneCommandSETUP(controller));// TO BE EXECUTED
        commandRegistry.put("login", new LoginCommandSETUP(controller)); // TO BE EXECUTED
        commandRegistry.put("help", new HelpCommand(controller));

        //SOVRASCRIVE executed = TRUE-->FALSE
        commandRegistry.put("setmax", new SetPersoneMaxCommand(controller, false));
    }

    @Override
    public boolean hasExecutedAllCommands() {
        for (Command c : commandRegistry.values()) {
            if (c.getCommandInfo().equals(CommandListSETUP.DONE)) continue;
            if (!c.hasBeenExecuted()) return false;
        }
        return true;
    }

    @Override
    public boolean isSetupCompleted() {
        return hasExecutedAllCommands() && commandRegistry.get("done").hasBeenExecuted();
    }
}
