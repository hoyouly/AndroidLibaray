package com.dcg.android;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import com.dcg.android.R;
import com.dcg.selectphoto.SelectPhotoFragment;
import com.dcg.top.base.*;
import org.xutils.x;

public class MainActivity extends TopMvpActivity {
    private SelectPhotoFragment fragment;

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

    @Override
    protected void initView() {
        super.initView();
        x.Ext.init(getApplication());
//        fragment = SelectPhotoFragment.newInstance(BuildConfig.APPLICATION_ID, 10);
//        getSupportFragmentManager()
//            .beginTransaction()
//            .replace(R.id.fragment_container, fragment)
//            .commitNowAllowingStateLoss();
    }
}
