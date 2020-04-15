package com.dcg.top.utils;

import java.util.regex.Pattern;

/**
 * @ Time :  2019-12-23
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public class NumUtils {


    private static Pattern NUMBER_PATTERN = Pattern.compile("^[-\\+]?[\\d]*$");

    public static boolean isNumber(String str) {
        if (str == null) {
            return false;
        }
        return NUMBER_PATTERN.matcher(str).matches();
    }


    public static long parseLong(String text) {
        return parseLong(text, 0L);
    }

    public static long parseLong(String text, long defalutValue) {
        if (isNumber(text)) {
            return Long.parseLong(text);
        } else {
            return defalutValue;
        }
    }


    public static int parseInt(String text) {
        return parseInt(text, 0);
    }


    public static int parseInt(String text, int defaultValue) {
        if (isNumber(text)) {
            return Integer.parseInt(text);
        } else {
            return defaultValue;
        }
    }

    /**
     * 单号是否匹配
     *
     * @param text
     * @return
     */
    public static boolean isOrderNoMatch(String text) {

        return isMatch("^(IN|OU|WV)[A-Z\\d]{10,}$", text);

    }

    /**
     * 货位号是否匹配
     *
     * @param text
     * @return
     */
    public static boolean isShelfNoMatch(String text) {
        return isMatch("^\\d{2}[A-Z]\\d{2}[A-E]$", text)
            || isMatch("(^\\d{4})$", text);
    }

    /**
     * RFID 是否匹配
     *
     * @param text
     * @return
     */
    public static boolean isRFIDMathc(String text) {
        return isMatch("^1916[0-1]4\\d{6}$", text);
    }


    private static boolean isMatch(String pattern, String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        return Pattern.matches(pattern, text);
    }

}
