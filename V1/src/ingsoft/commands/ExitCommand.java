package ingsoft.commands;

import ingsoft.ViewSE;
import ingsoft.commands.running.CommandList;

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