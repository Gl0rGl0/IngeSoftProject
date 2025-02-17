package ingsoft.commands.setup;

import ingsoft.commands.AbstractCommand;

public class DoneCommandSETUP extends AbstractCommand {

    public DoneCommandSETUP() {
        super.commandInfo = CommandListSETUP.DONE;
    }

    @Override
    public void execute(String[] options, String[] args) {
        super.hasBeenExecuted = true;
    }
}