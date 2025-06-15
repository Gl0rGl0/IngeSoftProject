package V.Ingsoft.controller.item.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.util.Date;

public class TipoVisitaTest {
    @Test
    void successTipoVisita() throws Exception{
        TipoVisita tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        assertNotNull(tv);
    }

    @Test
    void failTipoVisitaArgs(){
        TipoVisita tv = null;

        try {
            tv = new TipoVisita(new String[]{"Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Insufficient number of arguments", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Title visit can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Visit description can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Visit position can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Initial day is empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Last day can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Starting time can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Duration can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Cost is empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Minumum number of partecipants can't be empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Maximum number of partecipants day is empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", ""}, new Date());
        } catch (Exception e) {
            assertEquals("Day of visit is empty", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01-06-2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertEquals("Error in TipoVisita constructor while parsing: Error in parsing Date.", e.getMessage());
        }

        try {
            tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "a", "true", "5", "20", "LuMa"}, new Date());
        } catch (Exception e) {
            assertTrue(e != null);
        }

        assertNull(tv);
    }

    @Test
    void overlapTest() throws Exception{
        TipoVisita tv1 = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        TipoVisita tv2 = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        assertTrue(tv1.overlaps(tv2));

        tv2 = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "18:30", "120", "true", "5", "20", "LuMa"}, new Date());
        assertFalse(tv1.overlaps(tv2));
    }

    @Test
    void volontariList() throws Exception{
        TipoVisita tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        assertTrue(tv.getVolontariUIDs().isEmpty());
        
        tv.addVolontario("prova");
        assertTrue(tv.getVolontariUIDs().contains("prova"));
        
        tv.removeVolontario("prova");
        assertFalse(tv.getVolontariUIDs().contains("prova"));
        assertTrue(tv.getVolontariUIDs().isEmpty());
    }
}
