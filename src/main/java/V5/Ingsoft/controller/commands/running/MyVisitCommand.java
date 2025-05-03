package V5.Ingsoft.controller.commands.running;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.commands.AbstractCommand;
import V5.Ingsoft.controller.item.StatusVisita;
import V5.Ingsoft.controller.item.luoghi.Visita;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.StringUtils;

public class MyVisitCommand extends AbstractCommand {
    private static final String ERROR = "Wrong usage of the command 'myvisit'.";

    public MyVisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MYVISIT;
    }

    @Override
    public Payload execute(String[] options, String[] args) {
        PersonaType tipo = controller.getCurrentUser().getType();
        switch (tipo) {
            case FRUITORE:
                return listFruitore();
            case VOLONTARIO:
                return listVolontari(options, args);
            default:
                return Payload.warn(
                    "Option valid only for volunteers and visitors.",
                    "Invalid persona type " + tipo);
        }
    }

    private Payload listFruitore() {
        String userF = controller.getCurrentUser().getUsername();
        Fruitore f = controller.getDB().dbFruitoreHelper.getPersona(userF);
        if (f == null) {
            return Payload.error(
                "User data not found.",
                "Fruitore object null for user " + userF);
        }

        StringBuilder out = new StringBuilder();
        for (String vUid : f.getVisiteUIDs()) {
            Visita v = controller.getDB().dbVisiteHelper.getVisitaByUID(vUid);
            if (v == null) continue;
            if (v.getStatus() == StatusVisita.CANCELLED) {
                out.append(v.getTitle())
                   .append(" ( ")
                   .append(v.getDate())
                   .append(" ) CANCELLED\n");
            } else {
                out.append(v).append("\n");
            }
        }
        String result = out.length() == 0
            ? "You are not signed up for any visits"
            : out.toString();
        return Payload.info(
            result,
            "Listed fruitore subscriptions for " + userF);
    }

    private Payload listVolontari(String[] options, String[] args) {
        String uid = controller.getCurrentUser().getUsername();
        if (options == null || options.length < 1) {
            return defaultVolunteerList(uid);
        }
        if (args == null) {
            return Payload.error(
                ERROR,
                "Args null for volunteer list");
        }
        String[] a = StringUtils.joinQuotedArguments(args);
        if (a.length < 2 || options[0].charAt(0) != 'l') {
            return Payload.error(
                ERROR,
                "Invalid options/args for volunteer list");
        }
        Visita v = controller.getDB().dbVisiteHelper.findVisita(a[0], a[1]);
        if (v == null) {
            return Payload.warn(
                "No visit found with that name or date.",
                "FindVisita returned null for " + a[0] + "," + a[1]);
        }
        StringBuilder out = new StringBuilder("List of registrations:\n");
        for (Iscrizione i : v.getIscrizioni()) {
            out.append(i).append("\n");
        }
        String result = out.length() == 0
            ? "No registration found for your visits"
            : out.toString();
        return Payload.info(
            result,
            "Listed iscrizioni for volunteer " + uid);
    }

    private Payload defaultVolunteerList(String uid) {
        StringBuilder out = new StringBuilder("List of visits you are assigned to:\n");
        for (Visita v : controller.getDB().dbVisiteHelper.getConfermate()) {
            if (v.getUidVolontario().equals(uid)) {
                out.append(v).append("\n");
            }
        }
        String result = out.length() == 0
            ? "You have not been assigned any visits"
            : out.toString();
        return Payload.info(
            result,
            "Listed confirmed visits for volunteer " + uid);
    }
}
