package com.dcg.selectphoto;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @ Time  :  2020-02-20
 * @ Author :  helei
 * @ Email :   heleik@digitalchina.com
 * @ Description :
 */
public class GalleryFragment extends Fragment {

    private SpeedRecyclerView mRecyclerView;

    private ArrayList<String> mPicList;
    private int currentIndex = 0;
    private BaseAdapter<String> mAdapter;
    private OnItemScrollChangeListener onItemScrollChangeListener;


    public static GalleryFragment newInstance(ArrayList<String> pics, int currentIndex) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("pics", pics);
        bundle.putInt("currentIndex", currentIndex);
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerview);
        Bundle arguments = getArguments();
        if (arguments == null) {
            return view;
        }
        mPicList = arguments.getStringArrayList("pics");
        currentIndex = arguments.getInt("currentIndex", 0);
        initRecycleView();
        return view;
    }

    public void setPicList(ArrayList<String> mPicList) {
        this.mPicList = mPicList;
        mAdapter.notifyDataSetChanged();
    }

    private void initRecycleView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new BaseAdapter<String>(getContext(), R.layout.gallery_pic, mPicList) {
            @Override
            protected void convert(CommonViewHolder commonViewHolder, String path, int index) {
                commonViewHolder.setLocalImage(R.id.iv_pic, path);
            }
        };
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setOnItemScrollChangeListener(onItemScrollChangeListener);
        new LinearSnapHelper().attachToRecyclerView(mRecyclerView);
        linearLayoutManager.scrollToPosition(currentIndex);
    }

    public void setOnItemScrollChangeListener(OnItemScrollChangeListener onItemScrollChangeListener) {
        this.onItemScrollChangeListener = onItemScrollChangeListener;
    }

}
