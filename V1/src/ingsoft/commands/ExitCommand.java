package ingsoft.commands;

import ingsoft.util.ViewSE;

public class ExitCommand extends AbstractCommand {

    public ExitCommand(CommandList commandInfo) {
        super(null, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        ViewSE.log("Programma terminato. Arrivederci!");
        System.exit(0);
    } 
}