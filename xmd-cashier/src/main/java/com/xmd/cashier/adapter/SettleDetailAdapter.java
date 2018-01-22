package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 18-1-16.
 */

public class SettleDetailAdapter extends RecyclerView.Adapter<SettleDetailAdapter.ViewHolder> {
    private Context mContext;
    private List<SettleDetailInfo> mData = new ArrayList<>();

    public SettleDetailAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<SettleDetailInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettleDetailInfo detailInfo = mData.get(position);
        holder.mName.setText(detailInfo.name);
        holder.mCount.setText("(" + detailInfo.count + "笔)");
        holder.mAmount.setText(String.format(ResourceUtils.getString(R.string.cashier_money), Utils.moneyToStringEx(detailInfo.amount)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mCount;
        private TextView mAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mCount = (TextView) itemView.findViewById(R.id.tv_count);
            mAmount = (TextView) itemView.findViewById(R.id.tv_amount);
        }
    }
}
