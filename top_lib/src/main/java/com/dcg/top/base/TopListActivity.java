package com.dcg.top.base;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dcg.top.R;
import com.dcg.top.utils.DensityUtil;
import com.zhouyou.recyclerview.XRecyclerView;
import com.zhouyou.recyclerview.divider.XHorizontalDividerItemDecoration;

import java.util.ArrayList;
import java.util.List;


/**
 * @ Time :  2019-11-21
 * @ Author :  helei
 * @ Description :  封装了一个带有带有列表的Activity，可以给该列表添加Appbar和底部的View
 * <p/>
 * 子类Activity 只需要覆盖里面关键的方法，就可以了， 功能包括上拉刷新，下来加载，自定义头布局，
 */
public abstract class TopListActivity<T, P extends TopMvpPresenter> extends TopMvpActivity implements TopListMvpView<T>, XRecyclerView.LoadingListener {

    AppBarLayout mAppBarLayout;
    public XRecyclerView mRecyclerView;

    protected LinearLayout mRlBottom;

    protected BaseRecycleAdapter mAdapter;

    private List<T> mDatas;
    protected int currentPage = 1;

    protected LinearLayout mEmptyView;
    private TextView mTvReturn;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_top_list;
    }

    @Override
    public void addLayout() {
        super.addLayout();
        mAppBarLayout = findViewById(R.id.app_bar);
        mRecyclerView = findViewById(R.id.rv_list);
        int appBarLayout = getAppBarLayout();
        if (appBarLayout > 0) {
            LayoutInflater.from(this).inflate(appBarLayout, mAppBarLayout, true);
        } else {
            Log.d("hoyouly", "appBarLayout is null");
        }
        //无数据的时候的view
        mEmptyView = findViewById(R.id.ll_no_data);
        mRlBottom = findViewById(R.id.rl_bottom);
        int bottomLayout = getBottomLayout();
        if (bottomLayout > 0) {
            LayoutInflater.from(this).inflate(bottomLayout, mRlBottom, true);
        }
        LayoutInflater.from(this).inflate(getEmptyViewLayout(), mEmptyView, true);
        mTvReturn = findViewById(R.id.tv_return);
    }

    @Override
    protected void initView() {
        super.initView();
        initRecyclerView();
    }

    @Override
    protected void initData(@Nullable Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        mDatas = new ArrayList<>();
    }

    private void initRecyclerView() {
        mRecyclerView.setLoadingMoreEnabled(isEnableLoadMore());
        mRecyclerView.setPullRefreshEnabled(isEnableRefresh());
        mRecyclerView.setLoadingListener(this);
        mRecyclerView.setLayoutManager(getLayoutManger());

        if (isShowDecoration()) {
            mRecyclerView.addItemDecoration(getItemDecoration());
        }
        mAdapter = new BaseRecycleAdapter<T>(this, getItemLayout(), mDatas) {

            @Override
            protected void convert(VH vh, T o, int index) {
                TopListActivity.this.convert(vh, o, index);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
    }

    public RecyclerView.ItemDecoration getItemDecoration() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(DensityUtil.dip2px(1));
        paint.setColor(Color.parseColor("#ebebeb"));
        return new XHorizontalDividerItemDecoration.Builder(this)
            //分割线采用paint
            .paint(paint)
            .build();
    }

    /**
     * 是否显示分割线
     *
     * @return
     */
    public boolean isShowDecoration() {
        return true;
    }

    /**
     * 底部 layout 布局
     *
     * @return
     */
    protected int getBottomLayout() {
        return 0;
    }


    /**
     * 无数据的时候显示的布局
     *
     * @return
     */
    protected int getEmptyViewLayout() {
        return R.layout.layout_default_emptyview;
    }

    /**
     * app bar layout 布局
     *
     * @return
     */
    public abstract int getAppBarLayout();


    /**
     * 是否支持刷新
     *
     * @return
     */
    public boolean isEnableRefresh() {
        return true;
    }

    /**
     * 是否支持下来加载更多
     *
     * @return
     */
    public boolean isEnableLoadMore() {
        return false;
    }

    @Override
    public void saveData(Object o) {
    }

    /**
     * item 布局 资源id
     *
     * @return
     */
    public abstract int getItemLayout();


    /**
     * 得到的数据和item布局一一对应
     *
     * @param holder
     * @param bean
     */
    public abstract void convert(@NonNull VH holder, T bean, int index);

    /**
     * 请求数据
     */
    public abstract void requestData();

    @Override
    public P getPresenter() {
        return (P) super.getPresenter();
    }

    public void setEnableLoadMore(boolean enable) {
        mRecyclerView.setLoadingMoreEnabled(enable);
    }

    public void setEnableRefresh(boolean enable) {
        mRecyclerView.setPullRefreshEnabled(enable);
    }

    public int getItemCount() {
        return mAdapter.getItemCount();
    }

    public void updateList(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }


    public void appendList(List<T> datas) {
        mDatas.addAll(datas);
        mAdapter.notifyDataSetChanged();
    }


    public void stopRefresh() {
        mRecyclerView.loadMoreComplete();
        mRecyclerView.refreshComplete();
    }

    @Override
    public void subscribe() {
        super.subscribe();
        showLoading();
        requestData();
    }

    public RecyclerView.LayoutManager getLayoutManger() {
        return new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
    }

    @Override
    public void loadData(List<T> datas, boolean hasMore) {
        hideLoading();
        stopRefresh();
        if (currentPage == 1) {
            if (datas.isEmpty()) {
                showEmptyView(true);
            } else {
                showEmptyView(false);
                updateList(datas);
                mRecyclerView.smoothScrollToPosition(0);
            }
        } else {
            appendList(datas);
        }
        setEnableLoadMore(hasMore);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        showLoading();
        requestData();
    }

    @Override
    public void onLoadMore() {
        currentPage++;
        showLoading();
        requestData();
    }

    @Override
    public void hideLoading() {
        super.hideLoading();
        stopRefresh();
    }


    public List<T> getDatas() {
        return mDatas;
    }


    @Override
    public void showEmptyView(boolean isShow) {
        if (isShow) {
            mEmptyView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            if (mTvReturn != null) {
                mTvReturn.setOnClickListener(v -> finish());
            }
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void appendData(T data) {
        mDatas.add(data);
        mAdapter.notifyDataSetChanged();
    }
}
