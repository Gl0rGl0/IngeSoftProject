package ingsoft.util;

import ingsoft.App;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Date {

    public LocalDateTime localDate;

    /**
     * Costruisce una data completa (giorno, mese e anno).
     *
     * @param gg giorno
     * @param mm mese
     * @param aa anno
     */
    public Date(int gg, int mm, int aa) {
        this.localDate = LocalDate.of(aa, mm, gg).atStartOfDay();
    }

    public Date() {
        this.localDate = LocalDateTime.now();
    }

    /**
     * Costruisce una data partendo da una stringa nel formato "gg/mm[/aa]". Se
     * l'anno non è presente, viene impostato a -1.
     *
     * @param d la stringa contenente la data e opzionalmente un commento
     */
    public Date(String in) {
        String[] parts = in.split("-");

        setData(parts[0].split("/"));

        if (parts.length > 1) {
            setOra(parts[1].split(":"));
        }
    }

    private void setData(String[] in) {
        int day = Integer.parseInt(in[0]);
        int month = Integer.parseInt(in[1]);
        int year = -1; // Anno non specificato

        if (in.length == 3) {
            year = Integer.parseInt(in[2]);
        }
        this.localDate = LocalDate.of(year, month, day).atStartOfDay();
    }

    private void setOra(String[] in) {
        int hh = Integer.parseInt(in[0]);
        int mm = Integer.parseInt(in[1]);
        this.localDate.plusHours(hh).plusMinutes(mm);
    }

    /**
     * Modifica la data incrementandola (o decrementandola, se g è negativo) di
     * g giorni. Se l'anno non è noto (ossia -1), l'operazione non viene
     * eseguita.
     *
     * @param g numero di giorni da aggiungere (o sottrarre se negativo)
     */
    public synchronized void modifica(int g) {
        if (this.localDate.getYear() == -1) {
            // Se l'anno non è noto, non possiamo gestire correttamente l'incremento.
            ViewSE.print("Modifica non supportata per date senza anno.");
            return;
        }
        this.localDate = this.localDate.plusDays(g);
    }

    @Override
    public String toString() {
        if (this.localDate.getYear() != -1) {
            return String.format("%d/%d/%d-%02d:%02d",
                    this.localDate.getDayOfMonth(),
                    this.localDate.getMonthValue(),
                    this.localDate.getYear(),
                    this.localDate.getHour(),
                    this.localDate.getMinute());
        }
        return String.format("%d/%d",
                this.localDate.getDayOfMonth(),
                this.localDate.getMonthValue());
    }

    public void incrementa() {
        this.localDate = localDate.plusSeconds(App.SECONDIVIRTUALI_PS);
    }

    public int getGiorno() {
        return this.localDate.getDayOfMonth();
    }

    public int getMese() {
        return this.localDate.getMonthValue();
    }

    public static int lunghezzaMese(Date d) {
        return switch (d.localDate.getMonthValue()) {
            case 1 ->
                31;
            case 2 ->
                28;
            case 3 ->
                31;
            case 4 ->
                30;
            case 5 ->
                31;
            case 6 ->
                30;
            case 7 ->
                31;
            case 9 ->
                30;
            case 10 ->
                31;
            case 11 ->
                30;
            case 12 ->
                31;
            default ->
                31; //boh non si sa mai
        };
    }

    public DayOfWeek cheGiornoE() {
        return localDate.getDayOfWeek();
    }

    public int giornoDellAnno() {
        return localDate.getDayOfYear();
    }

    public boolean festivo() {
        return this.localDate.getDayOfWeek() == DayOfWeek.SUNDAY || this.localDate.getDayOfWeek() == DayOfWeek.SATURDAY;
    }
}
