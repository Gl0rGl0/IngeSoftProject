package V4.Ingsoft.controller.commands;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.running.CommandList;
import V4.Ingsoft.util.Date;
import V4.Ingsoft.view.ViewSE;

public class TimeCommand extends AbstractCommand {
    Controller controller;

    public TimeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1 && options.length == 0) {
            ViewSE.println(controller.date);
            return;
        }
        if (options.length > 0 && args.length > 0) {
            switch (options[0]) {
                case "s" -> {
                    controller.date = new Date(args[0]);
                    controller.dailyAction();
                }
                case "d" -> jumpDays(args[0], 1);
                case "m" -> jumpDays(args[0], 30);
                case "a" -> jumpDays(args[0], 365);
                case "n" -> controller.addPrecludedDate(args[0]);
                case "l" -> ViewSE.println(controller.db.getPrecludedDates());
                default -> ViewSE.println("Opzione non riconosciuta per il comando 'time'.");
            }
        } else {
            ViewSE.println(controller.date);
        }
    }

    private void jumpDays(String d, int mult) {
        int g;
        try {
            g = Integer.parseInt(d) * mult;
        } catch (NumberFormatException e) {
            ViewSE.println("Uso sbagliato del prompt");
            return;
        }

        controller.date.update(g);
        if (g > 0) {
            ViewSE.println("Saltati con successo " + g + " giorni.");
        } else {
            ViewSE.println("Tornato indietro di " + g + " giorni con successo.");
        }
    }
}