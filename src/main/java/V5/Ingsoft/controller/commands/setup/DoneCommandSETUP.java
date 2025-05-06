package V5.Ingsoft.controller.commands.setup;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.util.Payload;

public class DoneCommandSETUP extends AbstractCommand {

    public DoneCommandSETUP(Controller c) {
        super.commandInfo = CommandListSETUP.DONE;
        this.controller = c;
        this.hasBeenExecuted = false;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (controller.haveAllBeenExecuted()) {
            this.hasBeenExecuted = true;
            controller.switchInterpreter();
            return Payload.info(
                "Setup complete, switching to running mode",
                "All setup commands executed successfully");
        }
        return Payload.warn(
            "Cannot complete setup: not all commands executed",
            "DoneCommandSETUP invoked prematurely");
    }
}