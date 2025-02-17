package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;

public class PrecludeCommand extends AbstractCommand{
    
    public PrecludeCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per configuratore)
        if (options.length < 1) {
            ViewSE.print("Errore nell'utilizzo del comando 'add'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'a' -> {
                ViewSE.print("Eseguo: Aggiungo data da precludere");
                addPrecludedDate(args);
            }

            case 'r' -> {
                ViewSE.print("Eseguo: Rimuovo data da precludere");
                removePrecludedDate(args);
            }
            default -> ViewSE.print("Opzione non riconosciuta per 'preclude'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void addPrecludedDate(String[] args) {
        app.db.addPrecludedDate(new Date(args[0]));  //aggiunge una data speciale
    }

    private void removePrecludedDate(String[] args) {
        app.db.removePrecludedDate(new Date(args[0]));
    }
}
