package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;
import java.util.ArrayList;

public class MyVisitCommand extends AbstractCommand {

    public MyVisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MYVISIT;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        PersonaType tipo = controller.getCurrentUser().getType();
        return switch (tipo) {
            case FRUITORE -> listFruitore();
            case VOLONTARIO -> listVolontari();
            default -> Payload.warn(
                    "Option valid only for volunteers and visitors.",
                    "Invalid persona type " + tipo);
                    //NON PUÒ ARRIVARE QUA PERCHÈ IL CONFIGURATORE NON HA I PERMESSI NECESSARI
        };
    }

    private Payload<?> listFruitore() {
        String userF = controller.getCurrentUser().getUsername();
        Fruitore f = Model.getInstance().dbFruitoreHelper.getPersona(userF);
        if (f == null) {
            return Payload.error(
                    "User data not found.",
                    "Fruitore object null for user " + userF);
        }

        ArrayList<Iscrizione> out = new ArrayList<>();
        for (Iscrizione i : Model.getInstance().dbIscrizionisHelper.getItems()) {
            if (i.getUIDFruitore().equals(f.getUsername()))
                out.add(i);
        }

        if (out.isEmpty())
            return Payload.warn(
                    "You are not signed up for any visits",
                    "No visits found for the user: " + userF);

        return Payload.info(out, "Listed fruitore subscriptions for " + userF);
    }

    private Payload<?> listVolontari() {
        String userV = controller.getCurrentUser().getUsername();

        ArrayList<TipoVisita> out = new ArrayList<>();
        for (TipoVisita tv : Model.getInstance().dbTipoVisiteHelper.getItems()) {
            if (tv == null) continue;
            if (!tv.isUsable()) continue;

            if (tv.assignedTo(userV))
                out.add(tv);
        }
        if (out.isEmpty())
            return Payload.warn(
                    "You have not been assigned any visits",
                    "No visits found for the user: " + userV);

        return Payload.info(out, "Listed visit type for volunteer " + userV);
    }
}
