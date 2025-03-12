package V1.ingsoft.commands.running;

import V1.ingsoft.App;
import V1.ingsoft.ViewSE;
import V1.ingsoft.commands.AbstractCommand;
import V1.ingsoft.luoghi.Visita;
import V1.ingsoft.persone.Fruitore;
import V1.ingsoft.persone.PersonaType;
import V1.ingsoft.util.AssertionControl;
import V1.ingsoft.util.StringUtils;

public class VisitCommand extends AbstractCommand {
    private final App app;
    private final String CLASSNAME = this.getClass().getSimpleName();

    public VisitCommand(App app) {
        this.app = app;
        super.commandInfo = CommandList.ASSING; // VISIT;
    }

    @Override
    public void execute(String[] options, String[] args) {
        if (options.length < 1) {
            ViewSE.println("Nessun opzione inserita, riprova...");
            AssertionControl.logMessage("Opzioni insufficienti", 2, CLASSNAME);
            return;
        }

        if (args.length < 3) { // nome_visita data_visita quantita
            ViewSE.println("Utilizzo sbagliato del comando 'visit', riprova...");
            AssertionControl.logMessage("Argomenti insufficienti", 2, CLASSNAME);
            return;
        }

        switch (options[0].charAt(0)) {
            case 'a' -> iscriviAVisita(args);
            case 'r' -> disiscriviVisita(args);
            default -> ViewSE.println("Opzione non riconosciuta per 'myvisit'.");
        }
    }

    private void iscriviAVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = app.db.getVisitaByName(a[0], a[1]);

        if (v == null) {
            ViewSE.println("Non ci sono visite disponibili con quel nome...");
            AssertionControl.logMessage("Visita non esistente", 3, CLASSNAME);
            return;
        }

        if (app.getCurrentUser().type() != PersonaType.FRUITORE) {
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

        if (quantita > app.maxPrenotazioniPerPersona) {
            ViewSE.println("Non puoi iscrivere cosi tante persone, il massimo è: " + app.maxPrenotazioniPerPersona);
            AssertionControl.logMessage("Superato massimale prenotazioni per persona", 3,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) app.getCurrentUser();

        switch (v.addPartecipanti(f, quantita)) {
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
                f.iscriviVisita(v.getUID());
            }
        }

    }

    private void disiscriviVisita(String[] args) {
        String[] a = StringUtils.joinQuotedArguments(args);
        Visita v = app.db.getVisitaByName(a[0], a[1]);

        if (v == null) {
            ViewSE.println("Non ci sono visite disponibili con quel nome...");
            AssertionControl.logMessage("Visita non esistente", 3, CLASSNAME);
            return;
        }

        if (app.getCurrentUser().type() != PersonaType.FRUITORE) {
            ViewSE.println("Non posso disiscrivere un NON fruitore dalla visita");
            AssertionControl.logMessage("Tentativo iscrizione da config/volont, non può essere arrivato fino a qua", 1,
                    CLASSNAME);
            return;
        }

        Fruitore f = (Fruitore) app.getCurrentUser();
        f.disiscriviVisita(v.getUID());
        v.rimuoviPartecipante(f.getUsername());
    }
}