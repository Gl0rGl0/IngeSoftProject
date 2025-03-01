package GUI.frame;

import java.util.Random;

import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class GenericFrame {

    private BorderPane root;
    private MainFrame mainFrame;

    public GenericFrame(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        root = new BorderPane();
        Pane sidebar = createSidebar();
        root.setRight(sidebar);

        // Area contenuto centrale (placeholder)
        Pane contentArea = new Pane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);
    }

    public BorderPane getView() {
        return root;
    }

    private Pane createSidebar() {
        // Crea l'AnchorPane per la sidebar con dimensioni fisse
        AnchorPane sidebar = new AnchorPane();
        sidebar.getStyleClass().add("sidebar");

        // ------------------------------
        // Icona in alto (es. icona utente)
        // ------------------------------
        StackPane topIconContainer = new StackPane();
        topIconContainer.setPrefWidth(64);
        topIconContainer.setPrefHeight(64);
        Image topIcon = new Image(getClass().getResourceAsStream("/GUI/resources/icon/user.png"));
        ImageView topIconView = new ImageView(topIcon);
        topIconView.setFitWidth(32);
        topIconView.setFitHeight(32);
        topIconContainer.getChildren().add(topIconView);
        AnchorPane.setTopAnchor(topIconContainer, 10.0);
        AnchorPane.setLeftAnchor(topIconContainer, 0.0);
        AnchorPane.setRightAnchor(topIconContainer, 0.0);
        sidebar.getChildren().add(topIconContainer);

        // ------------------------------
        // Icona in basso per "Cambio Password" (icona Home)
        // ------------------------------
        StackPane bottomIconContainer = new StackPane();
        bottomIconContainer.setPrefWidth(64);
        bottomIconContainer.setPrefHeight(64);
        Image bottomIcon = new Image(getClass().getResourceAsStream("/GUI/resources/icon/key.png"));
        ImageView bottomIconView = new ImageView(bottomIcon);
        bottomIconView.setFitWidth(32);
        bottomIconView.setFitHeight(32);
        bottomIconContainer.getChildren().add(bottomIconView);
        // Ancoriamo questa icona a 10px dal fondo
        AnchorPane.setBottomAnchor(bottomIconContainer, 10.0);
        AnchorPane.setLeftAnchor(bottomIconContainer, 0.0);
        AnchorPane.setRightAnchor(bottomIconContainer, 0.0);
        sidebar.getChildren().add(bottomIconContainer);

        // ------------------------------
        // Icona in basso per Logout
        // ------------------------------
        StackPane bottomLogoutIconContainer = new StackPane();
        bottomLogoutIconContainer.setPrefWidth(64);
        bottomLogoutIconContainer.setPrefHeight(64);
        Image bottomLogoutIcon = new Image(getClass().getResourceAsStream("/GUI/resources/icon/logout.png"));
        ImageView bottomLogoutIconView = new ImageView(bottomLogoutIcon);
        bottomLogoutIconView.setFitWidth(32);
        bottomLogoutIconView.setFitHeight(32);
        bottomLogoutIconContainer.getChildren().add(bottomLogoutIconView);
        // Ancoriamo il logout un po' sopra, ad esempio 80px dal fondo
        AnchorPane.setBottomAnchor(bottomLogoutIconContainer, 74.0);
        AnchorPane.setLeftAnchor(bottomLogoutIconContainer, 0.0);
        AnchorPane.setRightAnchor(bottomLogoutIconContainer, 0.0);
        sidebar.getChildren().add(bottomLogoutIconContainer);

        // ------------------------------
        // Tooltip per le icone
        // ------------------------------
        Tooltip pswTooltip = new Tooltip("Cambio Password");
        pswTooltip.setShowDelay(Duration.millis(200));
        Tooltip.install(bottomIconContainer, pswTooltip);

        Tooltip userTooltip = new Tooltip("User");
        userTooltip.setShowDelay(Duration.millis(200));
        Tooltip.install(topIconContainer, userTooltip);

        Tooltip logoutTooltip = new Tooltip("Logout");
        logoutTooltip.setShowDelay(Duration.millis(200));
        Tooltip.install(bottomLogoutIconContainer, logoutTooltip);

        // Al click sull'icona in basso (home), passa alla pagina di cambio password
        bottomIconContainer.setOnMouseClicked(e -> {
            mainFrame.showChangePassword();
        });

        // Al click sull'icona di logout, torna alla login
        bottomLogoutIconContainer.setOnMouseClicked(e -> {
            mainFrame.showLogin();
        });

        // ------------------------------
        // Colore casuale della sidebar
        // ------------------------------
        Random random = new Random();
        int r = random.nextInt(3); // genera 0, 1 o 2
        switch (r) {
            case 0 -> sidebar.getStyleClass().add("sidebar-red");
            case 1 -> sidebar.getStyleClass().add("sidebar-green");
            case 2 -> sidebar.getStyleClass().add("sidebar-blue");
        }

        return sidebar;
    }

}
