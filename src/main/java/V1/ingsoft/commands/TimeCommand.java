package V1.ingsoft.commands;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.running.CommandList;
import V1.ingsoft.util.Date;

public class TimeCommand extends AbstractCommand {
    App app;

    public TimeCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.TIME;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1 && options.length == 0) {
            ViewSE.println(app.date);
            return;
        }
        if (options.length > 0) {
            switch (options[0]) {
                case "s" -> {
                    app.date = new Date(args[0]);
                    app.dailyAction();
                }
                case "d" -> jumpDays(args[0], 1);
                case "m" -> jumpDays(args[0], 30);
                case "a" -> jumpDays(args[0], 365);
                case "n" -> app.addPrecludedDate(args[0]);
                case "l" -> ViewSE.println(app.db.getPrecludedDates());
            }
        } else {
            ViewSE.println(app.date);
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

        app.date.update(g);
        if (g > 0) {
            ViewSE.println("Saltati con successo " + g + " giorni.");
        } else {
            ViewSE.println("Tornato indietro di " + g + " giorni con successo.");
        }
    }
}