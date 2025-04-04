package V4.Ingsoft.controller.commands;

import java.time.DayOfWeek;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AssignCommand extends AbstractCommand {

    private final Controller controller;

    public AssignCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandListSETUP.ASSIGN; // CommandList.ASSIGN when I feel like writing it
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.error("Error using the 'assign' command");
            return;
        }

        String[] arg = StringUtils.joinQuotedArguments(args);

        if (arg.length < 2) {
            if ("V".equals(options[0])) {
                ViewSE.error("Error using the 'assign' command: assign -V VisitName VolunteerUsername");
                AssertionControl.logMessage("Error using the 'assign' command: assign -V VisitName VolunteerUsername", 2, CLASSNAME);
            } else {
                ViewSE.error("Error using the 'assign' command: assign -L PlaceName VisitName");
                AssertionControl.logMessage("Error using the 'assign' command: assign -L PlaceName VisitName", 2, CLASSNAME);
            }
            return;
        }
        switch (options[0]) {
            case "V" -> {
                if (controller.canExecute16thAction)
                    assignVolontario(arg[0], arg[1]);
                else {
                    ViewSE.error("Action only possible on the 16th of the month!");
                    AssertionControl.logMessage("Action only possible on the 16th of the month!", 3, CLASSNAME);
                }
            }
            case "L" -> {
                if (controller.canExecute16thAction)
                    assignVisita(arg[0], arg[1]);
                else{
                    ViewSE.error("Action only possible on the 16th of the month!");
                    AssertionControl.logMessage("Action only possible on the 16th of the month!", 3, CLASSNAME);
                }
            }
            default ->
                ViewSE.error("Error using the 'assign' command"); // Cannot reach here
        }
    }

    private void assignVolontario(String type, String userNameVolontario) {
        Volontario v = controller.db.findVolontario(userNameVolontario);
        TipoVisita vToAssign = controller.db.dbTipoVisiteHelper.findTipoVisita(type);

        if (v == null) {
            ViewSE.error("No volunteer found with that username.");
            AssertionControl.logMessage("No volunteer found with that username.", 3, CLASSNAME);
            return;
        }

        if (vToAssign == null) {
            ViewSE.error("No type found with that title.");
            AssertionControl.logMessage("No type found with that title.", 3, CLASSNAME);
            return;
        }

        v.addTipoVisita(vToAssign.getUID());
        vToAssign.addVolontario(v.getUsername());
        ViewSE.println("Assigned volunteer " + v.getUsername() + " to visit " + vToAssign.getTitle());
        AssertionControl.logMessage("Assigned volunteer " + v.getUsername() + " to visit " + vToAssign.getTitle(), 3, CLASSNAME);
        
    }

    private void assignVisita(String name, String type) {
        Luogo luogo = controller.db.getLuogoByName(name);
        TipoVisita visitaDaAssegnare = controller.db.getTipoVisitaByName(type);

        if (luogo == null) {
            ViewSE.println("No place found with that name.");
            return;
        }
        if (visitaDaAssegnare == null) {
            ViewSE.println("No visit found with that title.");
            return;
        }

        // Check, for each day the visit can take place, that there are no conflicts
        boolean giorniPlausibili = true; // Plausible days
        for (DayOfWeek day : visitaDaAssegnare.getDays()) {
            if (!isAssignmentPlausibleOnDay(visitaDaAssegnare, luogo, day)) {
                giorniPlausibili = false;
                break;
            }
        }

        if (giorniPlausibili) {
            luogo.addTipoVisita(visitaDaAssegnare.getUID());
            visitaDaAssegnare.setLuogo(luogo.getUID());
            ViewSE.println("Assigned visit " + visitaDaAssegnare.getTitle() + " to place " + luogo.getName());
        } else {
            AssertionControl.logMessage("Cannot assign this visit because it overlaps with another one",
                    2, CLASSNAME);
        }
    }

    /**
     * Checks that the visit to be assigned does not overlap with others already assigned
     * to the place for the specified day.
     */
    private boolean isAssignmentPlausibleOnDay(TipoVisita visitaDaAssegnare, Luogo luogo, DayOfWeek day) {
        for (String uidTipoVisita : luogo.getTipoVisitaUID()) {
            // If it's the same visit, skip it
            if (uidTipoVisita.equals(visitaDaAssegnare.getUID()))
                continue;

            TipoVisita altraVisita = controller.db.dbTipoVisiteHelper.getTipiVisitaByUID(uidTipoVisita); // otherVisit
            // If the other visit is not scheduled for this day, continue
            if (!altraVisita.getDays().contains(day))
                continue;

            int startAltra = altraVisita.getInitTime().getMinutes(); // startOther
            int startDaAssegnare = visitaDaAssegnare.getInitTime().getMinutes(); // startToAssign

            // Check that the times do not overlap.
            // If they are not in conflict, one starts after the end of the other.
            if (!(startAltra > (startDaAssegnare + visitaDaAssegnare.getDuration())
                    || startDaAssegnare > (startAltra + altraVisita.getDuration()))) {
                AssertionControl.logMessage("Overlapping with " + altraVisita.getTitle(), // "I overlap with"
                        3, CLASSNAME);
                return false;
            }
        }
        return true;
    }

}
