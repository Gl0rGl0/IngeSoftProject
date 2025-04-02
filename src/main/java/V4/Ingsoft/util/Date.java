package V4.Ingsoft.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import V4.Ingsoft.controller.Controller;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE)
public class Date {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    public LocalDateTime localDate;

    @JsonCreator
    public Date(@JsonProperty("localDate") LocalDateTime lc) {
        this.localDate = lc;
    }

    /**
     * Constructs a complete date (day, month, and year).
     *
     * @param gg day
     * @param mm month
     * @param aa year
     */
    public Date(int gg, int mm, int aa) {
        this.localDate = LocalDate.of(aa, mm, gg).atStartOfDay();
    }

    public Date() {
        this.localDate = LocalDateTime.now();
    }

    /**
     * Constructs a date from a string in the format "dd/mm[/yy]-[hh:mm]".
     * If the year is not present, it is set to -1 (Note: This behavior seems incorrect, LocalDate requires a year).
     *
     * @param in the string containing the date and optionally a time part (comment seems outdated)
     */
    public Date(String in) {
        String[] parts = in.split("-");

        setDate(parts[0].split("/"));

        if (parts.length > 1) {
            setTime(parts[1].split(":"));
        } else {
            setTime(new String[] { "00", "00", "00" });
        }
    }

    private void setDate(String[] in) {
        int day = Integer.parseInt(in[0]);
        int month = Integer.parseInt(in[1]);
        int year = Integer.parseInt(in[2]);

        this.localDate = LocalDate.of(year, month, day).atStartOfDay();
    }

    private void setTime(String[] in) {
        int hh = Integer.parseInt(in[0]);
        int mm = Integer.parseInt(in[1]);
        localDate = this.localDate.plusHours(hh).plusMinutes(mm);
    }

    /**
     * Modifies the date by incrementing it (or decrementing, if g is negative) by
     * g days. (Note: The comment about year -1 seems irrelevant as LocalDate always has a year).
     *
     * @param g number of days to add (or subtract if negative)
     */
    public synchronized void update(int g) {
        this.localDate = this.localDate.plusDays(g);
    }

    @Override
    public String toString() {
        return String.format("%d/%d/%d",
                this.localDate.getDayOfMonth(),
                this.localDate.getMonthValue(),
                this.localDate.getYear());
    }

    public void incrementa() {
        this.localDate = localDate.plusSeconds(Controller.SECONDIVIRTUALI_PS);
    }

    public int getDay() {
        return this.localDate.getDayOfMonth();
    }

    public int getMonth() {
        return this.localDate.getMonthValue();
    }

    public static int monthLength(Date d) {
        return switch (d.localDate.getMonthValue()) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 2 -> 28;
            default -> 30;
        };
    }

    public static int monthLength(int numeroMese) {
        return switch (numeroMese) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 2 -> 28;
            default -> 30;
        };
    }

    public DayOfWeek dayOfTheWeek() {
        return localDate.getDayOfWeek();
    }

    public int dayOfTheYear() {
        return localDate.getDayOfYear();
    }

    public boolean holiday() {
        return this.localDate.getDayOfWeek() == DayOfWeek.SUNDAY || this.localDate.getDayOfWeek() == DayOfWeek.SATURDAY;
    }

    public String getTime() {
        return this.localDate.getHour() + ":" + this.localDate.getMinute();
    }

    public int getYear() {
        return this.localDate.getYear();
    }
}
