package GUI.it.proj.frame;

import java.io.IOException;

import GUI.it.proj.Launcher;
import V5.Ingsoft.controller.item.luoghi.TipoVisita;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class LuoghiTipoVisiteViewController {
    public static final String ID = "luoghi-visite";
    @FXML private StackPane contentLuoghi;
    @FXML private StackPane contentVisite;
    @FXML private Region overlayMask;
    @FXML private StackPane dialog;

    private LuoghiViewController luoghiController;
    private AddLuoghiDialogController luoghiDialogController;
    private TipoVisiteViewController visiteController;
    private AddTipoVisiteDialogController visiteDialogController;

    private Parent dialogLuoghi;
    private Parent dialogVisite;

    @FXML
    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/luoghi-view.fxml"));
            Parent luoghiView = loader.load();
            luoghiController = loader.getController();
            contentLuoghi.getChildren().add(luoghiView);
            luoghiController.setParentController(this);

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/visite-view.fxml"));
            Parent visiteView = loader.load();
            visiteController = loader.getController();
            contentVisite.getChildren().add(visiteView);
            visiteController.setParentController(this);

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/add-luoghi-dialog.fxml"));
            dialogLuoghi = loader.load();
            luoghiDialogController = loader.getController();
            luoghiDialogController.setParentController(luoghiController);

            loader = new FXMLLoader(Launcher.class.getResource("/GUI/frame/add-tipovisite-dialog.fxml"));
            dialogVisite = loader.load();
            visiteDialogController = loader.getController();
            visiteDialogController.setParentController(visiteController);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        dialog.setVisible(false);
        overlayMask.setVisible(false);
    }

    //modale visite
    public void showTipoVisita(boolean edit, TipoVisita v) {
        dialog.getChildren().clear();
        dialog.getChildren().add(dialogVisite);
        visiteDialogController.clearChecks();
        visiteDialogController.setEdit(edit, v);
        overlayMask.setVisible(true);
        dialog.setVisible(true);
    }

    //modale luoghi
    public void addLuogo() {
        dialog.getChildren().clear();
        dialog.getChildren().add(dialogLuoghi);
        overlayMask.setVisible(true);
        dialog.setVisible(true);
    }

    public void refreshItems() {
        luoghiController.refreshItems();
        visiteController.refreshItems();
    }
}
