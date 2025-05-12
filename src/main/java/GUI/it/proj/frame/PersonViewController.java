package GUI.it.proj.frame;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.Loader;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PersonViewController implements ListEditer<Persona> {
    public static final String ID = "persone";

    @FXML private ListView<Persona> listConfiguratori;
    @FXML private ListView<Persona> listFruitori;
    @FXML private ListView<Persona> listVolontari;

    @FXML private StackPane dialog;
    @FXML private Region overlayMask;

    private AddPersonDialogController addPersonDialogController;
    private Parent addPersonDialog;

    @FXML
    private void initialize() {
        VBox.setVgrow(listConfiguratori, Priority.ALWAYS);
        VBox.setVgrow(listFruitori, Priority.ALWAYS);
        VBox.setVgrow(listVolontari, Priority.ALWAYS);

        setupListView(listConfiguratori, PersonaType.CONFIGURATORE.toString());
        setupListView(listFruitori, PersonaType.FRUITORE.toString());
        setupListView(listVolontari, PersonaType.VOLONTARIO.toString());

        refreshItems();
    }

    private void setupListView(ListView<Persona> listView, String roleFilter) {
        listView.setCellFactory(e -> new Cell<>(this, roleFilter, true));
    }

    @Override
    public void removeItem(Persona user) {
        String prompt = null;
        switch (user.getType()) {
            case CONFIGURATORE -> prompt = "remove -c " + user.getUsername();
            case FRUITORE      -> prompt = "remove -f " + user.getUsername();
            case VOLONTARIO    -> prompt = "remove -v " + user.getUsername();
            default -> {}
        }

        if(prompt == null)
            return;

        Payload out = Launcher.controller.interpreter(prompt);

        if(out == null || out.getStatus() == Status.ERROR)
            return;
        
        refreshItems();
    }

    @Override
    public void addItem(Persona user) {
        String prompt = null;
        switch (user.getType()) {
            case CONFIGURATORE -> prompt = "add -c " + user.getUsername();
            case VOLONTARIO    -> prompt = "add -v " + user.getUsername();
            default -> {}
        }

        if(prompt == null)
            return;

        Payload out = Launcher.controller.interpreter(prompt);

        if(out == null || out.getStatus() == Status.ERROR)
            return;
        
        refreshItems();
    }

    @FXML
    private void onAggiungiConfiguratoreClick() {
        showAddPersonDialog(PersonaType.CONFIGURATORE);
    }

    @FXML
    private void onAggiungiVolontarioClick() {
        showAddPersonDialog(PersonaType.VOLONTARIO);
    }

    private void showAddPersonDialog(PersonaType role) {
        addPersonDialog = Loader.loadFXML("add-person-dialog");
        addPersonDialogController = (AddPersonDialogController) addPersonDialog.getUserData();
        addPersonDialogController.setRole(role);
        addPersonDialogController.setParentController(this);

        overlayMask.setVisible(true);
        dialog.getChildren().setAll(addPersonDialog);
        dialog.setVisible(true);
    }

    @FXML
    public void closeDialog() {
        overlayMask.setVisible(false);
        dialog.getChildren().clear();
        dialog.setVisible(false);
    }

    @Override
    public void refreshItems() {
        //niente migliorie per la memoria...
        listConfiguratori.getItems().clear();
        listConfiguratori.getItems().addAll(Launcher.controller.getDB().dbConfiguratoreHelper.getPersonList());

        listFruitori.getItems().clear();
        listFruitori.getItems().addAll(Launcher.controller.getDB().dbFruitoreHelper.getPersonList());

        listVolontari.getItems().clear();
        listVolontari.getItems().addAll(Launcher.controller.getDB().dbVolontarioHelper.getPersonList());
    }
}
