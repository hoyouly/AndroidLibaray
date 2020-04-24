package com.dcg.top.utils;

import com.dcg.top.TopApplication;

/**
 * @ Time  :  2020-03-05
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */

public final class DensityUtil {

    private static float density = -1F;
    private static int widthPixels = -1;
    private static int heightPixels = -1;

    private DensityUtil() {
    }

    public static float getDensity() {
        if (density <= 0F) {
            density = TopApplication.getAppContext().getResources().getDisplayMetrics().density;
        }
        return density;
    }

    public static int dip2px(float dpValue) {
        return (int) (dpValue * getDensity() + 0.5F);
    }

    public static int px2dip(float pxValue) {
        return (int) (pxValue / getDensity() + 0.5F);
    }

    public static int getScreenWidth() {
        if (widthPixels <= 0) {
            widthPixels = TopApplication.getAppContext().getResources().getDisplayMetrics().widthPixels;
        }
        return widthPixels;
    }


    public static int getScreenHeight() {
        if (heightPixels <= 0) {
            heightPixels = TopApplication.getAppContext().getResources().getDisplayMetrics().heightPixels;
        }
        return heightPixels;
    }
}

