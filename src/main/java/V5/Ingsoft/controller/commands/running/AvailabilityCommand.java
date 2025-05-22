package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

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

        char opt = options[0].charAt(0);
        switch (opt) {
            case 'a': return manageDate(args, true);
            case 'r': return manageDate(args, false);
            default:
                return Payload.error(
                    "Option '-" + opt + "' not recognized for 'setav'",
                    "Unknown option '" + opt + "'");
        }
    }

    private Payload<String> manageDate(String[] args, boolean toAdd) {
        if (!controller.isVolunteerCollectionOpen())
            return Payload.warn(
                "Cannot edit availability while collection is closed.",
                "Collection closed.");

        Volontario v = (Volontario) controller.getCurrentUser();
        StringBuilder feedback = new StringBuilder();
        for (String s : args) {
            try {
                Date d = new Date(s);
                if (controller.getDB().dbDatesHelper.getPrecludedDates().contains(d)) {
                    feedback.append("Date ").append(d).append(" is precluded and was ignored\n");
                    continue;
                }
                v.setAvailability(controller.date, d, toAdd);
                feedback.append(toAdd ? "Added " : "Removed ")
                        .append(d)
                        .append("\n");
            } catch (Exception e) {
                feedback.append("Invalid date format: ").append(s).append("\n");
            }
        }
        String result = feedback.toString().trim();
        return Payload.info(
            result.isEmpty() ? "No changes applied" : result,
            "Managed availability for " + args.length + " date(s)");
    }
}
