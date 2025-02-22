package ingsoft.luoghi;

import ingsoft.persone.Fruitore;
import ingsoft.util.Date;
import ingsoft.util.ViewSE;
import java.util.ArrayList;
import java.util.UUID;

public class Visita {
    public TipoVisita tipo;
    public Date data;
    public String UID;
    public String getTipoVisitaUID;

    public StatusVisita stato = StatusVisita.PROPOSTA;
    public ArrayList<Fruitore> partecipanti = new ArrayList<>();

    public Visita(TipoVisita tipo, Date data, String UID, String UIDTipoVisita) {
        this.tipo = tipo;
        this.data = data;
        this.getTipoVisitaUID = UIDTipoVisita;
        this.UID = UID;
    }

    public Visita(TipoVisita tipo, Date data, String UIDTipoVisita) {
        this.tipo = tipo;
        this.data = data;
        this.getTipoVisitaUID = UIDTipoVisita;
        this.UID = UUID.randomUUID().toString();
    }

    public StatusVisita getStatus() {
        return this.stato;
    }

    public String getUID() {
        return this.UID;
    }

    public String getTipoVisitaUID() {
        return this.tipo.getUID();
    }

    public TipoVisita getTipo() {
        return tipo;
    }

    public Date getData() {
        return data;
    }

    public String getTitolo() {
        return tipo.getTitolo();
    }

    public void setStatus(StatusVisita s) {
        this.stato = s;
    }

    public void aggiungiPartecipanti(Fruitore f) {
        int nNuovo = f.getNumeroIscrizioni();
        if (tipo.numMaxPartecipants - getAttualeCapienza() < nNuovo) {
            ViewSE.print("Impossibile iscriverti alla visita, la capienza eccede la tua richiesta.");
            return;
        }
        partecipanti.add(f);

        if (getAttualeCapienza() == tipo.numMaxPartecipants) {
            setStatus(StatusVisita.COMPLETA);
        }
    }

    public int getAttualeCapienza() {
        int out = 0;
        for (Fruitore f : partecipanti) {
            out += f.getNumeroIscrizioni();
        }
        return out;
    }
}
