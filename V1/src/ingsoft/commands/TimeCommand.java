package ingsoft.commands;

import ingsoft.App;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;

public class TimeCommand extends AbstractCommand {

    public TimeCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (args.length < 1 && options.length == 0) {
            ViewSE.print(app.date);
            return;
        }
        int g = 0;
        if(options.length > 0){
            switch (options[0]) {
                case "s" -> app.date = new Date(args[0]);
                case "m" -> g = Integer.parseInt(args[0]) * 30;
                case "a" -> g = Integer.parseInt(args[0]) * 365;
                case "n" -> {app.addSpecialDate(args[0], args[1]); return;}
                case "l" -> {ViewSE.print(app.getDate()); return;}
                default ->  {try {
                    g = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    ViewSE.print("Uso sbagliato del prompt");
                    return;
                }}
            }
        }else{
            try {
                g = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                ViewSE.print("Uso sbagliato del prompt");
                return;
            }
           
        }
        app.date.modifica(g);
        if(g > 0){
            ViewSE.print("Saltati con successo " + g + " giorni.");
        }else{
            ViewSE.print("Tornato indietro di " + g + " giorni con successo.");
        }
    }
}