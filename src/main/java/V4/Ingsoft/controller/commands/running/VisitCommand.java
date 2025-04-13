package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.luoghi.StatusVisita; // Added import
import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.util.AssertionControl;
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

    private Fruitore getCurrentFruitore(String subClassName) {
        if (controller.getCurrentUser() == null || controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Error: Only registered visitors (fruitori) can book visits.");
            String currentUser = controller.getCurrentUser().getUsername();
            AssertionControl.logMessage("Non-fruitore user (" + currentUser + ") attempted action.", 1, subClassName);
            return null;
        }
        return (Fruitore) controller.getCurrentUser();
    }

    // Metodo di utilità per cercare una visita
    private Visita findVisit(String visitName, String visitDateStr, String subClassName) {
        Visita v = controller.db.dbVisiteHelper.findVisita(visitName, visitDateStr);
        if (v == null) {
            ViewSE.println("Error: Visit '" + visitName + "' on date '" + visitDateStr + "' not found.");
            AssertionControl.logMessage("Visit not found: Name=" + visitName + ", Date=" + visitDateStr, 3, subClassName);
        }
        return v;
    }

    // Metodo di utilità per controllare il deadline delle iscrizioni (3 giorni prima)
    private boolean isBookingOpen(Visita v, String subClassName) {
        if (v.status == StatusVisita.PROPOSED) {
            return true;  
        }
        ViewSE.println("Error: Bookings for this visit are closed (deadline passed).");
            AssertionControl.logMessage("Attempted to book visit after deadline (UID: " + v.getUID() + ")", 2, subClassName);
            return false;
    }

    // Metodo per aggiungere un fruitore a una visita (booking)
    public void addFruitoreToVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addFruitoreToVisita";
        
        // Verifica argomenti: supponiamo che joinQuotedArguments() restituisca un array di almeno 3 argomenti
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a == null || a.length < 3 || a[0] == null || a[1] == null || a[2] == null) {
            AssertionControl.logMessage("Error processing arguments after joining.", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error processing visit details.");
            return;
        }
        String visitName = a[0];
        String visitDateStr = a[1];
        String quantityStr = a[2];

        // Controlla che l'utente corrente sia un fruitore
        Fruitore f = getCurrentFruitore(SUB_CLASSNAME);
        if (f == null) return;

        // Cerca la visita
        Visita v = findVisit(visitName, visitDateStr, SUB_CLASSNAME);
        if (v == null) return;

        // Controlla lo stato della visita (deve essere PROPOSED o COMPLETED)
        StatusVisita currentStatus = v.getStatus();
        if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
            ViewSE.println("Error: Cannot register for this visit. It is currently " + currentStatus + ".");
            AssertionControl.logMessage("Attempted to book visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
            return;
        }

        // Controlla se il deadline delle iscrizioni non è passato
        if (!isBookingOpen(v, SUB_CLASSNAME))
            return;

        // Validazione quantità
        int quantita;
        try {
            quantita = Integer.parseInt(quantityStr.replace(" ", ""));
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

        // Controlla il massimo di prenotazioni consentite
        int maxPrenotazioni = controller.getDB().appSettings.getMaxPrenotazioniPerPersona();
        if (quantita > maxPrenotazioni) {
            ViewSE.println("Error: You cannot register more than " + maxPrenotazioni + " people in a single booking.");
            AssertionControl.logMessage("Maximum bookings per person exceeded (" + quantita + " > " + maxPrenotazioni + ")", 3, SUB_CLASSNAME);
            return;
        }

        // Tentativo di aggiungere i partecipanti
        String bookingResult = v.addPartecipants(f, quantita);
        switch (bookingResult) {
            case "capacity":
                ViewSE.println("Error: Cannot register " + quantita + " people. Not enough capacity remaining for this visit.");
                AssertionControl.logMessage("Booking failed due to capacity limit (UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
                break;
            case "present":
                ViewSE.println("Error: You are already registered for this visit.");
                AssertionControl.logMessage("Booking failed, user already registered (UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
                break;
            default: // Caso di successo: bookingResult è il codice di prenotazione
                String bookingCode = bookingResult;
                ViewSE.println("Registration completed successfully! Your booking code is: " + bookingCode);
                AssertionControl.logMessage("Booking successful for user " + f.getUsername() + " (Code: " + bookingCode + ", Qty: " + quantita + ", Visit UID: " + v.getUID() + ")", 4, SUB_CLASSNAME);
                f.subscribeToVisit(v.getUID());
                controller.db.dbVisiteHelper.saveJson();
                controller.db.dbFruitoreHelper.saveJson();
                break;
        }
    }

    // Metodo per rimuovere un fruitore da una visita
    public void removeFromVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".removeFromVisita";
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a == null || a.length < 2 || a[0] == null || a[1] == null) {
            AssertionControl.logMessage("Error processing arguments after joining.", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error processing visit details for removal.");
            return;
        }
        String visitName = a[0];
        String visitDateStr = a[1];

        // Controlla che l'utente corrente sia un fruitore
        Fruitore f = getCurrentFruitore(SUB_CLASSNAME);
        if (f == null) return;

        // Cerca la visita
        Visita v = findVisit(visitName, visitDateStr, SUB_CLASSNAME);
        if (v == null) return;

        // Controlla lo stato della visita
        StatusVisita currentStatus = v.getStatus();
        if (currentStatus != StatusVisita.PROPOSED && currentStatus != StatusVisita.COMPLETED) {
            ViewSE.println("Error: Cannot remove booking for this visit. It is currently " + currentStatus + ".");
            AssertionControl.logMessage("Attempted to remove booking from visit with status " + currentStatus + " (UID: " + v.getUID() + ")", 2, SUB_CLASSNAME);
            return;
        }

        // Controlla se il deadline delle iscrizioni non è passato
        if (!isBookingOpen(v, SUB_CLASSNAME))
            return;

        // Tentativo di rimozione; supponiamo che removePartecipant restituisca booleano
        boolean removed = v.removePartecipant(f.getUsername());
        if (removed) {
            ViewSE.println("Booking for visit '" + visitName + "' on " + visitDateStr + " removed successfully.");
            AssertionControl.logMessage("Booking removed for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ")", 4, SUB_CLASSNAME);
            f.removeFromVisita(v.getUID());
            controller.db.dbVisiteHelper.saveJson();
            controller.db.dbFruitoreHelper.saveJson();
        } else {
            ViewSE.println("Error: Could not remove booking. Were you registered for this visit?");
            AssertionControl.logMessage("Failed to remove booking for user " + f.getUsername() + " (Visit UID: " + v.getUID() + ")", 3, SUB_CLASSNAME);
        }
    }
}
