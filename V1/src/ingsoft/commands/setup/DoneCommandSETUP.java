package ingsoft.commands.setup;

import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class DoneCommandSETUP extends AbstractCommand {

    public DoneCommandSETUP() {
        super.commandInfo = CommandListSETUP.DONE;
        this.hasBeenExecuted = false;
    }

    @Override
    public void execute(String[] options, String[] args) {
        super.hasBeenExecuted = true;
        ViewSE.println("finitooooo");
    }
}