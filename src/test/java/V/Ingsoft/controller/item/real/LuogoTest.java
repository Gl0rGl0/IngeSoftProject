package V.Ingsoft.controller.item.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import V5.Ingsoft.controller.item.real.Luogo;
import V5.Ingsoft.util.Date;

public class LuogoTest {
    @Test
    void successPlace() throws Exception{
        Luogo l = new Luogo(new String[]{"TITLE", "DESCRIPTION", "PLACE"}, new Date());
        assertNotNull(l);
    }

    @Test
    void failPlaceArgs() {
        Luogo l = null;

        try {
            l = new Luogo(new String[]{"TITLE", "DESCRIPTION"}, new Date());
        } catch (Exception e) {
           assertEquals("Insufficient number of arguments", e.getMessage());
        }

        try {
            l = new Luogo(new String[]{"", "DESCRIPTION", "PLACE"}, new Date());
        } catch (Exception e) {
           assertEquals("Title name can't be empty", e.getMessage());
        }

        try {
            l = new Luogo(new String[]{"TITLE", "", "PLACE"}, new Date());
        } catch (Exception e) {
           assertEquals("Place description can't be empty", e.getMessage());
        }

        try {
            l = new Luogo(new String[]{"TITLE", "DESCRIPTION", ""}, new Date());
        } catch (Exception e) {
           assertEquals("Place position can't be empty", e.getMessage());
        }
        assertNull(l);
    }

    @Test
    void tipoVisiteList() throws Exception{
        Luogo l = new Luogo(new String[]{"TITLE", "DESCRIPTION", "PLACE"}, new Date());
        assertTrue(l.getTipoVisitaUID().isEmpty());

        l.addTipoVisita("prova");
        assertTrue(l.getTipoVisitaUID().contains("prova"));

        l.removeTipoVisita("prova");
        assertTrue(l.getTipoVisitaUID().isEmpty());
    }
}
