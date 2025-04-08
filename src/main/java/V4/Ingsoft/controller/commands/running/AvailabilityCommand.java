package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

public class AvailabilityCommand extends AbstractCommand{

    private final Controller controller;

    public AvailabilityCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.AVAILABILITY;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if(options.length < 1 || args.length < 1){
            ViewSE.println("Error using the 'add' command");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Error using the 'add' command", 2,
                    CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> addDate(args);
            case 'r' -> removeDate(args);
            default -> {}
        }
    }

    private void addDate(String[] args){
        for (String s : args) {
            try {
                Date d = new Date(s);
            } catch (Exception e) {
                continue;
            }
        }
    }

    private void removeDate(String[] args){
        for (String s : args) {
            try {
                Date d = new Date(s);
            } catch (Exception e) {
                continue;
            }
        }
    }
    
}
