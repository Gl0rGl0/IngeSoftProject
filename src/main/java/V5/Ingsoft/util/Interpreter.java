package V5.Ingsoft.util;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AddCommand;
import V5.Ingsoft.controller.commands.AssignCommand;
import V5.Ingsoft.controller.commands.ChangePswCommand;
import V5.Ingsoft.controller.commands.Command;
import V5.Ingsoft.controller.commands.ExitCommand;
import V5.Ingsoft.controller.commands.ListInterface;
import V5.Ingsoft.controller.commands.LogoutCommand;
import V5.Ingsoft.controller.commands.SetPersoneMaxCommand;
import V5.Ingsoft.controller.commands.TimeCommand;
import V5.Ingsoft.controller.commands.running.CommandList;
import V5.Ingsoft.controller.commands.setup.CommandListSETUP;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Interpreter {
    private static final String MSG_NO_PROMPT     = "Error: no command provided.";
    private static final String LOG_NO_PROMPT     = "Interpreter: prompt null or blank";
    private static final String MSG_UNKNOWN       = " is not recognized as an internal command.";
    private static final String LOG_UNKNOWN       = "Interpreter: unknown command";

    protected HashMap<String, Command> commandRegistry = new HashMap<>();

    public Interpreter(Controller controller) {
        commandRegistry.put("add",      new AddCommand(controller));
        commandRegistry.put("time",     new TimeCommand(controller));
        commandRegistry.put("logout",   new LogoutCommand(controller));
        commandRegistry.put("changepsw",new ChangePswCommand(controller));
        commandRegistry.put("assign",   new AssignCommand(controller));
        commandRegistry.put("setmax",   new SetPersoneMaxCommand(controller, true));
        commandRegistry.put("exit",     new ExitCommand(controller));
    }

    /**
     * Parses and executes a prompt, returning a Payload.
     */
    public Payload interpret(String prompt, Persona currentUser) {
        // 1) Empty prompt
        if (prompt == null || prompt.isBlank()) {
            Payload p = Payload.warn(MSG_NO_PROMPT, LOG_NO_PROMPT);
            p.setCommand(null);
            return p;
        }

        // 2) Tokenize
        String[] tokens = prompt.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isBlank()) {
            Payload p = Payload.warn(MSG_NO_PROMPT, LOG_NO_PROMPT);
            p.setCommand(null);
            return p;
        }

        String cmdKey = tokens[0];
        List<String> opts = new ArrayList<>();
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            String t = tokens[i];
            if (t.startsWith("-") && t.length() > 1) opts.add(t.substring(1));
            else                                     args.add(t);
        }

        Command command = commandRegistry.get(cmdKey);
        if (command == null) {
            Payload p = Payload.error("\"" + cmdKey + "\"" + MSG_UNKNOWN, LOG_UNKNOWN);
            p.setCommand(null);
            return p;
        }

        // 3) Permission checks
        int userPri = currentUser.getType().getPriority();
        if (!command.canBeExecutedBy(userPri)) {
            Payload p = Payload.error(
                "You do not have permissions to execute '" + cmdKey + "'.",
                "Interpreter: permission denied for " + cmdKey
            );
            p.setCommand(command.getCommandInfo());
            return p;
        }
        if (currentUser.isNew() &&
            !command.canBeExecutedBy(PersonaType.CAMBIOPSW.getPriority())) {
            Payload p = Payload.warn(
                "You must change your password first via 'changepsw [new]'.",
                "Interpreter: new user blocked for " + cmdKey
            );
            p.setCommand(command.getCommandInfo());
            return p;
        }

        // 4) Execute
        String[] options = opts.toArray(String[]::new);
        String[] arguments = args.toArray(String[]::new);
        Payload out = command.execute(options, arguments);
        out.setCommand(command.getCommandInfo());
        return out;
    }

    /** Returns true if all non-setup commands have been executed. */
    public boolean haveAllBeenExecuted() {
        for (Command c : commandRegistry.values()) {
            if (c.getCommandInfo().equals(CommandListSETUP.DONE)) continue;
            if (!c.hasBeenExecuted()) return false;
        }
        return true;
    }

    /** For setup phase, checks that "done" was executed. */
    public boolean doneAll() {
        if (!(this instanceof SetupInterpreter)) return true;
        return haveAllBeenExecuted() && commandRegistry.get("done").hasBeenExecuted();
    }
}
