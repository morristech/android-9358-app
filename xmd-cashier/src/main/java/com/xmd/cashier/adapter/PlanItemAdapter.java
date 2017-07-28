package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.PackagePlanItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-22.
 */

public class PlanItemAdapter extends RecyclerView.Adapter<PlanItemAdapter.ViewHolder> {
    private Context mContext;
    private List<PackagePlanItem.PackageItem> mData = new ArrayList<>();
    private CallBack mCallBack;

    public void setData(List<PackagePlanItem.PackageItem> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    @Override
    public PlanItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_plan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlanItemAdapter.ViewHolder holder, int position) {
        PackagePlanItem.PackageItem item = mData.get(position);
        switch (item.type) {
            case AppConstants.MEMBER_PLAN_ITEM_TYPE_CREDIT:
                // 积分
                String credit = item.name + "积分";
                holder.mContent.setText(credit);
                holder.mPlus.setVisibility(View.GONE);
                holder.mCount.setVisibility(View.GONE);
                break;
            case AppConstants.MEMBER_PLAN_ITEM_TYPE_MONEY:
                // 现金
                String money = "现金" + item.name + "元";
                holder.mContent.setText(money);
                holder.mPlus.setVisibility(View.GONE);
                holder.mCount.setVisibility(View.GONE);
                break;
            default:
                holder.mContent.setText(item.name);
                holder.mPlus.setVisibility(View.VISIBLE);
                holder.mCount.setVisibility(View.VISIBLE);
                holder.mCount.setText(String.valueOf(item.itemCount));
                break;
        }
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onPlanItemClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mContent;
        public TextView mCount;
        public LinearLayout mLayout;
        public TextView mPlus;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.ll_sub_layout);
            mContent = (TextView) itemView.findViewById(R.id.tv_sub_item);
            mCount = (TextView) itemView.findViewById(R.id.tv_sub_item_count);
            mPlus = (TextView) itemView.findViewById(R.id.tv_sub_item_plus);
        }
    }

    public interface CallBack {
        void onPlanItemClick();
    }
}
