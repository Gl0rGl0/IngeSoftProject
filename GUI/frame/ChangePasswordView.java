package GUI.frame;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.Random;

public class ChangePasswordView {

    private MainFrame mainFrame;
    private BorderPane root;
    private String phase; // "change" o "home"

    public ChangePasswordView(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        phase = "change";
        createView();

        Pane sidebar = createSidebar();
        root.setRight(sidebar);
    }

    private void createView() {
        // Crea un VBox per impilare verticalmente gli elementi con uno spacing di 20px
        VBox vbox = new VBox(20);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        // Titolo in alto
        Label titleLabel = new Label("Cambio Password");
        titleLabel.getStyleClass().add("title-label"); // Definito in CSS

        // Campo per la nuova password
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(250);

        // Campo per confermare la password
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Conferma Password");
        confirmPasswordField.setMaxWidth(250);

        // Bottone per il reset/cambio password
        Button resetButton = new Button("Reset Password");

        // Label per mostrare errori o messaggi di successo (occupa sempre lo stesso
        // spazio)
        Label messageLabel = new Label();
        messageLabel.getStyleClass().add("message-label"); // In CSS, potrai definire stili per error e success
        messageLabel.setVisible(false);

        resetButton.setOnAction(e -> {
            if (phase.equals("change")) {
                String newPass = passwordField.getText();
                String confirmPass = confirmPasswordField.getText();

                if (newPass.length() < 6) {
                    messageLabel.setText("La password deve avere almeno 6 caratteri");
                    messageLabel.getStyleClass().removeAll("success-label");
                    if (!messageLabel.getStyleClass().contains("error-label"))
                        messageLabel.getStyleClass().add("error-label");
                    messageLabel.setVisible(true);
                } else {
                    if (newPass.equals(confirmPass)) {
                        // LOGICA CAMBIO PASSWORD
                        messageLabel.setText("Password cambiata con successo");
                        messageLabel.getStyleClass().removeAll("error-label");
                        if (!messageLabel.getStyleClass().contains("success-label"))
                            messageLabel.getStyleClass().add("success-label");
                        messageLabel.setVisible(true);
                        resetButton.setText("Continua");
                        phase = "home";
                    } else {
                        messageLabel.setText("Le password non coincidono");
                        messageLabel.getStyleClass().removeAll("success-label");
                        if (!messageLabel.getStyleClass().contains("error-label"))
                            messageLabel.getStyleClass().add("error-label");
                        messageLabel.setVisible(true);
                    }
                }
            } else {
                // Se la fase è "home", cliccando "Continua" si torna alla home
                mainFrame.showMainApp();
            }
        });

        // Usa uno StackPane per far occupare al messaggio sempre lo stesso spazio
        StackPane messageStack = new StackPane(messageLabel);
        messageStack.setAlignment(Pos.CENTER);

        vbox.getChildren().addAll(titleLabel, passwordField, confirmPasswordField, resetButton, messageStack);
        vbox.setAlignment(Pos.CENTER);

        // Richiedi il focus sul bottone dopo il rendering
        javafx.application.Platform.runLater(() -> resetButton.requestFocus());

        // Usa un BorderPane per centrare il VBox nella view
        root = new BorderPane(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);
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
        Image bottomIcon = new Image(getClass().getResourceAsStream("/GUI/resources/icon/home.png"));
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
        Tooltip pswTooltip = new Tooltip("Home");
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
            mainFrame.showMainApp();
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
