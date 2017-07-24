package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.MemberPlanInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-22.
 */

public class PlanItemAdapter extends RecyclerView.Adapter<PlanItemAdapter.ViewHolder> {
    private Context mContext;
    private List<MemberPlanInfo.PackageItem> mData = new ArrayList<>();

    public void setData(List<MemberPlanInfo.PackageItem> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public PlanItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_plan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(PlanItemAdapter.ViewHolder holder, int position) {
        MemberPlanInfo.PackageItem item = mData.get(position);
        //如果是现金
        if (item.type == 6) {
            holder.mContent.setText("现金" + Utils.moneyToString(item.oriAmount) + "元");
        } else {
            holder.mContent.setText(item.name + " * " + item.itemCount);
        }
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

        public ViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.tv_sub_item);
        }
    }
}
