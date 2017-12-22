package com.xmd.technician.Adapter;

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

    public static final int VIEW_TYPE_HEADER = 1;
    public static final int VIEW_TYPE_DATA = 2;
    public static final int VIEW_TYPE_FOOTER = 3;
    private int mHeaderLayout;
    private int mHeaderBR;
    private Object mHeaderData;
    private int mFooterLayout;
    private int mFooterBR;
    private Object mFooterData;
    private int mDataBR;
    private int mDataLayout;
    private List<T> mData;
    private int mHandlerBR;
    private Object mHandler;
    private int mShowDataCountLimit = Integer.MAX_VALUE;

    private boolean mInvert;
    //item view 处理接口
    private ViewInflatedListener mViewInflatedListener;
    //item 数据 处理接口
    private DataTranslator mDataTranslator;

    public CommonRecyclerViewAdapter setHeader(int layoutId, int br, Object data) {
        mHeaderLayout = layoutId;
        mHeaderBR = br;
        mHeaderData = data;
        return this;
    }

    public CommonRecyclerViewAdapter setFooter(int layoutId, int br, Object data) {
        mFooterLayout = layoutId;
        mFooterBR = br;
        mFooterData = data;
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

    public List<T> getDataList() {
        return mData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding;
        if (viewType == VIEW_TYPE_HEADER) {
            binding = DataBindingUtil.inflate(layoutInflater, mHeaderLayout, parent, false);
        } else if (viewType == VIEW_TYPE_FOOTER) {
            binding = DataBindingUtil.inflate(layoutInflater, mFooterLayout, parent, false);
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
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            binding.setVariable(mHeaderBR, mHeaderData);
        } else if (holder.getItemViewType() == VIEW_TYPE_FOOTER) {
            binding.setVariable(mFooterBR, mFooterData);
        } else {
            if (mHeaderLayout != 0) {
                position--;
            }
            if (mInvert) {
                position = mData.size() - position - 1;
            }
            if (mDataTranslator != null) {
                binding.setVariable(mDataBR, mDataTranslator.translate(mData.get(position)));
            } else {
                binding.setVariable(mDataBR, mData.get(position));
            }
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
        if (mFooterLayout != 0) {
            count++;
        }
        if (mData != null) {
            if (mData.size() > mShowDataCountLimit) {
                count += mShowDataCountLimit;
            } else {
                count += mData.size();
            }
        }
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (mHeaderLayout != 0 && position == 0) {
            return VIEW_TYPE_HEADER;
        } else if (mFooterLayout != 0 && position == getItemCount() - 1) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_DATA;
        }
    }

    public void setShowDataCountLimit(int count) {
        mShowDataCountLimit = count;
    }

    public void setViewInflatedListener(ViewInflatedListener listener) {
        mViewInflatedListener = listener;
    }

    public void setDataTranslator(DataTranslator translator) {
        mDataTranslator = translator;
    }

    public interface ViewInflatedListener {
        void onViewInflated(int viewType, View view);
    }

    public interface DataTranslator {
        Object translate(Object originData);
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
}
