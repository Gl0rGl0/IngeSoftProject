package V4.Ingsoft.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static String[] joinQuotedArguments(String[] tokens) {
        if(tokens == null)
            return new ArrayList<>().toArray(String[]::new);

        List<String> result = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("\"")) {
                // Process the quoted sequence and update the index accordingly
                QuotedToken joinedToken = processQuotedToken(tokens, i);
                if(joinedToken != null) {
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

    /**
     * Processes a sequence of tokens that start with a quote and returns a
     * QuotedToken
     * with the joined value (with outer quotes removed) and the last token index of
     * the sequence.
     */
    private static QuotedToken processQuotedToken(String[] tokens, int startIndex) {
        if(tokens == null || startIndex < 0)
            return null;

        String token = tokens[startIndex];
        // If the token both starts and ends with a quote and is longer than 1 char,
        // it's a complete quoted argument.
        if (token.endsWith("\"") && token.length() > 1) {
            return new QuotedToken(token.substring(1, token.length() - 1), startIndex);
        }

        // Otherwise, accumulate tokens until we find a token that ends with a quote.
        StringBuilder sb = new StringBuilder(token.substring(1)); // Remove starting quote
        int currentIndex = startIndex;
        while (currentIndex < tokens.length - 1) {
            currentIndex++;
            token = tokens[currentIndex];
            if (token.endsWith("\"")) {
                sb.append(" ").append(token.substring(0, token.length() - 1)); // Remove ending quote
                break;
            } else {
                sb.append(" ").append(token);
            }
        }
        return new QuotedToken(sb.toString(), currentIndex);
    }

    public static String removeParentheses(String out) {
        if(out == null)
            return "";

        return out.replaceAll("[()]", "");
    }

    public static String arrayToStringClean(String[] s) {
        if(s == null)
            return "";

        StringBuilder out = new StringBuilder();
        for (String a : s) {
            out.append("\"" + a + "\" ");
        }
        return out.toString().stripTrailing();
    }
}
