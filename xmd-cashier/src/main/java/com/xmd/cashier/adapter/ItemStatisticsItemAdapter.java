package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.ItemStatisticsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-12.
 */

public class ItemStatisticsItemAdapter extends RecyclerView.Adapter<ItemStatisticsItemAdapter.ViewHolder> {
    public static final int STYLE_SUMMARY = 0;
    public static final int STYLE_DETAIL = 1;

    private Context mContext;
    private List<ItemStatisticsInfo.CategoryItem> mData = new ArrayList<>();
    private int mStyle;

    public ItemStatisticsItemAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ItemStatisticsInfo.CategoryItem> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_category_items, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemStatisticsInfo.CategoryItem categoryItem = mData.get(position);
        holder.mItemName.setText(categoryItem.name);
        holder.mItemCount.setText(String.valueOf(categoryItem.sum));
        holder.mItemAmount.setText("￥" + Utils.moneyToStringEx(categoryItem.amount));
        switch (mStyle) {
            case STYLE_DETAIL:
                holder.mBellList.setVisibility(View.VISIBLE);
                if (categoryItem.bellList != null && !categoryItem.bellList.isEmpty()) {
                    ItemStatisticsBellAdapter itemStatisticsBellAdapter = new ItemStatisticsBellAdapter(mContext);
                    holder.mBellList.setLayoutManager(new LinearLayoutManager(mContext));
                    holder.mBellList.setAdapter(itemStatisticsBellAdapter);
                    itemStatisticsBellAdapter.setData(categoryItem.bellList);
                } else {
                    holder.mBellList.setVisibility(View.GONE);
                }
                break;
            case STYLE_SUMMARY:
                holder.mBellList.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mItemName;
        public TextView mItemCount;
        public TextView mItemAmount;
        public RecyclerView mBellList;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemName = (TextView) itemView.findViewById(R.id.tv_item_name);
            mItemCount = (TextView) itemView.findViewById(R.id.tv_item_count);
            mItemAmount = (TextView) itemView.findViewById(R.id.tv_item_amount);
            mBellList = (RecyclerView) itemView.findViewById(R.id.rv_item_bells);
        }
    }
}
