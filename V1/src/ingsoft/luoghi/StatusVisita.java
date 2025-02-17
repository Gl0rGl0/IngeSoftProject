package ingsoft.luoghi;

public enum StatusVisita {
    PROPOSTA,
    CONFERMATA,
    COMPLETA,
    CANCELLATA,
    EFFETTUATA;

    // Metodo per verificare se lo stato è PROPOSTA
    public boolean isPropostaSTATO () {
        return this == PROPOSTA;
    }
}