package V2.Ingsoft.util;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.controller.commands.HelpCommand;
import V2.Ingsoft.controller.commands.running.*;

public class RunningInterpreter extends Interpreter {

    public RunningInterpreter(Controller controller) {
        super(controller);

        commandRegistry.put("list", new ListCommand(controller));
        commandRegistry.put("login", new LoginCommand(controller));
        commandRegistry.put("preclude", new PrecludeCommand(controller));
        commandRegistry.put("myvisit", new MyVisitCommand(controller));
        commandRegistry.put("setav", new AvailabilityCommand(controller));
        commandRegistry.put("help", new HelpCommand(controller, CommandList.HELP));
    }

}
