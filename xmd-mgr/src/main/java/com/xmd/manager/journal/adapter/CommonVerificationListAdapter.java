package com.xmd.manager.journal.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.databinding.library.baseAdapters.BR;
import com.google.gson.Gson;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CheckInfo;
import com.xmd.manager.beans.PayOrderDetailBean;
import com.xmd.manager.beans.VerificationCouponDetailBean;
import com.xmd.manager.chat.CommonUtils;
import com.xmd.manager.databinding.CheckInfoListItemBinding;
import com.xmd.manager.verification.VerificationListListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 17-5-12.
 */

public class CommonVerificationListAdapter extends RecyclerView.Adapter<CommonVerificationListAdapter.ViewHolder> {

    private List<CheckInfo> mData = new ArrayList<>();
    private Object mHandler;

    @Override
    public int getItemViewType(int position) {
        return CommonUtils.verifyInfoTypeToViewType(mData.get(position).getInfoType());
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CheckInfoListItemBinding binding;
        int subLayoutId;
        switch (viewType) {
            case Constant.VERIFICATION_VIEW_COUPON:
                subLayoutId = R.layout.check_info_list_item_sub_coupon;
                break;
            case Constant.VERIFICATION_VIEW_ORDER:
                subLayoutId = R.layout.check_info_list_item_sub_order;
                break;
            default:
                subLayoutId = R.layout.check_info_list_item_sub_default;
                break;
        }
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.check_info_list_item, parent, false);
        ViewDataBinding subBinding = DataBindingUtil.inflate(layoutInflater, subLayoutId, binding.layoutVerificationInfo, true);
        ViewHolder viewHolder = new ViewHolder(binding.getRoot());
        viewHolder.setBinding(binding);
        viewHolder.setSubBinding(subBinding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CheckInfo data = mData.get(position);
        ViewDataBinding binding = holder.getBinding();
        holder.getSubBinding().setVariable(BR.data, data);
        binding.setVariable(BR.data, data);
        binding.setVariable(BR.handler, mHandler);
        binding.executePendingBindings();
    }


    public void setData(List<CheckInfo> data) {
        mData = data;
        //处理数据
        Gson gson = new Gson();
        for (CheckInfo checkInfo : mData) {
            switch (CommonUtils.verifyInfoTypeToViewType(checkInfo.getInfoType())) {
                case Constant.VERIFICATION_VIEW_COUPON:
                    if (checkInfo.getInfo() instanceof String) {
                        checkInfo.setInfo(gson.fromJson((String) checkInfo.getInfo(), VerificationCouponDetailBean.class));
                    } else {
                        checkInfo.setInfo(gson.fromJson(gson.toJson(checkInfo.getInfo()), VerificationCouponDetailBean.class));
                    }
                    break;
                case Constant.VERIFICATION_VIEW_ORDER:
                    if (checkInfo.getInfo() instanceof String) {
                        checkInfo.setInfo(gson.fromJson((String) checkInfo.getInfo(), PayOrderDetailBean.class));
                    } else {
                        checkInfo.setInfo(gson.fromJson(gson.toJson(checkInfo.getInfo()), PayOrderDetailBean.class));
                    }
                    break;
                default:
                    break;
            }
        }

        notifyDataSetChanged();
    }

    public void setHandler(VerificationListListener handler) {
        mHandler = handler;
    }

    public List<CheckInfo> getDataList() {
        return mData;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private ViewDataBinding subBinding;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewDataBinding getBinding() {
            return binding;
        }

        public void setBinding(ViewDataBinding binding) {
            this.binding = binding;
        }

        public ViewDataBinding getSubBinding() {
            return subBinding;
        }

        public void setSubBinding(ViewDataBinding subBinding) {
            this.subBinding = subBinding;
        }
    }
}
