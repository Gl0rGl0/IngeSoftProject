package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

public class AvailabilityCommand extends AbstractCommand {


    public AvailabilityCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.AVAILABILITY;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller.getCurrentUser().getType() != PersonaType.VOLONTARIO)
            return;

        if (options.length < 1 || args.length < 1) {
            ViewSE.println("Error using the 'setav' command");
            AssertionControl.logMessage(
                    controller.getCurrentUser().getUsername() + "| Error using the 'setav' command", 2,
                    CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> manageDate(args, true);
            case 'r' -> manageDate(args, false);
            default -> {
                ViewSE.println("Error using the 'setav' command");
                AssertionControl.logMessage(
                        controller.getCurrentUser().getUsername() + "| Error using the 'setav' command", 2,
                        CLASSNAME);
            }
        }
    }

    private void manageDate(String[] args, boolean toAdd) {
        if (!controller.isVolunteerCollectionOpen()) {
            ViewSE.println("Can't edit your availability if the collection it's closed.");
            AssertionControl.logMessage("Attempt to edit availability while collection is closed", 3, CLASSNAME);
            return;
        }

        Volontario v = (Volontario) controller.getCurrentUser();
        for (String s : args) {
            try {
                Date d = new Date(s);
                if (controller.getDB().dbDatesHelper.getPrecludedDates().contains(d)){
                    ViewSE.println("The date " + d + " it's precluded by the admins, it won't be used.");
                    continue;
                }
                v.setAvailability(controller.date, d, toAdd); //controllo avviene dentro il volontario direttamente eccetto per le date precluse (richiesta)
            } catch (Exception e) {
                AssertionControl.logMessage("Error in the date inserted", 3, CLASSNAME);
            }
        }
    }

}
