package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class VisitCommand extends AbstractCommand {
    private final Controller controller;

    public VisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.VISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("No option entered, please try again...");
            AssertionControl.logMessage("Insufficient options", 2, CLASSNAME);
            return;
        }

        if (args.length < 3) { // visit_name visit_date quantity
            ViewSE.println("Incorrect usage of the 'visit' command, please try again...");
            AssertionControl.logMessage("Insufficient arguments", 2, CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> addFruitoreToVisita(args);
            case 'r' -> removeFromVisita(args);
            default -> ViewSE.println("Option not recognized for 'visit'."); // Corrected command name from 'myvisit'
        }
    }

    private void addFruitoreToVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = controller.db.getVisitaByName(a[0], a[1]);

        if (controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Cannot register a NON-fruitore for the visit");
            AssertionControl.logMessage("Registration attempt from config/volunteer, should not have reached here", 1,
                    CLASSNAME);
            return;
        }

        if (v == null) {
            ViewSE.println("No visits available with that name...");
            AssertionControl.logMessage("Visit does not exist", 3, CLASSNAME);
            return;
        }

        int quantita = 0; // quantity
        try {
            quantita = Integer.valueOf(a[3]);
        } catch (NumberFormatException e) {
            ViewSE.println("Error reading quantity...");
            AssertionControl.logMessage("Numeric exception during registration", 2, CLASSNAME);
            return;
        }

        // Get max bookings from AppSettings via Controller
        int maxPrenotazioni = Model.appSettings.getMaxPrenotazioniPerPersona();

        if (quantita > maxPrenotazioni) {
            ViewSE.println(
                    "You cannot register so many people, the maximum is: " + maxPrenotazioni);
            AssertionControl.logMessage("Maximum bookings per person exceeded", 3,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) controller.getCurrentUser();

        switch (v.addPartecipants(f, quantita)) {
            case "capacity" -> { // Assuming "capienza" maps to "capacity" from Visita.java translation
                ViewSE.println("Cannot register you for the visit, the capacity exceeds your request.");
                return;
            }

            case "present" -> { // Assuming "presente" maps to "present" from Visita.java translation
                ViewSE.println("Cannot register you for the visit, you are already registered.");
                return;
            }

            default -> {
                ViewSE.println("Registration completed successfully");
                f.subscribeToVisit(v.getUID());
            }
        }

    }

    private void removeFromVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = controller.db.getVisitaByName(a[0], a[1]);

        if (v == null) {
            ViewSE.println("No visits available with that name...");
            AssertionControl.logMessage("Visit does not exist", 3, CLASSNAME);
            return;
        }

        if (controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Cannot unregister a NON-fruitore from the visit");
            AssertionControl.logMessage("Unregistration attempt from config/volunteer, should not have reached here", 1,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) controller.getCurrentUser();
        f.removeFromVisita(v.getUID());
        v.removePartecipant(f.getUsername());
    }
}
