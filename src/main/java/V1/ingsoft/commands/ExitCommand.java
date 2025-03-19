package V1.ingsoft.commands;

import V1.ingsoft.App;
import V1.ingsoft.commands.running.CommandList;

public class ExitCommand extends AbstractCommand {
    App controller;

    public ExitCommand(App controller) {
        this.controller = controller;
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        controller.close();
    }
}