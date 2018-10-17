package com.beyondxia.bussiness1.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.pingan.bussiness1.R;

import java.util.ArrayList;
import java.util.List;

public class SimpleFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private StaggerGridAdapter mAdapter;
    private List<String> mDatas;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.simple_fragment, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        initData();

        mAdapter = new StaggerGridAdapter(getActivity(), mDatas);
        mAdapter.setOnItemClickListener(new StaggerGridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(getActivity(), "Click" + mDatas.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                Toast.makeText(getActivity(), "LongClick" + mDatas.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        // 设置布局管理器
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        // 设置 item 动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    protected void initData() {
        mDatas = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            mDatas.add(String.valueOf(i));
        }
    }

}
