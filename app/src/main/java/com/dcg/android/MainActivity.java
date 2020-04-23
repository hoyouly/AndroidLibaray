package com.dcg.android;


import android.view.View;
import android.widget.TextView;
import butterknife.BindView;
import com.dcg.selectphoto.SelectPhotoFragment;
import com.dcg.top.base.*;
import org.xutils.x;

public class MainActivity extends TopMvpActivity<MainPresenter> {

    private SelectPhotoFragment fragment;

    @BindView(R.id.submit_retrofit)
    TextView submit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getTitleNameResId() {
        return R.string.app_name;
    }

    @Override
    protected MainPresenter setPresent() {
        return new MainPresenter();
    }

    @Override
    protected void initView() {
        super.initView();
        x.Ext.init(getApplication());
        fragment = SelectPhotoFragment.newInstance(BuildConfig.APPLICATION_ID, 10);
        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commitNowAllowingStateLoss();

        setOnClick(R.id.submit_retrofit, R.id.submit_xutil, R.id.submit_okhttp);
    }

    @Override
    public void onSingleClick(View view) {
        super.onSingleClick(view);
        switch (view.getId()) {
            case R.id.submit_retrofit:
                getPresenter().uploadPic(fragment.getLocalPathList(), 1);
                break;
            case R.id.submit_okhttp:
                getPresenter().uploadPic(fragment.getLocalPathList(), 2);
                break;
            case R.id.submit_xutil:
                getPresenter().uploadPic(fragment.getLocalPathList(), 3);
                break;
            default:
        }
    }
}
