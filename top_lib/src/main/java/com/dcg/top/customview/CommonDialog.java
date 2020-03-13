package com.dcg.top.customview;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dcg.top.R;


/**
 * Created by AmosShi on 2018/1/9.
 * @ Email :shixiuwen1991@yeah.net
 * Abstract : 公用弹窗
 */

public class CommonDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private String title;
    private String content;
    private String leftBtnText;
    private String rightBtnText;
    private boolean isContentAlignLeft;

    private CommonDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    public CommonDialog(Context context, String content) {
        this(context, "", content, context.getString(R.string.cancel), context.getString(R.string.sure), false);
    }

    public CommonDialog(Context context, String content, String leftStr, String rightStr) {
        this(context, "", content, leftStr, rightStr, false);
    }

    public CommonDialog(Context context, String title, String content, String leftBtnText, String rightBtnText, boolean isContentAlignLeft) {
        super(context, R.style.noTitleDialog);
        this.context = context;
        this.title = title;
        this.content = content;
        this.leftBtnText = leftBtnText;
        this.rightBtnText = rightBtnText;
        this.isContentAlignLeft = isContentAlignLeft;
        initView();
        initSetting();
    }

    public void setLeftButtonColor(@ColorInt int colorValue) {
        ((TextView) findViewById(R.id.btn_cancel)).setTextColor(colorValue);
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_common, null);
        setContentView(view);
        LinearLayout llParent = (LinearLayout) view.findViewById(R.id.ll_parent);
        TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) view.findViewById(R.id.tv_content);
        Button btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        Button btn_sure = (Button) view.findViewById(R.id.btn_sure);
        if (TextUtils.isEmpty(title)) {
            tv_title.setVisibility(View.GONE);
        } else {
            tv_title.setVisibility(View.VISIBLE);
            tv_title.setText(title);
        }
        tv_content.setText(content);
        if (isContentAlignLeft) {
            tv_content.setGravity(Gravity.LEFT);
        } else {
            tv_content.setGravity(Gravity.CENTER);
        }
        btn_cancel.setText(leftBtnText);
        btn_sure.setText(rightBtnText);
        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);
    }

    private void initSetting() {
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(true);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_cancel) {
            if (onCommonDialogClickListener != null) {
                onCommonDialogClickListener.onCancelClickListener(this);
            }
        } else if (i == R.id.btn_sure) {
            if (onCommonDialogClickListener != null) {
                onCommonDialogClickListener.onSureClickListener(this);
            }
        }
    }

    /**
     * ############################## 回调事件 ####################################
     */

    private OnCommonDialogClickListener onCommonDialogClickListener;

    public void setOnCommonDialogClickListener(OnCommonDialogClickListener onUpdateDialogClickListener) {
        this.onCommonDialogClickListener = onUpdateDialogClickListener;
    }

    public interface OnCommonDialogClickListener {
        void onCancelClickListener(Dialog dialog);

        void onSureClickListener(Dialog dialog);
    }
}
