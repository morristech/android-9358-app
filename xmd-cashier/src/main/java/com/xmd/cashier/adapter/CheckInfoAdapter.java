package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.CheckInfo;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-5-19.
 */

public class CheckInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM_COUPON = 1;
    private static final int TYPE_ITEM_ORDER = 2;
    private static final int TYPE_ITEM_UNKNOW = 3;

    private List<CheckInfo> mData = new ArrayList<>();
    private OnCheckItemCallBack mCallBack;
    private Context mContext;

    public CheckInfoAdapter(Context context) {
        mContext = context;
    }

    public interface OnCheckItemCallBack {
        void onInfoClick(CheckInfo info);

        void onInfoSelect(CheckInfo info, boolean selected);

        void onInfoSelectValid(CheckInfo info);
    }

    public void setCallBack(OnCheckItemCallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<CheckInfo> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void clearData() {
        if (mData != null) {
            mData.clear();
        }
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).getType()) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_PAID_COUPON:
            case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                return TYPE_ITEM_COUPON;
            case AppConstants.TYPE_ORDER:
                return TYPE_ITEM_ORDER;
            default:
                return TYPE_ITEM_UNKNOW;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM_COUPON:
                return new CouponViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_coupon, parent, false));
            case TYPE_ITEM_ORDER:
                return new OrderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_order, parent, false));
            case TYPE_ITEM_UNKNOW:
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final CheckInfo info = mData.get(position);
        if (holder instanceof CouponViewHolder) {
            CouponInfo coupon = (CouponInfo) info.getInfo();
            final CouponViewHolder couponViewHolder = (CouponViewHolder) holder;
            couponViewHolder.mCouponName.setText(info.getTitle());
            couponViewHolder.mCouponTypeName.setText(info.getTypeName());
            couponViewHolder.mCouponDescription.setText(coupon.consumeMoneyDescription);
            if (info.getValid()) {
                couponViewHolder.mCouponStatus.setText("可用");
                couponViewHolder.mCouponStatus.setTextColor(mContext.getResources().getColor(R.color.colorText4));
            } else {
                couponViewHolder.mCouponStatus.setText("不可用");
                couponViewHolder.mCouponStatus.setTextColor(mContext.getResources().getColor(R.color.colorPink));
            }
            couponViewHolder.mCouponEnableTime.setText(coupon.couponPeriod);
            couponViewHolder.mCouponUseTime.setText(Utils.getTimePeriodDes(coupon.useTimePeriod));
            couponViewHolder.mCouponInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onInfoClick(info);
                }
            });

            couponViewHolder.mSelect.setImageResource(info.getSelected() ? R.drawable.ic_item_selected : R.drawable.ic_item_unselected);
            couponViewHolder.mSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!info.getValid()) {
                        mCallBack.onInfoSelectValid(info);
                        return;
                    }
                    couponViewHolder.mSelect.setImageResource(info.getSelected() ? R.drawable.ic_item_unselected : R.drawable.ic_item_selected);
                    mCallBack.onInfoSelect(info, !info.getSelected());
                }
            });
        } else if (holder instanceof OrderViewHolder) {
            OrderInfo order = (OrderInfo) info.getInfo();
            final OrderViewHolder orderViewHolder = (OrderViewHolder) holder;
            orderViewHolder.mOrderName.setText(info.getTitle());
            orderViewHolder.mOrderTypeName.setText(info.getTypeName());
            orderViewHolder.mOrderDownPay.setText("预约定金" + Utils.moneyToStringEx(order.downPayment) + "元");
            if (info.getValid()) {
                orderViewHolder.mOrderStatus.setText("可用");
                orderViewHolder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorText4));
            } else {
                orderViewHolder.mOrderStatus.setText("不可用");
                orderViewHolder.mOrderStatus.setTextColor(mContext.getResources().getColor(R.color.colorPink));
            }
            orderViewHolder.mOrderArriveTime.setText(order.appointTime);
            orderViewHolder.mOrderTechName.setText(order.techName + (TextUtils.isEmpty(order.techNo) ? "" : "[" + order.techNo + "]"));
            orderViewHolder.mOrderInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onInfoClick(info);
                }
            });
            orderViewHolder.mSelect.setImageResource(info.getSelected() ? R.drawable.ic_item_selected : R.drawable.ic_item_unselected);
            orderViewHolder.mSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!info.getValid()) {
                        mCallBack.onInfoSelectValid(info);
                        return;
                    }
                    orderViewHolder.mSelect.setImageResource(info.getSelected() ? R.drawable.ic_item_unselected : R.drawable.ic_item_selected);
                    mCallBack.onInfoSelect(info, !info.getSelected());
                }
            });
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        private ImageView mSelect;
        private LinearLayout mCouponInfoLayout;
        private TextView mCouponName;
        private TextView mCouponTypeName;
        private TextView mCouponDescription;
        private TextView mCouponStatus;
        private TextView mCouponEnableTime;
        private TextView mCouponUseTime;

        public CouponViewHolder(View itemView) {
            super(itemView);
            mSelect = (ImageView) itemView.findViewById(R.id.img_check_info_select);
            mCouponInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_coupon_info);
            mCouponName = (TextView) itemView.findViewById(R.id.tv_coupon_name);
            mCouponTypeName = (TextView) itemView.findViewById(R.id.tv_coupon_type_name);
            mCouponDescription = (TextView) itemView.findViewById(R.id.tv_coupon_description);
            mCouponStatus = (TextView) itemView.findViewById(R.id.tv_coupon_status);
            mCouponEnableTime = (TextView) itemView.findViewById(R.id.tv_coupon_enable_time);
            mCouponUseTime = (TextView) itemView.findViewById(R.id.tv_coupon_use_time);
        }
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        private ImageView mSelect;
        private LinearLayout mOrderInfoLayout;
        private TextView mOrderName;
        private TextView mOrderTypeName;
        private TextView mOrderDownPay;
        private TextView mOrderStatus;
        private TextView mOrderArriveTime;
        private TextView mOrderTechName;

        public OrderViewHolder(View itemView) {
            super(itemView);
            mSelect = (ImageView) itemView.findViewById(R.id.img_check_info_select);
            mOrderInfoLayout = (LinearLayout) itemView.findViewById(R.id.layout_order_info);
            mOrderName = (TextView) itemView.findViewById(R.id.tv_order_customer_name);
            mOrderTypeName = (TextView) itemView.findViewById(R.id.tv_order_type);
            mOrderDownPay = (TextView) itemView.findViewById(R.id.tv_order_down_pay);
            mOrderStatus = (TextView) itemView.findViewById(R.id.tv_order_status);
            mOrderArriveTime = (TextView) itemView.findViewById(R.id.tv_order_arrive_time);
            mOrderTechName = (TextView) itemView.findViewById(R.id.tv_order_tech_name);
        }
    }
}
