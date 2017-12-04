package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OfflineStatisticInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-4.
 */

public class StatisticsNativeAdapter extends RecyclerView.Adapter<StatisticsNativeAdapter.ItemViewHolder> {
    private Context mContext;
    private List<OfflineStatisticInfo.OfflineNativeInfo> mData = new ArrayList<>();

    public StatisticsNativeAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<OfflineStatisticInfo.OfflineNativeInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistic_native, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        OfflineStatisticInfo.OfflineNativeInfo offlineNativeInfo = mData.get(position);
        holder.mChannelName.setText(offlineNativeInfo.name + "(记账)：");
        String temp = ((offlineNativeInfo.amount < 0) ? "-￥" : "￥") + Utils.moneyToStringEx(Math.abs(offlineNativeInfo.amount));
        holder.mChannelAmount.setText(temp);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public TextView mChannelName;
        public TextView mChannelAmount;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mChannelName = (TextView) itemView.findViewById(R.id.tv_channel_name);
            mChannelAmount = (TextView) itemView.findViewById(R.id.tv_channel_amount);
        }
    }
}
