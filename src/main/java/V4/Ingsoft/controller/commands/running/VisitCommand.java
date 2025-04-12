package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.luoghi.StatusVisita; // Added import
import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.Date; // Added import for Date
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class VisitCommand extends AbstractCommand {
    private final Controller controller;
    private static final String CLASSNAME = VisitCommand.class.getSimpleName(); // Added for logging

    public VisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.VISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        // Basic null checks
        if (controller == null) {
            System.err.println("FATAL: Controller is null in VisitCommand");
            return;
        }
         if (options == null || args == null) {
             AssertionControl.logMessage("Options or args array is null in VisitCommand.execute", 1, CLASSNAME);
             ViewSE.println("Internal error: Invalid command structure.");
             return;
         }

        // Check for minimum required option
        if (options.length < 1 || options[0] == null || options[0].isEmpty()) {
            ViewSE.println("Error: Missing or invalid option for 'visit' command (-a or -r).");
            AssertionControl.logMessage("Missing or invalid option for 'visit' command.", 2, CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);

        // Argument count check depends on the option
        if (option == 'a' && args.length < 3) { // add requires visit_name, visit_date, quantity
            ViewSE.println("Usage: visit -a <visit_name> <visit_date> <quantity>");
            AssertionControl.logMessage("Insufficient arguments for 'visit -a'", 2, CLASSNAME);
            return;
        } else if (option == 'r' && args.length < 2) { // remove requires visit_name, visit_date (or booking code?) - Assuming name/date for now
             ViewSE.println("Usage: visit -r <visit_name> <visit_date>"); // Or <booking_code> if implemented
             AssertionControl.logMessage("Insufficient arguments for 'visit -r'", 2, CLASSNAME);
             return;
        }


        switch (option) {
            case 'a' -> addFruitoreToVisita(args);
            case 'r' -> removeFromVisita(args);
            default -> {
                 ViewSE.println("Option '-" + option + "' not recognized for 'visit'. Use -a or -r.");
                 AssertionControl.logMessage("Unrecognized option for 'visit': " + option, 2, CLASSNAME);
            }
        }
    }

    private void addFruitoreToVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addFruitoreToVisita";
        String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "UNKNOWN";

        // Args already checked for minimum length 3 in execute()
        String[] a = StringUtils.joinQuotedArguments(args);
         if (a == null || a.length < 3 || a[0] == null || a[1] == null || a[2] == null) {
             AssertionControl.logMessage("Error processing arguments after joining.", 1, SUB_CLASSNAME);
             ViewSE.println("Internal error processing visit details.");
             return;
         }
        String visitName = a[0];
        String visitDateStr = a[1];
        String quantityStr = a[2]; // Index 2 for quantity based on execute check

        // Check user type
        if (controller.getCurrentUser() == null || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Error: Only registered visitors (fruitori) can book visits.");
            AssertionControl.logMessage("Non-fruitore user ("+ currentUser +") attempted to book visit.", 1, SUB_CLASSNAME);
            return;
        }
        Fruitore f = (Fruitore) controller.getCurrentUser(); // Safe cast after check

        // Find the visit
        Visita v = controller.db.getVisitaByName(visitName, visitDateStr);
        if (v == null) {
            ViewSE.println("Error: Visit '" + visitName + "' on date '" + visitDateStr + "' not found.");
            AssertionControl.logMessage("Visit not found: Name=" + visitName + ", Date=" + visitDateStr, 3, SUB_CLASSNAME);
            return;
        }

        // Check visit status (must be PROPOSED or COMPLETED)
        StatusVisita currentStatus = v.getStatus(); // Get status from the visit object
        if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
             ViewSE.println("Error: Cannot register for this visit. It is currently " + currentStatus + ".");
             AssertionControl.logMessage("Attempted to book visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
             return;
        }

        // Check if inscriptions are open (not 3 days before)
        // Assuming Visita.getDate() returns a Date object compatible with Date.minusDays()
        Date today = controller.date; // Get current simulated date
        Date visitDate = v.getDate();
        Date deadline = visitDate.minusDays(3);
        if (today.equals(deadline) || today.isAfter(deadline)) { // Assuming Date has isAfter()
             ViewSE.println("Error: Bookings for this visit are closed (deadline passed).");
             AssertionControl.logMessage("Attempted to book visit after deadline (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
             return;
        }


        // Validate quantity
        int quantita = 0; // quantity
        try {
            quantita = Integer.parseInt(quantityStr); // Use index 2
            if (quantita <= 0) {
                 ViewSE.println("Error: Quantity must be a positive number.");
                 AssertionControl.logMessage("Invalid quantity provided: " + quantityStr, 2, SUB_CLASSNAME);
                 return;
            }
        } catch (NumberFormatException e) {
            ViewSE.println("Error: Invalid quantity '" + quantityStr + "'. Please enter a number.");
            AssertionControl.logMessage("Numeric format exception for quantity: " + quantityStr, 2, SUB_CLASSNAME);
            return;
        }

        // Check against max bookings per person
        int maxPrenotazioni = controller.getDB().appSettings.getMaxPrenotazioniPerPersona();
        if (quantita > maxPrenotazioni) {
            ViewSE.println("Error: You cannot register more than " + maxPrenotazioni + " people in a single booking.");
            AssertionControl.logMessage("Maximum bookings per person exceeded (" + quantita + " > " + maxPrenotazioni + ")", 3, SUB_CLASSNAME);
            return;
        }

        // Attempt to add participants
        String bookingResult = v.addPartecipants(f, quantita);
        switch (bookingResult) {
            case "capacity":
                ViewSE.println("Error: Cannot register " + quantita + " people. Not enough capacity remaining for this visit.");
                 AssertionControl.logMessage("Booking failed due to capacity limit (UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
                break; // Use break instead of return to allow potential further actions if needed

            case "present":
                ViewSE.println("Error: You are already registered for this visit.");
                 AssertionControl.logMessage("Booking failed, user already registered (UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
                break;

            default: // Success case - result is the booking code (UIDIscrizione)
                 String bookingCode = bookingResult; // Assuming the default return is the code
                 ViewSE.println("Registration completed successfully! Your booking code is: " + bookingCode);
                 AssertionControl.logMessage("Booking successful for user " + f.getUsername() + " (Code: " + bookingCode + ", Qty: " + quantita + ", Visit UID: " + v.getUID() + ")", 4, SUB_CLASSNAME);
                 f.subscribeToVisit(v.getUID()); // Assuming this tracks visits user is subscribed to
                 // Need to save changes potentially made to Visita status (e.g., became COMPLETED)
                 controller.db.dbVisiteHelper.saveJson(); // Save Visita changes
                 controller.db.dbFruitoreHelper.saveJson(); // Save Fruitore changes (subscribed visits)
                 break;
        }
    }

    private void removeFromVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".removeFromVisita";
        String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "UNKNOWN";

        // Args already checked for minimum length 2 in execute()
        String[] a = StringUtils.joinQuotedArguments(args);
         if (a == null || a.length < 2 || a[0] == null || a[1] == null) {
             AssertionControl.logMessage("Error processing arguments after joining.", 1, SUB_CLASSNAME);
             ViewSE.println("Internal error processing visit details for removal.");
             return;
         }
        String visitName = a[0];
        String visitDateStr = a[1];
        // TODO: Consider allowing removal by booking code instead of name/date

        // Check user type
        if (controller.getCurrentUser() == null || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Error: Only registered visitors (fruitori) can remove bookings.");
            AssertionControl.logMessage("Non-fruitore user ("+ currentUser +") attempted to remove booking.", 1, SUB_CLASSNAME);
            return;
        }
        Fruitore f = (Fruitore) controller.getCurrentUser();

        // Find the visit
        Visita v = controller.db.getVisitaByName(visitName, visitDateStr);
        if (v == null) {
            ViewSE.println("Error: Visit '" + visitName + "' on date '" + visitDateStr + "' not found.");
            AssertionControl.logMessage("Visit not found for removal: Name=" + visitName + ", Date=" + visitDateStr, 3, SUB_CLASSNAME);
            return;
        }

         // Check visit status (must be PROPOSED or COMPLETED)
         StatusVisita currentStatus = v.getStatus(); // Get status from the visit object
         if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
              ViewSE.println("Error: Cannot remove booking for this visit. It is currently " + currentStatus + ".");
              AssertionControl.logMessage("Attempted to remove booking from visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
              return;
         }

         // Check if inscriptions are open (not 3 days before)
         Date today = controller.date;
         Date visitDate = v.getDate();
         Date deadline = visitDate.minusDays(3);
         if (today.equals(deadline) || today.isAfter(deadline)) {
              ViewSE.println("Error: Cannot remove booking for this visit (deadline passed).");
              AssertionControl.logMessage("Attempted to remove booking after deadline (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
              return;
         }

        // Attempt removal - Assuming Visita.removePartecipant handles finding the correct Iscrizione
        // It might be better to pass the booking code if available.
        // Visita.removePartecipant currently takes username, which might remove the wrong booking if user booked multiple times (though UC prevents this?)
        // Let's assume Visita.removeIscrizioneByUID is better if we had the code.
        // For now, using the existing Visita.removePartecipant(username)
        boolean removed = v.removePartecipant(f.getUsername()); // Visita.removePartecipant needs to return boolean

        if (removed) {
             ViewSE.println("Booking for visit '" + visitName + "' on " + visitDateStr + " removed successfully.");
             AssertionControl.logMessage("Booking removed for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ")", 4, SUB_CLASSNAME);
             f.removeFromVisita(v.getUID()); // Update fruitore's list
             // Save changes
             controller.db.dbVisiteHelper.saveJson(); // Save Visita changes (status might change)
             controller.db.dbFruitoreHelper.saveJson(); // Save Fruitore changes
        } else {
             ViewSE.println("Error: Could not remove booking. Were you registered for this visit?");
             AssertionControl.logMessage("Failed to remove booking for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ") - User possibly not registered.", 3, SUB_CLASSNAME);
        }
    }
}

// Removed helper Date interface
