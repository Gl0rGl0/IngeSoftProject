package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.CommandList;
import V5.Ingsoft.util.Payload;

public class ExitCommand extends AbstractCommand {
    Controller controller;

    public ExitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.EXIT;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        // chiudi risorse, log, ecc.
        controller.close();

        // prepari il Payload con la lambda
        Runnable afterExit = () -> System.exit(0);

        return Payload.info(afterExit,"ExitCommand: scheduled System.exit");
    }

}