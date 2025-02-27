package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.Luogo;
import ingsoft.luoghi.Visita;
import ingsoft.persone.Volontario;
import ingsoft.util.ViewSE;

public class ListCommand extends AbstractCommand {

    private final App app;

    public ListCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param opzioni le opzioni (es. -c per configuratore)
     * @param args    eventuali argomenti aggiuntivi
     */
    public void execute(String[] options, String[] args) {
        // Ad esempio, ci aspettiamo che il primo argomento sia l'opzione (es. "c" per
        // configuratore)
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'list'");
            return;
        }
        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari();
            case 'L' -> listLuoghi();
            case 'l' -> listLuoghi();
            case 'V' -> printTipiVisite(options);
            default -> ViewSE.println("Opzione non riconosciuta per 'list'.");
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L'
        // per luogo)
    }

    private void listLuoghi() {
        for (Luogo l : app.db.getLuoghi()) {
            for (Visita visita : app.db.trovaVisiteByLuogo(l)) {
                ViewSE.println(visita);
            }
        }
    }

    private void listVolontari() {
        for (Volontario v : app.db.getVolontari()) {
            for (Visita visita : app.db.trovaVisiteByVolontario(v)) {
                ViewSE.println(visita);
            }
        }
    }

    private void printTipiVisite(String[] s) {
        char option = 'a';
        try {
            option = s[1].charAt(0);
        } catch (Exception e) {
            //
        }

        switch (option) {
            case 'a' -> printAllTipi();
            case 'p' -> printProposte();
            case 'c' -> printComplete();
            case 'C' -> printCancellate();
            case 'e' -> printEffettuate();
            default -> ViewSE.println("Opzione non riconosciuta per 'list'.");
        }
    }

    private void printAllTipi() {
        ViewSE.println("Visite proposte:");
        printProposte();

        ViewSE.println("Visite complete:");
        printComplete();

        ViewSE.println("Visite cancellate:");
        printCancellate();

        ViewSE.println("Visite effettuate:");
        printEffettuate();
    }

    private void printProposte() {
        app.db.dbVisiteHelper.getVisiteProposte().forEach(v -> ViewSE.println(v));
    }

    private void printComplete() {
        app.db.dbVisiteHelper.getCompletate().forEach(v -> ViewSE.println(v));
    }

    private void printCancellate() {
        app.db.dbVisiteHelper.getCancellate().forEach(v -> ViewSE.println(v));
    }

    private void printEffettuate() {
        app.db.dbVisiteHelper.getVisiteEffettuate().forEach(v -> ViewSE.println(v));
    }
}
