package ingsoft.util;

public enum StatusLuoghi {
    VISITA_PROPOSTA,
    VISITA_CONFERMATA,
    VISITA_CANCELLATA,
    VISITA_EFFETTUATA;

    // Metodo per verificare se lo stato è VISITA_PROPOSTA
    public boolean isPropostaSTATO () {
        return this == VISITA_PROPOSTA;
    }
}