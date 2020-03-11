package com.dc.android;


import com.dc.top.base.TopMvpActivity;
import com.dc.top.base.TopMvpPresenter;

public class MainActivity extends TopMvpActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleNameResId() {
        return R.string.app_name;
    }

    @Override
    protected TopMvpPresenter setPresent() {
        return new TopMvpPresenter();
    }
}
