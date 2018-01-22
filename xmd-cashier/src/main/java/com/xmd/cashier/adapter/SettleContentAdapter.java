package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.SettleContentInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 18-1-16.
 */

public class SettleContentAdapter extends RecyclerView.Adapter<SettleContentAdapter.ViewHolder> {
    private Context mContext;
    private List<SettleContentInfo> mData = new ArrayList<>();

    public SettleContentAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<SettleContentInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_content, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettleContentInfo contentInfo = mData.get(position);
        holder.mName.setText("收款人员：" + contentInfo.operatorName);
        if (contentInfo.businessList != null && !contentInfo.businessList.isEmpty()) {
            holder.mName.setVisibility(View.VISIBLE);
            SettleBusinessAdapter businessAdapter = new SettleBusinessAdapter(mContext);
            holder.mContentList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mContentList.setAdapter(businessAdapter);
            businessAdapter.setData(contentInfo.businessList);
        } else {
            holder.mName.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mName;
        public RecyclerView mContentList;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_cashier_name);
            mContentList = (RecyclerView) itemView.findViewById(R.id.rv_content_list);
        }
    }
}
