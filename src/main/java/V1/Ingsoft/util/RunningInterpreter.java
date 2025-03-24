package V1.Ingsoft.util;

import V1.Ingsoft.Controller.Controller;
import V1.Ingsoft.Controller.commands.HelpCommand;
import V1.Ingsoft.Controller.commands.running.AddCommand;
import V1.Ingsoft.Controller.commands.running.CommandList;
import V1.Ingsoft.Controller.commands.running.ListCommand;
import V1.Ingsoft.Controller.commands.running.LoginCommand;
import V1.Ingsoft.Controller.commands.running.MyVisitCommand;
import V1.Ingsoft.Controller.commands.running.PrecludeCommand;
import V1.Ingsoft.Controller.commands.running.RemoveCommand;

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
