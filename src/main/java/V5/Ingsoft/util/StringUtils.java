package V5.Ingsoft.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static String[] joinQuotedArguments(String[] tokens) {
        if (tokens == null)
            return new ArrayList<String>().toArray(String[]::new);

        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("\"")) {
                // Process the quoted sequence and update the index accordingly
                QuotedToken joinedToken = processQuotedToken(tokens, i);
                if (joinedToken != null) {
                    result.add(joinedToken.value);
                    i = joinedToken.endIndex;
                }
            } else {
                result.add(token);
            }
        }
        return result.toArray(String[]::new);
    }

    /**
     * Processes a sequence of tokens starting with a quote.
     * Returns a QuotedToken containing the joined value (with outer quotes removed)
     * and the index of the last token in the sequence.
     */
    private static QuotedToken processQuotedToken(String[] tokens, int startIndex) {
        // Verifica degli input
        if (tokens == null || startIndex < 0 || startIndex >= tokens.length) {
            return null; // o lanciare un'eccezione
        }

        String token = tokens[startIndex];

        // Verifica che il token inizi con una virgoletta
        if (!token.startsWith("\"")) {
            // Se non inizia con una virgoletta, possiamo decidere di restituire null o gestire diversamente
            return null; // oppure lanciare un'eccezione
        }

        // Caso in cui il token sia già completo (inizia e finisce con la virgoletta)
        if (token.endsWith("\"") && token.length() > 1) {
            // Rimuove la prima e l'ultima virgoletta
            return new QuotedToken(token.substring(1, token.length() - 1), startIndex);
        }

        // Altrimenti, accumula token fino a trovare uno che termini con la virgoletta
        StringBuilder sb = new StringBuilder(token.substring(1)); // rimuove solo la prima virgoletta
        int currentIndex = startIndex;

        while (currentIndex < tokens.length - 1) {
            currentIndex++;
            token = tokens[currentIndex];
            if (token.endsWith("\"")) {
                // Rimuove solo l'ultima virgoletta
                sb.append(" ").append(token, 0, token.length() - 1);
                break;
            } else {
                sb.append(" ").append(token);
            }
        }

        return new QuotedToken(sb.toString(), currentIndex);
    }

    public static String arrayToStringClean(String[] s) {
        if (s == null)
            return "";

        StringBuilder out = new StringBuilder();
        for (String a : s) {
            out.append("\"").append(a).append("\" ");
        }
        return out.toString().stripTrailing();
    }

    /**
     * Helper class to return both the joined token and the index where the quoted
     * sequence ended.
     */
    private static class QuotedToken {
        String value;
        int endIndex;

        QuotedToken(String value, int endIndex) {
            this.value = value;
            this.endIndex = endIndex;
        }
    }
}
