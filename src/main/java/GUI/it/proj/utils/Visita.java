package GUI.it.proj.utils;

public class Visita {
    protected String titolo;
    protected String descrizione;

    public Visita(String titolo, String descrizione) {
        this.titolo = titolo;
        this.descrizione = descrizione;
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
