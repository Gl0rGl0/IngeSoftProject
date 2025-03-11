package V1.ingsoft.commands.running;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.luoghi.Visita;
import V1.ingsoft.persone.PersonaType;

public class MyVisitCommand extends AbstractCommand {
    private final App app;

    public MyVisitCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.ADD; // MYVISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {

        PersonaType tipo = app.getCurrentUser().type();

        switch (tipo) {
            case FRUITORE -> listFruitore();
            case VOLONTARIO -> listVolontari();
            default -> ViewSE.println("Opzione non riconosciuta per 'myvisit'.");
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
            if (v.isPresenteFruitore(userF))
                out.append(v + "\n");
        }

        ViewSE.println(out);
    }
}
