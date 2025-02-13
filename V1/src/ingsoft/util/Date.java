package ingsoft.util;

import java.time.LocalDate;

public class Date {
    private LocalDate localDate;
    private String comment;

    /**
     * Costruisce una data completa (giorno, mese e anno).
     * 
     * @param gg  giorno
     * @param mm  mese
     * @param aa  anno
     */
    public Date(int gg, int mm, int aa) {
        this.localDate = LocalDate.of(aa, mm, gg);
    }

    /**
     * Costruisce una data senza anno (utilizzando -1 come valore sentinella) e
     * associa un commento.
     * 
     * @param gg      giorno
     * @param mm      mese
     * @param comment commento associato alla data
     */
    public Date(int gg, int mm, String comment) {
        // Utilizziamo -1 come anno sentinella per indicare che non è specificato
        this.localDate = LocalDate.of(-1, mm, gg);
        this.comment = comment;
    }

    /**
     * Costruisce una data partendo da una stringa nel formato "gg/mm[/aa][=comment]".
     * Se l'anno non è presente, viene impostato a -1.
     * 
     * @param d la stringa contenente la data e opzionalmente un commento
     */
    public Date(String d) {
        String[] tmp = d.split("=");
        String datePart = tmp[0];
        String[] parts = datePart.split("/");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = -1; // Anno non specificato
        if (parts.length == 3) {
            year = Integer.parseInt(parts[2]);
        }
        this.localDate = LocalDate.of(year, month, day);
        if (tmp.length == 2) {
            this.comment = tmp[1];
        }
    }

    /**
     * Restituisce una nuova istanza di Date con lo stesso giorno e mese, ma senza anno.
     * 
     * @return una nuova Date con anno -1
     */
    public Date getNotAnno() {
        return new Date(this.localDate.getDayOfMonth(), this.localDate.getMonthValue(), -1);
    }

    /**
     * Modifica la data incrementandola (o decrementandola, se g è negativo) di g giorni.
     * Se l'anno non è noto (ossia -1), l'operazione non viene eseguita.
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
        if (this.localDate.getYear() != -1)
            return String.format("%d/%d/%d",
                    this.localDate.getDayOfMonth(),
                    this.localDate.getMonthValue(),
                    this.localDate.getYear());
        if (this.comment != null)
            return String.format("%d/%d: %s",
                    this.localDate.getDayOfMonth(),
                    this.localDate.getMonthValue(),
                    this.comment);
        return String.format("%d/%d",
                this.localDate.getDayOfMonth(),
                this.localDate.getMonthValue());
    }

    /**
     * Restituisce il commento associato alla data, se presente.
     * 
     * @return il commento o null se non presente
     */
    public String getDataMessage() {
        return this.comment;
    }
}