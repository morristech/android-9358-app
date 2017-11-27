package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CommissionNormalInfo;
import com.xmd.manager.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-24.
 */

public class CommissionNormalAdapter extends RecyclerView.Adapter<CommissionNormalAdapter.ViewHolder> {
    private Context mContext;
    private List<CommissionNormalInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public CommissionNormalAdapter(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<CommissionNormalInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_day, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommissionNormalInfo info = mData.get(position);
        holder.mDateText.setText(info.workDate);
        holder.mAmountText.setText("+" + Utils.moneyToStringEx(info.sumCommission));
        holder.itemView.setOnClickListener(v -> mCallBack.onItemClick(info.workDate));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_date)
        TextView mDateText;
        @BindView(R.id.tv_item_amount)
        TextView mAmountText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CallBack {
        void onItemClick(String date);
    }
}
