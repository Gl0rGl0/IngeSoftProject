package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.setup.CommandListSETUP;
import V4.Ingsoft.controller.item.StatusItem;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

import java.time.DayOfWeek;

public class AssignCommand extends AbstractCommand {
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
            case "V" -> assignVolontario(arg[0], arg[1]);
            case "L" -> assignVisita(arg[0], arg[1]);
            default -> ViewSE.error("Error using the 'assign' command"); // Cannot reach here
        }
    }

    private void assignVolontario(String type, String userNameVolontario) {
        Volontario v = controller.getDB().dbVolontarioHelper.getPersona(userNameVolontario);
        TipoVisita vToAssign = controller.getDB().dbTipoVisiteHelper.findTipoVisita(type);

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

        if (!isExecutable())
            return;

        if (!v.addTipoVisita(vToAssign.getUID())) {
            AssertionControl.logMessage("Can't assign volunteer " + userNameVolontario + " to visit " + type, 2, CLASSNAME);
            return;
        }

        StatusItem sp = v.getStatus();
        if (sp == StatusItem.PENDING_ADD || sp == StatusItem.DISABLED) {
            AssertionControl.logMessage("Can't assign volunteer " + userNameVolontario + " to visit " + type + " because volunteer it's in status: " + sp, 2, CLASSNAME);
            return;
        }

        StatusItem st = vToAssign.getStatus();
        if (st == StatusItem.PENDING_ADD || st == StatusItem.DISABLED) {
            AssertionControl.logMessage("Can't assign volunteer " + userNameVolontario + " to visit " + type + " because visit it's in status: " + st, 2, CLASSNAME);
            return;
        }

        if (!vToAssign.addVolontario(userNameVolontario)) {
            AssertionControl.logMessage("Can't assign volunteer " + userNameVolontario + " to visit " + type, 2, CLASSNAME);
            return;
        }

        ViewSE.println("Assigned volunteer " + userNameVolontario + " to visit " + type);
        AssertionControl.logMessage("Assigned volunteer " + userNameVolontario + " to visit " + type, 3, CLASSNAME);

    }

    private void assignVisita(String luogoName, String typeTitle) {
        Luogo luogo = controller.getDB().getLuogoByName(luogoName);
        TipoVisita visitaDaAssegnare = controller.getDB().dbTipoVisiteHelper.findTipoVisita(typeTitle);

        if (luogo == null) {
            ViewSE.println("No place found with that name.");
            return;
        }
        if (visitaDaAssegnare == null) {
            ViewSE.println("No visit found with that title.");
            return;
        }

        if (!isExecutable())
            return;

        StatusItem sl = luogo.getStatus();
        if (sl == StatusItem.PENDING_ADD || sl == StatusItem.DISABLED) {
            AssertionControl.logMessage("Can't assign visit " + typeTitle + " to place " + luogoName + " because visit it's in status: " + sl, 2, CLASSNAME);
            return;
        }

        StatusItem st = visitaDaAssegnare.getStatus();
        if (st == StatusItem.PENDING_ADD || st == StatusItem.DISABLED) {
            AssertionControl.logMessage("Can't assign visit " + typeTitle + " to place " + luogoName + " because place it's in status: " + st, 2, CLASSNAME);
            return;
        }

        // Check, for each day the visit can take place, that there are no conflicts
        boolean plausible = true; // Plausible days
        for (DayOfWeek day : visitaDaAssegnare.getDays()) {
            if (!isAssignmentPlausibleOnDay(visitaDaAssegnare, luogo, day)) {
                plausible = false;
                break;
            }
        }

        if (plausible) {
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
            // Se è la stessa visita, la saltiamo
            if (uidTipoVisita.equals(visitaDaAssegnare.getUID()))
                continue;

            TipoVisita altraVisita = controller.getDB().dbTipoVisiteHelper.getTipiVisitaByUID(uidTipoVisita);
            // Se l'altra visita non è programmata per questo giorno, continuiamo
            if (!altraVisita.getDays().contains(day))
                continue;

            // Sfrutta il metodo overlaps definito in TipoVisita
            if (visitaDaAssegnare.overlaps(altraVisita)) {
                AssertionControl.logMessage("Overlapping with " + altraVisita.getTitle(), 3, CLASSNAME);
                return false;
            }
        }
        return true;
    }
}
