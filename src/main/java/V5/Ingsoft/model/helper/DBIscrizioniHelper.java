package V5.Ingsoft.model.helper;

import java.util.ArrayList;

import V5.Ingsoft.controller.item.persone.Iscrizione;

public class DBIscrizioniHelper extends DBMapHelper<Iscrizione> {

    public DBIscrizioniHelper() {
        super(Iscrizione.PATH, Iscrizione.class);
    }

    public DBIscrizioniHelper(ArrayList<Iscrizione> list) {
        super(Iscrizione.PATH, Iscrizione.class, list);
    }
}
