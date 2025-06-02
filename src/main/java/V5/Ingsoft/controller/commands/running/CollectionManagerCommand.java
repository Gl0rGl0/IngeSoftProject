package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.util.Payload;

public class CollectionManagerCommand extends AbstractCommand {
    public CollectionManagerCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.COLLECTIONMANAGER;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        //Non so se lasciarlo
        if (controller == null)
            return Payload.error(
                    "Internal error: controller is null",
                    "Controller reference missing");

        if (options == null || args == null)
            return Payload.error(
                    "Internal error: invalid command structure",
                    "Options or args null");

        if (options.length < 1 || options[0].isBlank())
            return Payload.error(
                    "Usage: collection -<o|c>",
                    "Missing or invalid option");

        if (!isExecutable()) {
            return Payload.warn(
                    "Cannot edit the status of the collection: action not allowed now. Try Again on the 16th",
                    "CollectionManager: not executable");
        }

        char opt = options[0].charAt(0);
        switch (opt) {
            case 'o':
                controller.openCollection();
                break;
            case 'c':
                controller.closeCollection();
                break;
            default:
                return Payload.warn(
                        "Option '-" + opt + "' not recognized for 'collection'",
                        "Unknown option '" + opt + "'");
        }

        return Payload.info(
                opt == 'o' ? "Opened collection" : "Closed collection",
                "Executed '" + opt + "'");
    }
}
