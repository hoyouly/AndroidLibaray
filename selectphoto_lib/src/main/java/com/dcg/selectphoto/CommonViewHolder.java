package com.dcg.selectphoto;

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
public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mView;
    private View mContentView;

    public CommonViewHolder(View itemView) {
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
    public CommonViewHolder setText(int viewId, String text) {
        TextView view = getView(viewId);
        view.setText(text);
        return this;
    }

    public CommonViewHolder setImageResource(int viewId, int drawableId) {
        ImageView imageView = getView(viewId);
        imageView.setImageResource(drawableId);
        return this;
    }

    public CommonViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView imageView = getView(viewId);
        imageView.setImageBitmap(bitmap);
        return this;
    }

    public CommonViewHolder setImageURI(int viewId, Uri uri) {
        ImageView imageView = getView(viewId);
        imageView.setImageURI(uri);
        return this;
    }

    public CommonViewHolder setTextColor(int viewId, int color) {
        TextView view = getView(viewId);
        view.setTextColor(color);
        return this;
    }

    public CommonViewHolder setViewClick(int viewId, View.OnClickListener onClickListener) {
        View view = getView(viewId);
        view.setOnClickListener(onClickListener);
        return this;
    }

    public CommonViewHolder setVisibility(int viewId, int visibility) {
        getView(viewId).setVisibility(visibility);
        return this;
    }

    public CommonViewHolder setChecked(int viewId, boolean checked) {
        Checkable view = getView(viewId);
        view.setChecked(checked);
        return this;
    }


    public CommonViewHolder setClickable(int viewId, boolean clickable) {
        getView(viewId).setClickable(clickable);
        return this;
    }

    public CommonViewHolder setLocalImage(int viewId, String path) {
        ImageView imageView = getView(viewId);
        x.image().bind(imageView, path);
        return this;

    }

    public CommonViewHolder setOnItemClick(View.OnClickListener onItemClick) {
        itemView.setOnClickListener(onItemClick);
        return this;
    }


}