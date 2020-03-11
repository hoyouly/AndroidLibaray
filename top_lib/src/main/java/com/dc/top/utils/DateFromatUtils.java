package com.dc.top.utils;

import java.text.SimpleDateFormat;

/**
 * @ Time :  2019-12-18
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public class DateFromatUtils {

    public static String dataFormat(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String strDate = format.format(time);
        return strDate;
    }

    public static String dataFormatRemovalInterval(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String strDate = format.format(time);
        return strDate;
    }

    public static String dataFormatWithHMS(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取日期时间
        String strDate = format.format(time);
        return strDate;
    }

}
