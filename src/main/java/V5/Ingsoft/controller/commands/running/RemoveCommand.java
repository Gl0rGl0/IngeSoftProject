package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.util.AssertionControl;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.StringUtils;
import V5.Ingsoft.view.ViewSE;

public class RemoveCommand extends AbstractCommand {


    public RemoveCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.REMOVE;
    }

    @Override
    public void execute(String[] options, String[] args) {
        // Define CLASSNAME for logging context
        final String CLASSNAME = RemoveCommand.class.getSimpleName();

        if (controller == null) {
            // Cannot log using AssertionControl if controller might be needed for it,
            // but assuming AssertionControl.logMessage is static or accessible.
            AssertionControl.logMessage("Controller cannot be null in RemoveCommand", 1, CLASSNAME);
            ViewSE.println("Internal error: Controller not available."); // User feedback
            return; // Cannot proceed
        }
        if (options == null) {
            AssertionControl.logMessage("Options array cannot be null in RemoveCommand", 1, CLASSNAME);
            ViewSE.println("Internal error: Options not available.");
            return;
        }
        if (args == null) {
            AssertionControl.logMessage("Args array cannot be null in RemoveCommand", 1, CLASSNAME);
            ViewSE.println("Internal error: Arguments not available.");
            return;
        }

        // Check for minimum required option
        if (options.length < 1 || options[0] == null || options[0].isEmpty()) {
            // Log this as a user input error (maybe lower severity?)
            AssertionControl.logMessage("Missing or invalid option for 'remove' command.", 2, CLASSNAME);
            ViewSE.println("Error: Missing or invalid option for 'remove' command.");
            // Optionally print help/usage here
            return;
        }
        // Option string check - if options[0] wasn't null/empty, it must have length > 0
        char option = options[0].charAt(0);

        // Join arguments, handling potential quotes
        String[] ar = StringUtils.joinQuotedArguments(args);
        if (ar == null) {
            // This indicates an issue within StringUtils.joinQuotedArguments
            AssertionControl.logMessage("StringUtils.joinQuotedArguments returned null", 1, CLASSNAME);
            ViewSE.println("Internal error processing arguments.");
            return;
        }

        // Check if required arguments exist for the specific option
        if (ar.length < 1 || ar[0] == null || ar[0].trim().isEmpty()) {
            // Log as user input error
            AssertionControl.logMessage("Missing identifier argument for 'remove -" + option + "'.", 2, CLASSNAME);
            ViewSE.println("Error: Missing identifier argument for 'remove -" + option + "'.");
            return;
        }


        switch (option) {
            case 'c' -> removeConfiguratore(ar); // Assumes ar[0] is username
            case 'f' -> removeFruitore(ar); // Assumes ar[0] is username. No time restriction for Fruitore
            case 'v' -> removeVolontario(ar);
            case 'T' -> removeTipoVisita(ar);
            case 'L' -> removeLuogo(ar);
            default -> ViewSE.println("Option not recognized for 'remove'.");
        }
    }

    private void removeConfiguratore(String[] args) {
        // Precondition check already done in execute()
        final String CLASSNAME = RemoveCommand.class.getSimpleName() + ".removeConfiguratore";
        String username = args[0]; // Already validated in execute()

        ViewSE.println("Executing: Removing configurator " + username);
        // Assuming removePersona returns boolean for success/failure
        if (!controller.getDB().dbConfiguratoreHelper.removePersona(username)) {
            AssertionControl.logMessage("Failed to remove configurator: " + username, 1, CLASSNAME);
            ViewSE.println("Failed to remove configurator: " + username);
        } else {
            AssertionControl.logMessage("Configurator removed: " + username, 4, CLASSNAME); // Log success (level 4?)
            ViewSE.println("Configurator removed: " + username);
        }
    }

    private void removeFruitore(String[] args) {
        // Precondition check already done in execute()
        final String CLASSNAME = RemoveCommand.class.getSimpleName() + ".removeFruitore";
        String username = args[0]; // Already validated in execute()

        ViewSE.println("Executing: Removing fruitore (user/visitor) " + username);
        if (!controller.getDB().removeFruitore(username)) {
            AssertionControl.logMessage("Failed to remove fruitore: " + username, 1, CLASSNAME);
            ViewSE.println("Failed to remove fruitore: " + username);
        } else {
            AssertionControl.logMessage("Fruitore removed: " + username, 4, CLASSNAME);
            ViewSE.println("Fruitore removed: " + username);
        }
    }

    private void removeVolontario(String[] args) {
        if (!isExecutable())
            return;

        // Precondition checks (args valid, isActionDay16) done in execute()
        final String CLASSNAME = RemoveCommand.class.getSimpleName() + ".removeVolontario";
        String username = args[0];

        ViewSE.println("Executing: Removing volunteer " + username);
        if (!controller.getDB().removeVolontario(username)) {
            AssertionControl.logMessage("Failed to remove volunteer: " + username, 1, CLASSNAME);
            ViewSE.println("Failed to remove volunteer: " + username);
        } else {
            AssertionControl.logMessage("Volunteer removed: " + username, 4, CLASSNAME);
            ViewSE.println("Volunteer removed: " + username);
        }
    }

    private void removeTipoVisita(String[] args) {
        if (!isExecutable())
            return;

        // Precondition checks (args valid, isActionDay16) done in execute()
        final String CLASSNAME = RemoveCommand.class.getSimpleName() + ".removeTipoVisita";
        String typeName = args[0];
        TipoVisita toOperate = controller.getDB().dbTipoVisiteHelper.findTipoVisita(typeName);

        ViewSE.println("Executing: Removing visit type " + typeName);
        if (toOperate == null) {
            AssertionControl.logMessage("Failed to remove visit type: " + typeName, 1, CLASSNAME);
            ViewSE.println("Failed to remove visit type: " + typeName);
        } else {
            Date toRemove = controller.date.clone().addMonth(2);
            toOperate.setDeletionDate(toRemove);
            AssertionControl.logMessage("Added visit type to be removed: " + typeName + " on: " + toRemove, 4, CLASSNAME);
            ViewSE.println("Added visit type to be removed: " + typeName + " on: " + toRemove);
        }
    }

    private void removeLuogo(String[] args) {
        if (!isExecutable())
            return;

        // Precondition checks (args valid, isActionDay16) done in execute()
        final String CLASSNAME = RemoveCommand.class.getSimpleName() + ".removeLuogo";
        String placeName = args[0];
        Luogo toOperate = controller.getDB().dbLuoghiHelper.findLuogo(placeName);

        ViewSE.println("Executing: Removing place " + placeName);
        if (!controller.getDB().removeLuogo(placeName)) {
            AssertionControl.logMessage("Failed to remove place: " + placeName, 2, CLASSNAME);
            ViewSE.println("Failed to remove place: " + placeName);
        } else {
            Date toRemove = controller.date.clone().addMonth(2);
            toOperate.setDeletionDate(toRemove);
            AssertionControl.logMessage("Place removed: " + placeName, 4, CLASSNAME);
            ViewSE.println("Place removed: " + placeName);
        }
    }
}
