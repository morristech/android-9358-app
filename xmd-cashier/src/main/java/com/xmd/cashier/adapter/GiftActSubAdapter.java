package com.xmd.cashier.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.GiftActivityInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-9-8.
 */

public class GiftActSubAdapter extends RecyclerView.Adapter<GiftActSubAdapter.ViewHolder> {
    private List<GiftActivityInfo.GiftActivityPackageItem> mData = new ArrayList<>();

    public void setData(List<GiftActivityInfo.GiftActivityPackageItem> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_act_sub, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        GiftActivityInfo.GiftActivityPackageItem activityPackageItem = mData.get(position);
        String content = null;
        switch (activityPackageItem.type) {
            case AppConstants.ITEM_TYPE_CREDIT:
                content = activityPackageItem.count + activityPackageItem.itemName;
                break;
            case AppConstants.ITEM_TYPE_GIF:
            case AppConstants.ITEM_TYPE_COUPON:
            case AppConstants.ITEM_TYPE_SERVICE:
                content = activityPackageItem.itemName + " * " + activityPackageItem.count;
                break;
            default:
                break;
        }
        holder.mContent.setText(content);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mContent;

        public ViewHolder(View itemView) {
            super(itemView);
            mContent = (TextView) itemView.findViewById(R.id.tv_gift_act_sub_content);
        }
    }
}
