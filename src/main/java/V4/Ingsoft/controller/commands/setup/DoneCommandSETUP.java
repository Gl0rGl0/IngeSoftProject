package V4.Ingsoft.controller.commands.setup;

import V4.Ingsoft.controller.commands.AbstractCommand;

public class DoneCommandSETUP extends AbstractCommand {

    public DoneCommandSETUP() {
        super.commandInfo = CommandListSETUP.DONE;
        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        super.hasBeenExecuted = true;
    }
}