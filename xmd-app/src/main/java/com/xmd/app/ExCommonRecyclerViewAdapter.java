package com.xmd.app;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by mo on 16-12-23.
 */

public abstract class ExCommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<ExCommonRecyclerViewAdapter.ViewHolder> {
    protected List<T> mData;
    private int mDataBR;

    //设置数据
    public ExCommonRecyclerViewAdapter setData(int br, List<T> data) {
        mData = data;
        mDataBR = br;
        return this;
    }

    //获取特定位置的数据，index是数据索引，非列表索引
    public T getData(int index) {
        return mData.get(index);
    }

    public List<T> getDataList() {
        return mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = createViewDataBinding(parent, viewType);
        ViewHolder viewHolder = new ViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewDataBinding binding = holder.getBinding();
        Object data = mData.get(position);
        onDataBinding(binding, position);
        binding.setVariable(mDataBR, data);
        binding.executePendingBindings();
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.getBinding().unbind();
        onDataUnBinding(holder.getBinding(), holder.getAdapterPosition());
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mData != null) {
            count = mData.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return getViewType(position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }
    }

    public abstract ViewDataBinding createViewDataBinding(ViewGroup parent, int viewType);

    public abstract int getViewType(int position);

    public void onDataBinding(ViewDataBinding binding, int position) {

    }

    public void onDataUnBinding(ViewDataBinding binding, int position) {

    }
}
