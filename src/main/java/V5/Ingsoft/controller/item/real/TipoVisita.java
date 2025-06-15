package V5.Ingsoft.controller.item.real;

import V5.Ingsoft.controller.item.interfaces.Deletable;
import V5.Ingsoft.controller.item.statuses.StatusItem;
import V5.Ingsoft.util.Date;
import V5.Ingsoft.util.DayOfWeekConverter;
import V5.Ingsoft.util.StringUtils;
import V5.Ingsoft.util.Time;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class TipoVisita extends Deletable {
    public static final String PATH = "tipoVisite";

    private final String title;
    private final String description;
    private final String meetingPlace;
    private final Date initDay;
    private final Date finishDay;
    private final Time initTime;
    private final int duration;
    private final boolean free;
    private final int numMinPartecipants;
    private final int numMaxPartecipants;
    private final ArrayList<DayOfWeek> days;

    private ArrayList<String> volontariUID = new ArrayList<>();
    private String luogoUID = null;

    @JsonCreator
    public TipoVisita(
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("meetingPlace") String meetingPlace,
            @JsonProperty("initDay") Date initDay,
            @JsonProperty("finishDay") Date finishDay,
            @JsonProperty("initTime") Time initTime,
            @JsonProperty("duration") int duration,
            @JsonProperty("free") boolean free,
            @JsonProperty("numMinPartecipants") int numMinPartecipants,
            @JsonProperty("numMaxPartecipants") int numMaxPartecipants,
            @JsonProperty("days") ArrayList<DayOfWeek> days,
            @JsonProperty("volontari") ArrayList<String> volontariUID,
            @JsonProperty("luogoUID") String luogoUID,
            @JsonProperty("status") StatusItem status,
            @JsonProperty("insertionDate") Date insertionDate,
            @JsonProperty("deletionDate") Date deletionDate,
            @JsonProperty("UID") String UID) throws Exception {
        super(UID);

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
        this.volontariUID = volontariUID;
        this.luogoUID = luogoUID;
        this.si = status;
        this.insertionDate = insertionDate;
        this.deletionDate = deletionDate;
    }

    // Should this be split into some function? (?)
    public TipoVisita(String[] args, Date d) throws Exception {
        super(args[0].hashCode() + "t");

        if (args.length < 11)   throw new Exception("Insufficient number of arguments");
        if (args[0].isBlank())  throw new Exception("Title visit can't be empty");
        if (args[1].isBlank())  throw new Exception("Visit description can't be empty");
        if (args[2].isBlank())  throw new Exception("Visit position can't be empty");
        if (args[3].isBlank())  throw new Exception("Initial day is empty");
        if (args[4].isBlank())  throw new Exception("Last day can't be empty");
        if (args[5].isBlank())  throw new Exception("Starting time can't be empty");
        if (args[6].isBlank())  throw new Exception("Duration can't be empty");
        if (args[7].isBlank())  throw new Exception("Cost is empty");
        if (args[8].isBlank())  throw new Exception("Minumum number of partecipants can't be empty");
        if (args[9].isBlank())  throw new Exception("Maximum number of partecipants day is empty");
        if (args[10].isBlank()) throw new Exception("Day of visit is empty");

        try {
            this.title = args[0];
            this.description = args[1];
            this.meetingPlace = args[2];
            this.initDay = new Date(args[3]);
            this.finishDay = new Date(args[4]);
            this.initTime = new Time(args[5]);
            this.duration = Integer.parseInt(args[6]);
            this.free = Boolean.parseBoolean(args[7]);
            this.numMinPartecipants = Integer.parseInt(args[8]);
            this.numMaxPartecipants = Integer.parseInt(args[9]);
            this.days = new ArrayList<>(Arrays.asList(DayOfWeekConverter.stringToDays(args[10])));

            this.insertionDate = d;
            this.si = StatusItem.PENDING_ADD;
        } catch (Exception e) {
            throw new Exception("Error in TipoVisita constructor while parsing: " + e.getMessage());
        }
    }
    
    public boolean overlaps(TipoVisita other) {
        if (this.initTime.getMinutes() > other.initTime.getMinutes()) {
            return (other.initTime.getMinutes() + other.duration) > this.initTime.getMinutes();
        } else {
            return (this.initTime.getMinutes() + this.duration) > other.initTime.getMinutes();
        }
    }

    public String getDaysString()               { return DayOfWeekConverter.daysToString(days.toArray(new DayOfWeek[0])); }
    public ArrayList<DayOfWeek> getDays()       { return days; }
    public ArrayList<String> getVolontariUIDs() { return this.volontariUID; }
    
    public String getTitle()            { return this.title; }
    public String getDescription()      { return this.description; }
    public String getMeetingPlace()     { return this.meetingPlace; }
    public Date getInitDay()            { return this.initDay; }
    public Date getFinishDay()          { return this.finishDay; }
    public Time getInitTime()           { return this.initTime; }
    public boolean isFree()             { return this.free; }
    public int getDuration()            { return this.duration; }
    public int getNumMinPartecipants()  { return this.numMinPartecipants; }
    public int getNumMaxPartecipants()  { return this.numMaxPartecipants; }

    public String getLuogoUID()                  { return this.luogoUID; }
    public void setLuogo(String luogoUID)        { this.luogoUID = luogoUID; }
    @Override public String getMainInformation() { return this.title; }

    public boolean addVolontario(String uidVolontario) {
        if (!volontariUID.contains(uidVolontario))
            return this.volontariUID.add(uidVolontario);
        return false;
    }

    public boolean removeVolontario(String uidVolontario) { return this.volontariUID.remove(uidVolontario); }

    public boolean assignedTo(String userNameVolontario) {
        // for (String v : getVolontariUIDs())
        //     if (v.equals(userNameVolontario)) return true;
        // return false;

        return getVolontariUIDs().contains(userNameVolontario);
    }
    
    public int sort(TipoVisita o) {
        int i = this.luogoUID == null   ? 0 : luogoUID.length();
        int ii = o.luogoUID   == null   ? 0 : o.luogoUID.length();
        return i - ii;
    }
    
    public String toArray() {
        return "\"" + this.title + "\" " +
        "\"" + this.description + "\" " +
        "\"" + this.meetingPlace + "\" " +
        this.initDay + " " +
        this.finishDay + " " +
        this.initTime + " " +
        this.duration + " " +
        this.free + " " +
        this.numMinPartecipants + " " +
        this.numMaxPartecipants + " " +
        StringUtils.arrayToStringClean((String[]) this.days.toArray());
    }
    
    @Override public String toString() {
        return "Visit Type {" // Changed "Tipo di visita"
             + "Title='" + title + '\'' // Changed "Titolo"
             + ", Description='" + description + '\'' // Changed "Descrizione"
             + ", Meeting Point=" + meetingPlace // Changed "Punto di Incontro"
             + ", Start Period=" + initDay // Changed "Periodo Inizio"
             + ", End Period=" + finishDay // Changed "Periodo Fine"
             + ", Start Time=" + initTime // Changed "Time Inizio"
             + ", Duration=" + duration + " minutes" // Changed "minuti"
             + ", Free=" + (free ? "Free" : "Ticket required") // Changed "Gratuita", "Biglietto necessario"
             + ", Min Participants=" + numMinPartecipants // Changed "Numero Min Partecipanti"
             + ", Max Participants=" + numMaxPartecipants // Changed "Numero Max Partecipanti"
             + ", Available Days=" + getDaysString() // Changed "Days disponibilit"
             + ", Visit Status=" + si // Changed "Stato visita"
             + ", PlaceUID=" + luogoUID
             + ", Volunteer=" + volontariUID
             + "}\n";
    }

    @Override public boolean equals(Object a) {
        if (!(a instanceof TipoVisita)) return false;
        return this.title.equals(((TipoVisita) a).getTitle());
    }
}
