package org.rocs.osdrmsa.utils.ai;

final class LightStemmer {

    private LightStemmer() {
    }

    static String stem(String word) {
        if (word.length() <= 3) {
            return word;
        }

        if (word.endsWith("ies") && word.length() > 4) {
            return word.substring(0, word.length() - 3) + "y";
        }
        if (word.endsWith("edly") && word.length() > 6) {
            return word.substring(0, word.length() - 4);
        }
        if (word.endsWith("ing") && word.length() > 5) {
            return word.substring(0, word.length() - 3);
        }
        if (word.endsWith("ment") && word.length() > 6) {
            return word.substring(0, word.length() - 4);
        }
        if (word.endsWith("ness") && word.length() > 6) {
            return word.substring(0, word.length() - 4);
        }
        if (word.endsWith("tion") && word.length() > 6) {
            return word.substring(0, word.length() - 4);
        }
        if (word.endsWith("ed") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("ly") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("es") && word.length() > 4) {
            return word.substring(0, word.length() - 2);
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 3) {
            return word.substring(0, word.length() - 1);
        }

        return word;
    }
}
