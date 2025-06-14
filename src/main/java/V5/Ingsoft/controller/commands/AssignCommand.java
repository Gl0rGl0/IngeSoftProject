package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

import java.time.DayOfWeek;
import java.util.List;

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
                "Missing option for AssignCommand" );
        }
        String opt = options[0];
        String[] parsedArgs = StringUtils.joinQuotedArguments(args);
        if (parsedArgs == null || parsedArgs.length < 2) {
            if ("V".equals(opt)) {
                return Payload.warn(
                    "Usage: assign -V <VisitName> <VolunteerUsername>",
                    "Insufficient args for assign -V" );
            } else if ("L".equals(opt)) {
                return Payload.warn(
                    "Usage: assign -L <PlaceName> <VisitName>",
                    "Insufficient args for assign -L" );
            }
            return Payload.warn(
                "Unknown option '-" + opt + "'. Use -V or -L.",
                "Unknown option in AssignCommand: " + opt );
        }

        return switch (opt) {
            case "V" -> handleVolunteerAssignment(parsedArgs[0], parsedArgs[1]);
            case "L" -> handlePlaceAssignment(parsedArgs[0], parsedArgs[1]);
            default ->  Payload.warn(
                    "Unknown option '-" + opt + "'. Use -V or -L.",
                    "Unknown option in AssignCommand: " + opt );
        };
    }

    private Payload<String> handleVolunteerAssignment(String visitName, String volunteerUsername) {
        Model model = Model.getInstance();
        Volontario volunteer = model.dbVolontarioHelper.getPersona(volunteerUsername);
        if (volunteer == null) {
            return Payload.warn(
                "No volunteer found with username: " + volunteerUsername,
                "Volunteer not found: " + volunteerUsername );
        }
        TipoVisita tipoVisita = model.dbTipoVisiteHelper.findTipoVisita(visitName);
        if (tipoVisita == null) {
            return Payload.warn(
                "No visit type found with title: " + visitName,
                "TipoVisita not found: " + visitName );
        }
        if (volunteer.getStatus() == StatusItem.DISABLED) {
            return Payload.error(
                "Cannot assign: volunteer status is DISABLED.",
                "Volunteer disabled: " + volunteerUsername );
        }
        if (tipoVisita.getStatus() == StatusItem.DISABLED) {
            return Payload.error(
                "Cannot assign: visit type status is DISABLED.",
                "Visit type disabled: " + visitName );
        }
        boolean addedToVolunteer = volunteer.addTipoVisita(tipoVisita.getUID());
        if (!addedToVolunteer) {
            return Payload.error(
                "Cannot assign volunteer to visit.",
                "Failed addTipoVisita for volunteer: " + volunteerUsername );
        }
        boolean addedToVisit = tipoVisita.addVolontario(volunteerUsername);
        if (!addedToVisit) {
            return Payload.error(
                "Cannot assign volunteer to visit.",
                "Failed addVolontario for volunteer: " + volunteerUsername );
        }
        model.dbTipoVisiteHelper.saveDB();
        model.dbLuoghiHelper.saveDB();
        return Payload.info(
            "Assigned volunteer " + volunteerUsername + " to visit " + visitName,
            "Assigned in AssignCommand: volunteer " + volunteerUsername + ", visit " + visitName );
    }

    private Payload<String> handlePlaceAssignment(String placeName, String visitName) {
        Model model = Model.getInstance();
        Luogo luogo = model.getLuogoByName(placeName);
        if (luogo == null) {
            return Payload.warn(
                "No place found with name: " + placeName,
                "Luogo not found: " + placeName );
        }
        TipoVisita tipoVisita = model.dbTipoVisiteHelper.findTipoVisita(visitName);
        if (tipoVisita == null) {
            return Payload.warn(
                "No visit type found with title: " + visitName,
                "TipoVisita not found: " + visitName );
        }
        if (luogo.getStatus() == StatusItem.DISABLED) {
            return Payload.error(
                "Cannot assign: place status is DISABLED.",
                "Luogo disabled: " + placeName );
        }
        if (tipoVisita.getStatus() == StatusItem.DISABLED) {
            return Payload.error(
                "Cannot assign: visit type status is DISABLED.",
                "Visit type disabled: " + visitName );
        }
        for (DayOfWeek day : tipoVisita.getDays()) {
            if (!isAssignmentPlausibleOnDay(tipoVisita, luogo, day)) {
                return Payload.error(
                    "Cannot assign visit: time overlap detected.",
                    "Scheduling conflict for visit " + visitName + " at place " + placeName );
            }
        }
        boolean added = luogo.addTipoVisita(tipoVisita.getUID());
        if (!added) {
            return Payload.error(
                "Cannot assign visit to place.",
                "Failed addTipoVisita for place: " + placeName );
        }
        tipoVisita.setLuogo(luogo.getUID());
        model.dbTipoVisiteHelper.saveDB();
        model.dbLuoghiHelper.saveDB();
        return Payload.info(
            "Assigned visit " + visitName + " to place " + placeName,
            "Assigned in AssignCommand: visit " + visitName + ", place " + placeName );
    }

    private boolean isAssignmentPlausibleOnDay(TipoVisita visitaDaAssegnare, Luogo luogo, DayOfWeek day) {
        List<String> assignedUIDs = luogo.getTipoVisitaUID();
        for (String uidTipoVisita : assignedUIDs) {
            if (uidTipoVisita.equals(visitaDaAssegnare.getUID())) {
                continue;
            }
            TipoVisita altraVisita = Model.getInstance().dbTipoVisiteHelper.getItem(uidTipoVisita);
            if (!altraVisita.getDays().contains(day)) {
                continue;
            }
            if (visitaDaAssegnare.overlaps(altraVisita)) {
                AssertionControl.logMessage(
                    "Overlapping with " + altraVisita.getTitle(),
                    Payload.Status.WARN,
                    getClassName() );
                return false;
            }
        }
        return true;
    }
}
