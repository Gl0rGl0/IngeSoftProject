package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.list.CommandList;
import V5.Ingsoft.controller.item.interfaces.AbstractCommand;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Payload;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListCommand extends AbstractCommand {

    public ListCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    public Payload<?> execute(String[] options, String[] args) {
        if (options == null || options.length < 1) {
            return Payload.error(
                    "Usage: list -<v|T|L|V>",
                    "Missing option");
        }
        char opt = options[0].charAt(0);
        return switch (opt) {
            case 'v' -> listVolontari();
            case 'T' -> listTipoVisite();
            case 'L' -> listLuoghi();
            case 'V' -> listVisite(options);
            default  -> Payload.warn("Option '" + opt + "' not recognized for 'list'",
                    "Unknown option '" + opt + "'");
        };
    }

    private Payload<Collection<Luogo>> listLuoghi() {
        ArrayList<Luogo> out = new ArrayList<>();
        for (Luogo l : Model.getInstance().dbLuoghiHelper.getItems()) {
            if (!l.isUsable()) continue;
            out.add(l);
        }

        return Payload.info(out, "List of places returned.");
    }

    private Payload<Collection<Volontario>> listVolontari() {
        //        for (Volontario v : Model.getInstance().dbVolontarioHelper.getItems()) {
//            // if(!v.isUsable()) continue;
//            out.add(v);
//        }

        List<Volontario> out = new ArrayList<>(Model.getInstance().dbVolontarioHelper.getItems());
        return Payload.info(out, "List of volunteer returned.");
    }

    private Payload<Collection<TipoVisita>> listTipoVisite() {
//        ArrayList<TipoVisita> out = new ArrayList<>();
//        for (TipoVisita t : Model.getInstance().dbTipoVisiteHelper.getItems()) {
//            // if(!t.isUsable()) continue;
//            out.add(t);
//        }

        List<TipoVisita> out = new ArrayList<>(Model.getInstance().dbTipoVisiteHelper.getItems());
        return Payload.info(out, "List of visit type returned.");
    }

    private Payload<?> listVisite(String[] options) {
        char sub = (options.length > 1 && options[1] != null && !options[1].isEmpty())
                ? options[1].charAt(0) : 'a';
        return switch (sub) {
            case 'a' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getItems(), "List of all visit categories returned");
            case 'p' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getVisiteProposte(), "List of proposed visits returned");
            case 'c' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getVisiteComplete(), "List of complete visits returned");
            case 'C' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getVisiteConfermate(), "List of confirmed visits returned");
            case 'd' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getVisiteCancellate(), "List of cancelled visits returned");
            case 'e' -> Payload.<Collection<Visita>>info(Model.getInstance().dbVisiteHelper.getVisiteEffettuate(), "List of performed visits returned");
            default -> Payload.warn( "Option '" + sub + "' not recognized for 'list V'", "Unknown sub-option '" + sub + "'" );
        };
    }
}
