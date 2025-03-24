package V1.Ingsoft.util;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.HelpCommand;
import V1.Ingsoft.controller.commands.setup.AddCommandSETUP;
import V1.Ingsoft.controller.commands.setup.CommandListSETUP;
import V1.Ingsoft.controller.commands.setup.DoneCommandSETUP;
import V1.Ingsoft.controller.commands.setup.LoginCommandSETUP;
import V1.Ingsoft.controller.commands.setup.SetAmbitoCommandSETUP;

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
