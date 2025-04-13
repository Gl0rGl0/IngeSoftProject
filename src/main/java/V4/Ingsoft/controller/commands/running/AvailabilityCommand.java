package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;
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
        if(controller.getCurrentUser().getType() != PersonaType.VOLONTARIO)
            return;

        if(options.length < 1 || args.length < 1){
            ViewSE.println("Error using the 'setav' command");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Error using the 'setav' command", 2,
                    CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> manageDate(args, true);
            case 'r' -> manageDate(args, false);
            default -> {    ViewSE.println("Error using the 'setav' command");
                            AssertionControl.logMessage(
                                    controller.getCurrentUser().getUsername() + "| Error using the 'setav' command", 2,
                                    CLASSNAME); }
        }
    }

    private void manageDate(String[] args, boolean toAdd){
        Volontario v = (Volontario) controller.getCurrentUser();
        for (String s : args) {
            try {
                Date d = new Date(s);
                if(controller.db.dbDatesHelper.getPrecludedDates().contains(d))
                    continue;
                v.setAvailability(controller.date, d, toAdd); //controllo avviene dentro il volontario direttamente eccetto per le date precluse (richiesta)
            } catch (Exception e) {
                AssertionControl.logMessage("Error in the date inserted", 3, CLASSNAME);
                continue;
            }
        }
    }
    
}
