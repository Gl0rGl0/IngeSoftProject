package V1.ingsoft.util;

import V1.ingsoft.App;
import V1.ingsoft.commands.HelpCommand;
import V1.ingsoft.commands.setup.AddCommandSETUP;
import V1.ingsoft.commands.setup.CommandListSETUP;
import V1.ingsoft.commands.setup.DoneCommandSETUP;
import V1.ingsoft.commands.setup.LoginCommandSETUP;
import V1.ingsoft.commands.setup.SetAmbitoCommandSETUP;

public class SetupInterpreter extends Interpreter {

    public SetupInterpreter(App controller) {
        super(controller);
        commandRegistry.put("add", new AddCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("done", new DoneCommandSETUP());// DA ESEGUIRE
        commandRegistry.put("login", new LoginCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("setambito", new SetAmbitoCommandSETUP(controller)); // DA ESEGUIRE
        commandRegistry.put("help", new HelpCommand(controller, CommandListSETUP.HELP));
    }

}
