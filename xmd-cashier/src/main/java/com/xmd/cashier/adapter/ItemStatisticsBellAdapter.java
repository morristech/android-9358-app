package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.ItemStatisticsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-12.
 */

public class ItemStatisticsBellAdapter extends RecyclerView.Adapter<ItemStatisticsBellAdapter.ViewHolder> {
    private Context mContext;
    private List<ItemStatisticsInfo.CategoryItemBell> mData = new ArrayList<>();

    public ItemStatisticsBellAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ItemStatisticsInfo.CategoryItemBell> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_category_item_bells, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemStatisticsInfo.CategoryItemBell categoryItemBell = mData.get(position);
        holder.mBellName.setText(categoryItemBell.bellName);
        holder.mBellCount.setText(String.valueOf(categoryItemBell.bellCount));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mBellName;
        public TextView mBellCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mBellName = (TextView) itemView.findViewById(R.id.tv_bell_name);
            mBellCount = (TextView) itemView.findViewById(R.id.tv_bell_count);
        }
    }
}
