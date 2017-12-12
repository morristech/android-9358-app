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

public class ItemStatisticsCategoryAdapter extends RecyclerView.Adapter<ItemStatisticsCategoryAdapter.ViewHolder> {
    private int mStyle;
    private Context mContext;
    private List<ItemStatisticsInfo> mData = new ArrayList();

    public ItemStatisticsCategoryAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ItemStatisticsInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setStyle(int style) {
        mStyle = style;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statistics_category, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemStatisticsInfo itemStatisticsInfo = mData.get(position);
        holder.mCategoryName.setText(itemStatisticsInfo.categoryName);
        holder.mCategoryCount.setText(String.valueOf(itemStatisticsInfo.totalSum));
        holder.mCategoryAmount.setText("￥" + Utils.moneyToStringEx(itemStatisticsInfo.totalAmount));
        if (itemStatisticsInfo.list != null && !itemStatisticsInfo.list.isEmpty()) {
            ItemStatisticsItemAdapter mItemAdapter = new ItemStatisticsItemAdapter(mContext);
            mItemAdapter.setStyle(mStyle);
            holder.mCategoryList.setVisibility(View.VISIBLE);
            holder.mCategoryList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mCategoryList.setAdapter(mItemAdapter);
            mItemAdapter.setData(itemStatisticsInfo.list);
        } else {
            holder.mCategoryList.setVisibility(View.GONE);
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
        public TextView mCategoryName;
        public TextView mCategoryCount;
        public TextView mCategoryAmount;
        public RecyclerView mCategoryList;

        public ViewHolder(View itemView) {
            super(itemView);
            mCategoryName = (TextView) itemView.findViewById(R.id.tv_category_name);
            mCategoryCount = (TextView) itemView.findViewById(R.id.tv_category_count);
            mCategoryAmount = (TextView) itemView.findViewById(R.id.tv_category_amount);
            mCategoryList = (RecyclerView) itemView.findViewById(R.id.rv_category_items);
        }
    }
}
