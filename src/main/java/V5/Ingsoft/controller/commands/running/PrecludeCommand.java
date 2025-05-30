package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

public class PrecludeCommand extends AbstractCommand {

    public PrecludeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.PRECLUDE;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (options == null || options.length < 1 || args == null || args.length < 1) {
            return Payload.error(
                "Error using 'preclude'. Missing option or arguments.",
                "Options or args missing in PRECLUDE command.");
        }

        Date d;
        try {
            d = new Date(args[0]);
        } catch (Exception e) {
            return Payload.error(
                "Invalid date format.",
                "Failed to parse date in PRECLUDE command: " + args[0]);
        }

        if (Date.monthsDifference(controller.date, d) > 2) {
            return Payload.error(
                "Date too distant. Allowed only current month or next two.",
                "Precluded date too distant: " + d);
        }

        char option = options[0].charAt(0);
        return switch (option) {
            case 'a' -> addPrecludedDate(d);
            case 'r' -> removePrecludedDate(d);
            default -> Payload.error(
                    "Option not recognized for 'preclude'.",
                    "Received invalid option: " + option);
        };
    }

    private Payload<String> addPrecludedDate(Date d) {
        if (Model.getInstance().dbDatesHelper.addPrecludedDate(d)) {
            return Payload.info(
                "Successfully added precluded date.",
                "Added precluded date: " + d);
        } else {
            return Payload.error(
                "Failed to add precluded date.",
                "Error adding precluded date: " + d);
        }
    }

    private Payload<String> removePrecludedDate(Date d) {
        Model.getInstance().dbDatesHelper.removePrecludedDate(d);
        return Payload.info(
            "Successfully removed precluded date.",
            "Removed precluded date: " + d);
    }
}
