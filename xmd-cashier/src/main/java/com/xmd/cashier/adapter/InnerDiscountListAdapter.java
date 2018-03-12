package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.VerificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-13.
 */

public class InnerDiscountListAdapter extends RecyclerView.Adapter<InnerDiscountListAdapter.ViewHolder> {
    private Context mContext;
    private List<VerificationItem> mData = new ArrayList<>();
    private OnClickListener mListener;

    public InnerDiscountListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_discount, parent, false));
    }

    public void setListener(OnClickListener listener) {
        mListener = listener;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final VerificationItem item = mData.get(position);
        holder.mSelect.setImageResource(item.selected ? R.drawable.ic_discount_select : R.drawable.ic_discount_unselect);
        switch (item.type) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
            case AppConstants.TYPE_PAID_COUPON:
            case AppConstants.TYPE_SERVICE_ITEM_COUPON:
                CouponInfo couponInfo = item.couponInfo;
                holder.mName.setText(couponInfo.actTitle);
                holder.mType.setText(couponInfo.couponTypeName);
                holder.mInfo.setText(couponInfo.consumeMoneyDescription);
                holder.mMoney.setText(Utils.moneyToString(couponInfo.getReallyCouponMoney()));
                holder.mTime.setText(Utils.getTimePeriodDes(couponInfo.useTimePeriod));
                break;
            case AppConstants.TYPE_ORDER:
                OrderInfo order = item.order;
                holder.mName.setText(order.customerName);
                holder.mMoney.setText(Utils.moneyToString(order.downPayment));
                holder.mInfo.setText("预约技师：" + (TextUtils.isEmpty(order.techName) ? "未指定" : order.techName));
                holder.mType.setText("付费预约");
                holder.mTime.setText("预约时间：" + order.appointTime);
                break;
            case AppConstants.TYPE_PAY_FOR_OTHER:
                holder.mMoney.setText(Utils.moneyToString(item.treatInfo.amount));
                holder.mType.setText("会员请客");
                holder.mInfo.setText("朋友请客￥" + Utils.moneyToString(item.treatInfo.amount));
                break;
        }
        holder.mSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemSelect(item, position);
            }
        });

        holder.mLeftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                    mListener.onItemSelect(item, position);
                } else {
                    mListener.onItemClick(item, position);
                }
            }
        });

        holder.mRightLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                    mListener.onItemSelect(item, position);
                } else {
                    mListener.onItemClick(item, position);
                }
            }
        });
    }

    public void setData(List<VerificationItem> data) {
        mData = data;
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mMoney;
        private TextView mInfo;
        private TextView mType;
        private TextView mTime;
        private ImageView mSelect;
        private LinearLayout mLeftLayout;
        private RelativeLayout mRightLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            mSelect = (ImageView) itemView.findViewById(R.id.img_select);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mTime = (TextView) itemView.findViewById(R.id.tv_time);
            mLeftLayout = (LinearLayout) itemView.findViewById(R.id.layout_left);
            mRightLayout = (RelativeLayout) itemView.findViewById(R.id.layout_right);
        }
    }

    public interface OnClickListener {
        void onItemSelect(VerificationItem item, int position);

        void onItemClick(VerificationItem item, int position);
    }
}
