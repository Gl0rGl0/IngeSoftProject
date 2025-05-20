package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.CommandList;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.StatusItem;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;
import java.time.DayOfWeek;

public class AssignCommand extends AbstractCommand {

    public AssignCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ASSIGN;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (options == null || options.length < 1) {
            return Payload.error(
                "Usage: assign -V <VisitName> <VolunteerUsername> or -L <PlaceName> <VisitName>",
                "Missing option for AssignCommand");
        }
        String opt = options[0];
        String[] arg = StringUtils.joinQuotedArguments(args);
        if (arg == null || arg.length < 2) {
            if ("V".equals(opt)) {
                return Payload.warn(
                    "Usage: assign -V <VisitName> <VolunteerUsername>",
                    "Insufficient args for assign -V");
            } else if ("L".equals(opt)) {
                return Payload.warn(
                    "Usage: assign -L <PlaceName> <VisitName>",
                    "Insufficient args for assign -L");
            }
            return Payload.warn(
                "Unknown option '-" + opt + "'. Use -V or -L.",
                "Unknown option in AssignCommand: " + opt);
        }

        switch (opt) {
            case "V": {
                String visit = arg[0], volunteer = arg[1];
                Volontario v = controller.getDB().dbVolontarioHelper.getPersona(volunteer);
                if (v == null) {
                    return Payload.warn(
                        "No volunteer found with username: " + volunteer,
                        "Volunteer not found: " + volunteer);
                }
                TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita(visit);
                if (t == null) {
                    return Payload.warn(
                        "No visit type found with title: " + visit,
                        "TipoVisita not found: " + visit);
                }
                if (!isExecutable()) {
                    return Payload.error(
                        "Operation not permitted at this time.",
                        "AssignCommand not executable due to state");
                }
                if (v.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                        "Cannot assign: volunteer status is DISABLED.",
                        "Volunteer disabled: " + volunteer);
                }
                if (t.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                        "Cannot assign: visit type status is DISABLED.",
                        "Visit type disabled: " + visit);
                }
                if (!v.addTipoVisita(t.getUID())) {
                    return Payload.error(
                        "Cannot assign volunteer to visit.",
                        "Failed addTipoVisita for volunteer: " + volunteer);
                }
                if (!t.addVolontario(volunteer)) {
                    return Payload.error(
                        "Cannot assign volunteer to visit.",
                        "Failed addVolontario for volunteer: " + volunteer);
                }
                return Payload.info(
                    "Assigned volunteer " + volunteer + " to visit " + visit,
                    "Assigned in AssignCommand: volunteer " + volunteer + ", visit " + visit);
            }
            case "L": {
                String place = arg[0], visit  = arg[1];
                Luogo l = controller.getDB().getLuogoByName(place);
                if (l == null) {
                    return Payload.warn(
                        "No place found with name: " + place,
                        "Luogo not found: " + place);
                }
                TipoVisita t = controller.getDB().dbTipoVisiteHelper.findTipoVisita(visit);
                if (t == null) {
                    return Payload.warn(
                        "No visit type found with title: " + visit,
                        "TipoVisita not found: " + visit);
                }
                // if (!isExecutable()) {
                //     return Payload.error(
                //         "Operation not permitted at this time.",
                //         "AssignCommand not executable due to state");
                // }
                if (l.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                        "Cannot assign: place status is DISABLED.",
                        "Luogo disabled: " + place);
                }
                if (t.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                        "Cannot assign: visit type status is DISABLED.",
                        "Visit type disabled: " + visit);
                }
                for (DayOfWeek day : t.getDays()) {
                    if (!isAssignmentPlausibleOnDay(t, l, day)) {
                        return Payload.error(
                            "Cannot assign visit: time overlap detected.",
                            "Scheduling conflict for visit " + visit + " at place " + place);
                    }
                }
                if (!l.addTipoVisita(t.getUID())) {
                    return Payload.error(
                        "Cannot assign visit to place.",
                        "Failed addTipoVisita for place: " + place);
                }
    
                t.setLuogo(l.getUID());
                return Payload.info(
                    "Assigned visit " + visit + " to place " + place,
                    "Assigned in AssignCommand: visit " + visit + ", place " + place);
            }
            default:
                return Payload.warn(
                    "Unknown option '-" + opt + "'. Use -V or -L.",
                    "Unknown option in AssignCommand: " + opt);
        }
    }

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
                AssertionControl.logMessage("Overlapping with " + altraVisita.getTitle(), Payload.Status.WARN, getClassName());
                return false;
            }
        }
        return true;
    }
}
