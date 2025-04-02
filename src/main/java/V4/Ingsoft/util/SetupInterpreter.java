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
        commandRegistry.put("add", new AddCommandSETUP(controller)); // TO BE EXECUTED
        commandRegistry.put("done", new DoneCommandSETUP());// TO BE EXECUTED
        commandRegistry.put("login", new LoginCommandSETUP(controller)); // TO BE EXECUTED
        commandRegistry.put("setambito", new SetAmbitoCommandSETUP(controller)); // TO BE EXECUTED
        // Note: setmax command seems missing here compared to AbstractCommand comment
        commandRegistry.put("help", new HelpCommand(controller, CommandListSETUP.HELP));
    }

}
