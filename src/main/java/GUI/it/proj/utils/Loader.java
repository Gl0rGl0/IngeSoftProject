package GUI.it.proj.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class Loader {
    private static final String BASE_PATH = "/GUI/frame/";

    /**
     * Loads an FXML resource from the classpath under /GUI/resources/{fxml}.fxml.
     *
     * @param fxml the name (with optional subfolders) of the FXML file, without extension
     * @return the loaded Parent node
     * @throws IllegalArgumentException if the resource is not found
     * @throws UncheckedIOException if loading fails
     */
    public static Parent loadFXML(String fxml) {
        String resourcePath = BASE_PATH + fxml + ".fxml";
        URL fxmlUrl = Loader.class.getResource(resourcePath);
        if (fxmlUrl == null) {
            throw new IllegalArgumentException("FXML resource not found: " + resourcePath);
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        loader.setLocation(fxmlUrl);
        try {
            return loader.load();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load FXML: " + resourcePath, e);
        }
    }
}
