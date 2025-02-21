package ingsoft.luoghi;

import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;
import ingsoft.util.ViewSE;

public class TipoVisita {
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

    String UID;

    // Costruttore
    public TipoVisita(
            String titolo, String descrizione, GPS puntoIncontro,
            Date dataInizioPeriodo, Date dataFinePeriodo, Ora oraInizio, int durataVisita,
            boolean free, int numMinPartecipants, int numMaxPartecipants)
    {
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
        this.UID = this.hashCode() + ":" + titolo.charAt(0);
    }

    public String getUID(){
        return this.UID;
    }

    //da spezzettare in qualche funzione (?)
    public TipoVisita(String[] args) {
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
        } catch (NumberFormatException e) {
            ViewSE.print(e);
            ViewSE.print("Errore semantico: inserito una stringa al posto di un numero, o qualcosa di simile. VISITA NON CREATA");
        }
    }

    public boolean sovrappone(TipoVisita other){
        if(this.oraInizio.getMinuti() > other.oraInizio.getMinuti()){
            return (other.oraInizio.getMinuti() + other.durataVisita) > this.oraInizio.getMinuti();
        }else{
            return (this.oraInizio.getMinuti() + this.durataVisita) > other.oraInizio.getMinuti();
        }
    }

    public String getTitolo(){
        return this.titolo;
    }

    public String getDescrizione(){
        return this.descrizione;
    }

    public GPS getGps(){
        return this.puntoIncontro;
    }

    public Date getDataInizioPeriodo(){
        return this.dataInizioPeriodo;
    }

    public Date getDataFinePeriodo(){
        return this.dataFinePeriodo;
    }

    public Ora getOraInizio(){
        return this.oraInizio;
    }

    public boolean isFree(){
        return this.free;
    }

    public int getDurataVisita(){
        return this.durataVisita;
    }

    public int getNumMinPartecipants(){
        return this.numMinPartecipants;
    }

    public int getNumMaxPartecipants(){
        return this.numMaxPartecipants;
    }

    

    public boolean isName(String altroTitolo){
        return this.titolo.equalsIgnoreCase(altroTitolo);
    }

    @Override
    public String toString() {
        return "Tipo di visita {" +
                "Titolo='" + titolo + '\'' +
                ", Descrizione='" + descrizione + '\'' +
                ", Punto di Incontro=" + puntoIncontro +
                ", Periodo Inizio=" + dataInizioPeriodo +
                ", Periodo Fine=" + dataFinePeriodo +
                ", Ora Inizio=" + oraInizio +
                ", Durata=" + durataVisita + " minuti" +
                ", Gratuita=" + (free ? "Gratuita" : "Biglietto necessario") +
                ", Numero Min Partecipanti=" + numMinPartecipants +
                ", Numero Max Partecipanti=" + numMaxPartecipants +
                "}\n";
    }
}