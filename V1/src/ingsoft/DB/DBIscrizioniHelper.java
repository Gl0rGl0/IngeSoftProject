package ingsoft.DB;

import java.util.ArrayList;
import java.util.HashMap;

import ingsoft.persone.Iscrizione;

public class DBIscrizioniHelper {
    private HashMap<String, Iscrizione> iscrizioniRepo = new HashMap<>();

    public void addIscrizione(Iscrizione i){
        iscrizioniRepo.put(i.getUIDIscrizione(), i);
    }

    public Iscrizione getIscrizione(String uid){
        return iscrizioniRepo.get(uid);
    }
    
    public void removeIscrizione(String uid){
        iscrizioniRepo.remove(uid);
    }

    public ArrayList<Iscrizione> getIscrizioni(){
        return new ArrayList<Iscrizione>(iscrizioniRepo.values());
    }
}
