package V2.Ingsoft.controller.commands.running;

import V2.Ingsoft.controller.Controller;
import V2.Ingsoft.controller.commands.AbstractCommand;
import V2.Ingsoft.controller.item.StatusItem;
import V2.Ingsoft.controller.item.luoghi.Luogo;
import V2.Ingsoft.controller.item.luoghi.TipoVisita;
import V2.Ingsoft.controller.item.luoghi.Visita;
import V2.Ingsoft.controller.item.persone.PersonaType;
import V2.Ingsoft.controller.item.persone.Volontario;
import V2.Ingsoft.view.ViewSE;

import java.util.ArrayList;

public class ListCommand extends AbstractCommand {


    private static final String ERROR_NOT_RECOGNIZED = "Option not recognized for 'list'.";

    public ListCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.LIST;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Error using the 'list' command");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'v' -> listVolontari();
            //case 'd' -> printAvailabilityVolontari();
            case 't' -> printTipiVisita();
            case 'L' -> listLuoghi();
            case 'V' -> printVisite(options);
            default -> ViewSE.println(ERROR_NOT_RECOGNIZED);
        }
    }

    private void listLuoghi() {
        StringBuilder out = new StringBuilder();
        for (Luogo l : controller.getDB().dbLuoghiHelper.getLuoghi()) {
            out.append(l.getName() + " ( " + l.getStatus() + " )").append(":\n");
            
            if(l.getStatus() == StatusItem.DISABLED)
                break;

            ArrayList<Visita> vL = controller.getDB().trovaVisiteByLuogo(l);
            
            if(vL.isEmpty()){
                out.append("No visit type associated with this place, please assign at least one");
            }else{
                for (Visita visita : vL)
                    out.append(visita).append("\n");  
            }
            out.append("\n");
        }

        if(out.isEmpty())
            out.append("No place exists.");

        ViewSE.println(out);
    }

    private void listVolontari() {
        StringBuilder out = new StringBuilder();
        for (Volontario v : controller.getDB().dbVolontarioHelper.getPersonList()) {
            out.append(v.getUsername() + " ( " + v.getStatus() + " )").append(":\n");

            if(v.getStatus() == StatusItem.DISABLED)
                break;

            ArrayList<TipoVisita> vV = controller.getDB().trovaTipoVisiteByVolontario(v);

            if(vV.isEmpty()){
                out.append("No visit type associated with this volunteer, please assign at least one");
            }else{
                for (TipoVisita tv : vV)
                    out.append(tv);
            }
            out.append("\n");
        }

        if(out.isEmpty())
            out.append("No volunteer exists.");

        ViewSE.println(out);
    }

    private void printVisite(String[] s) {
        char option = s[1].charAt(0);

        switch (option) {
            case 'a' -> printAllTipi();
            case 'p' -> printProposte();
            case 'c' -> printComplete();
            case 'C' -> printCancellate();
            case 'e' -> printEffettuate();
            default -> ViewSE.println(ERROR_NOT_RECOGNIZED);
        }
    }

    private void printTipiVisita() {
        if (controller.getCurrentUser().getType() != PersonaType.CONFIGURATORE) {
            ViewSE.println(ERROR_NOT_RECOGNIZED);
            return;
        }

        StringBuilder out = new StringBuilder();
        for (TipoVisita t : controller.getDB().dbTipoVisiteHelper.getTipoVisiteIstanziabili()) {
            out.append(t);
        }
        ViewSE.println(out);
    }

    private void printAllTipi() {
        String out =  "Proposed visits:\n" + getVisiteProposte() +
                      "Completed visits:\n" + getVisiteComplete() +
                      "Cancelled visits:\n" + getVisiteCancellate() +
                      "Performed visits:\n" + getVisiteEffettuate();
        ViewSE.println(out);
    }

    private void printProposte() {
        ViewSE.println(getVisiteProposte());
    }

    private void printComplete() {
        ViewSE.println(getVisiteComplete());
    }

    private void printCancellate() {
        ViewSE.println(getVisiteCancellate());
    }

    private void printEffettuate() {
        ViewSE.println(getVisiteEffettuate());
    }

    private String getVisiteProposte() {
        StringBuilder out = new StringBuilder();
        controller.getDB().dbVisiteHelper.getVisiteProposte().forEach(v -> out.append(v).append("\n"));
        
        if(out.isEmpty())
            out.append("There are no proposed visits");

        return out.toString();
    }

    private String getVisiteComplete() {
        StringBuilder out = new StringBuilder();
        controller.getDB().dbVisiteHelper.getCompletate().forEach(v -> out.append(v).append("\n"));

        if(out.isEmpty())
            out.append("There are no completed visits");

        return out.toString();
    }

    private String getVisiteCancellate() {
        StringBuilder out = new StringBuilder();
        controller.getDB().dbVisiteHelper.getVisiteCancellate().forEach(v -> out.append(v).append("\n"));

        if(out.isEmpty())
            out.append("There are no cancelled visits");

        return out.toString();
    }

    private String getVisiteEffettuate() {
        StringBuilder out = new StringBuilder();
        controller.getDB().dbVisiteHelper.getVisiteEffettuate().forEach(v -> out.append(v).append("\n"));

        if(out.isEmpty())
            out.append("There are no performed visits");

        return out.toString();
    }
}
