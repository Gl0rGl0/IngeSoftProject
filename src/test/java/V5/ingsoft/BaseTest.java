package V5.ingsoft;

import V5.Ingsoft.controller.Controller;
import V5.Ingsoft.controller.item.persone.PersonaType;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.model.helper.DBConfiguratoreHelper;
import V5.Ingsoft.model.helper.DBDatesHelper;
import V5.Ingsoft.model.helper.DBFruitoreHelper;
import V5.Ingsoft.model.helper.DBIscrizioniHelper;
import V5.Ingsoft.model.helper.DBLuoghiHelper;
import V5.Ingsoft.model.helper.DBTipoVisiteHelper;
import V5.Ingsoft.model.helper.DBVisiteHelper;
import V5.Ingsoft.model.helper.DBVolontarioHelper;
import V5.Ingsoft.util.Payload;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTest {
    protected Controller controller;

    // Helper to reset data files before each test
    protected void resetDataFiles() {
        Model m = Model.getInstance();
        m.dbConfiguratoreHelper = new DBConfiguratoreHelper(new ArrayList<>());
        m.dbDatesHelper = new DBDatesHelper(new ArrayList<>());
        m.dbFruitoreHelper = new DBFruitoreHelper(new ArrayList<>());
        m.dbIscrizionisHelper = new DBIscrizioniHelper(new ArrayList<>());
        m.dbLuoghiHelper = new DBLuoghiHelper(new ArrayList<>());
        m.dbTipoVisiteHelper = new DBTipoVisiteHelper(new ArrayList<>());
        m.dbVisiteHelper = new DBVisiteHelper(new ArrayList<>());
        m.dbVolontarioHelper = new DBVolontarioHelper(new ArrayList<>());

        controller = new Controller(m);
    }

    public void enterRegimePhase() {
        Payload<?> o; //check with debugger
        // 1. First absolute login and password change for ADMIN
        controller.interpreter("login ADMIN PASSWORD");
        controller.interpreter("time -s 16/1/2025");

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
        controller.interpreter("add -c configRegime passRegimeInit");
        controller.interpreter("collection -o");

        // 4. Logout ADMIN and Login as the new configurator
        controller.interpreter("logout");
        o = controller.interpreter("login configRegime passRegimeInit");
        o = controller.interpreter("changepsw passRegime passRegime");

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
        Model.getInstance().saveAll();
    }
}
