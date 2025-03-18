package V1.ingsoft.util;

import V1.ingsoft.App;
import V1.ingsoft.commands.HelpCommand;
import V1.ingsoft.commands.running.AddCommand;
import V1.ingsoft.commands.running.CommandList;
import V1.ingsoft.commands.running.ListCommand;
import V1.ingsoft.commands.running.LoginCommand;
import V1.ingsoft.commands.running.MyVisitCommand;
import V1.ingsoft.commands.running.PrecludeCommand;
import V1.ingsoft.commands.running.RemoveCommand;

public class RunningInterpreter extends Interpreter {

    public RunningInterpreter(App controller) {
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
