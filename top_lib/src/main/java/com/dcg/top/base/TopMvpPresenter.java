package com.dcg.top.base;

import android.content.Context;

import java.lang.ref.WeakReference;


public class TopMvpPresenter<V extends TopMvpView> {

    private WeakReference<V> weakRefView;
    protected Context mContext;


    public TopMvpPresenter() {

    }

    public void attachView(V view) {
        this.weakRefView = new WeakReference<>(view);
        if (view instanceof TopMvpActivity) {
            mContext = (TopMvpActivity) view;
        }
    }

    /**
     * 解决view销毁时内存泄露的问题
     */
    public void detachView() {
        if (isAttach()) {
            weakRefView.clear();
            weakRefView = null;
        }

    }

    public void subscribe() {

    }


    public void unSubscribe() {

    }


    protected boolean isAttach() {
        return weakRefView != null &&
            weakRefView.get() != null;
    }

    public V getView() {
        return weakRefView.get();
    }

}
