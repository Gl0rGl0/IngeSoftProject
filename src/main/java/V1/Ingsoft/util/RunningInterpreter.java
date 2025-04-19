package V1.Ingsoft.util;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.HelpCommand;
import V1.Ingsoft.controller.commands.running.*;

public class RunningInterpreter extends Interpreter {

    public RunningInterpreter(Controller controller) {
        super(controller);

        commandRegistry.put("list", new ListCommand(controller));
        commandRegistry.put("login", new LoginCommand(controller));
        commandRegistry.put("preclude", new PrecludeCommand(controller));
        commandRegistry.put("help", new HelpCommand(controller, CommandList.HELP));
    }

}
