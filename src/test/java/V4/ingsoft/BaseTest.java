package V4.ingsoft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.model.Model;

public class BaseTest {
    protected Controller controller;
    private Model model;
    private String configPath = "data/configuratori.json";
    private String volontariPath = "data/volontari.json";
    private String fruitoriPath = "data/fruitori.json";
    private String luoghiPath = "data/luoghi.json";
    private String tipiVisitaPath = "data/tipoVisite.json";

    // Helper to reset data files before each test
    protected void resetDataFiles() {
        // Delete existing files to ensure clean state for setup
        try { Files.deleteIfExists(Paths.get(configPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(volontariPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(fruitoriPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(luoghiPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(tipiVisitaPath)); } catch (IOException e) { /* Ignore */ }
        Model.getInstance().clearAll();
        Model.instance = null;

        // Re-initialize model and controller for a fresh start
        // Implicitly create default ADMIN/PASSWORD if configPath is empty
        model = Model.getInstance(); 
        controller = new Controller(model);
    }

    @BeforeEach
    public void setup() {
        resetDataFiles();
    }

    //ensure logout
    @AfterEach
    public void cleanup() {
        controller.interpreter("logout");
    }
}
