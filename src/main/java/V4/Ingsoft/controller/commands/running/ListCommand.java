package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Luogo;
import V4.Ingsoft.controller.item.luoghi.StatusVisita;
import V4.Ingsoft.controller.item.luoghi.TipoVisita;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.controller.item.persone.Volontario;
import V4.Ingsoft.view.ViewSE;

public class ListCommand extends AbstractCommand {

    private final Controller controller;
    private static final String ERROR_NOT_RECOGNIZED = "Option not recognized for 'list'.";

    public ListCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    /**
     * Implementation of the "list" command. // Corrected command name
     *
     * @param options the options (e.g., -c for configurator)
     * @param args    any additional arguments
     */
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Error using the 'list' command");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari();
            case 'd' -> printAvailabilityVolontari();
            case 'L', 'l' -> listLuoghi();
            case 't' -> printTipiVisita();
            case 'V' -> printVisite(options);
            default -> ViewSE.println(ERROR_NOT_RECOGNIZED);
        }
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
        for (Volontario v : controller.db.dbVolontarioHelper.getPersonList()) {
            out = new StringBuilder();
            out.append(v.getUsername() + ":\n");
            for (TipoVisita tv : controller.db.trovaTipoVisiteByVolontario(v)) {
                // Use the translated enum constant PROPOSED
                out.append(tv.getTitle() + ": " + (tv.getStatus() == StatusVisita.PROPOSED ? "proposed" : "pending")
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
            default -> ViewSE.println(ERROR_NOT_RECOGNIZED);
        }
    }

    private void printAvailabilityVolontari() {
        if (controller.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println(ERROR_NOT_RECOGNIZED);
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
            ViewSE.println(ERROR_NOT_RECOGNIZED);
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
        sb.append("Proposed visits:\n").append(getVisiteProposte());
        sb.append("Completed visits:\n").append(getVisiteComplete());
        sb.append("Cancelled visits:\n").append(getVisiteCancellate());
        sb.append("Performed visits:\n").append(getVisiteEffettuate());
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
