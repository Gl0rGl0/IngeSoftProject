package V5.ingsoft;

import V4.Ingsoft.controller.Controller;
import V4.Ingsoft.controller.item.persone.PersonaType;
import V4.Ingsoft.model.Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTest {
    private final String configPath = "data/configuratori.json";
    private final String volontariPath = "data/volontari.json";
    private final String fruitoriPath = "data/fruitori.json";
    private final String luoghiPath = "data/luoghi.json";
    private final String tipiVisitaPath = "data/tipoVisite.json";
    protected Controller controller;

    // Helper to reset data files before each test
    protected void resetDataFiles() {
        // Delete existing files to ensure clean state for setup
        try { Files.deleteIfExists(Paths.get(configPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(volontariPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(fruitoriPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(luoghiPath)); } catch (IOException e) { /* Ignore */ }
        try { Files.deleteIfExists(Paths.get(tipiVisitaPath)); } catch (IOException e) { /* Ignore */ }
        Model.getInstance().clearAll();
        Model.appSettings = null;
        Model.instance = null;

        // Re-initialize model and controller for a fresh start
        // Implicitly create default ADMIN/PASSWORD if configPath is empty
        Model model = Model.getInstance();
        controller = new Controller(model);
    }

    public void enterRegimePhase() {
        // 1. First absolute login and password change for ADMIN
        controller.interpreter("time -s 16/1/2025");
        controller.interpreter("login ADMIN PASSWORD");

        // 2. Complete Setup Steps using known setup commands
        controller.interpreter("setmax 5");
        controller.interpreter("add -L PlaceRegime \"Regime Place\" 10.0:20.0");
        // Cannot add types/volunteers/assignments during setup via commands
        controller.interpreter("done"); // Finalize setup


        // 3. Add another configurator using the running phase command (now logged in as ADMIN)
        // Add initial TipoVisita and Volontario needed for some regime tests
        // Assuming 'add -T' format: <UID> <LuogoTitle> <OraInizio> <Durata> <MinPart> <MaxPart> [Descrizione]
        // Assuming 'assign' format: <VolUsername> <TipoVisitaUID>
        controller.interpreter("add -T TVRegime Description 1:1 20/06/2025 27/08/2025 10:00 60 false 1 10 MaSa");
        controller.interpreter("assign -L PlaceRegime TVRegime");
        controller.interpreter("add -v VolRegime PassVol");
        controller.interpreter("assign -V TVRegime VolRegime");
        controller.interpreter("add -c configRegime passRegime");
        controller.interpreter("collection -o");

        // 4. Logout ADMIN and Login as the new configurator
        controller.interpreter("logout");
        controller.interpreter("login configRegime passRegime");
        controller.interpreter("changepsw passRegime");

        assertEquals(PersonaType.CONFIGURATORE, controller.getCurrentUser().getType(), "User should be of type CONFIGURATORE.");
        controller.interpreter("time -s 17/1/2025");
        controller.interpreter("time -s 16/4/2025");
    }

    @BeforeEach
    public void setup() {
        resetDataFiles();
    }

    //ensure logout
    @AfterEach
    public void cleanup() {
        controller.interpreter("logout");
        Model.getInstance().closeAll();
    }
}
