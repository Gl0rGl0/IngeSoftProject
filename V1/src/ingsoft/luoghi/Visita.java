package ingsoft.luoghi;

import ingsoft.util.Date;

public class Visita {
    public TipoVisita tipo;
    public Date data;

    public Visita(TipoVisita tipo, Date data) {
        this.tipo = tipo;
        this.data = data;
    }
}
