package com.dcg.selectphoto;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * @ Time :  2019-12-05
 * @ Author :  helei
 * @ Email :  heleik@digitalchina.com
 * @ Description :
 */
public abstract class BaseAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private Context mContext;
    private int itemLayouId;
    private List<T> mDatas;

    public BaseAdapter(Context context, int itemLayouId, List<T> datas) {
        mContext = context;
        this.itemLayouId = itemLayouId;
        mDatas = datas;
    }

    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(itemLayouId, viewGroup, false);
        return new CommonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder commonViewHolder, int i) {
        convert(commonViewHolder, mDatas.get(i), i);
    }

    protected abstract void convert(CommonViewHolder commonViewHolder, T t, int index);

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public List<T> getDatas() {
        return mDatas;
    }

    public void setData(List<T> data) {
        mDatas = data;
        notifyDataSetChanged();
    }

}
