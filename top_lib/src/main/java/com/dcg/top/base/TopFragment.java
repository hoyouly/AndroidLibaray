package com.dcg.top.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.dcg.top.R;
import com.dcg.top.TopApplication;
import com.dcg.top.customview.TitleBar;

import java.lang.ref.WeakReference;

/**
 * @ Time  :  2020-02-20
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public abstract class TopFragment extends Fragment {

    public static String TAG;

    private Toast toast;

    protected LinearLayout mRootView;

    protected TitleBar mTitleBar;

    private View mLine;

    private Unbinder mBind;
    protected MyHandler mHandler;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_top, container, false);
        TAG = getClass().getSimpleName();

        mRootView = view.findViewById(R.id.root_view);
        mTitleBar = view.findViewById(R.id.com_title);
        mLine = view.findViewById(R.id.line);
        View contentView = inflater.inflate(getLayoutId(), null);
        mRootView.addView(contentView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        mHandler = new MyHandler(this);

        addLayout();

        mBind = ButterKnife.bind(this, view);
        initData(savedInstanceState);
        initTitleBar();
        initView(view);
        initListener();
        return view;
    }

    public void initListener() {

    }

    public void initView(View rootView) {

    }

    public void initTitleBar() {
        int titleNameResId = getTitleNameResId();
        if (titleNameResId > 0) {
            String title = getString(titleNameResId);
            mTitleBar.setTitle(title);
        }
//        mTitleBar.setLeftBack(v -> finish()); TODO
        if (isShowLine()) {
            mLine.setVisibility(View.VISIBLE);
        } else {
            mLine.setVisibility(View.GONE);
        }
    }

    protected abstract int getLayoutId();

    /**
     * 是否显示分割线，默认不显示
     *
     * @return
     */
    protected boolean isShowLine() {
        return false;
    }

    protected abstract int getTitleNameResId();

    public abstract void initData(Bundle savedInstanceState);

    public void addLayout() {

    }


    public static class MyHandler extends Handler {

        WeakReference<TopFragment> weakReference;

        public MyHandler(Fragment fragment) {
            weakReference = new WeakReference(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            if (weakReference.get() != null) {
                weakReference.get().handleMessage(msg);
            }
        }
    }

    //处理handle消息
    public void handleMessage(Message msg) {

    }

    public void showToast(@StringRes int resId) {
        showToast(getContext().getResources().getString(resId));
    }

    public void showToast(String message) {
        mHandler.post(() -> {
            try {
                if (toast == null) {
                    toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast.setText(message);
                    toast.show();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
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

    @Nullable
    @Override
    public Context getContext() {
        Context context = super.getContext();
        return context == null ? TopApplication.getAppContext() : context;
    }
}
