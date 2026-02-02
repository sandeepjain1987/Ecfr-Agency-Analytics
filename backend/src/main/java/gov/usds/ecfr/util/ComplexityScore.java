package gov.usds.ecfr.util;

import java.util.Locale;

public class ComplexityScore {
    public int complexityScore(String text) {
        if (text == null || text.isBlank()) return 0;

        String normalized = text.toLowerCase(Locale.ROOT);

        int sentences = normalized.split("[.!?]").length;
        int must = countOccurrences(normalized, " must ");
        int shall = countOccurrences(normalized, " shall ");
        int required = countOccurrences(normalized, " required ");

        return sentences + must + shall + required;
    }

    private int countOccurrences(String text, String token) {
        int count = 0;
        int idx = 0;
        while ((idx = text.indexOf(token, idx)) != -1) {
            count++;
            idx += token.length();
        }
        return count;
    }
}
