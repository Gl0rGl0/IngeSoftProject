package ingsoft.luoghi;

public enum StatusLuoghi {
    PROPOSTA,
    CONFERMATA,
    CANCELLATA,
    EFFETTUATA;

    // Metodo per verificare se lo stato è VISITA_PROPOSTA
    public boolean isPropostaSTATO () {
        return this == PROPOSTA;
    }
}