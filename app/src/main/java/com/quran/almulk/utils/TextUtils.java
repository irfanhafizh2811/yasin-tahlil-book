package com.quran.almulk.utils;

/**
 * Created by irfanbrader on 8/16/17.
 */

public class TextUtils {

    public static final String BLANK = "";
    public static final String NA = "NA";
    public static final String COMMA = ",";
    public static final String EMPTY_SPACE = " ";
    public static final String K = "K";
    public static final String SEMICOLON = ";";
    public static final String ZERO = "0";
    public static final String DASH = "-";
    public static final String UNDERSCORE = "_";

    public static boolean isEmpty(String value) {
        return value == null || value.length() == 0;
    }

    public static boolean isEmptyOrNullString(String value) {
        return isEmpty(value) || isEmpty(value.trim()) || value.equalsIgnoreCase("null");
    }

}