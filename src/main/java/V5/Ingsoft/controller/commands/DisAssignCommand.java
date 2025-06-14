package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

public class DisAssignCommand extends AbstractCommand {

    public DisAssignCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.DISASSIGN;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        if (options == null || options.length < 1) {
            return Payload.error(
                    "Usage: disassign -V <VisitName> <VolunteerUsername> or -L <PlaceName> <VisitName>",
                    "Missing option for AssignCommand");
        }
        String opt = options[0];
        String[] arg = StringUtils.joinQuotedArguments(args);
        if (arg == null || arg.length < 2) {
            if ("V".equals(opt)) {
                return Payload.warn(
                        "Usage: disassign -V <VisitName> <VolunteerUsername>",
                        "Insufficient args for disassign -V");
            } else if ("L".equals(opt)) {
                return Payload.warn(
                        "Usage: disassign -L <PlaceName> <VisitName>",
                        "Insufficient args for disassign -L");
            }
            return Payload.warn(
                    "Unknown option '-" + opt + "'. Use -V or -L.",
                    "Unknown option in DisAssignCommand: " + opt);
        }

        switch (opt) {
            case "V": {
                String visit = arg[0], volunteer = arg[1];
                Volontario v = Model.getInstance().dbVolontarioHelper.getPersona(volunteer);
                if (v == null) {
                    return Payload.warn(
                            "No volunteer found with username: " + volunteer,
                            "Volunteer not found: " + volunteer);
                }
                TipoVisita t = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(visit);
                if (t == null) {
                    return Payload.warn(
                            "No visit type found with title: " + visit,
                            "TipoVisita not found: " + visit);
                }
                if (v.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                            "Cannot deassign: volunteer status is DISABLED.",
                            "Volunteer disabled: " + volunteer);
                }
                if (t.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                            "Cannot deassign: visit type status is DISABLED.",
                            "Visit type disabled: " + visit);
                }
                if (!v.removeTipoVisita(t.getUID())) {
                    return Payload.error(
                            "Cannot deassign volunteer to visit.",
                            "Failed addTipoVisita for volunteer: " + volunteer);
                }
                if (!t.removeVolontario(volunteer)) {
                    return Payload.error(
                            "Cannot disassign volunteer to visit.",
                            "Failed addVolontario for volunteer: " + volunteer);
                }
                return Payload.info(
                        "Disassigned volunteer " + volunteer + " to visit " + visit,
                        "Disassigned in DisAssignCommand: volunteer " + volunteer + ", visit " + visit);
            }
            case "L": {
                String place = arg[0], visit = arg[1];
                Luogo l = Model.getInstance().getLuogoByName(place);
                if (l == null) {
                    return Payload.warn(
                            "No place found with name: " + place,
                            "Luogo not found: " + place);
                }
                TipoVisita t = Model.getInstance().dbTipoVisiteHelper.findTipoVisita(visit);
                if (t == null) {
                    return Payload.warn(
                            "No visit type found with title: " + visit,
                            "TipoVisita not found: " + visit);
                }
                // if (!isExecutable()) {
                //     return Payload.error(
                //             "Operation not permitted at this time.",
                //             "AssignCommand not executable due to state");
                // }
                if (l.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                            "Cannot disassign: place status is DISABLED.",
                            "Luogo disabled: " + place);
                }
                if (t.getStatus() == StatusItem.DISABLED) {
                    return Payload.error(
                            "Cannot disassign: visit type status is DISABLED.",
                            "Visit type disabled: " + visit);
                }
                if (!l.addTipoVisita(t.getUID())) {
                    return Payload.error(
                            "Cannot disassign visit to place.",
                            "Failed addTipoVisita for place: " + place);
                }

                t.setLuogo("null");
                t.setStatus(StatusItem.DISABLED);

                return Payload.info(
                        "Disassigned visit " + visit + " to place " + place,
                        "Disassigned in DisAssignCommand: visit " + visit + ", place " + place);
            }
            default:
                return Payload.warn(
                        "Unknown option '-" + opt + "'. Use -V or -L.",
                        "Unknown option in AssignCommand: " + opt);
        }
    }
}
