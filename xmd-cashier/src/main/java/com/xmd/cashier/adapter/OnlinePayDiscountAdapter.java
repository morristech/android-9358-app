package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OnlinePayInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-27.
 */

public class OnlinePayDiscountAdapter extends RecyclerView.Adapter<OnlinePayDiscountAdapter.ViewHolder> {
    private Context mContext;
    private List<OnlinePayInfo.OnlinePayDiscountInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<OnlinePayInfo.OnlinePayDiscountInfo> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_pay_discount, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final OnlinePayInfo.OnlinePayDiscountInfo info = mData.get(position);
        if (info.type != null) {
            switch (info.type) {
                case AppConstants.PAY_DISCOUNT_COUPON:
                    holder.mDiscountType.setText("用券抵扣");
                    holder.mDiscountCoupon.setVisibility(View.VISIBLE);
                    holder.mDiscountCoupon.setText(info.bizName);
                    break;
                case AppConstants.PAY_DISCOUNT_ORDER:
                    holder.mDiscountType.setText("预约抵扣");
                    holder.mDiscountCoupon.setVisibility(View.GONE);
                    break;
                case AppConstants.PAY_DISCOUNT_MEMBER:
                    holder.mDiscountType.setText("会员优惠");
                    holder.mDiscountCoupon.setVisibility(View.GONE);
                    break;
                default:
                    holder.mDiscountType.setText("其他抵扣");
                    holder.mDiscountCoupon.setVisibility(View.GONE);
                    break;
            }
            holder.mDiscountAmount.setText("-￥" + Utils.moneyToStringEx(info.amount));
            holder.mDiscountCoupon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClick(info);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mDiscountType;
        public TextView mDiscountCoupon;
        public TextView mDiscountAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            mDiscountType = (TextView) itemView.findViewById(R.id.item_discount_type);
            mDiscountCoupon = (TextView) itemView.findViewById(R.id.item_discount_coupon);
            mDiscountAmount = (TextView) itemView.findViewById(R.id.item_discount_money);
        }
    }

    public interface CallBack {
        void onItemClick(OnlinePayInfo.OnlinePayDiscountInfo info);
    }
}
