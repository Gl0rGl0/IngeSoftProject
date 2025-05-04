package GUI.it.proj.frame;

import java.util.ArrayList;
import java.util.List;

import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.Loader;
import GUI.it.proj.utils.Persona;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class PersonViewController implements ListViewController<Persona> {
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

        setupListView(listConfiguratori, "CONFIGURATORE");
        setupListView(listFruitori, "FRUITORE");
        setupListView(listVolontari, "VOLONTARIO");

        listConfiguratori.getItems().addAll(generatePeople(20, "CONFIGURATORE"));
        listFruitori.getItems().addAll(generatePeople(15, "FRUITORE"));
        listVolontari.getItems().addAll(generatePeople(15, "VOLONTARIO"));
    }

    private void setupListView(ListView<Persona> listView, String roleFilter) {
        listView.setCellFactory(e -> new Cell<>(this, roleFilter, true));
    }

    @Override
    public void removeItem(Persona user) {
        switch (user.tipo) {
            case "CONFIGURATORE" -> listConfiguratori.getItems().removeIf(p -> p.username.equals(user.username));
            case "FRUITORE"     -> listFruitori.getItems().removeIf(p -> p.username.equals(user.username));
            case "VOLONTARIO"  -> listVolontari.getItems().removeIf(p -> p.username.equals(user.username));
        }
    }

    @Override
    public void addItem(Persona user) {
        switch (user.tipo) {
            case "CONFIGURATORE" -> listConfiguratori.getItems().add(user);
            case "FRUITORE"     -> listFruitori.getItems().add(user);
            case "VOLONTARIO"  -> listVolontari.getItems().add(user);
        }
    }

    private List<Persona> generatePeople(int count, String role) {
        List<Persona> people = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            people.add(new Persona("user" + i, "pass" + i, role));
        }
        return people;
    }

    @FXML
    private void onAggiungiConfiguratoreClick() {
        showAddPersonDialog("CONFIGURATORE");
    }

    @FXML
    private void onAggiungiFruitoreClick() {
        showAddPersonDialog("FRUITORE");
    }

    @FXML
    private void onAggiungiVolontarioClick() {
        showAddPersonDialog("VOLONTARIO");
    }

    private void showAddPersonDialog(String role) {
        addPersonDialog = Loader.loadFXML("add-person-dialog");
        addPersonDialogController = (AddPersonDialogController) addPersonDialog.getUserData();
        addPersonDialogController.setRole(role);
        addPersonDialogController.setParentController(this);

        overlayMask.setVisible(true);
        dialog.getChildren().setAll(addPersonDialog);
        dialog.setVisible(true);
    }

    @Override
    public void modifyItem(Persona p) {
        System.out.println("MODIFICO " + p.getUsername());
        addPersonDialog = Loader.loadFXML("add-person-dialog");
        addPersonDialogController = (AddPersonDialogController) addPersonDialog.getUserData();
        addPersonDialogController.setRole(p.tipo);
        addPersonDialogController.setParentController(this);
        addPersonDialogController.setStatus(true, p.getUsername());

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
}
