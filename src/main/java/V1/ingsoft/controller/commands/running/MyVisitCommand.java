package V1.ingsoft.controller.commands.running;

import V1.ingsoft.controller.Controller;
import V1.ingsoft.controller.commands.AbstractCommand;
import V1.ingsoft.controller.item.luoghi.Visita;
import V1.ingsoft.controller.item.persone.PersonaType;
import V1.ingsoft.view.ViewSE;

public class MyVisitCommand extends AbstractCommand {
    private final Controller app;

    public MyVisitCommand(Controller app) {
        this.app = app;
        super.commandInfo = CommandList.MYVISIT; // MYVISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {

        PersonaType tipo = app.getCurrentUser().getType();

        switch (tipo) {
            case FRUITORE -> listFruitore();
            case VOLONTARIO -> listVolontari();
            default -> ViewSE.println("Opzione valida solo per volontari e fruitori");
        }
    }

    private void listVolontari() {
        String uidCV = app.getCurrentUser().getUsername();
        StringBuilder out = new StringBuilder();

        out.append("Lista delle visite al quale sei convocato: \n");
        // app.db.dbVisiteHelper.getConfermate() non si può fare perchè una visita può
        // avere piu volontari disponibili
        for (Visita v : app.db.dbVisiteHelper.getVisite()) {
            if (v.getUidVolontario().equals(uidCV))
                out.append(v + "\n");
        }

        ViewSE.println(out);
    }

    private void listFruitore() {
        String userF = app.getCurrentUser().getUsername();
        StringBuilder out = new StringBuilder();

        out.append("Lista delle visite al quale sei iscritto: ");
        for (Visita v : app.db.dbVisiteHelper.getVisite()) {
            if (v.hasFruitore(userF))
                out.append(v + "\n");
        }

        ViewSE.println(out);
    }
}
