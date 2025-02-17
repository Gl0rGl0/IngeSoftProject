package ingsoft.commands.running;

import ingsoft.commands.AbstractCommand;
import ingsoft.util.ViewSE;

public class ExitCommand extends AbstractCommand {

    public ExitCommand(CommandList commandInfo) {
        super(null, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        ViewSE.print("Programma terminato. Arrivederci!");
        System.exit(0);
    } 
}