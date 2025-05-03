package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.StatusItem;
import V5.Ingsoft.controller.item.luoghi.Luogo;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.util.Payload;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends AbstractCommand {
    private static final String CLASSNAME = ListCommand.class.getSimpleName();

    public ListCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        if (options == null || options.length < 1) {
            return Payload.error(
                "Usage: list -<v|T|L|V>",
                CLASSNAME + ": missing option");
        }
        char opt = options[0].charAt(0);
        switch (opt) {
            case 'v': return listVolontari();
            case 'T': return printVisitTypes();
            case 'L': return listLuoghi();
            case 'V': return printVisite(options);
            default:
                return Payload.warn(
                    "Option '" + opt + "' not recognized for 'list'", 
                    CLASSNAME + ": unknown option '" + opt + "'");
        }
    }

    private Payload listLuoghi() {
        StringBuilder out = new StringBuilder();
        for (Luogo l : controller.getDB().dbLuoghiHelper.getLuoghi()) {
            out.append(l.getName())
               .append(" (")
               .append(l.getStatus())
               .append("):\n");
            if (l.getStatus() == StatusItem.DISABLED) break;
            ArrayList<TipoVisita> visits = controller.getDB().trovaTipoVisiteByLuogo(l);
            if (visits.isEmpty()) {
                out.append("No visit type associated with this place\n");
            } else {
                visits.forEach(v -> out.append(v).append("\n"));
            }
            out.append("\n");
        }
        String result = out.length() == 0
            ? "No place exists." : out.toString();
        return Payload.info(
            result,
            CLASSNAME + ": listed places");
    }

    private Payload listVolontari() {
        StringBuilder out = new StringBuilder();
        for (Volontario v : controller.getDB().dbVolontarioHelper.getPersonList()) {
            out.append(v.getUsername())
               .append(" (")
               .append(v.getStatus())
               .append("):\n");
            if (v.getStatus() == StatusItem.DISABLED) break;
            ArrayList<TipoVisita> types = controller.getDB().trovaTipoVisiteByVolontario(v);
            if (types.isEmpty()) {
                out.append("No visit type associated with this volunteer\n");
            } else {
                types.forEach(tv -> out.append(tv).append("\n"));
            }
            out.append("\n");
        }
        String result = out.length() == 0
            ? "No volunteer exists." : out.toString();
        return Payload.info(
            result,
            CLASSNAME + ": listed volunteers");
    }

    private Payload printVisitTypes() {
        if (controller.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            return Payload.error(
                "Not authorized to list visit types", 
                CLASSNAME + ": unauthorized access");
        }
        StringBuilder out = new StringBuilder();
        controller.getDB().dbTipoVisiteHelper.getTipoVisiteIstanziabili()
            .forEach(t -> out.append(t).append("\n"));
        String result = out.length() == 0
            ? "No instantiable visit types." : out.toString();
        return Payload.info(
            result,
            CLASSNAME + ": listed visit types");
    }

    private Payload printVisite(String[] options) {
        char sub = (options.length > 1 && options[1] != null && !options[1].isEmpty())
            ? options[1].charAt(0) : 'a';
        switch (sub) {
            case 'a': return printAllTipi();
            case 'p': return printCategory("Proposed visits:", controller.getDB().dbVisiteHelper.getVisiteProposte());
            case 'c': return printCategory("Completed visits:", controller.getDB().dbVisiteHelper.getCompletate());
            case 'C': return printCategory("Cancelled visits:", controller.getDB().dbVisiteHelper.getVisiteCancellate());
            case 'e': return printCategory("Performed visits:", controller.getDB().dbVisiteHelper.getVisiteEffettuate());
            default:
                return Payload.warn(
                    "Option '" + sub + "' not recognized for 'list V'", 
                    CLASSNAME + ": unknown sub-option '" + sub + "'");
        }
    }

    private Payload printAllTipi() {
        StringBuilder out = new StringBuilder();
        out.append("Proposed visits:\n")  .append(getVisiteProposte())
           .append("Completed visits:\n").append(getVisiteComplete())
           .append("Cancelled visits:\n").append(getVisiteCancellate())
           .append("Performed visits:\n").append(getVisiteEffettuate());
        return Payload.info(
            out.toString(),
            CLASSNAME + ": listed all visit categories");
    }

    private Payload printCategory(String header, List<?> list) {
        StringBuilder out = new StringBuilder(header).append("\n");
        list.forEach(v -> out.append(v).append("\n"));
        String result = out.length() == header.length()+1
            ? header + " None" : out.toString();
        return Payload.info(
            result,
            CLASSNAME + ": listed " + header);
    }

    private String getVisiteProposte() {
        return buildList(controller.getDB().dbVisiteHelper.getVisiteProposte(), "There are no proposed visits");
    }
    private String getVisiteComplete() {
        return buildList(controller.getDB().dbVisiteHelper.getCompletate(), "There are no completed visits");
    }
    private String getVisiteCancellate() {
        return buildList(controller.getDB().dbVisiteHelper.getVisiteCancellate(), "There are no cancelled visits");
    }
    private String getVisiteEffettuate() {
        return buildList(controller.getDB().dbVisiteHelper.getVisiteEffettuate(), "There are no performed visits");
    }
    private String buildList(java.util.List<?> list, String emptyMsg) {
        if (list.isEmpty()) return emptyMsg + "\n";
        StringBuilder out = new StringBuilder();
        list.forEach(v -> out.append(v).append("\n"));
        return out.toString();
    }
}
