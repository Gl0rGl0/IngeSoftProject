package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Volontario;
import ingsoft.util.StringUtils;
import ingsoft.util.ViewSE;

public class AddCommand extends AbstractCommand {

    public AddCommand(App app, CommandList commandInfo) {
        super(app, commandInfo);
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per configuratore)
        if (options.length < 1) {
            ViewSE.print("Errore nell'utilizzo del comando 'list'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari(args);
            case 'L' -> listLuoghi(args);
            case 'l' -> listLuoghi(args);
            case 'V' -> listVisite(args);
            default -> ViewSE.print("Opzione non riconosciuta per 'list'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void listLuoghi(String[] args){
        for (Luogo l : app.db.getLuoghi()) {
            ViewSE.print(l);
        }
    }

    private void listVolontari(String[] args){
        for (Volontario v : app.db.getVolontari()) {
            ViewSE.print(v);
        }
    }

    private void listVisite(String[] args){
        for (Visita v : app.db.getVisite()) {
            ViewSE.print(v);
        }
    }

}
