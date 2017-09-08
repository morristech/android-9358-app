package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.GiftActivityInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-9-8.
 */

public class GiftActAdapter extends RecyclerView.Adapter<GiftActAdapter.ViewHolder> {
    private Context mContext;
    private List<GiftActivityInfo.GiftActivityPackage> mData = new ArrayList<>();

    public GiftActAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<GiftActivityInfo.GiftActivityPackage> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_act, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GiftActivityInfo.GiftActivityPackage activityPackage = mData.get(position);
        holder.mAmount.setText("满" + Utils.moneyToString(activityPackage.amount) + "元");
        if (activityPackage.packageItems != null && !activityPackage.packageItems.isEmpty()) {
            holder.mSongImg.setVisibility(View.VISIBLE);
            holder.mSubList.setVisibility(View.VISIBLE);
            GiftActSubAdapter subAdapter = new GiftActSubAdapter();
            subAdapter.setData(activityPackage.packageItems);
            holder.mSubList.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mSubList.setAdapter(subAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mAmount;
        public ImageView mSongImg;
        public RecyclerView mSubList;

        public ViewHolder(View itemView) {
            super(itemView);
            mAmount = (TextView) itemView.findViewById(R.id.tv_gift_act_amount);
            mSongImg = (ImageView) itemView.findViewById(R.id.img_gift_act_song);
            mSubList = (RecyclerView) itemView.findViewById(R.id.rv_gift_act_sub_list);
        }
    }
}
