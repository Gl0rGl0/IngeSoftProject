package V4.Ingsoft.controller.commands.running;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.commands.AbstractCommand;
import V4.Ingsoft.controller.item.luoghi.Visita;
import V4.Ingsoft.controller.item.persone.Fruitore;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.view.ViewSE;

public class MyVisitCommand extends AbstractCommand {


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

        if(uidCV == null)
            return;

        StringBuilder out = new StringBuilder();

        out.append("List of visits you are assigned to: \n");
        for (Visita v : controller.db.dbVisiteHelper.getConfermate()) {
            if (v.getUidVolontario().equals(uidCV))
                out.append(v).append("\n");
        }

        ViewSE.println(out);
    }

    private void listFruitore() {
        String userF = controller.getCurrentUser().getUsername();

        if(userF == null)
            return;

        Fruitore f = controller.getDB().dbFruitoreHelper.getPersona(userF);
        if(f == null)
            return;

        StringBuilder out = new StringBuilder();

        out.append("List of visits you are registered for: ");
        Visita v;
        for(String vUid : f.getVisiteUIDs()){
            v = controller.db.dbVisiteHelper.getVisitaByUID(vUid);
            if(v == null)
                continue;
            out.append(v).append("\n");
        }

        ViewSE.println(out);
    }
}
