package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class AvailabilityCommand extends AbstractCommand {

    public AvailabilityCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.AVAILABILITY;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (controller.getCurrentUser().getType() != PersonaType.VOLONTARIO)
            return Payload.error(
                    "Only volunteers can set availability",
                    "Non-volunteer attempted availability change");

        if (options == null || options.length < 1 || args == null || args.length < 1)
            return Payload.error(
                    "Usage: setav -<a|r> <date1> [date2 ...]",
                    "Missing options or arguments");
        
        if (!controller.isVolunteerCollectionOpen())
            return Payload.warn(
                    "Cannot edit availability while collection is closed.",
                    "Collection closed.");

        char opt = options[0].charAt(0);
        return switch (opt) {
            case 'a' -> manageDate(args, true);
            case 'r' -> manageDate(args, false);
            case 'b' -> manageBulk(args);
            default -> Payload.error(
                    "Option '-" + opt + "' not recognized for 'setav'",
                    "Unknown option '" + opt + "'");
        };
    }

    private Payload<String> manageBulk(String[] args) {
        ((Volontario) controller.getCurrentUser()).clearAvailability();
        return manageDate(args, true);
    }

    private Payload<String> manageDate(String[] args, boolean toAdd) {
        Volontario v = (Volontario) controller.getCurrentUser();
        StringBuilder feedback = new StringBuilder();
        Model m = Model.getInstance();
        Status status;
        for (String s : args) {
            try {
                Date d = new Date(s);
                if (m.dbDatesHelper.getItems().contains(d)) {
                    feedback.append("Date ").append(d).append(" is precluded and was ignored\n");
                    continue;
                }
                status = v.setAvailability(controller.date, d, toAdd);
                if(status == Status.ERROR){
                    feedback.append("OutOfRange").append(d).append("\n");
                }else if(status == Status.INFO){
                    feedback.append(toAdd ? "Added " : "Removed ")
                    .append(d)
                    .append("\n");
                }
            } catch (Exception e) {
                feedback.append("Invalid date format: ").append(s).append("\n");
            }
        }
        String result = feedback.toString().trim();

        if(result.isEmpty()){
            return Payload.info(
                    "No changes applied",
                    "Managed availability for " + args.length + " date(s)");
        }else{
            System.out.println( Model.getInstance().dbVolontarioHelper.saveDB() );
            return Payload.info(
                    result,
                    "Managed availability for " + args.length + " date(s)");
        }
    }
}
