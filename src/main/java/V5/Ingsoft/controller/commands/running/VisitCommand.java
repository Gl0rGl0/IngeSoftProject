package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.statuses.StatusVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

import java.util.Objects;

public class VisitCommand extends AbstractCommand {

    public VisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.VISIT;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (options == null || args == null) {
            return Payload.error(
                    "Internal error: invalid command structure.",
                    "Options or args are null in VisitCommand");
        }
        if (options.length < 1 || options[0].isBlank()) {
            return Payload.warn(
                    "Missing or invalid option for 'visit' command (-a, -r or -i).",
                    "Missing or invalid option in VisitCommand");
        }

        char opt = options[0].charAt(0);
        String[] parsed = StringUtils.joinQuotedArguments(args);
        if (parsed == null) {
            return Payload.error(
                    "Internal error processing arguments.",
                    "JoinQuotedArguments returned null in VisitCommand");
        }

        switch (opt) {
            case 'a': {
                // add: visit -a <visit_name> <visit_date> <quantity>
                if (parsed.length < 3) {
                    return Payload.warn(
                            "Usage: visit -a <visit_name> <visit_date> <quantity>",
                            "Insufficient args for visit -a");
                }
                String visitName = parsed[0];
                String visitDate = parsed[1];
                String qtyStr = parsed[2];

                // only fruitori can book
                if (controller.getCurrentUser() == null
                        || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
                    return Payload.error(
                            "Only registered visitors can book visits.",
                            "Non-fruitore attempted booking in VisitCommand");
                }
                Fruitore f = (Fruitore) controller.getCurrentUser();

                Visita v = Model.getInstance().dbVisiteHelper.findVisita(visitName, visitDate);
                if (v == null) {
                    return Payload.warn(
                            "Visit '" + visitName + "' on " + visitDate + " not found.",
                            "Visit not found in VisitCommand");
                }
                StatusVisita status = v.getStatus();
                if (status != StatusVisita.PROPOSED && status != StatusVisita.COMPLETED) {
                    return Payload.warn(
                            "Cannot register for this visit; status is " + status + ".",
                            "Invalid visit status in VisitCommand");
                }
                if (status != StatusVisita.PROPOSED) {
                    return Payload.warn(
                            "Bookings for this visit are closed.",
                            "Booking closed in VisitCommand");
                }
                int qty;
                try {
                    qty = Integer.parseInt(qtyStr.trim());
                    if (qty <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    return Payload.warn(
                            "Invalid quantity '" + qtyStr + "'. Must be a positive integer.",
                            "Invalid quantity in VisitCommand");
                }
                int max = Objects.requireNonNull(Model.getInstance().appSettings).getMaxPrenotazioniPerPersona();
                if (qty > max) {
                    return Payload.warn(
                            "Cannot register more than " + max + " per booking.",
                            "Exceeded max booking in VisitCommand");
                }

                Iscrizione i;
                try {
                    i = new Iscrizione(v.getUID(), f.getUsername(), qty);
                } catch (Exception e) {
                    return Payload.error(
                            "Registration successful! Your booking code is",
                            "Booking successful in VisitCommand");
                }

                String result = v.addPartecipants(i);
                if ("capacity".equals(result)) {
                    return Payload.warn(
                            "Not enough capacity remaining for " + qty + " people.",
                            "Capacity limit in VisitCommand");
                } else if ("present".equals(result)) {
                    return Payload.warn(
                            "You are already registered for this visit.",
                            "Already registered in VisitCommand");
                } else {
                    f.subscribeToVisit(v.getUID());
                    Model.getInstance().dbIscrizionisHelper.addItem(i);
                    Model.getInstance().dbVisiteHelper.saveDB();
                    Model.getInstance().dbFruitoreHelper.saveDB();
                    this.hasBeenExecuted = true;
                    return Payload.info(
                            "Registration successful! Your booking code is: " + result,
                            "Booking successful in VisitCommand");
                }
            }
            case 'r': {
                // remove by name/date: visit -r <visit_name> <visit_date>
                if (parsed.length < 2) {
                    return Payload.warn(
                            "Usage: visit -r <visit_name> <visit_date>",
                            "Insufficient args for visit -r"
                    );
                }
                String visitName = parsed[0];
                String visitDate = parsed[1];

                if (controller.getCurrentUser() == null
                        || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
                    return Payload.error(
                            "Only registered visitors can cancel bookings.",
                            "Non-fruitore attempted cancellation in VisitCommand"
                    );
                }
                Fruitore f = (Fruitore) controller.getCurrentUser();

                Visita v = Model.getInstance().dbVisiteHelper.findVisita(visitName, visitDate);
                if (v == null) {
                    return Payload.warn(
                            "Visit '" + visitName + "' on " + visitDate + " not found.",
                            "Visit not found in VisitCommand");
                }
                StatusVisita status = v.getStatus();
                if (status != StatusVisita.PROPOSED && status != StatusVisita.COMPLETED) {
                    return Payload.warn(
                            "Cannot remove booking; visit status is " + status + ".",
                            "Invalid visit status in VisitCommand"
                    );
                }
                if (v.removePartecipant(f.getUsername())) {
                    f.removeFromVisita(v.getUID());
                    Model.getInstance().dbVisiteHelper.saveDB();
                    Model.getInstance().dbFruitoreHelper.saveDB();
                    this.hasBeenExecuted = true;
                    return Payload.info(
                            "Booking for '" + visitName + "' on " + visitDate + " removed successfully.",
                            "Cancellation successful in VisitCommand"
                    );
                } else {
                    return Payload.warn(
                            "You were not registered for this visit.",
                            "Cancellation failed in VisitCommand"
                    );
                }
            }
            case 'i': {
                // remove by subscription ID: visit -i <subscription_id>
                if (parsed.length < 1) {
                    return Payload.warn(
                            "Usage: visit -i <subscription_id>",
                            "Insufficient args for visit -i"
                    );
                }
                String subId = parsed[0].trim();
                if (subId.isEmpty()) {
                    return Payload.error(
                            "Subscription ID cannot be empty.",
                            "Empty subscription ID in VisitCommand"
                    );
                }
                if (controller.getCurrentUser() == null
                        || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
                    return Payload.error(
                            "Only registered visitors can cancel bookings.",
                            "Non-fruitore attempted cancellation by ID in VisitCommand"
                    );
                }
                Fruitore f = (Fruitore) controller.getCurrentUser();

                Iscrizione i = Model.getInstance().dbIscrizionisHelper.getItem(subId);
                if (i == null) {
                    return Payload.warn(
                            "No booking found with ID '" + subId + "'.",
                            "Subscription not found in VisitCommand");
                }

                if (!i.getUIDFruitore().equals(f.getUsername())) {
                    return Payload.error(
                            "The subscription doesn't match the current user",
                            "Subscription not found in VisitCommand");
                }

                for (Visita v : Model.getInstance().dbVisiteHelper.getItems()) {
                    if ((v.getStatus() == StatusVisita.PROPOSED || v.getStatus() == StatusVisita.COMPLETED)
                            && v.getIscrizioni().stream()
                            .anyMatch(is -> subId.equals(is.getUID()))) {
                        Model.getInstance().unsubscribeUserToVisit(v, i);
                        Model.getInstance().dbVisiteHelper.saveDB();
                        Model.getInstance().dbFruitoreHelper.saveDB();
                        this.hasBeenExecuted = true;
                        return Payload.info(
                                "Booking ID '" + subId + "' removed from visit '" + v.getTitle() + "'.",
                                "Unsubscribed by ID in VisitCommand"
                        );
                    }
                }
                return Payload.warn(
                        "Booking ID '" + subId + "' not linked to any active visit.",
                        "Unsubscription by ID failed in VisitCommand");
            }
            default:
                return Payload.warn(
                        "Unknown option '-" + opt + "' for 'visit' command.",
                        "Unrecognized option in VisitCommand: " + opt);
        }
    }
}
