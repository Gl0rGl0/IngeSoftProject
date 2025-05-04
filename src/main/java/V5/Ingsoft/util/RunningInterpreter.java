package V5.Ingsoft.util;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.HelpCommand;
import V5.Ingsoft.controller.commands.running.*;

public class RunningInterpreter extends Interpreter {

    public RunningInterpreter(Controller controller) {
        super(controller);

        commandRegistry.put("list", new ListCommand(controller));
        commandRegistry.put("login", new LoginCommand(controller));
        commandRegistry.put("preclude", new PrecludeCommand(controller));
        commandRegistry.put("remove", new RemoveCommand(controller));
        commandRegistry.put("myvisit", new MyVisitCommand(controller));
        commandRegistry.put("setav", new AvailabilityCommand(controller));
        commandRegistry.put("makeplan", new MakePlanCommand(controller));
        commandRegistry.put("visit", new VisitCommand(controller));
        commandRegistry.put("collection", new CollectionManagerCommand(controller));
        commandRegistry.put("help", new HelpCommand(controller));
    }

}
