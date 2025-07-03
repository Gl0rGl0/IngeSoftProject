package V2.Ingsoft.controller.commands;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.controller.commands.running.CommandList;
import V2.Ingsoft.controller.item.luoghi.Luogo;
import V2.Ingsoft.controller.item.luoghi.TipoVisita;
import V2.Ingsoft.controller.item.persone.Configuratore;
import V2.Ingsoft.controller.item.persone.Volontario;
import V2.Ingsoft.util.AssertionControl;
import V2.Ingsoft.util.StringUtils;
import V2.Ingsoft.view.ViewSE;

public class AddCommand extends AbstractCommand {
    private static final String CLASSNAME = AddCommand.class.getSimpleName(); // Added for logging

    public AddCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.ADD;
        this.hasBeenExecuted = false;
    }

    @Override
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

        this.hasBeenExecuted = true;
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
        if (controller.getDB().dbConfiguratoreHelper.addConfiguratore(c)) {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Added configurator: " + username, 4, SUB_CLASSNAME); // Level 4 for success info
            ViewSE.println("Configuratore " + username + " added successfully.");
        } else {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Failed to add configurator: " + username + " (likely already exists)", 2, SUB_CLASSNAME);
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
            v = new Volontario(new String[]{username, args[1]}, controller.date.clone());
        } catch (Exception e) {
            AssertionControl.logMessage("Error creating Volontario object: " + e.getMessage(), 1, SUB_CLASSNAME);
            ViewSE.println("Error creating volunteer: " + e.getMessage());
            return;
        }

        // adds a new volunteer who will need to change password on first login
        if (controller.getDB().dbVolontarioHelper.addVolontario(v)) {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Added volunteer: " + username, 4, SUB_CLASSNAME);
            ViewSE.println("Volunteer " + username + " added successfully.");
            ViewSE.println("Please assign this place to a visit type!");
        } else {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Failed to add volunteer: " + username + " (likely already exists)", 2, SUB_CLASSNAME);
            ViewSE.println("Failed to add volunteer '" + username + "'. It might already exist.");
        }
    }

    private void addTipoVisita(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addTipoVisita";

        // Join arguments first to handle quotes
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a == null) {
            AssertionControl.logMessage("StringUtils.joinQuotedArguments returned null", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error processing arguments for adding visit type");
            return;
        }

        // Check time restriction
        if (!isExecutable())
            return;

        // Check argument count (TipoVisita constructor expects 11 specific args)
        if (a.length < 11) {
            AssertionControl.logMessage("Insufficient arguments for adding TipoVisita. Expected 11, got " + a.length, 2, SUB_CLASSNAME);
            ViewSE.println("Error: Insufficient arguments provided for adding visit type. Please provide all required details and use \" \" if there's more then a word.");
            return;
        }
        String title = a[0]; // For logging

        TipoVisita t;
        try {
            t = new TipoVisita(a, controller.date.clone());
        } catch (Exception e) {
            AssertionControl.logMessage("Error creating TipoVisita object: " + e.getMessage(), 1, SUB_CLASSNAME);
            ViewSE.println("Error creating visit type: " + e.getMessage());
            return;
        }

        if (controller.getDB().dbTipoVisiteHelper.addTipoVisita(t)) {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Added TipoVisita: " + title, 4, SUB_CLASSNAME);
            ViewSE.println("Visit type '" + title + "' added successfully.");
            ViewSE.println("Please assign this visit type to a place and at least a volunteer to this visit type!");
        } else {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Failed to add TipoVisita: " + title + " (likely already exists)", 2, SUB_CLASSNAME);
            ViewSE.println("Failed to add visit type '" + title + "'. It might already exist.");
        }
    }

    private void addLuogo(String[] args) {
        final String SUB_CLASSNAME = CLASSNAME + ".addLuogo";
        

        // Join arguments first
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a == null) {
            AssertionControl.logMessage("StringUtils.joinQuotedArguments returned null", 1, SUB_CLASSNAME);
            ViewSE.println("Internal error processing arguments for adding place.");
            return;
        }

        // Check time restriction
        if (!isExecutable())
            return;

        // Check argument count (Luogo constructor expects name, description, location)
        if (a.length < 3 || a[0] == null || a[0].trim().isEmpty() || a[2] == null || a[2].trim().isEmpty()) {
            AssertionControl.logMessage("Insufficient or invalid arguments for adding Luogo. Expected name, description, location.", 2, SUB_CLASSNAME);
            ViewSE.println("Usage: add -L \"<name>\" \"<description>\" <location>");
            return;
        }
        String name = a[0]; // For logging

        if (controller.getDB().dbLuoghiHelper.findLuogo(name) != null) {
            AssertionControl.logMessage("Already exist a Place with the same name", 2, CLASSNAME);
        }

        Luogo l;
        try {
            l = new Luogo(a, controller.date.clone()); // Assumes constructor takes String[] {name, desc, loc}
        } catch (Exception e) {
            AssertionControl.logMessage("Error creating Luogo object: " + e.getMessage(), 1, SUB_CLASSNAME);
            ViewSE.println("Error creating place: " + e.getMessage());
            return;
        }

        if (controller.getDB().dbLuoghiHelper.addLuogo(l)) {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Added place: " + name, 4, SUB_CLASSNAME);
            ViewSE.println("Place '" + name + "' added successfully.");
            ViewSE.println("Please assign a visit type to this place!");
        } else {
            AssertionControl.logMessage(controller.getCurrentUser().getUsername() + "| Failed to add place: " + name + " (likely already exists)", 2, SUB_CLASSNAME);
            ViewSE.println("Failed to add place '" + name + "'. It might already exist.");
        }
    }
}
