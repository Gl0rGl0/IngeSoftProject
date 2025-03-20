package V1.ingsoft.util;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.HelpCommand;
import V1.ingsoft.controller.commands.running.AddCommand;
import V1.ingsoft.controller.commands.running.CommandList;
import V1.ingsoft.controller.commands.running.ListCommand;
import V1.ingsoft.controller.commands.running.LoginCommand;
import V1.ingsoft.controller.commands.running.MyVisitCommand;
import V1.ingsoft.controller.commands.running.PrecludeCommand;
import V1.ingsoft.controller.commands.running.RemoveCommand;

public class RunningInterpreter extends Interpreter {

    public RunningInterpreter(Controller controller) {
        super(controller);

        commandRegistry.put("add", new AddCommand(controller));
        commandRegistry.put("list", new ListCommand(controller));
        commandRegistry.put("login", new LoginCommand(controller));
        commandRegistry.put("preclude", new PrecludeCommand(controller));
        commandRegistry.put("remove", new RemoveCommand(controller));
        commandRegistry.put("myvisit", new MyVisitCommand(controller));
        commandRegistry.put("help", new HelpCommand(controller, CommandList.HELP));
    }

}
