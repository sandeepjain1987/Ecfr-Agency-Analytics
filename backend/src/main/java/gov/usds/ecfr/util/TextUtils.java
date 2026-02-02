package gov.usds.ecfr.util;

public class TextUtils {

    public static long countWords(String text) {
        if (text == null || text.isEmpty()) {
            return 0L;
        }
        // Simple split-based word count is fine for this assignment
        return text.trim().split("\\s+").length;
    }
}