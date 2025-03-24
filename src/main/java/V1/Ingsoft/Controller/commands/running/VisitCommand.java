package V1.Ingsoft.Controller.commands.running;

import V1.Ingsoft.Controller.Controller;
import V1.Ingsoft.Controller.commands.AbstractCommand;
import V1.Ingsoft.Controller.item.luoghi.Visita;
import V1.Ingsoft.Controller.item.persone.Fruitore;
import V1.Ingsoft.Controller.item.persone.PersonaType;
import V1.Ingsoft.util.AssertionControl;
import V1.Ingsoft.util.StringUtils;
import V1.Ingsoft.view.ViewSE;

public class VisitCommand extends AbstractCommand {
    private final Controller controller;
    private final String CLASSNAME = this.getClass().getSimpleName();

    public VisitCommand(Controller controller) {
        this.controller = controller;
        super.commandInfo = CommandList.VISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Nessun opzione inserita, riprova...");
            AssertionControl.logMessage("Opzioni insufficienti", 2, CLASSNAME);
            return;
        }

        if (args.length < 3) { // name_visita data_visita quantita
            ViewSE.println("Utilizzo sbagliato del comando 'visit', riprova...");
            AssertionControl.logMessage("Argomenti insufficienti", 2, CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> addFruitoreToVisita(args);
            case 'r' -> removeFromVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'myvisit'.");
        }
    }

    private void addFruitoreToVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = controller.db.getVisitaByName(a[0], a[1]);

        if (v == null) {
            ViewSE.println("Non ci sono visite disponibili con quel nome...");
            AssertionControl.logMessage("Visita non esistente", 3, CLASSNAME);
            return;
        }

        if (controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Non posso iscrivere un NON fruitore alla visita");
            AssertionControl.logMessage("Tentativo iscrizione da config/volont, non può essere arrivato fino a qua", 1,
                    CLASSNAME);
            return;
        }

        int quantita = 0;
        try {
            quantita = Integer.valueOf(a[3]);
        } catch (NumberFormatException e) {
            ViewSE.println("Errore nella lettura della quantita...");
            AssertionControl.logMessage("Exception numerica nell'iscrizione", 2, CLASSNAME);
            return;
        }

        if (quantita > controller.maxPrenotazioniPerPersona) {
            ViewSE.println(
                    "Non puoi iscrivere cosi tante persone, il massimo è: " + controller.maxPrenotazioniPerPersona);
            AssertionControl.logMessage("Superato massimale prenotazioni per persona", 3,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) controller.getCurrentUser();

        switch (v.addPartecipants(f, quantita)) {
            case "capienza" -> {
                ViewSE.println("Impossibile iscriverti alla visita, la capienza eccede la tua richiesta.");
                return;
            }

            case "presente" -> {
                ViewSE.println("Impossibile iscriverti alla visita, sei gia iscritto.");
                return;
            }

            default -> {
                ViewSE.println("Iscrizione completata con successo");
                f.subscribeToVisit(v.getUID());
            }
        }

    }

    private void removeFromVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = controller.db.getVisitaByName(a[0], a[1]);

        if (v == null) {
            ViewSE.println("Non ci sono visite disponibili con quel name...");
            AssertionControl.logMessage("Visita non esistente", 3, CLASSNAME);
            return;
        }

        if (controller.getCurrentUser().getType() != PersonaType.FRUITORE) {
            ViewSE.println("Non posso disiscrivere un NON fruitore dalla visita");
            AssertionControl.logMessage("Tentativo iscrizione da config/volont, non può essere arrivato fino a qua", 1,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) controller.getCurrentUser();
        f.removeFromVisita(v.getUID());
        v.removePartecipant(f.getUsername());
    }
}