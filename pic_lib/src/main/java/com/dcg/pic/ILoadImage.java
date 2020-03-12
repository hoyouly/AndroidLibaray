package com.dcg.pic;

import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * @ Time  :  2020-03-10
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public interface ILoadImage {

    /**
     * 设置通用的默认角度和默认加载图片
     *
     * @param defalutRaius
     * @param defalutResId
     */
    void init(int defalutRaius, int defalutResId);

    /**
     *
     * @param t 可以是 View，Context，Activity，Fragment 可以理解为是一个占位符，如果其他框架不需要，可以设置为null
     * @param imageView   加载的ImageView
     * @param k  加载的资源，可以是路径，Bitmap,res资源等
     * @param defalutResId 默认的资源id
     * @param radius 图片圆角角度
     * @param <T>
     * @param <K>
     */
    <T, K> void loadImge(T t, ImageView imageView, K k, @DrawableRes int defalutResId, int radius);

    <T, K> void loadImge(T t, ImageView imageView, K k, @DrawableRes int defalutResId);

    <T, K> void loadImge(T t, ImageView imageView, K k);


}
