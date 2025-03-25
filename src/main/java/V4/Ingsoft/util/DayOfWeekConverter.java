package V4.Ingsoft.util;

import java.time.DayOfWeek;

public class DayOfWeekConverter {

    /**
     * Converte una stringa formata da abbreviazioni a 2 lettere in un array di
     * DayOfWeek.
     * Ad esempio: "MaMeVe" -> [TUESDAY, WEDNESDAY, FRIDAY]
     *
     * @param input la stringa con le abbreviazioni
     * @return un array di DayOfWeek corrispondente
     * @throws IllegalArgumentException se la stringa è nulla, la lunghezza non è
     *                                  multiplo di 2
     *                                  o se viene trovata un'abbreviazione
     *                                  sconosciuta
     */
    public static DayOfWeek[] stringToDays(String input) {
        if (input == null || input.length() % 2 != 0) {
            throw new IllegalArgumentException("La stringa deve essere non nulla e avere una lunghezza multipla di 2");
        }
        int numDays = input.length() / 2;
        DayOfWeek[] days = new DayOfWeek[numDays];

        for (int i = 0; i < numDays; i++) {
            String abbr = input.substring(i * 2, i * 2 + 2);
            switch (abbr) {
                case "Lu":
                    days[i] = DayOfWeek.MONDAY;
                    break;
                case "Ma":
                    days[i] = DayOfWeek.TUESDAY;
                    break;
                case "Me":
                    days[i] = DayOfWeek.WEDNESDAY;
                    break;
                case "Gi":
                    days[i] = DayOfWeek.THURSDAY;
                    break;
                case "Ve":
                    days[i] = DayOfWeek.FRIDAY;
                    break;
                case "Sa":
                    days[i] = DayOfWeek.SATURDAY;
                    break;
                case "Do":
                    days[i] = DayOfWeek.SUNDAY;
                    break;
                default:
                    throw new IllegalArgumentException("Abbreviazione non riconosciuta: " + abbr);
            }
        }
        return days;
    }

    /**
     * Converte un array di DayOfWeek in una stringa concatenando le abbreviazioni a
     * 2 lettere.
     * Ad esempio: [TUESDAY, WEDNESDAY, FRIDAY] -> "MaMeVe"
     *
     * @param days l'array di DayOfWeek
     * @return la stringa con le abbreviazioni corrispondenti
     * @throws IllegalArgumentException se viene trovato un valore DayOfWeek non
     *                                  gestito
     */
    public static String daysToString(DayOfWeek[] days) {
        StringBuilder sb = new StringBuilder();
        for (DayOfWeek day : days) {
            switch (day) {
                case MONDAY:
                    sb.append("Lu");
                    break;
                case TUESDAY:
                    sb.append("Ma");
                    break;
                case WEDNESDAY:
                    sb.append("Me");
                    break;
                case THURSDAY:
                    sb.append("Gi");
                    break;
                case FRIDAY:
                    sb.append("Ve");
                    break;
                case SATURDAY:
                    sb.append("Sa");
                    break;
                case SUNDAY:
                    sb.append("Do");
                    break;
                default:
                    throw new IllegalArgumentException("Day non riconosciuto: " + day);
            }
        }
        return sb.toString();
    }
}
