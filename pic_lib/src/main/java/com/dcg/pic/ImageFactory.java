package com.dcg.pic;

/**
 * @ Time  :  2020-03-11
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class ImageFactory {

    public static ILoadImage getClient() {
        return GlideClient.getInstance();
    }

}
