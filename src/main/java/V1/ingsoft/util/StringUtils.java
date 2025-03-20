package V1.ingsoft.util;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static String[] joinQuotedArguments(String[] tokens) {
        List<String> result = new ArrayList<>();

        // Si parte dall'indice 2, saltando (ad esempio) il comando e l'opzione
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            // Se il token inizia con la virgoletta
            if (token.startsWith("\"")) {
                // Se il token inizia e finisce con " (caso in cui la frase è in un unico slot)
                if (token.endsWith("\"") && token.length() > 1) {
                    result.add(token.substring(1, token.length() - 1));
                } else {
                    // Accumulo delle parole fino a trovare la fine della frase racchiusa tra
                    // virgolette
                    StringBuilder sb = new StringBuilder(token.substring(1)); // rimuovo la prima "
                    while (i < tokens.length - 1) {
                        i++;
                        token = tokens[i];
                        if (token.endsWith("\"")) {
                            // Rimuovo l'ultima virgoletta e termino l'accumulo
                            sb.append(" ").append(token.substring(0, token.length() - 1));
                            break;
                        } else {
                            sb.append(" ").append(token);
                        }
                    }
                    result.add(sb.toString());
                }
            } else {
                // Se il token non inizia con ", lo aggiungo così com'è
                result.add(token);
            }
        }

        return result.toArray(String[]::new);
    }

    public static String removeParentheses(String out) {
        return out.replaceAll("[()]", "");
    }

    public static String arrayToStringClean(String[] s) {
        StringBuilder out = new StringBuilder();
        for (String a : s) {
            out.append("\"" + a + "\" ");
        }
        return out.toString().stripTrailing();
    }
}
