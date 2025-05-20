package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.util.Payload;

public class CollectionManagerCommand extends AbstractCommand {
    private static final String CLASSNAME = CollectionManagerCommand.class.getSimpleName();

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
                CLASSNAME + ": controller reference missing");

        if (options == null || args == null)
            return Payload.error(
                "Internal error: invalid command structure",
                CLASSNAME + ": options or args null");

        if (options.length < 1 || options[0].isBlank())
            return Payload.error(
                "Usage: collection -<o|c>",
                CLASSNAME + ": missing or invalid option");

        if (!isExecutable()) {
            return Payload.warn(
                "Cannot edit the status of the collection: action not allowed now. Try Again on the 16th",
                "CollectionManager: not executable"
            );
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
                    CLASSNAME + ": unknown option '" + opt + "'");
        }

        return Payload.info(
            opt == 'o' ? "Opened collection" : "Closed collection",
            CLASSNAME + ": executed '" + opt + "'");
    }
}
