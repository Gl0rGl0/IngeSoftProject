package V.Ingsoft.controller.commands.running;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import V.Ingsoft.DefaultTest;
import V5.Ingsoft.controller.item.persone.Fruitore;
import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.persone.Volontario;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.Payload;
import V5.Ingsoft.util.Payload.Status;

public class MyVisitCommandTest extends DefaultTest {
    Payload<?> o;
    Model m;

    // Test data
    static final String FRUITORE_USER = "fruitoreTest";
    static final String VOLONTARIO_USER = "volontarioTest";
    static final String VISIT_TITLE = "Titolo Visita";
    static final String PLACE_NAME = "Luogo Test";

    @BeforeEach
    void setupTest() {
        c.skipSetup(); // Reset Model and Controller
        m = Model.getInstance(); // Get current Model instance

        c.interpreter("login ADMIN PASSWORD");
        c.interpreter("time -s 16/06/2025");
    }

    // --- Common Tests (User Type) ---

    @Test
    void testMyVisitFailInvalidUserType() {
        c.interpreter("login ADMIN PASSWORD"); // Log in as ADMIN
        o = c.interpreter("myvisit");
        assertEquals(Status.ERROR, o.getStatus());
        assertEquals("Interpreter: permission denied for myvisit", o.getLogMessage()); // Assuming ADMIN is CONFIGURATORE
    }

    // --- Fruitore Specific Tests ---

    @Test
    void testMyVisitFruitoreSuccess() throws Exception {
        Visita v = createVisit();
        
        c.interpreter("logout");
        c.interpreter("login " + FRUITORE_USER + " password"); // Log in as Fruitore
        o = c.interpreter("visit -a \"" + VISIT_TITLE + "\" " + v.getDate() + " " + 3);
        assertEquals(Status.INFO, o.getStatus());
        String visitTicket = (String) o.getData();
        visitTicket = visitTicket.substring(visitTicket.length() - 8);

        o = c.interpreter("myvisit");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Listed fruitore subscriptions for " + FRUITORE_USER, o.getLogMessage());

        // Verify content (list of Iscrizione objects)
        assertNotNull(o.getData());
        assertTrue(o.getData() instanceof ArrayList);
        ArrayList<Iscrizione> subscriptions = (ArrayList<Iscrizione>) o.getData();
        assertFalse(subscriptions.isEmpty());
        assertEquals(1, subscriptions.size());
        assertEquals(visitTicket, subscriptions.get(0).getUID());
    }

    @Test
    void testMyVisitFruitoreNoVisits() {
        c.interpreter("logout");
        c.interpreter("login " + FRUITORE_USER + " password password"); // Log in as Fruitore

        o = c.interpreter("myvisit");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("No visits found for the user: " + FRUITORE_USER, o.getLogMessage());
    }
    
    Visita createVisit() throws Exception{
        o = c.interpreter("add -L \"" + PLACE_NAME + "\" \"Desc\" \"10:10\"");
        o = c.interpreter("add -T \"" + VISIT_TITLE + "\" \"Desc\" 10:10 01/01/2025 01/12/2025 09:00 60 true 5 20 Lu");
        o = c.interpreter("add -v " + VOLONTARIO_USER + " password"); // Need a volunteer for the visit
        m.dbFruitoreHelper.addFruitore(new Fruitore(FRUITORE_USER, "password", false, true));

        c.interpreter(String.format("assign -L \"%s\" \"%s\"", PLACE_NAME, VISIT_TITLE));
        c.interpreter(String.format("assign -V \"%s\" \"%s\"", VISIT_TITLE, VOLONTARIO_USER));

        c.interpreter("time -m 2");

        TipoVisita tv = m.dbTipoVisiteHelper.findTipoVisita(VISIT_TITLE);
        Volontario vol = m.dbVolontarioHelper.getPersona(VOLONTARIO_USER);
        vol.setAsNotNew();
        assertNotNull(tv);
        assertNotNull(vol);

        c.interpreter("collection -o");
        int meseLenght = c.date.getMonth().maxLength();
        int meseValue = c.date.clone().addMonth(1).getMonth().getValue();
        for (int i = 1; i <= meseLenght; i++)
                try {
                    vol.setAvailability(c.date, new Date(String.format("%d/%d/2025", i, meseValue)), true);
                } catch (Exception ignored) {}
        c.interpreter("collection -c");
        
        // Manually create a Visita and Iscrizione (as there's no direct command for them in the provided code)
        // In a real app, this would be via a "book" command.
        o = c.interpreter("makeplan");
        return m.dbVisiteHelper.getItems().getFirst();
    }

    // The 'Fruitore object null for user' branch is hard to test directly
    // because `controller.getCurrentUser()` usually guarantees a non-null Fruitore
    // if `dbFruitoreHelper.getPersona()` returns null after a successful login,
    // it implies an inconsistency in your Model/Controller state handling.
    // We will assume that a successfully logged-in Fruitore will always exist in dbFruitoreHelper.
    // If you explicitly want to test this, you'd need to mock the Model or DB helper.

    // --- Volontario Specific Tests ---

    @Test
    void testMyVisitVolontarioSuccess() throws Exception {
        // 1. Create a Volontario user
        createVisit();
        c.interpreter("logout");
        c.interpreter("login " + VOLONTARIO_USER + " password");

        // 3. Execute myvisit command (needs dummy args for a.length < 2 check)
        o = c.interpreter("myvisit");
        assertEquals(Status.INFO, o.getStatus());
        assertEquals("Listed visit type for volunteer " + VOLONTARIO_USER, o.getLogMessage());

        // Verify content (list of TipoVisita objects)
        assertNotNull(o.getData());
        assertTrue(o.getData() instanceof ArrayList);
        ArrayList<TipoVisita> assignedVisits = (ArrayList<TipoVisita>) o.getData();
        assertFalse(assignedVisits.isEmpty());
        assertEquals(1, assignedVisits.size());
        assertEquals(VISIT_TITLE, assignedVisits.get(0).getTitle());

        assertTrue(assignedVisits.stream().anyMatch(tv -> tv.getTitle().equals(VISIT_TITLE)));
    }

    @Test
    void testMyVisitVolontarioNoVisits() throws Exception {
        m.dbVolontarioHelper.addItem(new Volontario(VOLONTARIO_USER, "password", false, true));
        c.interpreter("logout");
        c.interpreter("login " + VOLONTARIO_USER + " password"); // Log in as Volontario

        // Execute myvisit command (needs dummy args for a.length < 2 check)
        o = c.interpreter("myvisit");
        assertEquals(Status.WARN, o.getStatus());
        assertEquals("No visits found for the user: " + VOLONTARIO_USER, o.getLogMessage());
    }
}