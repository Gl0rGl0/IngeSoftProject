package V5.Ingsoft.model;

import V5.Ingsoft.controller.item.persone.Iscrizione;

public class DBIscrizioniHelper extends DBMapHelper<Iscrizione> {

    public DBIscrizioniHelper() {
        super(Iscrizione.PATH, Iscrizione.class);
    }
}
