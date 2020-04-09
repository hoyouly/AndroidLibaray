package com.dcg.top.base;

import android.app.ActionBar;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.*;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.dcg.top.R;
import com.dcg.top.TopApplication;
import com.dcg.top.customview.TitleBar;
import com.dcg.top.utils.Utils;

import java.lang.ref.WeakReference;

/**
 * 定义一个activity_base.xml,包含 通用的title，getLayoutId()只需要添加自己业务逻辑布局即可，不需要每个页面都添加一个title布局
 * </p>
 * 1. 默认支持沉浸式
 * </p>
 * 2. 默认支持ButterKnife
 * </p>
 * 3. 默认注册了 EventBus
 */
public abstract class TopActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "TopActivity";
    private Toast toast;

    protected LinearLayout mRootView;

    protected TitleBar mTitleBar;

    private View mLine;

    private Unbinder mBind;
    protected MyHandler mHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarTransparent();
        //处理 Android 软键盘挡住输入框
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_top);
        mRootView = findViewById(R.id.root_view);
        mTitleBar = findViewById(R.id.com_title);
        mLine = findViewById(R.id.line);
        LayoutInflater.from(TopActivity.this).inflate(getLayoutId(), mRootView, true);

        mHandler = new MyHandler(this);

        addLayout();

        mBind = ButterKnife.bind(this);
        initData(savedInstanceState);
        initTitleBar();
        initView();
        initListener();
    }

    /**
     * 添加布局，主要是添加头布局和底部布局，放在ButterKnife初始化之前，这样就可以直接使用了
     */
    public void addLayout() {

    }


    @Override
    public void onClick(View v) {
        if (Utils.isFastClick()) {
            Log.e("TopActivity", getClass().getSimpleName() + " onClick: 重复点击。");
        } else {
            onSingleClick(v);
        }
    }

    /**
     * 处理重复点击后的事件
     *
     * @param view
     */
    public void onSingleClick(View view) {

    }

    /**
     * 设置资源文件ID
     *
     * @return
     */
    protected abstract int getLayoutId();


    /**
     * 设置title 左侧默认是一个返回按钮，中间是一个 TextView ,右侧是空的
     */
    protected void initTitleBar() {
        int titleNameResId = getTitleNameResId();
        if (titleNameResId > 0) {
            String title = getString(titleNameResId);
            mTitleBar.setTitle(title);
        }
        mTitleBar.setLeftBack(v -> finish());
        if (isShowLine()) {
            mLine.setVisibility(View.VISIBLE);
        } else {
            mLine.setVisibility(View.GONE);
        }
    }


    /**
     * titile 的资源id
     *
     * @return
     */
    protected abstract int getTitleNameResId();


    /**
     * 初始化数据
     *
     * @param savedInstanceState
     */
    protected abstract void initData(@Nullable Bundle savedInstanceState);

    /**
     * 初始化监听事件
     */
    protected void initListener() {
    }

    public void setOnClick(int... ids) {
        for (int id : ids) {
            findViewById(id).setOnClickListener(this);
        }
    }


    /**
     * 是否显示分割线，默认不显示
     *
     * @return
     */
    protected boolean isShowLine() {
        return false;
    }

    /**
     * 对View的初始化，这个不包括findViewById ,而是对View的一些初始设置，例如对RecycleView的一些设置等。
     */
    protected void initView() {

    }

    //处理handle消息
    public void handleMessage(Message msg) {

    }

    public void showToast(String message) {
        runOnUiThread(() -> Toast.makeText(TopActivity.this, message, Toast.LENGTH_SHORT).show());
    }


    public void showToast(@StringRes int resId) {
        showToast(getString(resId));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //反注册EventBus监听
//        EventBus.getDefault().unregister(this);
        if (mBind != null) {
            mBind.unbind();
        }
    }


    public static class MyHandler extends Handler {

        WeakReference<TopActivity> weakReference;

        public MyHandler(TopActivity activity) {
            weakReference = new WeakReference(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                weakReference.get().handleMessage(msg);
            }
        }
    }

    /**
     * 设置透明状态栏
     */
    private void setStatusBarTransparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * 格式化字符串
     *
     * @param resId
     * @param args
     * @return
     */
    public String formatString(@StringRes int resId, Object... args) {
        return String.format(getString(resId), args);
    }

}
