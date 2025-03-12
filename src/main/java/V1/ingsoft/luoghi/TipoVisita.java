package V1.ingsoft.luoghi;

import V1.ingsoft.ViewSE;
import V1.ingsoft.util.Date;
import V1.ingsoft.util.DayOfWeekConverter;
import V1.ingsoft.util.GPS;
import V1.ingsoft.util.Ora;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class TipoVisita {
    public static final String PATH = "tipoVisite";

    String titolo;
    String descrizione;
    GPS puntoIncontro;
    Date dataInizioPeriodo;
    Date dataFinePeriodo;
    Ora oraInizio;
    int durataVisita;
    boolean free;
    int numMinPartecipants;
    int numMaxPartecipants;
    ArrayList<DayOfWeek> giorni;
    Date dataInserimento;
    String UID;

    private ArrayList<String> volontariUID = new ArrayList<>();
    private String luogoUID = null;

    @JsonIgnore
    public StatusVisita sv = StatusVisita.ATTESA;

    @JsonCreator
    public TipoVisita(
            @JsonProperty("titolo") String titolo,
            @JsonProperty("descrizione") String descrizione,
            @JsonProperty("puntoIncontro") GPS puntoIncontro,
            @JsonProperty("dataInizioPeriodo") Date dataInizioPeriodo,
            @JsonProperty("dataFinePeriodo") Date dataFinePeriodo,
            @JsonProperty("oraInizio") Ora oraInizio,
            @JsonProperty("durataVisita") int durataVisita,
            @JsonProperty("free") boolean free,
            @JsonProperty("numMinPartecipants") int numMinPartecipants,
            @JsonProperty("numMaxPartecipants") int numMaxPartecipants,
            @JsonProperty("giorni") ArrayList<DayOfWeek> giorni,
            @JsonProperty("dataInserimento") Date dataInserimento,
            @JsonProperty("volontari") ArrayList<String> volontariUID,
            @JsonProperty("luogoUID") String luogoUID,
            @JsonProperty("UID") String UID) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.dataInizioPeriodo = dataInizioPeriodo;
        this.dataFinePeriodo = dataFinePeriodo;
        this.oraInizio = oraInizio;
        this.durataVisita = durataVisita;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.giorni = giorni;
        this.dataInserimento = dataInserimento;
        this.volontariUID = volontariUID;
        this.luogoUID = luogoUID;
        this.UID = UID;
    }

    // Costruttore
    public TipoVisita(
            String titolo, String descrizione, GPS puntoIncontro,
            Date dataInizioPeriodo, Date dataFinePeriodo, Ora oraInizio, int durataVisita,
            boolean free, int numMinPartecipants, int numMaxPartecipants, String days, String UID, Date dataI) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.dataInizioPeriodo = dataInizioPeriodo;
        this.dataFinePeriodo = dataFinePeriodo;
        this.oraInizio = oraInizio;
        this.durataVisita = durataVisita;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.UID = titolo.hashCode() + "";

        this.dataInserimento = dataI;

        this.giorni = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(days)));
    }

    public TipoVisita(
            String titolo, String descrizione, GPS puntoIncontro,
            Date dataInizioPeriodo, Date dataFinePeriodo, Ora oraInizio, int durataVisita,
            boolean free, int numMinPartecipants, int numMaxPartecipants, String days, Date dateI) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.puntoIncontro = puntoIncontro;
        this.dataInizioPeriodo = dataInizioPeriodo;
        this.dataFinePeriodo = dataFinePeriodo;
        this.oraInizio = oraInizio;
        this.durataVisita = durataVisita;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.dataInserimento = dateI;

        this.UID = titolo.hashCode() + "";

        this.giorni = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(days)));

    }

    public String getGiorniString() {
        return DayOfWeekConverter.daysToString(giorni.toArray(new DayOfWeek[0]));
    }

    public ArrayList<DayOfWeek> getGiorni() {
        return giorni;
    }

    public String getUID() {
        return this.UID;
    }

    public ArrayList<String> getVolontariUID() {
        return this.volontariUID;
    }

    // da spezzettare in qualche funzione (?)
    public TipoVisita(String[] args, Date d) {
        int length = args.length;
        try {
            this.titolo = (length > 0 && !args[0].equals("/")) ? args[0] : null;
            this.descrizione = (length > 1 && !args[1].equals("/")) ? args[1] : null;
            this.puntoIncontro = (length > 2 && !args[2].equals("/")) ? new GPS(args[2]) : null;
            this.dataInizioPeriodo = (length > 3 && !args[3].equals("/")) ? new Date(args[3]) : null;
            this.dataFinePeriodo = (length > 4 && !args[4].equals("/")) ? new Date(args[4]) : null;
            this.oraInizio = (length > 5 && !args[5].equals("/")) ? new Ora(args[5]) : null;
            this.durataVisita = (length > 6 && !args[6].equals("/")) ? Integer.parseInt(args[6]) : -1;
            this.free = (length > 7 && !args[7].equals("/")) ? Boolean.parseBoolean(args[7]) : false;
            this.numMinPartecipants = (length > 8 && !args[8].equals("/")) ? Integer.parseInt(args[8]) : -1;
            this.numMaxPartecipants = (length > 9 && !args[9].equals("/")) ? Integer.parseInt(args[9]) : -1;
            this.UID = titolo.hashCode() + "";
            this.giorni = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(args[10])));

            this.dataInserimento = d;
        } catch (NumberFormatException e) {
            ViewSE.println(e);
            ViewSE.println(
                    "Errore semantico: inserito una stringa al posto di un numero, o qualcosa di simile. VISITA NON CREATA");
        }
    }

    public boolean sovrappone(TipoVisita other) {
        if (this.oraInizio.getMinuti() > other.oraInizio.getMinuti()) {
            return (other.oraInizio.getMinuti() + other.durataVisita) > this.oraInizio.getMinuti();
        } else {
            return (this.oraInizio.getMinuti() + this.durataVisita) > other.oraInizio.getMinuti();
        }
    }

    public String getTitolo() {
        return this.titolo;
    }

    public String getDescrizione() {
        return this.descrizione;
    }

    public GPS getGps() {
        return this.puntoIncontro;
    }

    public Date getDataInizioPeriodo() {
        return this.dataInizioPeriodo;
    }

    public Date getDataFinePeriodo() {
        return this.dataFinePeriodo;
    }

    public Ora getOraInizio() {
        return this.oraInizio;
    }

    public boolean isFree() {
        return this.free;
    }

    public int getDurataVisita() {
        return this.durataVisita;
    }

    public int getNumMinPartecipants() {
        return this.numMinPartecipants;
    }

    public int getNumMaxPartecipants() {
        return this.numMaxPartecipants;
    }

    public Date getDataInserimento() {
        return this.dataInserimento;
    }

    public boolean isName(String altroTitolo) {
        return this.titolo.equalsIgnoreCase(altroTitolo);
    }

    public void addVolontario(String uidVolontario) {
        this.volontariUID.add(uidVolontario);
    }

    public void removeVolontario(String uidVolontario) {
        this.volontariUID.remove(uidVolontario);
    }

    @Override
    public String toString() {
        return "Tipo di visita {"
                + "Titolo='" + titolo + '\''
                + ", Descrizione='" + descrizione + '\''
                + ", Punto di Incontro=" + puntoIncontro
                + ", Periodo Inizio=" + dataInizioPeriodo
                + ", Periodo Fine=" + dataFinePeriodo
                + ", Ora Inizio=" + oraInizio
                + ", Durata=" + durataVisita + " minuti"
                + ", Gratuita=" + (free ? "Gratuita" : "Biglietto necessario")
                + ", Numero Min Partecipanti=" + numMinPartecipants
                + ", Numero Max Partecipanti=" + numMaxPartecipants
                + ", Giorni disponibilit=" + getGiorniString()
                + "}\n";
    }

    public void isMeseScaduto(Date d) {
        if (this.sv == StatusVisita.PROPOSTA)
            return;

        if (Math.abs(d.giornoDellAnno() - dataInserimento.giornoDellAnno()) >= Date.lunghezzaMese(dataInserimento))
            this.sv = StatusVisita.PROPOSTA;
    }

    public StatusVisita getStato() {
        return this.sv;
    }

    public boolean lavoraUIDVolontario(String usernameVolontario) {
        for (String v : getVolontariUID()) {
            if (v.equals(usernameVolontario))
                return true;
        }
        return false;
    }

    public void setLuogo(String luogoUID) {
        this.luogoUID = luogoUID;
    }

    public String getLuogoUID() {
        return this.luogoUID;
    }
}
