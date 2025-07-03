package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.item.persone.Iscrizione;
import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class VisitCommand extends AbstractCommand {

    private static final String CLASSNAME = VisitCommand.class.getSimpleName();

    public VisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.VISIT; // Supponendo che CommandList.VISIT sia una costante appropriata
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller == null) {
            System.err.println("FATAL: Controller is null in VisitCommand");
            return;
        }
        if (options == null || args == null) {
            AssertionControl.logMessage("Options or args array is null in VisitCommand.execute", 1, CLASSNAME);
            ViewSE.println("Internal error: Invalid command structure.");
            return;
        }
        if (options.length < 1 || options[0] == null || options[0].isEmpty()) {
            ViewSE.println("Error: Missing or invalid option for 'visit' command (-a or -r).");
            AssertionControl.logMessage("Missing or invalid option for 'visit' command.", 2, CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        if (option == 'a' && args.length < 3) {
            ViewSE.println("Usage: visit -a <visit_name> <visit_date> <quantity>");
            AssertionControl.logMessage("Insufficient arguments for 'visit -a'", 2, CLASSNAME);
            return;
        } else if (option == 'r' && args.length < 2) {
            ViewSE.println("Usage: visit -r <visit_name> <visit_date>");
            AssertionControl.logMessage("Insufficient arguments for 'visit -r'", 2, CLASSNAME);
            return;
        } else if (option == 'i' && args.length < 1) {
            ViewSE.println("Usage: visit -i <subscription_id>");
            AssertionControl.logMessage("Insufficient arguments for 'visit -i'", 2, CLASSNAME);
            return;
        }

        switch (option) {
            case 'a' -> addFruitoreToVisita(args);
            case 'r' -> removeFromVisita(args);
            case 'i' -> removeFromVisitaWithUID(args);
            default -> {
                ViewSE.println("Option '-" + option + "' not recognized for 'visit'. Use -a or -r.");
                AssertionControl.logMessage("Unrecognized option for 'visit': " + option, 2, CLASSNAME);
            }
        }
    }

    private void removeFromVisitaWithUID(String[] args) {
        if(args[0].isEmpty()){
            AssertionControl.logMessage("Error processing arguments.", 1, CLASSNAME);
            ViewSE.println("Can't unsubscribe from visit if the ID of the booking is empty.");
            return;
        }

        final String SUB_CLASSNAME = CLASSNAME + ".removeFromVisitaWithUID";
        String uidVisit = args[0];
        if (uidVisit == null){
            AssertionControl.logMessage("Error processing arguments.", 1, SUB_CLASSNAME);
            ViewSE.println("Can't unsubscribe from visit if the ID of the booking is empty.");
            return;
        }    

        Fruitore f = getCurrentFruitore(SUB_CLASSNAME);
        if (f == null) return;

        Iscrizione i = controller.getDB().dbIscrizionisHelper.getIscrizione(uidVisit);
        if(i == null){
            AssertionControl.logMessage("Error retriving iscrizione.", 1, SUB_CLASSNAME);
            ViewSE.println("No subscription found with that ID");
            return;
        }

        for(Visita v : controller.getDB().dbVisiteHelper.getVisite()){
            if(v.getStatus() == StatusVisita.COMPLETED || v.getStatus() == StatusVisita.PROPOSED)
                for(Iscrizione is : v.getIscrizioni()){
                    if(uidVisit.equals(is.getUIDIscrizione())){
                        ViewSE.println("Removed booking from the visit: " + v.getTitle());
                        AssertionControl.logMessage("Removed booking from the visit: " + v.getTitle(), 3, SUB_CLASSNAME);
                        
                        controller.getDB().unsubscribeUserToVisit(v, i);

                        controller.getDB().dbVisiteHelper.saveJson();
                        controller.getDB().dbFruitoreHelper.saveJson();
                        return;
                    }
                }
        }
    }

    // Validazione argomenti per il booking (add)
    private boolean validateArgsForAdd(String[] args, String subClassName) {
        String[] parsed = StringUtils.joinQuotedArguments(args);
        if (parsed == null || parsed.length < 3 ||
                parsed[0] == null || parsed[1] == null || parsed[2] == null) {
            AssertionControl.logMessage("Error processing arguments after joining.", 1, subClassName);
            ViewSE.println("Internal error processing visit details.");
            return false;
        }
        return true;
    }

    // Restituisce il fruitore corrente se valido, altrimenti segnala l'errore.
    private Fruitore getCurrentFruitore(String subClassName) {
        if (controller.getCurrentUser() == null ||
                controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Error: Only registered visitors (fruitori) can book visits.");
            String currentUser = (controller.getCurrentUser() != null)
                    ? controller.getCurrentUser().getUsername() : "UNKNOWN";
            AssertionControl.logMessage("Non-fruitore user (" + currentUser + ") attempted action.", 1, subClassName);
            return null;
        }
        return (Fruitore) controller.getCurrentUser();
    }

    // Cerca la visita e la restituisce, oppure segnala l'errore.
    private Visita findVisit(String visitName, String visitDateStr, String subClassName) {
        Visita v = controller.getDB().dbVisiteHelper.findVisita(visitName, visitDateStr);
        if (v == null) {
            ViewSE.println("Error: Visit '" + visitName + "' on date '" + visitDateStr + "' not found.");
            AssertionControl.logMessage("Visit not found: Name=" + visitName + ", Date=" + visitDateStr, 3, subClassName);
        }
        return v;
    }

    // Controlla se lo stato della visita permette di fare booking
    private boolean validateVisitState(Visita v, String subClassName) {
        StatusVisita currentStatus = v.getStatus();
        if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
            ViewSE.println("Error: Cannot register for this visit. It is currently " + currentStatus + ".");
            AssertionControl.logMessage("Attempted to book visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, subClassName);
            return false;
        }
        return true;
    }

    // Controlla se il booking è aperto, qui semplificato: se lo stato è PROPOSED si assume aperto.
    private boolean isBookingOpen(Visita v, String subClassName) {
        if (v.getStatus() == StatusVisita.PROPOSED) {
            return true;
        }
        ViewSE.println("Error: Bookings for this visit are closed (deadline passed).");
        AssertionControl.logMessage("Attempted to book visit after deadline (UID: " + v.getUID() + ")", 2, subClassName);
        return false;
    }

    // Validazione della quantità: restituisce un intero valido, oppure -1 in caso di errore.
    private int validateQuantity(String quantityStr, String subClassName) {
        try {
            int q = Integer.parseInt(quantityStr.replace(" ", ""));
            if (q <= 0) {
                ViewSE.println("Error: Quantity must be a positive number.");
                AssertionControl.logMessage("Invalid quantity provided: " + quantityStr, 2, subClassName);
                return -1;
            }
            return q;
        } catch (NumberFormatException e) {
            ViewSE.println("Error: Invalid quantity '" + quantityStr + "'. Please enter a number.");
            AssertionControl.logMessage("Numeric format exception for quantity: " + quantityStr, 2, subClassName);
            return -1;
        }
    }

    // Elabora il booking, invocando il metodo di Visita e gestendo il risultato
    private void processBooking(Visita v, Fruitore f, int quantita, String subClassName) {
        int maxPrenotazioni = Model.appSettings.getMaxPrenotazioniPerPersona();
        if (quantita > maxPrenotazioni) {
            ViewSE.println("Error: You cannot register more than " + maxPrenotazioni + " people in a single booking.");
            AssertionControl.logMessage("Maximum bookings per person exceeded (" + quantita + " > " + maxPrenotazioni + ")", 3, subClassName);
            return;
        }

        Iscrizione i = new Iscrizione(f.getUsername(), quantita);
        String bookingResult = v.addPartecipants(i);
        switch (bookingResult) {
            case "capacity":
                ViewSE.println("Error: Cannot register " + quantita + " people. Not enough capacity remaining for this visit.");
                AssertionControl.logMessage("Booking failed due to capacity limit (UID: " + v.getUID() + ")", 3, subClassName);
                break;
            case "present":
                ViewSE.println("Error: You are already registered for this visit.");
                AssertionControl.logMessage("Booking failed, user already registered (UID: " + v.getUID() + ")", 3, subClassName);
                break;
            default: // Successo: bookingResult è il codice di prenotazione
                ViewSE.println("Registration completed successfully! Your booking code is: " + bookingResult);
                AssertionControl.logMessage("Booking successful for user " + f.getUsername() + " (Code: " + bookingResult + ", Qty: " + quantita + ", Visit UID: " + v.getUID() + ")", 4, subClassName);
                f.subscribeToVisit(v.getUID());
                controller.getDB().dbIscrizionisHelper.addIscrizione(i);
                controller.getDB().dbVisiteHelper.saveJson();
                controller.getDB().dbFruitoreHelper.saveJson();
                break;
        }
    }

    // Metodo per aggiungere un fruitore a una visita (booking)
    public void addFruitoreToVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addFruitoreToVisita";
        if (!validateArgsForAdd(args, SUB_CLASSNAME))
            return;

        String[] parsedArgs = StringUtils.joinQuotedArguments(args);
        String visitName = parsedArgs[0];
        String visitDateStr = parsedArgs[1];
        String quantityStr = parsedArgs[2];

        Fruitore f = getCurrentFruitore(SUB_CLASSNAME);
        if (f == null) return;

        Visita v = findVisit(visitName, visitDateStr, SUB_CLASSNAME);
        if (v == null) return;

        if (!validateVisitState(v, SUB_CLASSNAME))
            return;
        if (!isBookingOpen(v, SUB_CLASSNAME))
            return;

        int quantita = validateQuantity(quantityStr, SUB_CLASSNAME);
        if (quantita == -1) return;

        processBooking(v, f, quantita, SUB_CLASSNAME);
    }

    // Metodo di utilità per rimuovere un fruitore da una visita
    public void removeFromVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".removeFromVisita";
        String[] parsedArgs = StringUtils.joinQuotedArguments(args);
        if (parsedArgs == null || parsedArgs.length < 2 ||
                parsedArgs[0] == null || parsedArgs[1] == null) {
            AssertionControl.logMessage("Error processing arguments after joining.", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error processing visit details for removal.");
            return;
        }
        String visitName = parsedArgs[0];
        String visitDateStr = parsedArgs[1];

        Fruitore f = getCurrentFruitore(SUB_CLASSNAME);
        if (f == null) return;

        Visita v = findVisit(visitName, visitDateStr, SUB_CLASSNAME);
        if (v == null) return;

        // Controlla lo stato della visita: deve essere PROPOSED o COMPLETED per poter rimuovere la prenotazione
        StatusVisita currentStatus = v.getStatus();
        if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
            ViewSE.println("Error: Cannot remove booking for this visit. It is currently " + currentStatus + ".");
            AssertionControl.logMessage("Attempted to remove booking from visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
            return;
        }

        if (!isBookingOpen(v, SUB_CLASSNAME))
            return;

        boolean removed = v.removePartecipant(f.getUsername());
        if (removed) {
            ViewSE.println("Booking for visit '" + visitName + "' on " + visitDateStr + " removed successfully.");
            AssertionControl.logMessage("Booking removed for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ")", 4, SUB_CLASSNAME);
            f.removeFromVisita(v.getUID());
            controller.getDB().dbVisiteHelper.saveJson();
            controller.getDB().dbFruitoreHelper.saveJson();
        } else {
            ViewSE.println("Error: Could not remove booking. Were you registered for this visit?");
            AssertionControl.logMessage("Failed to remove booking for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
        }
    }
}
