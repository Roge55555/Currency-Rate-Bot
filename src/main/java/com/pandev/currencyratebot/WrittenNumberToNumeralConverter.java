package com.pandev.currencyratebot;

public class WrittenNumberToNumeralConverter {

    private static final String[] UNITS = {
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
            "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
    };

    private static final String[] TENS = {
            "", "ten", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
    };

    public int parse(String word) throws NumberFormatException {
        for (int i = 0; i < UNITS.length; i++) {
            if (word.equalsIgnoreCase(UNITS[i])) {
                return i;
            }
        }

        for (int i = 0; i < TENS.length; i++) {
            if (word.equalsIgnoreCase(TENS[i])) {
                return i * 10;
            }
        }

        throw new NumberFormatException("Not a valid spelled-out number: " + word);
    }
}