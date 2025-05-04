package GUI.it.proj.utils;

public class Luogo {
    protected String titolo;
    protected String descrizione;
    protected String posizione;

    public Luogo(String titolo, String descrizione, String posizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.posizione = posizione;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public String toString() {
        return titolo;
    }

    public void setTitle(String username) {
        this.titolo = username;
    }
}
