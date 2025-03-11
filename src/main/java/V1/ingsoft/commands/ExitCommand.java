package V1.ingsoft.commands;

import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.running.CommandList;

public class ExitCommand extends AbstractCommand {

    public ExitCommand() {
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        ViewSE.println("Programma terminato. Arrivederci!");
        System.exit(0);
    }
}