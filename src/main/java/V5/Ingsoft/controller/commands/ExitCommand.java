package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.CommandList;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.view.ViewSE;

public class ExitCommand extends AbstractCommand {
    Controller controller;

    public ExitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        controller.close();
        ViewSE.println("Program terminated. Goodbye!");
        System.exit(0);

        return null; //It cant be here
    }
}