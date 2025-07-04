package V3.Ingsoft.controller.commands.running;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.controller.commands.AbstractCommand;
import V3.Ingsoft.util.AssertionControl;
import V3.Ingsoft.view.ViewSE;

public class CollectionManagerCommand extends AbstractCommand {


    public CollectionManagerCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.COLLECTIONMANAGER;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (controller == null) {
            AssertionControl.logMessage("Controller cannot be null in MakePlanCommand", 1, CLASSNAME);
            return; // Cannot proceed
        }

        if (options == null || args == null) {
            AssertionControl.logMessage("Options or args array is null in CollectionManagerCommand.execute", 1, CLASSNAME);
            ViewSE.println("Internal error: Invalid command structure.");
            return;
        }

        // Check for minimum required option
        if (options.length < 1 || options[0] == null || options[0].isEmpty()) {
            ViewSE.println("Error: Missing or invalid option for 'collection' command.");
            AssertionControl.logMessage("Missing or invalid option for 'collection' command.", 2, CLASSNAME);
            return;
        }

        char option = options[0].charAt(0);

        switch (option) {
            case 'o' -> controller.openCollection();
            case 'c' -> controller.closeCollection();
            default -> {
                ViewSE.println("Option '-" + option + "' not recognized for 'collection'.");
                AssertionControl.logMessage("Unrecognized option for 'collection': " + option, 2, CLASSNAME);
                return;
            }
        }

        ViewSE.println("Executed: " + option + " on collection");
        AssertionControl.logMessage("Executed " + option, 3, CLASSNAME);
    }
}
