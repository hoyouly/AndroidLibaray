package com.dcg.top.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.WindowManager;

import com.dcg.top.customview.LodingDialog;

/**
 * 这一层只做Mvp相关的处理
 */
public abstract class TopMvpActivity<P extends TopMvpPresenter> extends TopActivity implements TopMvpView {

    private P mPresent;

    private LodingDialog mLodingDialog;

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        mPresent = setPresent();
        mPresent.attachView(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onStart() {
        super.onStart();
        mPresent.subscribe();
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribe();
    }

    public void subscribe() {
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPresenter().unSubscribe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().detachView();
    }

    protected abstract P setPresent();

    public P getPresenter() {
        return mPresent;
    }

    @Override
    public void showLoading() {
        if (mLodingDialog == null) {
            mLodingDialog = new LodingDialog(TopMvpActivity.this);
        }
        mLodingDialog.show();
    }

    @Override
    public void hideLoading() {
        if (mLodingDialog != null) {
            mLodingDialog.dismiss();
        }
    }


    @Override
    public void onSuccess() {
        Log.d("hoyouly", "TopMvpActivity : " + "onSuccess() called");
    }

    @Override
    public void onError(String msg) {
        showToast(msg);
    }
}
