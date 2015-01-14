package com.vanzstuff.readdit;

public class Utils {

    /**
     * Check if String s is not null or empty
     * @param s
     * @return true if string s is not null or empty. Otherwise, false
     */
    public static final boolean stringNotNullOrEmpty(String s) {
        return !(s == null || s.isEmpty());
    }

    /**
     * Check if the String s is alphanumeric.
     * @param s
     * @return true if the string s is alphanumeric. Otherwise, false
     */
    public static boolean isAlphaNumeric(String s) {
        return s.matches("\\w+");
    }
}