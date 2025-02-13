package ingsoft.util;

import java.time.LocalDate;

public class Date {
    private int gg;
    private int mm;
    private int aa;
    private String comment;

    public Date(int gg, int mm, int aa){
        this.gg = gg;
        this.mm = mm;
        this.aa = aa;
    }

    public Date(int gg, int mm, String comment){
        this.gg = gg;
        this.mm = mm;
        this.aa = -1;
        this.comment = comment;
    }

    public Date(String d){
        String[] tmp = d.split("=");

        String[] sub = tmp[0].split("/");
        this.gg = Integer.parseInt(sub[0]);
        this.mm = Integer.parseInt(sub[1]);
        if(sub.length == 3){
            this.aa = Integer.parseInt(sub[2]);
        }else{
            this.aa = -1;
        }
 
        if(tmp.length == 2){
            this.comment = tmp[1];
        } 
    }

    public Date getNotAnno(){
        return new Date(gg, mm, -1);
    }

    /**
     * Modifica la data incrementandola (o decrementandola, se g è negativo) di g giorni.
     */
    public void modifica(int g) {
        if (aa == -1) {
            // Se l'anno non è noto, non possiamo gestire correttamente l'incremento.
            ViewSE.print("Modifica non supportata per date senza anno.");
            return;
        }
        // Crea un LocalDate dalla data corrente
        LocalDate localDate = LocalDate.of(aa, mm, gg);
        // Aggiunge (o sottrae) g giorni
        localDate = localDate.plusDays(g);
        // Aggiorna i campi della data
        this.gg = localDate.getDayOfMonth();
        this.mm = localDate.getMonthValue();
        this.aa = localDate.getYear();
    }

    @Override
    public String toString(){
        if(aa != -1)
            return String.format("%d/%d/%d", gg, mm, aa);
        if(comment != null)
            return String.format("%d/%d: %s", gg, mm, comment);
        return String.format("%d/%d", gg, mm);
    }

    public String getDataMessage(){
        return comment;
    }
}