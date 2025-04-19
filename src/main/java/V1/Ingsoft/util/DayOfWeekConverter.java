package V1.Ingsoft.util;

import java.time.DayOfWeek;

public class DayOfWeekConverter {

    /**
     * Converts a string formed by 2-letter abbreviations into an array of DayOfWeek.
     * Example: "MaMeVe" -> [TUESDAY, WEDNESDAY, FRIDAY] (Using Italian abbreviations)
     * Note: The abbreviations used are Italian (Lu, Ma, Me, Gi, Ve, Sa, Do).
     *
     * @param input the string with the abbreviations
     * @return a corresponding array of DayOfWeek
     * @throws IllegalArgumentException if the string is null, the length is not a
     *                                  multiple of 2, or if an unknown
     *                                  abbreviation is found
     */
    public static DayOfWeek[] stringToDays(String input) {
        if (input == null || input.length() % 2 != 0) {
            throw new IllegalArgumentException("String must be non-null and have a length that is a multiple of 2");
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
                    throw new IllegalArgumentException("Unrecognized abbreviation: " + abbr);
            }
        }
        return days;
    }

    /**
     * Converts an array of DayOfWeek into a string by concatenating the 2-letter
     * abbreviations.
     * Example: [TUESDAY, WEDNESDAY, FRIDAY] -> "MaMeVe" (Using Italian abbreviations)
     * Note: The abbreviations generated are Italian (Lu, Ma, Me, Gi, Ve, Sa, Do).
     *
     * @param days the array of DayOfWeek
     * @return the string with the corresponding abbreviations
     * @throws IllegalArgumentException if an unhandled DayOfWeek value is found
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
                    // This case should theoretically not be reachable with standard DayOfWeek enum
                    throw new IllegalArgumentException("Unrecognized DayOfWeek: " + day);
            }
        }
        return sb.toString();
    }
}
