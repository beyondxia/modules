package com.beyondxia.bussiness1.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pingan.bussiness1.R;

import java.util.ArrayList;
import java.util.List;

public class StaggerGridAdapter extends RecyclerView.Adapter<StaggerGridAdapter.ItemViewHolder> {

    private List<String> mDatas;
    private List<Integer> mHeights;
    private LayoutInflater mInflater;
    private OnItemClickListener mOnItemClickListener;
    private int mOrientation = StaggeredGridLayoutManager.VERTICAL;

    public StaggerGridAdapter(Context context, List<String> mDatas) {
        this.mDatas = mDatas;
        mInflater = LayoutInflater.from(context);
        mHeights = new ArrayList<Integer>();
        for (int i = 0; i < mDatas.size(); i++) {
            mHeights.add( (int) (100 + Math.random() * 300));
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ItemViewHolder holder = new ItemViewHolder(mInflater.inflate(
                R.layout.stagger_item, parent, false));
        return holder;
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ItemViewHolder itemViewHolder, int position) {
        itemViewHolder.mTextView.setText(mDatas.get(position));
        ViewGroup.LayoutParams lp = itemViewHolder.mTextView.getLayoutParams();
        if(mOrientation == StaggeredGridLayoutManager.VERTICAL) {
            lp.height = mHeights.get(position);
        } else {
            lp.width = mHeights.get(position);
        }

        itemViewHolder.mTextView.setLayoutParams(lp);
        if(mOnItemClickListener != null) {
            if(!itemViewHolder.itemView.hasOnClickListeners()) {
                Log.e("ListAdapter", "setOnClickListener");
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = itemViewHolder.getPosition();
                        mOnItemClickListener.onItemClick(v, pos);
                    }
                });
                itemViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        int pos = itemViewHolder.getPosition();
                        mOnItemClickListener.onItemLongClick(v, pos);
                        return true;
                    }
                });
            }
        }
    }

    /**
     * 添加元素
     * @param position  添加的位置
     * @param value     添加元素的值
     */
    public void add(int position, String value) {
        if(position > mDatas.size()) {
            position = mDatas.size();
        }
        if(position < 0) {
            position = 0;
        }
        mDatas.add(position, value);
        mHeights.add(position, (int) (100 + Math.random() * 300));
        notifyItemInserted(position);
    }

    /**
     * 移除指定位置的元素
     * @param position
     * @return
     */
    public String remove(int position) {
        if(position >= mDatas.size() || position < 0) {
            return null;
        }
        String value = mDatas.remove(position);
        mHeights.remove(position);
        notifyItemRemoved(position);
        return value;
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOrientation(int mOrientation) {
        this.mOrientation = mOrientation;
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    /**
     * 处理 item 的点击事件和长按事件
     */
    interface OnItemClickListener {
        public void onItemClick(View view, int position);
        public void onItemLongClick(View view, int position);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.textview);
        }
    }
}