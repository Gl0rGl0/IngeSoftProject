package GUI.it.proj.frame;

import java.io.IOException;

import GUI.it.proj.Launcher;
import GUI.it.proj.utils.Luogo;
import GUI.it.proj.utils.Visita;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

public class LuoghiVisiteViewController {
    public static final String ID = "luoghiEvisite";
    @FXML
    private StackPane contentLuoghi;
    @FXML
    private StackPane contentVisite;
    @FXML
    private Region overlayMask;
    @FXML
    private StackPane dialog;

    private LuoghiViewController luoghiController;
    private AddLuoghiDialogController luoghiDialogController;
    private VisiteViewController visiteController;
    private AddVisiteDialogController visiteDialogController;

    private Parent dialogLuoghi;
    private Parent dialogVisite;

    @FXML
    private void initialize() {
        try {
            FXMLLoader loaderLuoghi = new FXMLLoader(Launcher.class.getResource("/GUI/frame/luoghi-view.fxml"));
            Parent luoghiView = loaderLuoghi.load();
            luoghiController = loaderLuoghi.getController();
            contentLuoghi.getChildren().add(luoghiView);
            luoghiController.setParentController(this);

            FXMLLoader loaderVisite = new FXMLLoader(Launcher.class.getResource("/GUI/frame/visite-view.fxml"));
            Parent visiteView = loaderVisite.load();
            visiteController = loaderVisite.getController();
            contentVisite.getChildren().add(visiteView);
            visiteController.setParentController(this);

            FXMLLoader loaderLuoghiDialog = new FXMLLoader(Launcher.class.getResource("/GUI/frame/add-luoghi-dialog.fxml"));
            dialogLuoghi = loaderLuoghiDialog.load();
            luoghiDialogController = loaderLuoghiDialog.getController();
            luoghiDialogController.setParentController(luoghiController);
            luoghiDialogController.setSuperParentController(this);

            FXMLLoader loaderVisiteDialog = new FXMLLoader(Launcher.class.getResource("/GUI/frame/add-visite-dialog.fxml"));
            dialogVisite = loaderVisiteDialog.load();
            visiteDialogController = loaderVisiteDialog.getController();
            visiteDialogController.setParentController(visiteController);
            visiteDialogController.setSuperParentController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeDialog() {
        dialog.setVisible(false);
        overlayMask.setVisible(false);
    }

    public void addVisita() {
        dialog.getChildren().clear();
        dialog.getChildren().add(dialogVisite);
        overlayMask.setVisible(true);
        dialog.setVisible(true);
    }

    public void editItem(Visita visita) {
        visiteDialogController.setEdit(true);

        dialog.getChildren().clear();
        dialog.getChildren().add(dialogVisite);
        overlayMask.setVisible(true);
        dialog.setVisible(true);

        System.out.println("Modifico " + visita.getTitolo());
    }

    public void addLuogo() {
        dialog.getChildren().clear();
        dialog.getChildren().add(dialogLuoghi);
        overlayMask.setVisible(true);
        dialog.setVisible(true);
    }

    public void editItem(Luogo luogo) {
        System.out.println("Modifico " + luogo.getTitolo());
    }

}
