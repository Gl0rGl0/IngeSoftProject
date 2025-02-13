package ingsoft.luoghi;

public enum StatusVisita {
    PROPOSTA,
    CONFERMATA,
    CANCELLATA,
    EFFETTUATA;

    // Metodo per verificare se lo stato è PROPOSTA
    public boolean isPropostaSTATO () {
        return this == PROPOSTA;
    }
}