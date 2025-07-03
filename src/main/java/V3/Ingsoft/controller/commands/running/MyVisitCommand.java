package V3.Ingsoft.controller.commands.running;

import V3.Ingsoft.controller.Controller;
import V3.Ingsoft.controller.commands.AbstractCommand;
import V3.Ingsoft.controller.item.luoghi.Visita;
import V3.Ingsoft.controller.item.persone.Iscrizione;
import V3.Ingsoft.controller.item.persone.PersonaType;
import V3.Ingsoft.util.AssertionControl;
import V3.Ingsoft.util.StringUtils;
import V3.Ingsoft.view.ViewSE;

public class MyVisitCommand extends AbstractCommand {

    private final String ERROR = "Wrong usage of the command 'myvisit'.";

    public MyVisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.MYVISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {

        PersonaType tipo = controller.getCurrentUser().getType();

        switch (tipo) {
            case VOLONTARIO -> listVolontari(options, args);
            default -> ViewSE.println("Option valid only for volunteers and visitors (fruitori)");
        }
    }

    private void listVolontari(String[] options, String[] args) {
        String uidCV = controller.getCurrentUser().getUsername();

        if (uidCV == null)
            return;

        if(options == null || options.length < 1){
            defaultVolunteerList(uidCV);
            return;
        }

        if(args == null){
            ViewSE.println(ERROR);
            AssertionControl.logMessage(ERROR + " args null", 2, CLASSNAME);
            return;
        }

        String[] a = StringUtils.joinQuotedArguments(args);
        if(a.length < 2){
            ViewSE.println(ERROR);
            AssertionControl.logMessage(ERROR + ", not enough args", 2, CLASSNAME);
            return;
        }

        if(options[0] == null || options[0].charAt(0 ) != 'l' || a[0] == null || a[1] == null){
            ViewSE.println(ERROR);
            AssertionControl.logMessage(ERROR + ", wrong option char", 2, CLASSNAME);
            return;
        }

        Visita v = controller.getDB().dbVisiteHelper.findVisita(a[0], a[1]);
        if(v == null){
            ViewSE.println("No visit found with that name or in that date.");
            AssertionControl.logMessage("No visit found with that name or in that date.", 2, CLASSNAME);
            return;
        }

        StringBuilder out = new StringBuilder();

        out.append("List of subscription: \n");
        boolean addedSmth = false;
        for(Iscrizione i : v.getIscrizioni()){
            out.append(i).append("\n");
            addedSmth = true;
        }

        if(!addedSmth){
            out.setLength(0);
            out.append("No subscription done in your visits");
        }

        ViewSE.print(out);
    }

    private void defaultVolunteerList(String uidCV){
        StringBuilder out = new StringBuilder();
        boolean addedSmth = false;

        out.append("List of visits you are assigned to: \n");
        for (Visita v : controller.getDB().dbVisiteHelper.getConfermate()) {
            if (v.getUidVolontario().equals(uidCV)){
                out.append(v).append("\n");
                addedSmth = true;
            }
        }

        if(!addedSmth){
            out.setLength(0);
            out.append("You have not been assigned any visits");
        }

        ViewSE.println(out);
    }
}
