package ingsoft.commands.running;

import ingsoft.App;
import ingsoft.ViewSE;
import ingsoft.commands.AbstractCommand;
import ingsoft.luoghi.Visita;
import ingsoft.persone.PersonaType;

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
        ViewSE.println("Lista delle visite al quale sei convocato: ");
        // app.db.dbVisiteHelper.getConfermate() non si può fare perchè una visita può
        // avere piu volontari disponibili

    }

    private void listFruitore() {
        String userF = app.getCurrentUser().getUsername();
        ViewSE.println("Lista delle visite al quale sei iscritto: ");
        for (Visita v : app.db.dbVisiteHelper.getVisite()) {
            if (v.isPresenteFruitore(userF))
                ViewSE.println(v.toString());
        }
    }
}
