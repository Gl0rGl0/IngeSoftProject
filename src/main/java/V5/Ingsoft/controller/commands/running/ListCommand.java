package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.util.Payload;

import java.util.ArrayList;
import java.util.Collection;

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
        switch (opt) {
            case 'v': return listVolontari();
            case 'T': return listTipoVisite();
            case 'L': return listLuoghi();
            case 'V': return listVisite(options);
            default:
                return Payload.warn("Option '" + opt + "' not recognized for 'list'", 
                    "Unknown option '" + opt + "'");
        }
    }
    
    private Payload<Collection<Luogo>> listLuoghi() {
        ArrayList<Luogo> out = new ArrayList<>();
        for (Luogo l : controller.getDB().dbLuoghiHelper.getLuoghi()) {
            if(!l.isUsable()) continue;
            out.add(l);
        }
        
        return Payload.info(out,"List of places returned.");
    }

    private Payload<Collection<Volontario>> listVolontari() {
        ArrayList<Volontario> out = new ArrayList<>();
        for (Volontario v : controller.getDB().dbVolontarioHelper.getPersonList()) {
            // if(!v.isUsable()) continue;
            out.add(v);
        }
        
        return Payload.info(out,"List of volunteer returned.");
    }

    private Payload<Collection<TipoVisita>> listTipoVisite() {
        ArrayList<TipoVisita> out = new ArrayList<>();
        for (TipoVisita t : controller.getDB().dbTipoVisiteHelper.getTipiVisita()) {
            // if(!t.isUsable()) continue;
            out.add(t);
        }
        
        return Payload.info(out,"List of visit type returned.");
    }

    private Payload<?> listVisite(String[] options) {
        char sub = (options.length > 1 && options[1] != null && !options[1].isEmpty())
            ? options[1].charAt(0) : 'a';
        return switch (sub) {
            case 'a' -> listAllType();
            case 'p' -> Payload.<Collection<Visita>>info(controller.getDB().dbVisiteHelper.getVisiteProposte(), "List of proposed visits returned");
            case 'c' -> Payload.<Collection<Visita>>info(controller.getDB().dbVisiteHelper.getCompletate(), "List of completed visits returned");
            case 'C' -> Payload.<Collection<Visita>>info(controller.getDB().dbVisiteHelper.getVisiteCancellate(), "List of cancelled visits returned");
            case 'e' -> Payload.<Collection<Visita>>info(controller.getDB().dbVisiteHelper.getVisiteEffettuate(), "List of performed visits returned");
            default  -> Payload.warn(
                            "Option '" + sub + "' not recognized for 'list V'", 
                            "Unknown sub-option '" + sub + "'");
        };
    }

    private Payload<Collection<Collection<Visita>>> listAllType() {
        ArrayList<Collection<Visita>> out =new ArrayList<>();
        out.add(controller.getDB().dbVisiteHelper.getVisiteProposte());
        out.add(controller.getDB().dbVisiteHelper.getCompletate());
        out.add(controller.getDB().dbVisiteHelper.getVisiteCancellate());
        out.add(controller.getDB().dbVisiteHelper.getVisiteEffettuate());
        return Payload.info(out,"Returned list of all visit categories");
    }
}
