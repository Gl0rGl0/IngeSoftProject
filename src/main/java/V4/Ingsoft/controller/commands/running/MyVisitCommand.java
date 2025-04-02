package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class MyVisitCommand extends AbstractCommand {
    private final Controller controller;

    public MyVisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MYVISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {

        PersonaType tipo = controller.getCurrentUser().getType();

        switch (tipo) {
            case FRUITORE -> listFruitore();
            case VOLONTARIO -> listVolontari();
            default -> ViewSE.println("Option valid only for volunteers and visitors (fruitori)");
        }
    }

    private void listVolontari() {
        String uidCV = controller.getCurrentUser().getUsername();
        StringBuilder out = new StringBuilder();

        out.append("List of visits you are assigned to: \n");
        // controller.db.dbVisiteHelper.getConfermate() cannot be done because a
        // visit can
        // have multiple available volunteers
        for (Visita v : controller.db.dbVisiteHelper.getVisite()) {
            if (v.getUidVolontario().equals(uidCV))
                out.append(v + "\n");
        }

        ViewSE.println(out);
    }

    private void listFruitore() {
        String userF = controller.getCurrentUser().getUsername();
        StringBuilder out = new StringBuilder();

        out.append("List of visits you are registered for: ");
        for (Visita v : controller.db.dbVisiteHelper.getVisite()) {
            if (v.hasFruitore(userF))
                out.append(v + "\n");
        }

        ViewSE.println(out);
    }
}
