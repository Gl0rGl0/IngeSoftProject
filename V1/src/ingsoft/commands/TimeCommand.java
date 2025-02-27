package ingsoft.commands;

import ingsoft.App;
import ingsoft.commands.running.CommandList;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;

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
        if(options.length > 0){
            switch (options[0]) {
                case "s" -> app.date = new Date(args[0]);
                case "d" -> saltaG(args[0], 1);
                case "m" -> saltaG(args[0], 30);
                case "a" -> saltaG(args[0], 365);
                case "n" -> app.addPrecludedDate(args[0]);
                case "l" -> ViewSE.println(app.getDate());
            }
        }else{
            ViewSE.println(app.date);
        }
    }

    private void saltaG(String d, int mult){
        int g;
        try {
            g = Integer.parseInt(d) * mult;
        } catch (NumberFormatException e) {
            ViewSE.println("Uso sbagliato del prompt");
            return;
        }

        app.date.modifica(g);
        if(g > 0){
            ViewSE.println("Saltati con successo " + g + " giorni.");
        }else{
            ViewSE.println("Tornato indietro di " + g + " giorni con successo.");
        }
    }
}