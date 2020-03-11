package com.dc.top.utils;

import java.util.regex.Pattern;

/**
 * @ Time :  2019-12-23
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public class NumUtils {


    public static long parseLong(String text) {
        if (text == null || text.isEmpty()) {
            return 0L;
        }
        try {
            return Long.parseLong(text);
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }


    public static int parseInt(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(text);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
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
