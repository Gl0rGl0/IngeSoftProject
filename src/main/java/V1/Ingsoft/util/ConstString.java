package V1.Ingsoft.util;

import javax.print.DocFlavor.STRING;

public class ConstString {
    // --- Comandi della modalità RUNNING ---
    public static final String RUN_ADD_INFO = """
            add [-c] [-f] [-v] [String: username] [String: psw]
                -c        Aggiunge un configuratore
                -f        Aggiunge un fruitore
                -v        Aggiunge un volontario
                username  Specifica l'username della persona inserita
                psw       Specifica la password della persona inserita

            add [-t] [String: title] [String: description] [GPS: meetingPlace] [Date: initDay] [Date: finishDay] [Time: initTime] [int: duration] [boolean: free] [int: numMinPartecipants] [int: numMaxPartecipants] [StatusLuoghi: stato]
                -t                  Aggiunge un tipo di visita
                title               Il title del tipo di visita
                description         Una breve description del tipo di visita
                meetingPlace        Il punto di incontro per il tipo di visita [latitude,longitude]
                initDay             Data di inizio del periodo del tipo di visita [YYYY-MM-DD]
                finishDay           Data di fine del periodo del tipo di visita [YYYY-MM-DD]
                initTime            L'orario di inizio del tipo di visita [HH:MM]
                duration            Durata del tipo di visita in minuti
                free                Indica se il tipo di visita è gratuito [true/false]
                numMinPartecipants  Numero minimo di fruitori
                numMaxPartecipants  Numero massimo di fruitori
                stato               Stato del tipo di visita [PROPOSTA/CONFERMATA/CANCELLATA/EFFETTUATA]

            Per lasciare un campo vuoto digitare "/"
            """;

    public static final String RUN_ADD_LINE_INFO = "Aggiunge una Persona/TipoVisita/Luogo al database";

    public static final String REMOVE_INFO = """
            remove [-c] [-f] [-v] [String: username]
                -c        Rimuove un configuratore
                -f        Rimuove un fruitore
                -v        Rimuove un volontario
                username  Specifica l'username della persona da rimuovere

            remove [-V] [-L] [String: title]
                -V        Rimuove una visita
                -L        Rimuove un luogo
                title     Specifica il title della visita o del luogo da rimuovere
            """;

    public static final String REMOVE_LINE_INFO = "Rimuove una Persona/Visita/Luogo dal database";

    public static final String LOGIN_INFO = """
            login [String: username] [String: password]
                username  Specifica l'username con cui fare il login
                password  Specifica la password con cui fare il login
            """;

    public static final String LOGIN_LINE_INFO = "Esegui il login immettendo le credenziali";

    public static final String LOGOUT_INFO = """
            logout
            """;

    public static final String LOGOUT_LINE_INFO = "Esegui il logout dal sistema";

    public static final String CHANGEPSW_INFO = """
            changepsw [String: nuovapsw]
                nuovapsw  Specifica la nuova password per l'account
            """;

    public static final String CHANGEPSW_LINE_INFO = "Cambia la password";

    public static final String TIME_INFO = """
                time [[-d] [-m] [-a]] [int: days]
                    days  Specifica il numero di days da saltare
                    opzionalmente -d: numero di days
                                  -m: numero di mesi
                                  -a: numero di anni
                time -s [Date gg/mm/aa]
                    Imposta la data attuale a gg/mm/aa
                time
                    Mostra la data attuale
            """;

    public static final String TIME_LINE_INFO = "Gestione della data del sistema";

    public static final String ASSIGN_INFO = """
            assign [-L] [String: titoloLuogo] [String: titoloVisita]
                titoloLuogo     Titolo del luogo a cui assegnare il tipo di visita
                titoloVisita    Titilo della visita da assegnare
            assign [-V] [String: titoloVisita] [String: usernameVolontario]
                titoloVisita        Titolo della visita a cui assegnare il volontario
                usernameVolontario  Username del volontario da assegnare
            """;

    public static final String ASSIGN_LINE_INFO = "Assegna una visita ad un luogo o un volontario ad una visita";

    public static final String LIST_INFO = """
                list [-l] [-v]
                    -l(L):  Lista dei luoghi visitabili
                    -v:     Lista dei volontari disponibili
                list [-V] [[-a] [-p] [-c] [-C] [-e]]
                    -V: Lista delle visite disponibili
                        Opzionalmente ([-p] default):   [-a]  All, tutte le visite (anche passate)
                                                        [-p]  Lista delle visite Proposte
                                                        [-c]  Lista delle visite Complete
                                                        [-C]  Lista delle visite Cancellate
                                                        [-e]  Lista delle visite Effettuate (passate)
            """;

    public static final String LIST_LINE_INFO = "Visualizza la lista desiderata";

    public static final String PRECLUDE_INFO = """

            """;

    public static final String PRECLUDE_LINE_INFO = "Aggiunge/rimuove data da precludere";

    public static final String EXIT_INFO = """
            exit
            """;

    public static final String EXIT_LINE_INFO = "Chiude il programma";

    public static final String VISIT_INFO = """
            visit [[-a] [-r]] [String: username_Fruitore] [String: titolo_visita] [Date: data_visita]
                [-a]                Aggiunge un fruitore alla visita selezionata
                [-r]                Rimuove un fruitore dalla visita selezionata
                username_Fruitore   Username del fruitore da aggiungere/rimuovere
                titolo_visita       Titolo della visita su cui operare
                data_visita         Data della visita su cui operare
            """;
    public static final String VISIT_LINE_INFO = "Iscrivi un fruitore ad una visita";

    public static final String MYVISIT_INFO = """

            """;
    public static final String MYVISIT_LINE_INFO = "Visualizza le visite associate all'utente corrente";

    // --- Comandi della modalità SETUP ---
    public static final String SETUP_ADD_INFO = """
            add [-L] [String: name] [String: description] [GPS: position]
                -L            Aggiunge un luogo
                name          Nome del luogo
                description   Breve description del luogo
                position      Posizione GPS [latitude,longitude]
            """;

    public static final String SETUP_ADD_LINE_INFO = "Aggiunge un Luogo al database";

    // LOGIN, LOGOUT, CHANGEPSW e TIME sono identici a quelli della modalità RUNNING

    public static final String SETMAX_INFO = """
            setmax [int: max]
                max     Specifica il numero massimo di fruitori per una visita
            """;

    public static final String SETMAX_LINE_INFO = "(SETUP) Assegna il valore massimo delle visite";

    public static final String SETAMBITO_INFO = """
            setambito [String: nomeAmbito]
                nomeAmbito     Specifica l'ambito territoriale del programma
            """;

    public static final String SETAMBITO_LINE_INFO = "(SETUP) Assegna il nome del territorio";

    public static final String DONE_INFO = """
            done
            """;

    public static final String DONE_LINE_INFO = "Termina il turno di setup";

    public static final String HELP_INFO = """
            help
            """;

    public static final String HELP_LINE_INFO = "Fornisce informazioni sui comandi disponibili.";
}
