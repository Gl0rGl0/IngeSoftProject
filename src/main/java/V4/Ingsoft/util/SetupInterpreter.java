package V4.Ingsoft.util;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.HelpCommand;
import V4.Ingsoft.controller.commands.setup.AddCommandSETUP;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.controller.commands.setup.DoneCommandSETUP;
import V4.Ingsoft.controller.commands.setup.LoginCommandSETUP;
import V4.Ingsoft.controller.commands.setup.SetAmbitoCommandSETUP;

public class SetupInterpreter extends Interpreter {

    public SetupInterpreter(Controller controller) {
        super(controller);
        commandRegistry.put("add", new AddCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("done", new DoneCommandSETUP());// DA ESEGUIRE
        commandRegistry.put("login", new LoginCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("setambito", new SetAmbitoCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("help", new HelpCommand(controller, CommandListSETUP.HELP));
    }

}
