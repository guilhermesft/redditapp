package com.vanzstuff.readdit;

public class Utils {

    private static final String URL_IMAGE = "^.+\\.[jpg|jpeg|png|gif]$";

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

    /**
     * Check if a given url is to an image ( url ending with images extensions like jpg and png )
     * @param url the url to check
     * @return true if is a url to an image. Otherwise, false
     */
    public static boolean isImageUrl(String url) {
        return url.matches(URL_IMAGE);
    }
}