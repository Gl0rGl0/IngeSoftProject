package V1.Ingsoft.controller.commands.running;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.AbstractCommand;
import V1.Ingsoft.controller.item.luoghi.Luogo;
import V1.Ingsoft.controller.item.luoghi.StatusVisita;
import V1.Ingsoft.controller.item.luoghi.TipoVisita;
import V1.Ingsoft.controller.item.luoghi.Visita;
import V1.Ingsoft.controller.item.persone.PersonaType;
import V1.Ingsoft.controller.item.persone.Volontario;
import V1.Ingsoft.view.ViewSE;

public class ListCommand extends AbstractCommand {

    private final Controller controller;
    private static final String ERRORE_NON_RICONOSCIUTO = "Opzione non riconosciuta per 'list'.";

    public ListCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    /**
     * Implementazione del comando "add".
     *
     * @param options le options (es. -c per configuratore)
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
            case 'L', 'l' -> listLuoghi();
            case 't' -> printTipiVisita();
            case 'V' -> printVisite(options);
            default -> ViewSE.println(ERRORE_NON_RICONOSCIUTO);
        }
        // Puoi aggiungere ulteriori casi per altri tipi (ad esempio 'V' per visita, 'L'
        // per luogo)
    }

    private void listLuoghi() {
        for (Luogo l : controller.db.dbLuoghiHelper.getLuoghi()) {
            ViewSE.println(l.getName());
            for (Visita visita : controller.db.trovaVisiteByLuogo(l)) {
                ViewSE.println(visita);
            }
        }
    }

    private void listVolontari() {
        StringBuilder out;
        for (Volontario v : controller.db.dbVolontarioHelper.getVolontari()) {
            out = new StringBuilder();
            out.append(v.getUsername() + ":\n");
            for (TipoVisita tv : controller.db.trovaTipoVisiteByVolontario(v)) {
                out.append(tv.getTitle() + ": " + (tv.getStatus() == StatusVisita.PROPOSTA ? "proposta" : "attesa")
                        + "\n");
            }
            ViewSE.println(out);
        }
    }

    private void printVisite(String[] s) {
        char option = 'a';
        option = s[1].charAt(0);

        switch (option) {
            case 'a' -> printAllTipi();
            case 'p' -> printProposte();
            case 'c' -> printComplete();
            case 'C' -> printCancellate();
            case 'e' -> printEffettuate();
            default -> ViewSE.println(ERRORE_NON_RICONOSCIUTO);
        }
    }

    private void printAvailabilityVolontari() {
        if (controller.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println(ERRORE_NON_RICONOSCIUTO);
            return;
        }

        int targetMonth = controller.date.getMonth() + 2;
        if (controller.date.getDay() > 16)
            targetMonth++;

        StringBuilder out = new StringBuilder();
        for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
            out.append(v.getUsername() + ":\n");
            int i = 1;
            for (boolean b : v.getAvailability()) {
                out.append(String.format("%d:%d [%b]", i++, targetMonth, b));
            }
        }
        ViewSE.println(out);
    }

    private void printTipiVisita() {
        if (controller.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println(ERRORE_NON_RICONOSCIUTO);
            return;
        }

        StringBuilder out = new StringBuilder();
        for (TipoVisita t : controller.db.dbTipoVisiteHelper.getTipoVisiteIstanziabili()) {
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
        controller.db.dbVisiteHelper.getVisiteProposte().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteComplete() {
        StringBuilder sb = new StringBuilder();
        controller.db.dbVisiteHelper.getCompletate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteCancellate() {
        StringBuilder sb = new StringBuilder();
        controller.db.dbVisiteHelper.getVisiteCancellate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }

    private String getVisiteEffettuate() {
        StringBuilder sb = new StringBuilder();
        controller.db.dbVisiteHelper.getVisiteEffettuate().forEach(v -> sb.append(v).append("\n"));
        return sb.toString();
    }
}
