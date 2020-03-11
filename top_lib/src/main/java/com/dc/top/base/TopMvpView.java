package com.dc.top.base;

import android.support.annotation.StringRes;

/**
 *
 */

public interface TopMvpView {

    void showToast(String message);

    void showToast(@StringRes int resId);


    void onSuccess();

    /**
     * P-V-UI 初始化成功：显示UI loading, 同时可以开始进行与UI相关的数据存取
     */
    void showLoading();

    /**
     * (后台)数据存取成功：隐藏UI loading，或展示其他页面
     */
    void hideLoading();


    /**
     * 请求失败
     */
    void onError(String msg);


}
