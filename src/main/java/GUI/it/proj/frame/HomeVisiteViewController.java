package GUI.it.proj.frame;

import java.io.IOException;
import java.util.List;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListBase;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.model.Model;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Controller principale per la view delle visite.
 * Implementa ListBase<Visita> e gestisce la lista delle visite
 * e l’apertura del dialog di iscrizione/visualizzazione.
 */
public class HomeVisiteViewController implements ListBase<Visita> {
    public static final String ID = "home";

    @FXML private ListView<Visita> listVisite;
    @FXML private Region overlayMask;
    @FXML private StackPane dialogContainer;    // ← qui deve corrispondere a fx:id nel FXML

    /** Il “root” del dialog (ciò che carichiamo da home-visita-dialog.fxml) */
    private Parent dialogRoot;
    /** Il controller associato a quello stesso dialog */
    private VisitaIscrizioneDialogController dialogController;

    @FXML
    private void initialize() {
        // 1) ListView occupi tutto lo spazio verticale
        VBox.setVgrow(listVisite, Priority.ALWAYS);

        // 2) Imposta la cell factory (uguale a come facevi per le persone)
        listVisite.setCellFactory(e -> new Cell<Visita>(this, TipoVisiteViewController.ID));

        // 3) Carica il dialog (FXML + controller), ma NON sostituire dialogContainer:
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/home-visita-dialog.fxml"));
            dialogRoot = loader.load();                         // qui prendo il Parent del pop-up
            dialogController = loader.getController();           // qui prendo il controller
            dialogController.setParentController(this);          // passo il riferimento al parent

            // Ora inserisco il dialogRoot dentro “dialogContainer” (che era iniettato)
            dialogContainer.getChildren().clear();
            dialogContainer.getChildren().add(dialogRoot);

            // All’inizio, overlay e dialog restano nascosti:
            overlayMask.setVisible(false);
            dialogContainer.setVisible(false);

        } catch (IOException e) {
            System.err.println("Errore nel caricamento di home-visita-dialog.fxml:");
            e.printStackTrace();
        }

        // 4) Popola subito la ListView
        refreshItems();
    }

    /**
     * Metodo invocato da ogni cella (Cell<Visita>) quando si clicca su una riga
     * per mostrare il dialog con i dettagli di una singola Visita.
     * @param v l’oggetto Visita da mostrare o modificare (oppure null se vogliamo "aggiungere")
     */
    public void showVisita(Visita v) {
        if (dialogController == null) return;

        // if(Launcher.controller.getCurrentUser().getType() == PersonaType.VOLONTARIO) return;

        // Imposto i valori esistenti (oppure nuovi) nel controller del dialog
        dialogController.setVisitaCorrente(v);

        // Rendo visibili overlayMask e dialogContainer
        overlayMask.setVisible(true);
        dialogContainer.setManaged(true);
        dialogContainer.setVisible(true);
        
        boolean disable = Launcher.controller.getCurrentUser().getType() != PersonaType.FRUITORE;
        dialogController.toggleConfirmBUtton(disable);
    }

    /**
     * Nasconde il dialog, invocato dal dialog stesso quando clicchi “Annulla” o “Conferma” con successo.
     */
    public void closeDialog() {
        overlayMask.setVisible(false);
        dialogContainer.setVisible(false);
    }

    @Override
    public void refreshItems() {
        listVisite.getItems().clear();
        List<Visita> tutteLeVisite = Model.getInstance().dbVisiteHelper.getVisiteProposte();
        listVisite.getItems().addAll(tutteLeVisite);
    }

    /** Se hai un pulsante “Aggiungi Visita” nel main, richiama questo metodo: */
    @FXML
    private void onAggiungiVisitaClick() {
        // Passo “null” per indicare “nuova Visita” (il dialog saprà creare una nuova)
        showVisita(null);
    }
}
