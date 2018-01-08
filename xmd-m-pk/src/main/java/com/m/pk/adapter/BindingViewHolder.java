package com.m.pk.adapter;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Lhj on 18-1-3.
 */

public class BindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {

    private T mBinding;

    public BindingViewHolder(T itemBinding) {
        super(itemBinding.getRoot());
        mBinding = itemBinding;
    }

    public T getBinding() {
        return mBinding;
    }
}
