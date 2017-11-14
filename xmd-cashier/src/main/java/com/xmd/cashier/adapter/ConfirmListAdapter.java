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
import com.xmd.cashier.contract.ConfirmContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.Trade;
import com.xmd.cashier.dal.bean.VerificationItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by heyangya on 16-8-24.
 */

public class ConfirmListAdapter extends RecyclerView.Adapter<ConfirmListAdapter.ViewHolder> {
    private ConfirmContract.Presenter mPresenter;
    private Trade mTrade;
    private int mItemCount;
    private List<VerificationItem> mData = new ArrayList<>();

    public ConfirmListAdapter(ConfirmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = null;
        switch (viewType) {
            case AppConstants.VIEW_TYPE_COUPON:
            case AppConstants.VIEW_TYPE_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_coupon_status, parent, false);
                break;
            case AppConstants.VIEW_TYPE_TREAT:
                view = LayoutInflater.from(context).inflate(R.layout.item_treat_status, parent, false);
                break;
            case AppConstants.VIEW_TYPE_UNKNOWN:
            default:
                break;
        }
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public int getItemViewType(int position) {
        switch (mData.get(position).type) {
            case AppConstants.TYPE_COUPON:
            case AppConstants.TYPE_CASH_COUPON:
            case AppConstants.TYPE_DISCOUNT_COUPON:
            case AppConstants.TYPE_PAID_COUPON:
                return AppConstants.VIEW_TYPE_COUPON;
            case AppConstants.TYPE_ORDER:
                return AppConstants.VIEW_TYPE_ORDER;
            case AppConstants.TYPE_PAY_FOR_OTHER:
                return AppConstants.VIEW_TYPE_TREAT;
        }
        return AppConstants.VIEW_TYPE_UNKNOWN;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mItemCount;
    }

    public void setData(Trade trade) {
        mTrade = trade;
        mItemCount = 0;
        mData.clear();
        for (VerificationItem item : mTrade.getCouponList()) {
            if (item.selected) {
                mData.add(item);
            }
        }
        Collections.sort(mData, new Comparator<VerificationItem>() {
            @Override
            public int compare(VerificationItem lhs, VerificationItem rhs) {
                if (lhs.success) {
                    return 1;
                } else if (rhs.success) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        mItemCount += mData.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mMoney;
        private TextView mMoneyDesc;
        private TextView mType;
        private TextView mInfo;
        private TextView mStatus;
        private ConfirmContract.Presenter mPresenter;

        private ViewHolder(View itemView, ConfirmContract.Presenter presenter) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mMoneyDesc = (TextView) itemView.findViewById(R.id.tv_money_desc);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mStatus = (TextView) itemView.findViewById(R.id.tv_status);
            mPresenter = presenter;
        }

        private void bind(final VerificationItem item) {
            switch (item.type) {
                case AppConstants.TYPE_COUPON:
                case AppConstants.TYPE_CASH_COUPON:
                case AppConstants.TYPE_DISCOUNT_COUPON:
                case AppConstants.TYPE_PAID_COUPON:
                    CouponInfo couponInfo = item.couponInfo;
                    mName.setText(couponInfo.actTitle);
                    mType.setText(couponInfo.useTypeName);
                    mInfo.setText(couponInfo.consumeMoneyDescription);
                    mMoney.setText(Utils.moneyToString(couponInfo.getReallyCouponMoney()));
                    mMoneyDesc.setVisibility(AppConstants.TYPE_DISCOUNT_COUPON.equals(item.type) ? View.VISIBLE : View.GONE);
                    mStatus.setText(item.success ? "已核销" : item.errorMsg);
                    mName.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mType.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mInfo.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mMoney.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mStatus.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorRed));
                    break;
                case AppConstants.TYPE_ORDER:
                    mName.setText(item.order.customerName);
                    mType.setText("付费预约");
                    mInfo.setText("技师：" + item.order.techName);
                    mMoney.setText(Utils.moneyToString(item.order.downPayment));
                    mMoneyDesc.setVisibility(View.GONE);
                    mStatus.setText(item.success ? "已核销" : item.errorMsg);
                    mName.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mType.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mInfo.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mMoney.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mStatus.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorRed));
                    break;
                case AppConstants.TYPE_PAY_FOR_OTHER:
                    (itemView.findViewById(R.id.checkbox)).setVisibility(View.GONE);
                    mName.setText("朋友请客￥" + Utils.moneyToString(item.treatInfo.amount));
                    mName.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorText));
                    mStatus.setVisibility(View.VISIBLE);
                    mStatus.setText(item.success ? "已核销￥" + Utils.moneyToString(item.treatInfo.useMoney) : item.errorMsg);
                    mStatus.setTextColor(itemView.getContext().getResources().getColor(item.success ? R.color.colorDivide : R.color.colorRed));
                    break;
            }

            if (mPresenter != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPresenter.onVerificationItemClicked(item);
                    }
                });
            }
        }
    }
}
