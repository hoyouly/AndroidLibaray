package com.dcg.top.customview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.Window;
import android.view.WindowManager;

import com.dcg.top.R;

/**
 * @ Time :  2019-12-10
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 */
public class LodingDialog extends Dialog {

    private Window mWindow;//当前Activity 的窗口
    private static final float DEFAULT_ALPHA = 0.7f;
    private ContentLoadingProgressBar mProgressBar;
    private float mBackgroundDrakValue = 0;// 背景变暗的值，0 - 1

    public LodingDialog(Context context) {
        super(context, R.style.dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
    }

    private void initView() {
        setContentView(R.layout.dialog_loding);

        //mProgressBar = (ContentLoadingProgressBar) findViewById(R.id.pb);
        //mProgressBar.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        //设置自适应的方法：
        WindowManager.LayoutParams dialogParams = getWindow().getAttributes();
        dialogParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        dialogParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        //设置底部显示
        getWindow().setAttributes(dialogParams);

        // 控制亮度
        setBgDarkAlpha(0.9f);
        // 弹出Dialog时，背景是否变暗
        enableBackgroundDark(true);

    }

    @Override
    public void show() {
        super.show();
    }

    /**
     * 设置背景变暗的值
     * @param darkValue
     * @return
     */
    public void setBgDarkAlpha(float darkValue){
        mBackgroundDrakValue = darkValue;
    }

    private void enableBackgroundDark(boolean enable) {
        //如果设置的值在0 - 1的范围内，则用设置的值，否则用默认值
        float alpha = (mBackgroundDrakValue > 0 && mBackgroundDrakValue < 1) ? mBackgroundDrakValue : DEFAULT_ALPHA;
        mWindow = getWindow();
        WindowManager.LayoutParams params = mWindow.getAttributes();
        params.alpha = alpha;
        mWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        mWindow.setAttributes(params);
    }

}
