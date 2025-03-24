package V1.Ingsoft.controller.commands.running;

import V1.Ingsoft.controller.Controller;
import V1.Ingsoft.controller.commands.AbstractCommand;
import V1.Ingsoft.util.Date;
import V1.Ingsoft.view.ViewSE;

public class PrecludeCommand extends AbstractCommand {

    private final Controller controller;

    public PrecludeCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.PRECLUDE;
    }

    @Override
    public void execute(String[] options, String[] args) {

        if (options.length < 1) {
            ViewSE.println("Errore nell'utilizzo del comando 'preclude'");
            return;
        }

        // Controllo interno
        Date toOperate = new Date(args[0]);
        if (toOperate.toString().equals("00/00/00")) {
            ViewSE.println("Errore nell'inserimento della data, ricontrollare");
            return;
        }

        if (!twoMonthsDifference(toOperate, controller.date)) {
            ViewSE.println(
                    "Non è possibile aggiungere una data cosi avanti/indietro nel tempo, attenersi al month successivo al prossimo");
            return;
        }

        char option = options[0].charAt(0);
        switch (option) {
            case 'a' ->
                addPrecludedDate(args);
            case 'r' ->
                removePrecludedDate(args);
            default ->
                ViewSE.println("Opzione non riconosciuta per 'preclude'.");
        }
    }

    private boolean twoMonthsDifference(Date d1, Date d2) {
        int currentMonth = d2.getMonth();
        int targetMonth = d1.getMonth();

        if (d1.getDay() < 16) {
            return (currentMonth + 2) == targetMonth;
        } else {
            return (currentMonth + 3) == targetMonth;
        }
    }

    private void addPrecludedDate(String[] args) {
        ViewSE.println("Eseguo: Aggiungo data da precludere");
        controller.db.addPrecludedDate(new Date(args[0])); // aggiunge una data speciale
    }

    private void removePrecludedDate(String[] args) {
        ViewSE.println("Eseguo: Rimuovo data da precludere");
        controller.db.removePrecludedDate(new Date(args[0]));
    }
}
