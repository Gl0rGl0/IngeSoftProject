package V1.ingsoft.commands.setup;

import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;

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