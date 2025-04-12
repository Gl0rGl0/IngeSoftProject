package V4.Ingsoft.controller.item.luoghi;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import V4.Ingsoft.util.Date;
import V4.Ingsoft.util.DayOfWeekConverter;
import V4.Ingsoft.util.Time;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class TipoVisita {
    public static final String PATH = "tipoVisite";

    String title;
    String description;
    String meetingPlace;
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
    public StatusVisita sv = StatusVisita.PENDING; // Updated enum constant

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

    // Should this be split into some function? (?)
    public TipoVisita(String[] args, Date d) throws Exception {
        if(args.length < 11)
        throw new Exception("Error in TipoVisita constructor: Insufficient number of arguments");

        if(args[0].isBlank())
            throw new Exception("Error in TipoVisita constructor: Title name can't be empty");

        if(args[1].isBlank())
            throw new Exception("Error in TipoVisita constructor: Description can't be empty");

        if(args[2].isBlank())
            throw new Exception("Error in TipoVisita constructor: Position can't be empty");

        if(args[3].isBlank())
            throw new Exception("Error in TipoVisita constructor: Initial day is empty");

        if(args[4].isBlank())
            throw new Exception("Error in TipoVisita constructor: Last day name can't be empty");

        if(args[5].isBlank())
            throw new Exception("Error in TipoVisita constructor: Starting time can't be empty");

        if(args[6].isBlank())
            throw new Exception("Error in TipoVisita constructor: Duration can't be empty");

        if(args[7].isBlank())
            throw new Exception("Error in TipoVisita constructor: Cost is empty");

        if(args[8].isBlank())
            throw new Exception("Error in TipoVisita constructor: Minumum number of partecipants can't be empty");

        if(args[9].isBlank())
            throw new Exception("Error in TipoVisita constructor: Maximum number of partecipants day is empty");
        
        if(args[10].isBlank())
            throw new Exception("Error in TipoVisita constructor: Day of visit is empty");
        
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
            
                this.insertionDay = d;
            } catch (Exception e) {
                throw new Exception("Error in TipoVisita constructor while parsing: " + e.getMessage());
            }
            

        this.UID = title.hashCode() + "t";
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

    public String getMeetingPlace() {
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

    public boolean addVolontario(String uidVolontario) {
        if (!volontariUID.contains(uidVolontario))
            return this.volontariUID.add(uidVolontario);
        return false;
    }

    public void removeVolontario(String uidVolontario) {
        this.volontariUID.remove(uidVolontario);
    }

    @Override
    public String toString() {
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
                + ", Visit Status=" + sv // Changed "Stato visita"
                + "}\n";
    }

    public void isMonthExpired(Date d) {
        // System.out.println("AAAAAA " + (Math.abs(d.dayOfTheYear() - insertionDay.dayOfTheYear()) >= Date
        //         .monthLength(insertionDay)) + d.dayOfTheYear() + " " + insertionDay.dayOfTheYear() + this.title);

        if (this.sv == StatusVisita.PROPOSED) // Updated enum constant
            return;

        if (Math.abs(d.dayOfTheYear() - insertionDay.dayOfTheYear()) >= insertionDay.getMonth().getValue())
            this.sv = StatusVisita.PROPOSED; // Updated enum constant
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
