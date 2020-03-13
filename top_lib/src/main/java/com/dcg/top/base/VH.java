package com.dcg.top.base;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.x;

/**
 * @ Time :  2019-12-05
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public class VH extends RecyclerView.ViewHolder {

    private SparseArray<View> mView;
    private View mContentView;

    public VH(View itemView) {
        super(itemView);
        mView = new SparseArray<>();
        mContentView = itemView;
    }


    public View getContentView() {
        return mContentView;
    }


    public <T extends View> T getView(int viewId) {
        View view = mView.get(viewId);

        if (view == null) {
            view = mContentView.findViewById(viewId);
            mView.put(viewId, view);
        }

        return (T) view;
    }

    /**
     * 为TextView 设置字符串
     *
     * @param viewId
     * @param text
     * @return
     */
    public VH setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public VH setImageResource(int viewId, int drawableId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(drawableId);
        return this;
    }

    public VH setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public VH setImageURI(int viewId, Uri uri) {
        ImageView imageView = getView(viewId);
        imageView.setImageURI(uri);
        return this;
    }

    public VH setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public VH setViewClick(int viewId, View.OnClickListener onClickListener) {
        View view = getView(viewId);
        view.setOnClickListener(onClickListener);
        return this;
    }

    public VH setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public VH setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }


    public VH setClickable(int viewId, boolean clickable) {
        getView(viewId).setClickable(clickable);
        return this;
    }

    public VH setLocalImage(int viewId, String path) {
        ImageView imageView = getView(viewId);
        x.image().bind(imageView, path);
        return this;

    }

    public VH setOnItemClick(View.OnClickListener onItemClick) {
        itemView.setOnClickListener(onItemClick);
        return this;
    }


}