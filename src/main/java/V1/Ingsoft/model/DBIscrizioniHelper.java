package V1.Ingsoft.model;

import V1.Ingsoft.controller.item.persone.Iscrizione;

import java.util.ArrayList;
import java.util.HashMap;

public class DBIscrizioniHelper {
    private final HashMap<String, Iscrizione> iscrizioniRepo = new HashMap<>();

    public void addIscrizione(Iscrizione i) {
        iscrizioniRepo.put(i.getUIDIscrizione(), i);
    }

    public Iscrizione getIscrizione(String uid) {
        return iscrizioniRepo.get(uid);
    }

    public void removeIscrizione(String uid) {
        iscrizioniRepo.remove(uid);
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return new ArrayList<>(iscrizioniRepo.values());
    }
}
