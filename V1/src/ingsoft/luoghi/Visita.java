package ingsoft.luoghi;

import ingsoft.util.Date;
import ingsoft.util.GPS;
import ingsoft.util.Ora;

public class Visita {
    private final String idVisita;

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

    StatusVisita stato = StatusVisita.PROPOSTA;

    // Costruttore
    public Visita(
            String idVisita, String titolo, String descrizione, GPS puntoIncontro,
            Date dataInizioPeriodo, Date dataFinePeriodo, Ora oraInizio, int durataVisita,
            boolean free, int numMinPartecipants, int numMaxPartecipants)
    {
        this.idVisita = idVisita;
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
    }   

    public boolean sovrappone(Visita other){
        if(this.oraInizio.getMinuti() > other.oraInizio.getMinuti()){
            return (other.oraInizio.getMinuti() + other.durataVisita) > this.oraInizio.getMinuti();
        }else{
            return (this.oraInizio.getMinuti() + this.durataVisita) > other.oraInizio.getMinuti();
        }
    }

    public boolean isProposta(){
        return this.stato.isPropostaSTATO();
    }

    public String getIDVisita(){
        return this.idVisita;
    }

    public boolean isName(String altroTitolo){
        return this.titolo.equalsIgnoreCase(altroTitolo);
    }

    @Override
    public String toString() {
        return "Visita {" +
                "Titolo='" + titolo + '\'' +
                ", Descrizione='" + descrizione + '\'' +
                ", Punto di Incontro=" + puntoIncontro +
                ", Periodo Inizio=" + dataInizioPeriodo +
                ", Periodo Fine=" + dataFinePeriodo +
                ", Ora Inizio=" + oraInizio +
                ", Durata=" + durataVisita + " minuti" +
                ", Gratuita=" + (free ? "Sì" : "No") +
                ", Numero Min Partecipanti=" + numMinPartecipants +
                ", Numero Max Partecipanti=" + numMaxPartecipants +
                ", Stato=" + stato +
                '}';
    }
}