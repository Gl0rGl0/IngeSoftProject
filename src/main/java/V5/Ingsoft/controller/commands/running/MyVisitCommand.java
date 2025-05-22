package V5.Ingsoft.controller.commands.running;

import java.util.ArrayList;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.commands.running.list.CommandList;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

public class MyVisitCommand extends AbstractCommand {

    public MyVisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MYVISIT;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        PersonaType tipo = controller.getCurrentUser().getType();
        switch (tipo) {
            case FRUITORE:
                return listFruitore();
            case VOLONTARIO:
                return listVolontari( args);
            default:
                return Payload.warn(
                    "Option valid only for volunteers and visitors.",
                    "Invalid persona type " + tipo);
        }
    }

    private Payload<?> listFruitore() {
        String userF = controller.getCurrentUser().getUsername();
        Fruitore f = controller.getDB().dbFruitoreHelper.getPersona(userF);
        if (f == null) {
            return Payload.error(
                "User data not found.",
                "Fruitore object null for user " + userF);
        }

        ArrayList<Iscrizione> out = new ArrayList<>();
        for (Iscrizione i : controller.getDB().dbIscrizionisHelper.getIscrizioni()) {
            if(i.getUIDFruitore().equals(f.getUsername()))
                out.add(i);
        }
        
        if(out.size() == 0)
            return Payload.warn(
                "You are not signed up for any visits",
                "No visits found for the user: " + userF);

        return Payload.info(out, "Listed fruitore subscriptions for " + userF);
    }

    private Payload<?> listVolontari(String[] args) {
        String userV = controller.getCurrentUser().getUsername();

        if (args == null) {
            return Payload.error(
                "No args for volunteer list",
                "Args null for volunteer list");
        }
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a.length < 2) {
            return Payload.error(
                "Invalid args for volunteer list",
                "Invalid args for volunteer list");
        }

        ArrayList<TipoVisita> out = new ArrayList<>();
        for (TipoVisita tv : controller.getDB().dbTipoVisiteHelper.getTipiVisita()) {
            if (tv == null) continue;
            if (!tv.isUsable()) continue;

            if(tv.assignedTo(userV))
                out.add(tv);
        }
        if(out.size() == 0)
            return Payload.warn(
                "You have not been assigned any visits",
                "No visits found for the user: " + userV);

        return Payload.info(out, "Listed visit type for volunteer " + userV);
    }
}
