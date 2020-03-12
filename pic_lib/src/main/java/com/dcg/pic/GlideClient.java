package com.dcg.pic;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.dcg.util.R;

/**
 * @ Time  :  2020-03-10
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class GlideClient implements ILoadImage {

    private static GlideClient INSTANCE;
    private int defalutRaius = 20;
    private int defalutResId = R.drawable.ic_launcher_round;

    public static GlideClient getInstance() {
        if (INSTANCE == null) {
            synchronized (GlideClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GlideClient();
                }
            }
        }
        return INSTANCE;
    }

    private GlideClient() {
    }


    @Override
    public void init(int defalutRaius, int defalutResId) {
        this.defalutRaius = defalutRaius;
        this.defalutResId = defalutResId;
    }

    @Override
    public <T, K> void loadImge(T t, ImageView imageView, K k, int defalutResId, int radius) {
        if (isDestoryed(t)) {
            return;
        }
        if (k instanceof Integer) {
            int resId = (Integer) k;
            if (resId == 0) {
                imageView.setImageResource(defalutResId);
                return;
            }
            loadImage(t, imageView, resId, defalutResId, radius);
        } else if (k instanceof String) {
            String path = (String) k;
            if (TextUtils.isEmpty(path)) {
                imageView.setImageResource(defalutResId);
                return;
            }
            loadImage(t, imageView, path, defalutResId, radius);
        } else if (k instanceof Bitmap) {
            Bitmap bitmap = (Bitmap) k;
            loadImge(t, imageView, bitmap, defalutResId, radius);
        }
    }

    @Override
    public <T, K> void loadImge(T t, ImageView imageView, K k, int defalutResId) {
        loadImge(t, imageView, k, defalutResId, defalutRaius);
    }

    @Override
    public <T, K> void loadImge(T t, ImageView imageView, K k) {
        loadImge(t, imageView, k, defalutResId);
    }

    private <T> void loadImage(T t, ImageView imageView, int resId, int defalutResId, int radius) {
        if (t instanceof Activity) {
            Activity activity = (Activity) t;
            Glide.with(activity).load(resId).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Fragment) {
            Fragment fragment = (Fragment) t;
            Glide.with(fragment).load(resId).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof View) {
            View view = (View) t;
            Glide.with(view).load(resId).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Context) {
            Context context = (Context) t;
            Glide.with(context).load(resId).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        }
    }

    private <T> void loadImage(T t, ImageView imageView, String path, int defalutResId, int radius) {
        if (t instanceof Activity) {
            Activity activity = (Activity) t;
            Glide.with(activity).load(path).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Fragment) {
            Fragment fragment = (Fragment) t;
            Glide.with(fragment).load(path).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof View) {
            View view = (View) t;
            Glide.with(view).load(path).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Context) {
            Context context = (Context) t;
            Glide.with(context).load(path).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        }
    }


    private <T> void loadImage(T t, ImageView imageView, Bitmap bitmap, int defalutResId, int radius) {
        if (t instanceof Activity) {
            Activity activity = (Activity) t;
            Glide.with(activity).load(bitmap).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Fragment) {
            Fragment fragment = (Fragment) t;
            Glide.with(fragment).load(bitmap).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof View) {
            View view = (View) t;
            Glide.with(view).load(bitmap).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        } else if (t instanceof Context) {
            Context context = (Context) t;
            Glide.with(context).load(bitmap).apply(getOptionsWithRoundedCorners(defalutResId, radius)).into(imageView);
        }
    }


    public RequestOptions getOptions(@DrawableRes int defaultResourceId) {
        return new RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .centerCrop()
            .placeholder(defaultResourceId)
            .error(defaultResourceId);
    }

    private RequestOptions getOptionsWithRoundedCorners(@DrawableRes int defaultResourceId, int radius) {

        RequestOptions options = new RequestOptions();
        if (radius > 0) {
//        设置图片圆角角度
            RoundedCorners roundedCorners = new RoundedCorners(10);
            options = RequestOptions.bitmapTransform(roundedCorners);
        }
        return options
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(defaultResourceId)
            .error(defaultResourceId);
    }

    private <T> boolean isDestoryed(T t) {
        if (t == null) {
            return true;
        }
        if (t instanceof FragmentActivity) {
            FragmentActivity activity = (FragmentActivity) t;
            return activity.isDestroyed();

        } else if (t instanceof Activity) {
            Activity activity = (Activity) t;
            return activity.isDestroyed() || activity.isFinishing();
        } else if (t instanceof Fragment) {
            Fragment fragment = (Fragment) t;
            return fragment.isDetached();
        } else if (t instanceof View) {
            View view = (View) t;
            return view.getContext() == null;
        }

        return false;
    }

}
