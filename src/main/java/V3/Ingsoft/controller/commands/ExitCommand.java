package V3.Ingsoft.controller.commands;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.controller.commands.running.CommandList;

public class ExitCommand extends AbstractCommand {
    Controller controller;

    public ExitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        controller.close();
    }
}