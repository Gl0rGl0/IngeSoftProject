package V1.ingsoft.luoghi;

import V1.ingsoft.persone.Fruitore;
import V1.ingsoft.persone.Iscrizione;
import V1.ingsoft.util.Date;

import java.util.ArrayList;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Visita {
    @JsonIgnore
    public static final String PATH = "archivio-visite";

    @JsonIgnore
    public TipoVisita tipo;
    public Date data;
    public String UID;
    public String tipoVisitaUID;

    @JsonIgnore
    public String volontarioUID;
    public StatusVisita stato = StatusVisita.PROPOSTA;
    @JsonIgnore
    public ArrayList<Iscrizione> partecipanti = new ArrayList<>();

    @JsonCreator
    public Visita(
            @JsonProperty("data") Date data,
            @JsonProperty("UID") String UID,
            @JsonProperty("stato") StatusVisita stato) {

        this.data = data;
        this.UID = UID;
        this.stato = stato;
    }

    public Visita(TipoVisita tipo, Date data, String UID, String UIDTipoVisita) {
        this.tipo = tipo;
        this.data = data;
        this.tipoVisitaUID = UIDTipoVisita;
        this.UID = UID;
    }

    public Visita(TipoVisita tipo, Date data, String UIDTipoVisita) {
        this.tipo = tipo;
        this.data = data;
        this.tipoVisitaUID = UIDTipoVisita;
        this.UID = UIDTipoVisita + data.hashCode();
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

    public String tipoVisitaUID() {
        return this.tipo.getUID();
    }

    public TipoVisita getTipo() {
        return tipo;
    }

    public Date getDate() {
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

    public String addPartecipanti(Fruitore f, int n) {
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
        if (data.dayOfTheYear() - d.dayOfTheYear() <= 3) {
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
                + "\nTime: " + tipo.getTimeInizio()
                + "\nBiglietto necessario: " + tipo.isFree();
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return partecipanti;
    }
}
