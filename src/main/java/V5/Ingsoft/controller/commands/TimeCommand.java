package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

public class TimeCommand extends AbstractCommand {
    public TimeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        // Controller null-check
        if (controller == null) {
            return Payload.error(
                    "FATAL: controller not initialized.",
                    "TimeCommand executed with null controller.");
        }

        // Structure null-check
        if (options == null || args == null) {
            return Payload.error(
                    "Internal error: invalid command structure.",
                    "Options or args are null in TimeCommand.execute");
        }

        // Case 1: no options, no args -> display current date
        if (options.length == 0 && args.length == 0) {
            if (controller.date != null) {
                return Payload.info(
                        "Current date: " + controller.date,
                        "Displayed current date: " + controller.date);
            } else {
                return Payload.error(
                        "Current date not available.",
                        "controller.date is null in TimeCommand");
            }
        }

        // Case 2: option provided
        if (options.length > 0 && options[0] != null && !options[0].isBlank()) {
            String opt = options[0];
            try {
                switch (opt) {
                    case "s": // set date
                        if (args.length < 1 || args[0].isBlank()) {
                            return Payload.warn(
                                    "Usage: time -s <dd/mm/yyyy>",
                                    "Missing date argument for -s");
                        }

                        controller.date = new Date(args[0]);
                        controller.dailyAction();
                        return Payload.info(
                                "Date set: " + controller.date,
                                "New date set: " + controller.date);

                    case "d": // add days
                        int days;
                        try {
                            days = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            return Payload.warn(
                                    "Invalid number of days: " + args[0],
                                    "ParseInt failed for -d: " + e.getMessage());
                        }

                        for (int i = 0; i < days; i++) {
                            controller.date.addDay(1);
                            controller.dailyAction();
                        }

                        return Payload.info(
                                "Added " + days + " days, current date: " + controller.date,
                                "AddDays: +" + days);

                    case "m": // add months
                        int months;
                        try {
                            months = Integer.parseInt(args[0]);
                        } catch (NumberFormatException e) {
                            return Payload.warn(
                                    "Invalid number of months: " + args[0],
                                    "ParseInt failed for -m: " + e.getMessage());
                        }

                        for (int i = 0; i < months; i++) {
                            controller.date.addMonth(1);
                            controller.dailyAction();
                        }

                        return Payload.info(
                                "Added " + months + " months, current date: " + controller.date,
                                "AddMonths: +" + months);

                    default:
                        return Payload.warn(
                                "Option '-" + opt + "' not recognized.",
                                "Unknown option in TimeCommand: " + opt);
                }
            } catch (Exception e) {
                return Payload.error(
                        "Error processing time command.",
                        "Exception in TimeCommand: " + e.getMessage());
            }
        }

        // incorrect usage
        return Payload.warn(
                "Correct usage: 'time' or 'time -<s|d|m> [arg]'.",
                "Invalid combination of options/args in TimeCommand");
    }
}