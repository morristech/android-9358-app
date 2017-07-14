package com.xmd.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.ConsumeInfo;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-11-24.
 */
public class CouponUseDataListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;

    private List<ConsumeInfo> mData;

    public CouponUseDataListRecycleViewAdapter(List<ConsumeInfo> data) {
        mData = data;
    }

    public void setData(List<ConsumeInfo> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_use_list_item, parent, false);
        return new ConsumeListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        ConsumeListViewHolder itemHolder = (ConsumeListViewHolder) holder;
        ConsumeInfo consumeInfo = mData.get(position);
        itemHolder.phoneNumber.setText(consumeInfo.telephone);
        itemHolder.consumeDate.setText(consumeInfo.consumeDate);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ConsumeListViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.phone_number)
        TextView phoneNumber;
        @BindView(R.id.consume_date)
        TextView consumeDate;

        public ConsumeListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
