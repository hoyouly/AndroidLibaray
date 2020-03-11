package com.dc.top.customview;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dc.baselibrary.R;


public class TitleBar extends ConstraintLayout {

    public static final int FLAG_BACK = 1;
    public static final int FLAG_TITLE = 2;
    public static final int FLAG_MENU = 3;
    public static final int FLAG_SEARCH = 4;
    public static final int FLAG_SUBMIT = 5;


    private LinearLayout mLlBack;
    private LinearLayout mllSetting;
    private LinearLayout mllSearch;
    private TextView tvTitle;
    private TextView tvRightText;

    private ImageView ivSearch;


    public TitleBar(Context context) {
        super(context);
        init(null);
    }


    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void setTitle(String title) {
        tvTitle.setVisibility(VISIBLE);
        tvTitle.setText(title);
    }

    private void init(AttributeSet attrs) {
        LayoutInflater.from(getContext()).inflate(R.layout.title_bar, this, true);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        mllSetting = (LinearLayout) findViewById(R.id.ll_setting);
        mllSearch = (LinearLayout) findViewById(R.id.ll_titlebar_search);
        mLlBack = (LinearLayout) findViewById(R.id.llBack);
        ivSearch = findViewById(R.id.iv_search);
        tvRightText = findViewById(R.id.tv_submit);
    }


    public void setViewVisiBility(int viewFlag, int visibility) {
        switch (viewFlag) {
            case FLAG_BACK:
                mLlBack.setVisibility(visibility);
                break;
            case FLAG_TITLE:
                tvTitle.setVisibility(visibility);
                break;
            case FLAG_MENU:
                mllSetting.setVisibility(visibility);
                break;
            case FLAG_SEARCH:
                mllSearch.setVisibility(visibility);
                break;
        }
    }

    public void setImageRes(int viewFlag, int resId) {
        switch (viewFlag) {
            case FLAG_SEARCH:
                ivSearch.setImageResource(resId);
                mllSearch.setVisibility(VISIBLE);
                break;
        }
    }


    public void setTitleColor(int color) {
        tvTitle.setTextColor(color);
    }


    public void setSettingClick(OnClickListener onClickListener) {
        mllSetting.setVisibility(VISIBLE);
        mllSetting.setOnClickListener(onClickListener);
    }

    public void setSearchClick(OnClickListener onClickListener) {
        mllSearch.setVisibility(VISIBLE);
        mllSearch.setOnClickListener(onClickListener);
    }

    public void setLeftBack(OnClickListener onClickListener) {
        mLlBack.setVisibility(VISIBLE);
        mLlBack.setOnClickListener(onClickListener);
    }

    public void setSubmitClick(OnClickListener onClickListener) {
        tvRightText.setVisibility(VISIBLE);
        tvRightText.setOnClickListener(onClickListener);
    }
}
