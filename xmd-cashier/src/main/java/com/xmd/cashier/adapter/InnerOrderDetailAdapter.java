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
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.InnerOrderItemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-7.
 */

public class InnerOrderDetailAdapter extends RecyclerView.Adapter<InnerOrderDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerOrderItemInfo> mData = new ArrayList<>();

    public void setData(List<InnerOrderItemInfo> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_order_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InnerOrderItemInfo info = mData.get(position);
        holder.mItemNameText.setText(info.itemName + " * " + info.itemCount);
        holder.mItemAmountText.setText("￥" + Utils.moneyToStringEx(info.itemAmount));
        switch (info.itemType) {
            case AppConstants.INNER_ORDER_ITEM_TYPE_SPA:
                holder.mItemInfoLayout.setVisibility(View.VISIBLE);
                holder.mItemTechText.setText("[技师]：" + info.employeeNo);
                holder.mItemTypeText.setText(info.itemBellName);
                break;
            case AppConstants.INNER_ORDER_ITEM_TYPE_GOODS:
            default:
                holder.mItemInfoLayout.setVisibility(View.GONE);
                break;
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
        public TextView mItemNameText;
        public TextView mItemTechText;
        public TextView mItemTypeText;
        public TextView mItemAmountText;
        public LinearLayout mItemInfoLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemNameText = (TextView) itemView.findViewById(R.id.tv_item_name);
            mItemTechText = (TextView) itemView.findViewById(R.id.tv_item_tech);
            mItemTypeText = (TextView) itemView.findViewById(R.id.tv_item_type);
            mItemAmountText = (TextView) itemView.findViewById(R.id.tv_item_amount);
            mItemInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_item_info);
        }
    }
}