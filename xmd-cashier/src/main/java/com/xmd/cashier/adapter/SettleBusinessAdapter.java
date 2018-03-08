package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleBusinessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 18-1-16.
 */

public class SettleBusinessAdapter extends RecyclerView.Adapter<SettleBusinessAdapter.ViewHolder> {
    private Context mContext;
    private List<SettleBusinessInfo> mData = new ArrayList<>();

    public SettleBusinessAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<SettleBusinessInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_business, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettleBusinessInfo businessInfo = mData.get(position);
        holder.mTotalName.setText(businessInfo.businessName);
        holder.mTotalCount.setText("(" + businessInfo.count + "笔)");
        holder.mTotalAmount.setText((businessInfo.amount < 0 ? "-" : "") + String.format(ResourceUtils.getString(R.string.cashier_money), Utils.moneyToStringEx(Math.abs(businessInfo.amount))));
        if (businessInfo.detailList != null && !businessInfo.detailList.isEmpty()) {
            holder.mTotalList.setVisibility(View.VISIBLE);
            SettleDetailAdapter detailAdapter = new SettleDetailAdapter(mContext);
            holder.mTotalList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mTotalList.setAdapter(detailAdapter);
            detailAdapter.setData(businessInfo.detailList);
        } else {
            holder.mTotalList.setVisibility(View.GONE);
        }

        if (businessInfo.remarkList != null && !businessInfo.remarkList.isEmpty()) {
            holder.mRemarkLayout.setVisibility(View.VISIBLE);
            SettleDetailAdapter detailAdapter = new SettleDetailAdapter(mContext);
            holder.mRemarkList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mRemarkList.setAdapter(detailAdapter);
            detailAdapter.setData(businessInfo.remarkList);
        } else {
            holder.mRemarkLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTotalName;
        public TextView mTotalCount;
        public TextView mTotalAmount;
        public RecyclerView mTotalList;

        public LinearLayout mRemarkLayout;
        public RecyclerView mRemarkList;

        public ViewHolder(View itemView) {
            super(itemView);
            mTotalName = (TextView) itemView.findViewById(R.id.tv_total_name);
            mTotalCount = (TextView) itemView.findViewById(R.id.tv_total_count);
            mTotalAmount = (TextView) itemView.findViewById(R.id.tv_total_amount);
            mTotalList = (RecyclerView) itemView.findViewById(R.id.rv_total_list);

            mRemarkLayout = (LinearLayout) itemView.findViewById(R.id.ll_remark);
            mRemarkList = (RecyclerView) itemView.findViewById(R.id.rv_remark_list);
        }
    }
}
