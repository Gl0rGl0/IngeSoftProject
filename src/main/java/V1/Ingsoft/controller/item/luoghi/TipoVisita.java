package V1.Ingsoft.controller.item.luoghi;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import V1.Ingsoft.util.Date;
import V1.Ingsoft.util.DayOfWeekConverter;
import V1.Ingsoft.util.GPS;
import V1.Ingsoft.util.Time;
import V1.Ingsoft.view.ViewSE;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class TipoVisita {
    public static final String PATH = "tipoVisite";

    String title;
    String description;
    GPS meetingPlace;
    Date initDay;
    Date finishDay;
    Time initTime;
    int duration;
    boolean free;
    int numMinPartecipants;
    int numMaxPartecipants;
    ArrayList<DayOfWeek> days;
    Date insertionDay;
    String UID;
    public StatusVisita sv = StatusVisita.ATTESA;

    private ArrayList<String> volontariUID = new ArrayList<>();
    private String luogoUID = null;

    @JsonCreator
    public TipoVisita(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("meetingPlace") GPS meetingPlace,
            @JsonProperty("initDay") Date initDay,
            @JsonProperty("finishDay") Date finishDay,
            @JsonProperty("initTime") Time initTime,
            @JsonProperty("duration") int duration,
            @JsonProperty("free") boolean free,
            @JsonProperty("numMinPartecipants") int numMinPartecipants,
            @JsonProperty("numMaxPartecipants") int numMaxPartecipants,
            @JsonProperty("days") ArrayList<DayOfWeek> days,
            @JsonProperty("insertionDay") Date insertionDay,
            @JsonProperty("volontari") ArrayList<String> volontariUID,
            @JsonProperty("luogoUID") String luogoUID,
            @JsonProperty("status") StatusVisita status,
            @JsonProperty("UID") String UID) {
        this.title = title;
        this.description = description;
        this.meetingPlace = meetingPlace;
        this.initDay = initDay;
        this.finishDay = finishDay;
        this.initTime = initTime;
        this.duration = duration;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.days = days;
        this.insertionDay = insertionDay;
        this.volontariUID = volontariUID;
        this.luogoUID = luogoUID;
        this.sv = status;
        this.UID = UID;
    }

    // Costruttore
    public TipoVisita(
            String title, String description, GPS meetingPlace,
            Date initDay, Date finishDay, Time initTime, int duration,
            boolean free, int numMinPartecipants, int numMaxPartecipants, String days, String UID, Date dataI) {
        this.title = title;
        this.description = description;
        this.meetingPlace = meetingPlace;
        this.initDay = initDay;
        this.finishDay = finishDay;
        this.initTime = initTime;
        this.duration = duration;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.UID = UID;

        this.insertionDay = dataI;

        this.days = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(days)));
    }

    public TipoVisita(
            String title, String description, GPS meetingPlace,
            Date initDay, Date finishDay, Time initTime, int duration,
            boolean free, int numMinPartecipants, int numMaxPartecipants, String days, Date dateI) {
        this.title = title;
        this.description = description;
        this.meetingPlace = meetingPlace;
        this.initDay = initDay;
        this.finishDay = finishDay;
        this.initTime = initTime;
        this.duration = duration;
        this.free = free;
        this.numMinPartecipants = numMinPartecipants;
        this.numMaxPartecipants = numMaxPartecipants;
        this.insertionDay = dateI;

        this.UID = title.hashCode() + "";

        this.days = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(days)));

    }

    public String getDaysString() {
        return DayOfWeekConverter.daysToString(days.toArray(new DayOfWeek[0]));
    }

    public ArrayList<DayOfWeek> getDays() {
        return days;
    }

    public String getUID() {
        return this.UID;
    }

    public ArrayList<String> getVolontariUIDs() {
        return this.volontariUID;
    }

    // da spezzettare in qualche funzione (?)
    public TipoVisita(String[] args, Date d) {
        int length = args.length;
        try {
            this.title = (length > 0 && !args[0].equals("/")) ? args[0] : null;
            this.description = (length > 1 && !args[1].equals("/")) ? args[1] : null;
            this.meetingPlace = (length > 2 && !args[2].equals("/")) ? new GPS(args[2]) : null;
            this.initDay = (length > 3 && !args[3].equals("/")) ? new Date(args[3]) : null;
            this.finishDay = (length > 4 && !args[4].equals("/")) ? new Date(args[4]) : null;
            this.initTime = (length > 5 && !args[5].equals("/")) ? new Time(args[5]) : null;
            this.duration = (length > 6 && !args[6].equals("/")) ? Integer.parseInt(args[6]) : -1;
            this.free = (length > 7 && !args[7].equals("/")) ? Boolean.parseBoolean(args[7]) : false;
            this.numMinPartecipants = (length > 8 && !args[8].equals("/")) ? Integer.parseInt(args[8]) : -1;
            this.numMaxPartecipants = (length > 9 && !args[9].equals("/")) ? Integer.parseInt(args[9]) : -1;
            this.UID = title.hashCode() + "";
            this.days = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(args[10])));

            this.insertionDay = d;
        } catch (NumberFormatException e) {
            ViewSE.println(e);
            ViewSE.println(
                    "Errore semantico: inserito una stringa al posto di un numero, o qualcosa di simile. VISITA NON CREATA");
        }
    }

    public boolean overlaps(TipoVisita other) {
        if (this.initTime.getMinutes() > other.initTime.getMinutes()) {
            return (other.initTime.getMinutes() + other.duration) > this.initTime.getMinutes();
        } else {
            return (this.initTime.getMinutes() + this.duration) > other.initTime.getMinutes();
        }
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public GPS getMeetingPlace() {
        return this.meetingPlace;
    }

    public Date getInitDay() {
        return this.initDay;
    }

    public Date getFinishDay() {
        return this.finishDay;
    }

    public Time getInitTime() {
        return this.initTime;
    }

    public boolean isFree() {
        return this.free;
    }

    public int getDuration() {
        return this.duration;
    }

    public int getNumMinPartecipants() {
        return this.numMinPartecipants;
    }

    public int getNumMaxPartecipants() {
        return this.numMaxPartecipants;
    }

    public Date getInsertionDay() {
        return this.insertionDay;
    }

    public boolean isName(String other) {
        return this.title.equalsIgnoreCase(other);
    }

    public void addVolontario(String uidVolontario) {
        if (!volontariUID.contains(uidVolontario))
            this.volontariUID.add(uidVolontario);
    }

    public void removeVolontario(String uidVolontario) {
        this.volontariUID.remove(uidVolontario);
    }

    @Override
    public String toString() {
        return "Tipo di visita {"
                + "Titolo='" + title + '\''
                + ", Descrizione='" + description + '\''
                + ", Punto di Incontro=" + meetingPlace
                + ", Periodo Inizio=" + initDay
                + ", Periodo Fine=" + finishDay
                + ", Time Inizio=" + initTime
                + ", Durata=" + duration + " minuti"
                + ", Gratuita=" + (free ? "Gratuita" : "Biglietto necessario")
                + ", Numero Min Partecipanti=" + numMinPartecipants
                + ", Numero Max Partecipanti=" + numMaxPartecipants
                + ", Days disponibilit=" + getDaysString()
                + ", Stato visita=" + sv
                + "}\n";
    }

    public void isMonthExpired(Date d) {
        System.out.println("AAAAAA " + (Math.abs(d.dayOfTheYear() - insertionDay.dayOfTheYear()) >= Date
                .monthLength(insertionDay)) + d.dayOfTheYear() + " " + insertionDay.dayOfTheYear() + this.title);

        if (this.sv == StatusVisita.PROPOSTA)
            return;

        if (Math.abs(d.dayOfTheYear() - insertionDay.dayOfTheYear()) >= Date.monthLength(insertionDay))
            this.sv = StatusVisita.PROPOSTA;
    }

    public StatusVisita getStatus() {
        return this.sv;
    }

    public boolean assignedTo(String userNameVolontario) {
        for (String v : getVolontariUIDs()) {
            if (v.equals(userNameVolontario))
                return true;
        }
        return false;
    }

    public void setLuogo(String luogoUID) {
        this.luogoUID = luogoUID;
    }

    public String getLuogo() {
        return this.luogoUID;
    }
}
