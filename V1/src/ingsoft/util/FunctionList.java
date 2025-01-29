package ingsoft.util;

public enum FunctionList {
    ADD("""
        add [-c] [-f] [-v] [String: username] [String: psw]
            -c\t\tAggiunge un configuratore
            -f\t\tAggiunge un fruitore
            -v\t\tAggiunge un volontario
            username\tSpecifica l'username della persona inserita
            psw\t\tSpecifica la password della persona inserita

        add [-V] [String: titolo] [String: descrizione] [GPS: puntoIncontro] [Date: dataInizioPeriodo] [Date: dataFinePeriodo] [Ora: oraInizio] [int: durataVisita] [boolean: free] [int: numMinPartecipants] [int: numMaxPartecipants] [StatusLuoghi: stato]
            -V\t\t\tAggiunge una visita
            titolo\t\t\tIl titolo della visita
            descrizione\t\tUna breve descrizione della visita
            puntoIncontro\t\tIl punto di incontro per la visita [latitudine,longitudine]
            dataInizioPeriodo\tLa data di inizio del periodo della visita [GG:MM:AA]
            dataFinePeriodo\t\tLa data di fine del periodo della visita [GG:MM:AA]
            oraInizio\t\tL'orario di inizio della visita [HH:MM]
            durataVisita\t\tLa durata della visita in minuti
            free\t\t\tIndica se la visita è gratuita [True/False]
            numMinPartecipants\tIl numero minimo di partecipanti
            numMaxPartecipants\tIl numero massimo di partecipanti
            stato\t\t\tLo stato della visita [PROPOSTA/CONFERMATA/CANCELLATA/EFFETTUATA]

        add [-L] [String: nomeLuogo] [String: descrizioneLuogo] [GPS: posizione] [List<Visita>: visite]
            -L\t\t\tAggiunge un luogo
            nomeLuogo\t\tIl nome del luogo
            descrizioneLuogo\tUna breve descrizione del luogo
            posizione\t\tLa posizione GPS del luogo [latitudine,longitudine]
            visite\t\t\tLa lista delle visite programmate per questo luogo [ids (id1,id2...)]
    """),

    REMOVE("""
        remove [-c] [-f] [-v] [String: username]
            -c\t\tRimuove un configuratore
            -f\t\tRimuove un fruitore
            -v\t\tRimuove un volontario
            username\tSpecifica l'username della persona da rimuovere
        
        remove [-V] [-L] [String: titolo]
            -V\t\tRimuove una visita
            -L\t\tRimuove un luogo
            titolo\tSpecifica il titolo della visita o del luogo da rimuovere
        """),

    HELP("""
        ADD aggiunge una Persona/Visita/Luogo al database
        REMOVE rimuove una Persona/Visita/Luogo dal database
        """);

    private final String errorMessage;

    FunctionList(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return errorMessage;
    }
}
