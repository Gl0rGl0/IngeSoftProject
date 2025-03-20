package V1.ingsoft.controller.item.luoghi;

import V1.ingsoft.controller.item.persone.Fruitore;
import V1.ingsoft.controller.item.persone.Iscrizione;
import V1.ingsoft.util.Date;

import java.util.ArrayList;

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
    public Date date;
    public String UID;
    public String tipoVisitaUID;

    @JsonIgnore
    public String volontarioUID;
    public StatusVisita status = StatusVisita.PROPOSTA;
    @JsonIgnore
    public ArrayList<Iscrizione> fruitori = new ArrayList<>();

    @JsonCreator
    public Visita(
            @JsonProperty("data") Date data,
            @JsonProperty("UID") String UID,
            @JsonProperty("stato") StatusVisita status) {

        this.date = data;
        this.UID = UID;
        this.status = status;
    }

    public Visita(TipoVisita tipo, Date date, String uidVolontario) {
        this.tipo = tipo;
        this.date = date;
        this.volontarioUID = uidVolontario;
        this.UID = tipo.getUID() + date.hashCode() + uidVolontario;
    }

    public StatusVisita getStatus() {
        return this.status;
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
        return date;
    }

    public String getTitle() {
        return tipo.getTitle();
    }

    public void setStatus(StatusVisita status) {
        this.status = status;
    }

    public boolean hasFruitore(String userF) {
        for (Iscrizione i : fruitori) {
            if (i.fruitoreUID.equals(userF))
                return true;
        }
        return false;
    }

    public String addPartecipants(Fruitore f, int n) {
        if (tipo.numMaxPartecipants - getCurrentNumber() < n) {
            return "capienza";
        }

        if (hasFruitore(f.getUsername())) {
            return "presente";
        }

        Iscrizione newIscrizione = new Iscrizione(f.getUsername(), n);
        fruitori.add(newIscrizione);

        if (getCurrentNumber() == tipo.numMaxPartecipants) {
            setStatus(StatusVisita.COMPLETA);
        }
        return newIscrizione.getUIDFruitore();
    }

    public void removePartecipant(String user) {
        int capienzaAttuale = getCurrentNumber();
        for (Iscrizione i : fruitori) {
            if (i.getUIDFruitore().equals(user)) {
                if (fruitori.remove(i) && capienzaAttuale == tipo.getNumMaxPartecipants())
                    setStatus(StatusVisita.PROPOSTA);
                return;
            }
        }
    }

    public int getCurrentNumber() {
        int out = 0;
        for (Iscrizione i : fruitori) {
            out += i.getQuantity();
        }
        return out;
    }

    public void isThreeDaysFrom(Date d) {
        if (date.dayOfTheYear() - d.dayOfTheYear() <= 3) {
            if (getCurrentNumber() < this.tipo.numMinPartecipants) {
                this.status = StatusVisita.CANCELLATA;
            } else {
                this.status = StatusVisita.CONFERMATA;
            }
        }
    }

    @Override
    public String toString() {
        return "Titolo: " + tipo.getTitle()
                + "\nVolontario: " + getUidVolontario()
                + "\nDescrizione: " + tipo.getDescription()
                + "\nPunto Incontro: " + tipo.getMeetingPlace()
                + "\nData: " + date
                + "\nTime: " + tipo.getInitTime()
                + "\nBiglietto necessario: " + tipo.isFree()
                + "\n";
    }

    public ArrayList<Iscrizione> getIscrizioni() {
        return fruitori;
    }
}
