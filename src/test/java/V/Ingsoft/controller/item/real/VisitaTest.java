package V.Ingsoft.controller.item.real;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import V5.Ingsoft.controller.item.persone.Iscrizione;
import V5.Ingsoft.controller.item.real.TipoVisita;
import V5.Ingsoft.controller.item.real.Visita;
import V5.Ingsoft.controller.item.statuses.Result;
import V5.Ingsoft.controller.item.statuses.StatusVisita;
import V5.Ingsoft.model.Model;
import V5.Ingsoft.util.Date;

public class VisitaTest {
    private TipoVisita tv;
    
    public VisitaTest() throws Exception{
        tv = new TipoVisita(new String[]{"Tour della Foresta", "Esplora i sentieri nascosti della foresta.", "12.34:56.78", "01/06/2025", "15/06/2025", "08:30", "120", "true", "5", "20", "LuMa"}, new Date());
        Model.getInstance().dbTipoVisiteHelper.addItem(tv);
    }

    @Test
    void successVisita() throws Exception{
        Visita v = new Visita(tv, new Date().addDay(5), "prova");
        assertNotNull(v);
    } //inutile fare i test sul fallimento della visita perchè sono gli stessi di tipovisita

    @Test
    void addFruitori() throws Exception{
        Visita v = new Visita(tv, new Date().addDay(5), "prova");
        assertFalse(v.hasFruitore("prova"));

        Iscrizione i = new Iscrizione(v.getUID(),"prova", 1);
        assertEquals(Result.SUCCESS, v.addPartecipants(i));
        assertTrue(v.hasFruitore("prova"));
        assertEquals(StatusVisita.PROPOSED.name(), v.getStatus().name());
        assertEquals(1, v.getCurrentNumber());

        assertEquals(Result.ALREADY_SIGNED, v.addPartecipants(i));

        Iscrizione ii = new Iscrizione(v.getUID(),"prova2", tv.getNumMaxPartecipants() - 1);
        assertEquals(Result.SUCCESS, v.addPartecipants(ii));
        assertTrue(v.hasFruitore("prova2"));
        assertEquals(StatusVisita.COMPLETED.name(), v.getStatus().name());
        
        
        Iscrizione iii = new Iscrizione(v.getUID(),"prova3", 1);
        assertEquals(Result.NOTENOUGH_CAPACITY, v.addPartecipants(iii));
        assertFalse(v.hasFruitore("prova3"));
    }

    @Test
    void removeFruitore() throws Exception{
        Visita v = new Visita(tv, new Date().addDay(5), "prova");

        Iscrizione i = new Iscrizione(v.getUID(),"prova", 1);
        v.addPartecipants(i);

        v.removeIscrizioneByUID(i.getUID());
        assertFalse(v.hasFruitore("prova"));
        assertEquals(0, v.getCurrentNumber());

        v.addPartecipants(i);

        v.removePartecipant("prova");   
        assertFalse(v.hasFruitore("prova"));
    }
}
