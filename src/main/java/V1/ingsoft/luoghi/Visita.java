package V1.ingsoft.luoghi;

import V1.ingsoft.persone.Fruitore;
import V1.ingsoft.persone.Iscrizione;
import V1.ingsoft.util.Date;

import java.util.ArrayList;
import java.util.UUID;

public class Visita {
    public TipoVisita tipo;
    public Date data;
    public String UID;
    public String getTipoVisitaUID;

    public String volontarioUID;
    public StatusVisita stato = StatusVisita.PROPOSTA;
    public ArrayList<Iscrizione> partecipanti = new ArrayList<>();

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

    public void setVolontario(String uidVolontario) {
        this.volontarioUID = uidVolontario;
    }

    public String getUidVolontario() {
        return this.volontarioUID;
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

    public boolean isPresenteFruitore(String userF) {
        for (Iscrizione i : partecipanti) {
            if (i.fruitoreUID.equals(userF))
                return true;
        }
        return false;
    }

    public String aggiungiPartecipanti(Fruitore f, int n) {
        if (tipo.numMaxPartecipants - getAttualeCapienza() < n) {
            return "capienza";
        }

        if (isPresenteFruitore(f.getUsername())) {
            return "presente";
        }

        Iscrizione nuova = new Iscrizione(f.getUsername(), n);
        partecipanti.add(nuova);

        if (getAttualeCapienza() == tipo.numMaxPartecipants) {
            setStatus(StatusVisita.COMPLETA);
        }
        return nuova.getUIDFruitore();
    }

    public void rimuoviPartecipante(String user) {
        int capienzaAttuale = getAttualeCapienza();
        for (Iscrizione i : partecipanti) {
            if (i.getUIDFruitore().equals(user)) {
                if (partecipanti.remove(i) && capienzaAttuale == tipo.getNumMaxPartecipants())
                    setStatus(StatusVisita.PROPOSTA);
                return;
            }
        }
    }

    public int getAttualeCapienza() {
        int out = 0;
        for (Iscrizione i : partecipanti) {
            out += i.getNumeroIscrizioni();
        }
        return out;
    }

    public void mancano3Giorni(Date d) {
        if (data.giornoDellAnno() - d.giornoDellAnno() <= 3) {
            if (getAttualeCapienza() < this.tipo.numMinPartecipants) {
                this.stato = StatusVisita.CANCELLATA;
            } else {
                this.stato = StatusVisita.CONFERMATA;
            }
        }
    }

    @Override
    public String toString() {
        return "Titolo: " + tipo.getTitolo()
                + "\nDescrizione: " + tipo.getDescrizione()
                + "\nPunto Incontro: " + tipo.getGps()
                + "\nData: " + data
                + "\nOra: " + tipo.getOraInizio()
                + "\nBiglietto necessario: " + tipo.isFree();
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return partecipanti;
    }
}
