package V5.Ingsoft.controller.commands;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;

public class TimeCommand extends AbstractCommand {

    public TimeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public Payload<String> execute(String[] options, String[] args) {
        // Verifica controller
        if (controller == null) {
            return Payload.error(
                "FATAL: controller not initialized.",
                "TimeCommand executed with null controller." );
        }
        // Verifica struttura
        if (options == null || args == null) {
            return Payload.error(
                "Internal error: invalid command structure.",
                "Options or args are null in TimeCommand.execute" );
        }
        // Caso base: nessuna opzione e nessun argomento
        if (options.length == 0 && args.length == 0) {
            return displayCurrentDate();
        }
        // Se arriva almeno un’opzione valida
        String opt = options.length > 0 ? options[0] : null;
        if (opt == null || opt.isBlank()) {
            return usageWarning();
        }
        try {
            return switch (opt) {
                case "s" -> handleSetDate(args);
                case "d" -> handleAddDays(args);
                case "m" -> handleAddMonths(args);
                default  -> Payload.warn(
                        "Option '-" + opt + "' not recognized.",
                        "Unknown option in TimeCommand: " + opt );
            };
        } catch (Exception e) {
            return Payload.error(
                "Error processing time command.",
                "Exception in TimeCommand: " + e.getMessage() );
        }
    }

    private Payload<String> displayCurrentDate() {
        if (controller.date != null) {
            return Payload.info(
                "Current date: " + controller.date,
                "Displayed current date: " + controller.date );
        } else {
            return Payload.error(
                "Current date not available.",
                "controller.date is null in TimeCommand" );
        }
    }

    private Payload<String> handleSetDate(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            return Payload.warn(
                "Usage: time -s <dd/mm/yyyy>",
                "Missing date argument for -s" );
        }
        try {
            controller.date = new Date(args[0]);
            controller.dailyAction();
            return Payload.info(
                "Date set: " + controller.date,
                "New date set: " + controller.date );
        } catch (Exception e) {
            return Payload.error(
                "Error processing time command.",
                "Exception in TimeCommand set-date: " + e.getMessage() );
        }
    }

    private Payload<String> handleAddDays(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            return Payload.warn(
                "Usage: time -d <number_of_days>",
                "Missing days argument for -d" );
        }
        int days;
        try {
            days = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return Payload.warn(
                "Invalid number of days: " + args[0],
                "ParseInt failed for -d: " + e.getMessage() );
        }
        // Se days è negativo o zero, il comportamento originale iterava 0 volte; qui lo rispettiamo
        for (int i = 0; i < days; i++) {
            controller.date.addDay(1);
            controller.dailyAction();
        }
        return Payload.info(
            "Added " + days + " days, current date: " + controller.date,
            "AddDays: +" + days );
    }

    private Payload<String> handleAddMonths(String[] args) {
        if (args.length < 1 || args[0].isBlank()) {
            return Payload.warn(
                "Usage: time -m <number_of_months>",
                "Missing months argument for -m" );
        }
        int months;
        try {
            months = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return Payload.warn(
                "Invalid number of months: " + args[0],
                "ParseInt failed for -m: " + e.getMessage() );
        }
        for (int i = 0; i < months; i++) {
            controller.date.addMonth(1);
            controller.dailyAction();
        }
        return Payload.info(
            "Added " + months + " months, current date: " + controller.date,
            "AddMonths: +" + months );
    }

    private Payload<String> usageWarning() {
        return Payload.warn(
            "Correct usage: 'time' or 'time -<s|d|m> [arg]'.",
            "Invalid combination of options/args in TimeCommand" );
    }
}