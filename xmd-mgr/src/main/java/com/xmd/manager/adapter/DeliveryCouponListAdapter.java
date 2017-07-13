package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 15-12-11.
 */
public class DeliveryCouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface CallBackListener {
        void onSelectedItemClicked(CouponInfo couponInfo, Integer position, boolean isSelected);
    }

    private List<CouponInfo> mCouponInfoList;
    private CallBackListener mCallBackListener;
    private Context mContext;


    public DeliveryCouponListAdapter(Context context, List<CouponInfo> couponInfoList, CallBackListener onCallClickListener) {
        mContext = context;
        mCouponInfoList = couponInfoList;
        mCallBackListener = onCallClickListener;
        notifyDataSetChanged();
    }

    public void setData(List<CouponInfo> couponInfoList) {
        mCouponInfoList = couponInfoList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_coupon_list_item, parent, false);
        return new DeliveryCouponItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeliveryCouponItemViewHolder) {
            final CouponInfo couponInfo = mCouponInfoList.get(position);
            DeliveryCouponItemViewHolder itemHolder = (DeliveryCouponItemViewHolder) holder;

            itemHolder.mCouponName.setText(couponInfo.actTitle);
            itemHolder.mCouponType.setText(couponInfo.useTypeName);
            itemHolder.mCouponDesc.setText(couponInfo.consumeMoneyDescription);
            itemHolder.mCouponDuration.setText(couponInfo.couponPeriod);
            if (couponInfo.isSelected == 1) {
                itemHolder.mImgCouponDelected.setSelected(false);
            } else {
                itemHolder.mImgCouponDelected.setSelected(true);
            }

            itemHolder.itemView.setOnClickListener(v -> {
                if (couponInfo.isSelected == 1) {
                    itemHolder.mImgCouponDelected.setSelected(true);
                    mCallBackListener.onSelectedItemClicked(couponInfo, position, false);
                } else if (couponInfo.isSelected == 2) {
                    itemHolder.mImgCouponDelected.setSelected(false);
                    mCallBackListener.onSelectedItemClicked(couponInfo, position, true);
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mCouponInfoList.size();
    }

    static class DeliveryCouponItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.coupon_name)
        TextView mCouponName;
        @BindView(R.id.coupon_type)
        TextView mCouponType;
        @BindView(R.id.coupon_desc)
        TextView mCouponDesc;
        @BindView(R.id.coupon_duration)
        TextView mCouponDuration;
        @BindView(R.id.img_coupon_selected)
        ImageView mImgCouponDelected;

        DeliveryCouponItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
