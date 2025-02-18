package ingsoft.commands;

import ingsoft.commands.running.CommandList;
import ingsoft.util.ViewSE;

public class ExitCommand extends AbstractCommand {

    public ExitCommand() {
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        ViewSE.print("Programma terminato. Arrivederci!");
        System.exit(0);
    } 
}