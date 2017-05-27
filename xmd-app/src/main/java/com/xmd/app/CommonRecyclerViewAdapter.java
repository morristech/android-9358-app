package com.xmd.app;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by mo on 16-12-23.
 */

public class CommonRecyclerViewAdapter<T> extends RecyclerView.Adapter<CommonRecyclerViewAdapter.ViewHolder> {

    protected int mHeaderLayout;
    protected int mHeaderBR;
    protected Object mHeader;

    protected int mDataBR;
    protected int mDataLayout;
    protected List<T> mData;

    protected int mHandlerBR;
    protected Object mHandler;

    protected static final int VIEW_TYPE_HEADER = 1;
    protected static final int VIEW_TYPE_DATA = 2;

    protected boolean mInvert;

    public CommonRecyclerViewAdapter setHeader(int layoutId, int br, Object data) {
        mHeaderLayout = layoutId;
        mHeaderBR = br;
        mHeader = data;
        return this;
    }

    public CommonRecyclerViewAdapter setData(int layoutId, int br, List<T> data) {
        mDataLayout = layoutId;
        mDataBR = br;
        mData = data;
        return this;
    }

    public CommonRecyclerViewAdapter setHandler(int br, Object handler) {
        mHandlerBR = br;
        mHandler = handler;
        return this;
    }

    public void setInvert(boolean invert) {
        mInvert = invert;
    }

    public T getData(int index) {
        return mData.get(index);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding;
        if (viewType == VIEW_TYPE_HEADER) {
            binding = DataBindingUtil.inflate(layoutInflater, mHeaderLayout, parent, false);
        } else {
            binding = DataBindingUtil.inflate(layoutInflater, mDataLayout, parent, false);
        }
        if (mViewInflatedListener != null) {
            mViewInflatedListener.onViewInflated(viewType, binding.getRoot());
        }
        ViewHolder viewHolder = new ViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ViewDataBinding binding = holder.getBinding();
        if (mHeaderLayout != 0) {
            position--;
        }
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            binding.setVariable(mHeaderBR, mHeader);
        } else {
            if (mInvert) {
                position = mData.size() - position - 1;
            }
            binding.setVariable(mDataBR, mData.get(position));
        }
        if (mHandler != null) {
            binding.setVariable(mHandlerBR, mHandler);
        }
        binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        int count = 0;
        if (mHeaderLayout != 0) {
            count++;
        }
        if (mData != null) {
            count += mData.size();
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderLayout != 0 && position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_DATA;
        }
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

    protected ViewInflatedListener mViewInflatedListener;

    public interface ViewInflatedListener {
        void onViewInflated(int viewType, View view);
    }

    public void setViewInflatedListener(ViewInflatedListener listener) {
        mViewInflatedListener = listener;
    }
}
