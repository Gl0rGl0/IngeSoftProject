package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.persone.Configuratore;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.util.AssertionControl;
import V4.Ingsoft.util.StringUtils;
import V4.Ingsoft.view.ViewSE;

public class AddCommand extends AbstractCommand {
    private final Controller controller;
    private static final String CLASSNAME = AddCommand.class.getSimpleName(); // Added for logging

    public AddCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ADD;
    }

    @Override
    /**
     * Implementation of the "add" command.
     *
     * @param options the options (e.g., -c for configurator)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        // Basic null checks
        if (controller == null) {
            // Cannot use AssertionControl if controller might be needed for it.
            System.err.println("FATAL: Controller is null in AddCommand");
            return;
        }
         if (options == null || args == null) {
             AssertionControl.logMessage("Options or args array is null in AddCommand.execute", 1, CLASSNAME);
             ViewSE.println("Internal error: Invalid command structure.");
             return;
         }

        // Check for minimum required option
        if (options.length < 1 || options[0] == null || options[0].isEmpty()) {
            ViewSE.println("Error: Missing or invalid option for 'add' command.");
            AssertionControl.logMessage("Missing or invalid option for 'add' command.", 2, CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);
        // Note: Argument count check is deferred to specific add methods

        switch (option) {
            case 'c' -> addConfiguratore(args); // Expects username, password
            case 'v' -> addVolontario(args); // Expects username, password
            case 'L' -> addLuogo(args); // Expects name, description, location
            case 'T' -> addTipoVisita(args); // Expects many args
            default -> {
                 ViewSE.println("Option '-" + option + "' not recognized for 'add'.");
                 AssertionControl.logMessage("Unrecognized option for 'add': " + option, 2, CLASSNAME);
            }
        }
    }

    private void addConfiguratore(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addConfiguratore";
        // Expects username and password
        if (args == null || args.length < 2 || args[0] == null || args[0].trim().isEmpty() || args[1] == null || args[1].isEmpty()) {
             AssertionControl.logMessage("Insufficient or invalid arguments for adding configurator.", 2, SUB_CLASSNAME);
             ViewSE.println("Usage: add -c <username> <initial_password>");
             return;
        }
        String username = args[0];

        Configuratore c;
        try {
            // Assuming constructor takes username and password
            c = new Configuratore(new String[]{username, args[1]});
        } catch (Exception e) {
            // Catch potential exceptions from the constructor itself if it validates
            AssertionControl.logMessage("Error creating Configuratore object: " + e.getMessage(), 1, SUB_CLASSNAME);
            ViewSE.println("Error creating configurator: " + e.getMessage());
            return;
        }

        // adds a new configurator who will need to change password on first login
        if (controller.db.dbConfiguratoreHelper.addConfiguratore(c)) {
             String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";
            AssertionControl.logMessage(currentUser + "| Added configurator: " + username, 4, SUB_CLASSNAME); // Level 4 for success info
             ViewSE.println("Configuratore " + username + " added successfully.");
        } else {
             String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";
            AssertionControl.logMessage(currentUser + "| Failed to add configurator: " + username + " (likely already exists)", 2, SUB_CLASSNAME);
             ViewSE.println("Failed to add configurator '" + username + "'. It might already exist.");
        }
    }

    private void addVolontario(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addVolontario";
        // Expects username and password
        if (args == null || args.length < 2 || args[0] == null || args[0].trim().isEmpty() || args[1] == null || args[1].isEmpty()) {
             AssertionControl.logMessage("Insufficient or invalid arguments for adding volunteer.", 2, SUB_CLASSNAME);
             ViewSE.println("Usage: add -v <username> <initial_password>");
             return;
        }
        String username = args[0];

        Volontario v;
        try {
            // Assuming constructor takes username and password
             v = new Volontario(new String[]{username, args[1]});
        } catch (Exception e) {
             AssertionControl.logMessage("Error creating Volontario object: " + e.getMessage(), 1, SUB_CLASSNAME);
             ViewSE.println("Error creating volunteer: " + e.getMessage());
            return;
        }

        // adds a new volunteer who will need to change password on first login
        if (controller.db.dbVolontarioHelper.addVolontario(v)) {
             String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";
            AssertionControl.logMessage(currentUser + "| Added volunteer: " + username, 4, SUB_CLASSNAME);
             ViewSE.println("Volunteer " + username + " added successfully.");
        } else {
             String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";
            AssertionControl.logMessage(currentUser + "| Failed to add volunteer: " + username + " (likely already exists)", 2, SUB_CLASSNAME);
             ViewSE.println("Failed to add volunteer '" + username + "'. It might already exist.");
        }
    }

    private void addTipoVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addTipoVisita";
        String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";

        // Join arguments first to handle quotes
        String[] a = StringUtils.joinQuotedArguments(args);
         if (a == null) {
             AssertionControl.logMessage("StringUtils.joinQuotedArguments returned null", 1, SUB_CLASSNAME);
             ViewSE.println("Internal error processing arguments for adding visit type.");
             return;
         }

        // Check time restriction
        if (!controller.isActionDay16) {
            AssertionControl.logMessage(
                    currentUser
                            + "| Cannot add a visit type if it's not the 16th of the month: " + (a.length > 0 ? a[0] : "N/A"),
                    1, // High severity for violating time constraint
                    SUB_CLASSNAME);
             ViewSE.println("Error: Adding visit types is only allowed on the designated action day (e.g., 16th).");
            return;
        }

        // Check argument count (TipoVisita constructor expects 11 specific args)
        if (a.length < 11) {
             AssertionControl.logMessage("Insufficient arguments for adding TipoVisita. Expected 11, got " + a.length, 2, SUB_CLASSNAME);
             ViewSE.println("Error: Insufficient arguments provided for adding visit type. Please provide all required details.");
             return;
        }
        String title = a[0]; // For logging

        TipoVisita t;
        try {
            t = new TipoVisita(a, controller.date);
        } catch (Exception e) {
            AssertionControl.logMessage("Error creating TipoVisita object: " + e.getMessage(), 1, SUB_CLASSNAME);
             ViewSE.println("Error creating visit type: " + e.getMessage());
            return;
        }

        if (controller.db.dbTipoVisiteHelper.addTipoVisita(t)) {
            AssertionControl.logMessage(currentUser + "| Added TipoVisita: " + title, 4, SUB_CLASSNAME);
             ViewSE.println("Visit type '" + title + "' added successfully.");
        } else {
            AssertionControl.logMessage(currentUser + "| Failed to add TipoVisita: " + title + " (likely already exists)", 2, SUB_CLASSNAME);
             ViewSE.println("Failed to add visit type '" + title + "'. It might already exist.");
        }
    }

    private void addLuogo(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addLuogo";
        String currentUser = controller.getCurrentUser() != null ? controller.getCurrentUser().getUsername() : "SYSTEM";

        // Join arguments first
        String[] a = StringUtils.joinQuotedArguments(args);
         if (a == null) {
             AssertionControl.logMessage("StringUtils.joinQuotedArguments returned null", 1, SUB_CLASSNAME);
             ViewSE.println("Internal error processing arguments for adding place.");
             return;
         }

        // Check time restriction
        if (!controller.isActionDay16) {
            AssertionControl.logMessage(
                    currentUser
                            + "| Cannot add a place if it's not the 16th of the month: "
                            + (a.length > 0 ? a[0] : "N/A"),
                    1, // High severity
                    SUB_CLASSNAME);
             ViewSE.println("Error: Adding places is only allowed on the designated action day (e.g., 16th).");
            return;
        }

        // Check argument count (Luogo constructor expects name, description, location)
        if (a.length < 3 || a[0] == null || a[0].trim().isEmpty() || a[2] == null || a[2].trim().isEmpty()) {
             AssertionControl.logMessage("Insufficient or invalid arguments for adding Luogo. Expected name, description, location.", 2, SUB_CLASSNAME);
             ViewSE.println("Usage: add -L <name> \"<description>\" <location>");
             return;
        }
        String name = a[0]; // For logging

        Luogo l;
        try {
            l = new Luogo(a); // Assumes constructor takes String[] {name, desc, loc}
        } catch (Exception e) {
            AssertionControl.logMessage("Error creating Luogo object: " + e.getMessage(), 1, SUB_CLASSNAME);
             ViewSE.println("Error creating place: " + e.getMessage());
            return;
        }

        if (controller.db.dbLuoghiHelper.addLuogo(l)) {
            AssertionControl.logMessage(currentUser + "| Added place: " + name, 4, SUB_CLASSNAME);
             ViewSE.println("Place '" + name + "' added successfully.");
        } else {
            AssertionControl.logMessage(currentUser + "| Failed to add place: " + name + " (likely already exists)", 2, SUB_CLASSNAME);
             ViewSE.println("Failed to add place '" + name + "'. It might already exist.");
        }
    }

    

}
