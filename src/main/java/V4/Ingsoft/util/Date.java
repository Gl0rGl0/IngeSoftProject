package V4.Ingsoft.util;

import V4.Ingsoft.controller.Controller;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.*;

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
     * Constructs a date from a string in the format "dd/mm/yy-[hh:mm]".
     *
     * @param in the string containing the date and optionally a time part
     */
    public Date(String in) throws Exception {
        String[] parts = in.split("-");

        setDate(parts[0].split("/"));

        if (parts.length > 1) {
            setTime(parts[1].split(":"));
        } else {
            setTime(new String[]{"00", "00", "00"});
        }
    }

    public static boolean monthsDifference(Date d1, Date d2, int n) {
        Month currentMonth;
        Month targetMonth;

        if (d1.dayOfTheYear() < d2.dayOfTheYear()) {
            currentMonth = d1.getMonth();
            targetMonth = d2.getMonth();
        } else {
            targetMonth = d1.getMonth();
            currentMonth = d2.getMonth();
        }

        if (d1.getDay() < 16) {
            return (currentMonth.plus(n).equals(targetMonth));
        } else {
            return (currentMonth.plus(n + 1).equals(targetMonth));
        }
    }

    public static boolean between(Date initDay, Date date, Date finishDay) {
        return initDay.dayOfTheYear() <= date.dayOfTheYear() && date.dayOfTheYear() <= finishDay.dayOfTheYear();
    }

    private void setDate(String[] in) throws NumberFormatException, DateTimeException {
        int day = Integer.parseInt(in[0]);
        int month = Integer.parseInt(in[1]);
        int year = Integer.parseInt(in[2]);

        this.localDate = LocalDate.of(year, month, day).atStartOfDay();
    }

    /**
     * Modifies the date by incrementing it (or decrementing, if g is negative) by
     * g days.
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

    public void setDay(int i) {
        this.localDate = LocalDate.of(localDate.getYear(), localDate.getMonthValue(), i).atStartOfDay();
    }

    public Month getMonth() {
        return this.localDate.getMonth();
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

    private void setTime(String[] in) throws DateTimeException {
        int hh = Integer.parseInt(in[0]);
        int mm = Integer.parseInt(in[1]);
        localDate = this.localDate.plusHours(hh).plusMinutes(mm);
    }

    public int getYear() {
        return this.localDate.getYear();
    }

    /**
     * Checks if this date is strictly before another date (ignores time part).
     *
     * @param other The date to compare against.
     * @return true if this date is before the other date, false otherwise or if other is null.
     */
    public boolean isBefore(Date other) {
        if (other == null || other.localDate == null) {
            return false; // Cannot compare with null
        }
        // Compare only the date part
        return this.localDate.toLocalDate().isBefore(other.localDate.toLocalDate());
    }

    /**
     * Returns a new Date object representing this date minus the specified number of days.
     *
     * @param days The number of days to subtract.
     * @return A new Date object, or null if the operation fails.
     */
    public Date minusDays(int days) {
        try {
            LocalDateTime newLocalDate = this.localDate.minusDays(days);
            return new Date(newLocalDate);
        } catch (Exception e) {
            // Log error if needed
            return null;
        }
    }

    @Override
    public boolean equals(Object d) {
        if (this == d) return true; // Same object
        if (d == null || getClass() != d.getClass()) return false; // Null or different class

        Date otherDate = (Date) d;

        if (this.localDate == null) {
            return otherDate.localDate == null;
        }
        if (otherDate.localDate == null) {
            return false;
        }
        // Compare only the date part for equality, ignoring time
        return this.localDate.toLocalDate().equals(otherDate.localDate.toLocalDate());
    }

    @Override
    public int hashCode() {
        // Simple hash based on LocalDate part
        return localDate != null ? localDate.toLocalDate().hashCode() : 0;
    }

    /**
     * Creates a clone of this Date object.
     * Since LocalDateTime is immutable, this is effectively a deep copy.
     *
     * @return a new Date instance with the same localDate value.
     */
    @Override
    public Date clone() {
        return new Date(this.localDate);
    }

    public Date addMonth(int n) {
        localDate.plusMonths(n);
        return this;
    }

    public Date addDay(int n) {
        localDate.plusDays(n);
        return this;
    }
}
