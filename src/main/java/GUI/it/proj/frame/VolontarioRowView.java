package GUI.it.proj.frame;

import java.util.ArrayList;
import java.util.List;

import V5.Ingsoft.controller.item.persone.Volontario;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * Helper class che incapsula la riga grafica per un singolo Volontario,
 * gestendo la costruzione iniziale e l'aggiornamento delle fasce giornaliere.
 */
public class VolontarioRowView {
    private final Volontario volontario;
    private final HBox row;
    private final List<Label> dayBoxes = new ArrayList<>();

    public VolontarioRowView(Volontario v, int daysInMonth) {
        this.volontario = v;
        this.row = new HBox(5);
        this.row.getStyleClass().add("vol-row");

        // Colonna del nome
        Label name = new Label(v.getUsername());
        name.setPrefWidth(140);
        row.getChildren().add(name);

        // Creazione delle caselle per ogni giorno
        boolean[] disponibilita = v.getAvailability();
        for (int day = 1; day <= daysInMonth; day++) {
            Label box = new Label();
            box.getStyleClass().add("day-box");
            HBox.setHgrow(box, Priority.ALWAYS);
            box.setMaxWidth(Double.MAX_VALUE);
            updateBoxStyle(box, disponibilita[day - 1]);
            row.getChildren().add(box);
            dayBoxes.add(box);
        }
    }

    /**
     * Aggiorna lo stato di disponibilità per ciascun giorno,
     * modificando le classi CSS dei Label già esistenti.
     */
    public void update() {
        boolean[] disponibilita = volontario.getAvailability();
        for (int i = 0; i < dayBoxes.size(); i++) {
            updateBoxStyle(dayBoxes.get(i), disponibilita[i]);
        }
    }

    /** Restituisce il nodo HBox che contiene la riga intera. */
    public HBox getNode() {
        return row;
    }

    /** Utility per applicare la classe CSS corretta (“available” o “unavailable”). */
    private void updateBoxStyle(Label box, boolean available) {
        box.getStyleClass().removeAll("day-available", "day-unavailable");
        box.getStyleClass().add(available ? "day-available" : "day-unavailable");
    }
}
