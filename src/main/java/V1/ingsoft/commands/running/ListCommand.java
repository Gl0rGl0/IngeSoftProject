package V1.ingsoft.commands.running;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.luoghi.Luogo;
import V1.ingsoft.luoghi.TipoVisita;
import V1.ingsoft.luoghi.Visita;
import V1.ingsoft.persone.PersonaType;
import V1.ingsoft.persone.Volontario;

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
        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'list'");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari();
            case 'd' -> printAvailabilityVolontari();
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
            ViewSE.println(l.getName());
            for (Visita visita : app.db.trovaVisiteByLuogo(l)) {
                ViewSE.println(visita);
            }
        }
    }

    private void listVolontari() {
        StringBuilder out;
        for (Volontario v : app.db.getVolontari()) {
            out = new StringBuilder();
            out.append(v.getUsername() + ":\n");
            for (Visita visita : app.db.trovaVisiteByVolontario(v)) {
                out.append(visita + "\n");
            }
            ViewSE.println(out);
        }
    }

    private void printTipiVisite(String[] s) {
        char option = 'a';
        option = s[1].charAt(0);

        switch (option) {
            case 'a' -> printAllTipi();
            case 'p' -> printProposte();
            case 'c' -> printComplete();
            case 'C' -> printCancellate();
            case 'e' -> printEffettuate();
            case 'T' -> printTipiVisita();
            default -> ViewSE.println("Opzione non riconosciuta per 'list'.");
        }
    }

    private void printAvailabilityVolontari() {
        if (app.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println("Opzione non riconosciuta per 'list'.");
            return;
        }

        int targetMonth = app.date.getMonth() + 2;
        if (app.date.getDay() > 16)
            targetMonth++;

        StringBuilder out = new StringBuilder();
        for (Volontario v : app.db.dbVolontarioHelper.getPersonList()) {
            out.append(v.getUsername() + ":\n");
            int i = 1;
            for (boolean b : v.getAvailability()) {
                out.append(String.format("%d:%d [%b]", i++, targetMonth, b));
            }
        }
        ViewSE.println(out);
    }

    private void printTipiVisita() {
        if (app.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println("Opzione non riconosciuta per 'list'.");
            return;
        }

        StringBuilder out = new StringBuilder();
        for (TipoVisita t : app.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili()) {
            out.append(t);
        }
        ViewSE.println(out);
    }

    private void printAllTipi() {
        StringBuilder sb = new StringBuilder();
        sb.append("Visite proposte:\n").append(getVisiteProposte());
        sb.append("Visite complete:\n").append(getVisiteComplete());
        sb.append("Visite cancellate:\n").append(getVisiteCancellate());
        sb.append("Visite effettuate:\n").append(getVisiteEffettuate());
        ViewSE.println(sb.toString());
    }

    private void printProposte() {
        ViewSE.println(getVisiteProposte());
    }

    private void printComplete() {
        ViewSE.println(getVisiteComplete());
    }

    private void printCancellate() {
        ViewSE.println(getVisiteCancellate());
    }

    private void printEffettuate() {
        ViewSE.println(getVisiteEffettuate());
    }

    private String getVisiteProposte() {
        StringBuilder sb = new StringBuilder();
        app.db.dbVisiteHelper.getVisiteProposte().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteComplete() {
        StringBuilder sb = new StringBuilder();
        app.db.dbVisiteHelper.getCompletate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteCancellate() {
        StringBuilder sb = new StringBuilder();
        app.db.dbVisiteHelper.getCancellate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteEffettuate() {
        StringBuilder sb = new StringBuilder();
        app.db.dbVisiteHelper.getVisiteEffettuate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }
}
