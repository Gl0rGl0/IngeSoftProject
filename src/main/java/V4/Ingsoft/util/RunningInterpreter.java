package V4.Ingsoft.util;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.HelpCommand;
import V4.Ingsoft.controller.commands.running.AvailabilityCommand;
import V4.Ingsoft.controller.commands.running.CollectionManagerCommand;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.controller.commands.running.ListCommand;
import V4.Ingsoft.controller.commands.running.LoginCommand;
import V4.Ingsoft.controller.commands.running.MakePlanCommand;
import V4.Ingsoft.controller.commands.running.MyVisitCommand;
import V4.Ingsoft.controller.commands.running.PrecludeCommand;
import V4.Ingsoft.controller.commands.running.RemoveCommand;
import V4.Ingsoft.controller.commands.running.VisitCommand;

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
        commandRegistry.put("help", new HelpCommand(controller, CommandList.HELP));
    }

}
