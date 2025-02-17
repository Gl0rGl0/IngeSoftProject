package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Volontario;
import ingsoft.util.ViewSE;

public class ListCommand extends AbstractCommand {

    public ListCommand(App app, CommandList commandInfo) {
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
            case 'v' -> listVolontari();
            case 'L' -> listLuoghi();
            case 'l' -> listLuoghi();
            case 'V' -> listVisite();
            default -> ViewSE.print("Opzione non riconosciuta per 'list'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L' per luogo)
    }

    private void listLuoghi(){
        //for (Luogo l : app.db.getLuoghi()) {
        //    ViewSE.print(l);
        //}
        app.db.getLuoghi().forEach(l -> ViewSE.print(l)); //boh sono uguali
    }

    private void listVolontari(){
        for (Volontario v : app.db.getVolontari()) {
            ViewSE.print(v);
        }
    }

    private void listVisite(){
        for (Visita v : app.db.getVisite()) {
            ViewSE.print(v);
        }
    }

}
