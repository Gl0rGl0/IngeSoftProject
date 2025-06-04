package V5.Ingsoft.util.interpreter;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.*;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class Interpreter {
    private static final String MSG_NO_PROMPT = "Error: no command provided.";
    private static final String LOG_NO_PROMPT = "Interpreter: prompt null or blank";
    private static final String MSG_UNKNOWN = " is not recognized as an internal command.";
    private static final String LOG_UNKNOWN = "Interpreter: unknown command";

    protected HashMap<String, Command> commandRegistry = new HashMap<>();

    public Interpreter(Controller controller) {
        commandRegistry.put("add", new AddCommand(controller));
        commandRegistry.put("time", new TimeCommand(controller));
        commandRegistry.put("logout", new LogoutCommand(controller));
        commandRegistry.put("changepsw", new ChangePswCommand(controller));
        commandRegistry.put("assign", new AssignCommand(controller));
        commandRegistry.put("disassign", new DisAssignCommand(controller));
        commandRegistry.put("setmax", new SetPersoneMaxCommand(controller, true));
        commandRegistry.put("exit", new ExitCommand(controller));
    }

    /**
     * Parses and executes a prompt, returning a Payload.
     */
    public final Payload<?> interpret(String prompt, Persona currentUser) {
        // 1) Empty prompt
        if (prompt == null || prompt.isBlank()) {
            Payload<String> p = Payload.warn(MSG_NO_PROMPT, LOG_NO_PROMPT);
            p.setCommand(null);
            return p;
        }

        // 2) Tokenize
        String[] tokens = prompt.trim().split("\\s+");
        if (tokens.length == 0 || tokens[0].isBlank()) {
            Payload<String> p = Payload.warn(MSG_NO_PROMPT, LOG_NO_PROMPT);
            p.setCommand(null);
            return p;
        }

        String cmdKey = tokens[0];
        List<String> opts = new ArrayList<>();
        List<String> args = new ArrayList<>();
        for (int i = 1; i < tokens.length; i++) {
            String t = tokens[i];
            if (t.startsWith("-") && t.length() > 1) opts.add(t.substring(1));
            else args.add(t);
        }

        Command command = commandRegistry.get(cmdKey);
        if (command == null) {
            Payload<String> p = Payload.error("\"" + cmdKey + "\"" + MSG_UNKNOWN, LOG_UNKNOWN);
            p.setCommand(null);
            return p;
        }

        // 3) Permission checks
        int userPri = currentUser.getType().getPriority();
        if (!command.canBeExecutedBy(userPri)) {
            Payload<String> p = Payload.error(
                    "You do not have permissions to execute '" + cmdKey + "'.",
                    "Interpreter: permission denied for " + cmdKey
            );
            p.setCommand(command.getCommandInfo());
            return p;
        }
        if (currentUser.isNew() &&
                !command.canBeExecutedBy(PersonaType.CAMBIOPSW.getPriority())) {
            Payload<String> p = Payload.warn(
                    "You must change your password first via 'changepsw [new]'.",
                    "Interpreter: new user blocked for " + cmdKey
            );
            p.setCommand(command.getCommandInfo());
            return p;
        }

        // 4) Execute
        String[] options = opts.toArray(String[]::new);
        String[] arguments = args.toArray(String[]::new);
        Payload<?> out = command.execute(options, arguments);
        out.setCommand(command.getCommandInfo());

        if (out.getLogMessage() != null) {
            AssertionControl.logMessage(out.getLogMessage(), out.getStatus(), command.getClassName());
        }

        return out;
    }
    
    /**
     * Returns true if all non-setup commands have been executed.
     */
    public boolean hasExecutedAllCommands() {
        return true;
    }

    /**
     * For setup phase, checks that "done" was executed.
     */
    public boolean isSetupCompleted() {
        return true;
    }
}
