package V1.ingsoft.commands.setup;

import V1.ingsoft.commands.ListInterface;
import V1.ingsoft.persone.PersonaType;

public enum CommandListSETUP implements ListInterface {
    ADD("""
                add [-L] [String: name] [String: description] [GPS: position] [List<Integer>: visite]
                    -L                 Aggiunge un luogo
                    name          Nome del luogo
                    description   Breve description del luogo
                    position          Posizione GPS [latitude,longitude]
                    visite             Lista degli ID delle visite associate [id1,id2,...]
            """,
            "Aggiunge un Luogo al database", PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()), // Solo i configuratori (4)

    LOGIN("""
                login [String: userName] [String: password]
                    userName  Specifica l'userName con cui fare il login
                    password  Specifica la password con cui fare il login
            """,
            "Esegui il login immettendo le credenziali", PersonaType.GUEST.getPriority(),
            PersonaType.GUEST.getPriority()), // SOLO GUEST (0,0)

    LOGOUT("""
                logout
            """,
            "Esegui il logout dal sistema", PersonaType.CAMBIOPSW.getPriority(), PersonaType.MAX.getPriority()), // Devi
                                                                                                                 // essere
                                                                                                                 // almeno
                                                                                                                 // nel
                                                                                                                 // sistema
                                                                                                                 // (1,100)

    CHANGEPSW("""
            changepsw [String: nuovapsw]
                nuovapsw  Specifica la nuova password per l'account
                """, "Cambia la password", PersonaType.CAMBIOPSW.getPriority(), PersonaType.MAX.getPriority()), // Devi
                                                                                                                // essere
                                                                                                                // almeno
                                                                                                                // loggato
                                                                                                                // ma
                                                                                                                // devi
                                                                                                                // cambiare
                                                                                                                // psw
                                                                                                                // perche
                                                                                                                // GUEST
                                                                                                                // non
                                                                                                                // puo...
                                                                                                                // (1,100)

    TIME("""
                time [[-d] [-m] [-a]] [int: days]
                    days  Specifica il numero di days da saltare
                    opzionalmente -d: numero di days
                                  -m: numero di mesi
                                  -a: numero di anni
                time -s [Date gg/mm/aa]
                    Imposta la data attuale a gg/mm/aa
                time
                    Mostra la data attuale
            """,
            "Gestione della data del sistema", PersonaType.GUEST.getPriority(), PersonaType.MAX.getPriority()), // TUTTI
                                                                                                                // ma
                                                                                                                // solo
                                                                                                                // in
                                                                                                                // demo
                                                                                                                // (tempo
                                                                                                                // virtuale)...
                                                                                                                // (0,100)

    SETMAX("""
                setmax [int: max]
                    max     Specifica il numero massimo di fruitori per una visita
            """, "(SETUP) Assegna il valore massimo delle visite", PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    SETAMBITO("""
                setambito [String: max]
                    max     Specifica l'ambito territoriale del programma
            """, "(SETUP) Assegna il name del territorio", PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    ASSIGN("""
                assign ...
            """, "Assegna una visita ad un luogo o un volontario ad una visita",
            PersonaType.CONFIGURATORE.getPriority(), PersonaType.CONFIGURATORE.getPriority()),

    DONE("""

            """, "Termina il turno di setup", PersonaType.CONFIGURATORE.getPriority(),
            PersonaType.CONFIGURATORE.getPriority()),

    EXIT("""
                exit
            """,
            "Chiude il programma", PersonaType.GUEST.getPriority(), PersonaType.MAX.getPriority()), // TUTTI (0,100)

    HELP("Questa lista", "Fornisce informazioni sui comandi disponibili.", PersonaType.GUEST.getPriority(),
            PersonaType.MAX.getPriority()); // TUTTI (0,100)

    @Override
    public String getHelpMessage(int userPriority) {
        StringBuilder out = new StringBuilder();
        for (CommandListSETUP element : CommandListSETUP.values()) {
            if (element != HELP && canBeExecutedBy(userPriority)) {
                out.append(element.name()).append(" ").append(element.lineInfo).append("\n");
            }
        }
        return out.toString().substring(0, out.length() - 2); // toglie l'ultimo "\n" cosi da non avere troppi spazi ma
                                                              // non appesantendo la logica del for
    }

    private final String message;
    private final String lineInfo;
    private final int minRequiredPermission; // livello minimo richiesto
    private final int maxRequiredPermission; // livello massimo richiesto

    CommandListSETUP(String message, String lineInfo, int minRequiredPermission, int maxRequiredPermission) {
        this.message = message;
        this.lineInfo = lineInfo;
        this.minRequiredPermission = minRequiredPermission;
        this.maxRequiredPermission = maxRequiredPermission;
    }

    // CommandListSETUP(CommandList l){
    // this.message =
    // }

    @Override
    public String toString() {
        return this == HELP ? getHelpMessage(minRequiredPermission) : (this.lineInfo + "\n" + this.message);
    }

    @Override
    public String getInfo() {
        return this.lineInfo;
    }

    @Override
    public boolean canBeExecutedBy(int userPriority) {
        return minRequiredPermission <= userPriority && userPriority <= maxRequiredPermission;
    }
}