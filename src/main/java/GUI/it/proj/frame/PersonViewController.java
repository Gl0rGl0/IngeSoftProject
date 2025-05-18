package GUI.it.proj.frame;

import java.io.IOException;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Cell;
import GUI.it.proj.utils.interfaces.ListEditer;
import V5.Ingsoft.controller.item.persone.Persona;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    @FXML
    private void initialize() {
        VBox.setVgrow(listConfiguratori, Priority.ALWAYS);
        VBox.setVgrow(listFruitori, Priority.ALWAYS);
        VBox.setVgrow(listVolontari, Priority.ALWAYS);

        setupListView(listConfiguratori, PersonaType.CONFIGURATORE.toString());
        setupListView(listFruitori, PersonaType.FRUITORE.toString());
        setupListView(listVolontari, PersonaType.VOLONTARIO.toString());

        addEditButtonListView(listVolontari);

        refreshItems();
    }

    private void addEditButtonListView(ListView<Persona> lV) {
        
    }

    private void setupListView(ListView<Persona> listView, String roleFilter) {
        listView.setCellFactory(e -> new Cell<>(this, roleFilter));
    }

    @Override
    public void removeItem(String user) {
        Persona res = Launcher.controller.getDB().findPersona(user);
        String prompt = null;
        switch (res.getType()) {
            case CONFIGURATORE -> prompt = "remove -c " + user;
            case FRUITORE      -> prompt = "remove -f " + user;
            case VOLONTARIO    -> prompt = "remove -v " + user;
            default -> {}
        }

        if(prompt == null)
            return;

        Payload<?> out = Launcher.controller.interpreter(prompt);
        Launcher.toast(out);

        if(out != null && out.getStatus() == Status.OK)
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
        FXMLLoader loaderPerson = new FXMLLoader(Launcher.class.getResource("/GUI/frame/add-person-dialog.fxml"));
        Parent addPersonDialog;

        try {
            addPersonDialog = loaderPerson.load();
            addPersonDialogController = loaderPerson.getController();
            addPersonDialogController.setParentController(this);
        } catch (IOException e) {
            return;
        }
        
        addPersonDialogController.setRole(role);
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
        refreshConfiguratori();
        refreshFruitori();
        refreshVolontari();   
    }
    
    public void refreshConfiguratori(){
        listConfiguratori.getItems().clear();
        listConfiguratori.getItems().addAll(Launcher.controller.getDB().dbConfiguratoreHelper.getPersonList());
    }

    public void refreshFruitori(){
        listFruitori.getItems().clear();
        listFruitori.getItems().addAll(Launcher.controller.getDB().dbFruitoreHelper.getPersonList());
    }

    public void refreshVolontari(){
        listVolontari.getItems().clear();
        listVolontari.getItems().addAll(Launcher.controller.getDB().dbVolontarioHelper.getPersonList());
    }
}
