package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.UiNavigation;
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
import java.util.Locale;

/**
 * Created by heyangya on 16-8-24.
 */

public class ConfirmListAdapter extends RecyclerView.Adapter<ConfirmListAdapter.ViewHolder> {
    private ConfirmContract.Presenter mPresenter;
    private Trade mTrade;
    private int mItemCount;
    private boolean mShowMemberPay;

    private List<VerificationItem> mData = new ArrayList<>();
    private final int VIEW_TYPE_COUPON = 1;
    private final int VIEW_TYPE_ORDER = 2;
    private final int VIEW_TYPE_TREAT = 3;
    private final int VIEW_TYPE_MEMBER_PAY = 4;

    public ConfirmListAdapter(ConfirmContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_COUPON:
            case VIEW_TYPE_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_coupon_status, parent, false);
                break;
            case VIEW_TYPE_TREAT:
                view = LayoutInflater.from(context).inflate(R.layout.item_treat_status, parent, false);
                break;
            case VIEW_TYPE_MEMBER_PAY:
                view = LayoutInflater.from(context).inflate(R.layout.item_member_pay_status, parent, false);
                break;
        }
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public int getItemViewType(int position) {
        if (mShowMemberPay) {
            if (position == 0) {
                //会员支付状态放在第一个
                return VIEW_TYPE_MEMBER_PAY;
            } else {
                position--;
            }
        }
        String type = mData.get(position).type;
        if (type.equals(AppConstants.TYPE_COUPON)) {
            return VIEW_TYPE_COUPON;
        } else if (type.equals(AppConstants.TYPE_ORDER)) {
            return VIEW_TYPE_ORDER;
        } else if (type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
            return VIEW_TYPE_TREAT;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mShowMemberPay) {
            if (position == 0) {
                holder.bindMemberPayInfo(mTrade);
                return;
            } else {
                position--;
            }
        }
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
        if (mTrade.currentCashier == AppConstants.CASHIER_TYPE_POS && mTrade.getMemberPaidMoney() > 0) {
            //已进行会员支付，要显示会员支付状态
            mItemCount++;
            mShowMemberPay = true;
        }
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
        private TextView mType;
        private TextView mInfo;
        private TextView mStatus;
        private TextView mRetry;
        private ConfirmContract.Presenter mPresenter;

        private ViewHolder(View itemView, ConfirmContract.Presenter presenter) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mStatus = (TextView) itemView.findViewById(R.id.tv_status);
            mRetry = (TextView) itemView.findViewById(R.id.tv_retry);
            mPresenter = presenter;
        }


        private void bind(final VerificationItem item) {
            switch (item.type) {
                case AppConstants.TYPE_COUPON:
                    CouponInfo couponInfo = item.couponInfo;
                    mName.setText(couponInfo.actTitle);
                    mType.setText(couponInfo.useTypeName);
                    mInfo.setText(couponInfo.consumeMoneyDescription);
                    mMoney.setText(Utils.moneyToString(couponInfo.getReallyCouponMoney()));
                    break;
                case AppConstants.TYPE_ORDER:
                    mName.setText(item.order.customerName);
                    mType.setText("付费预约");
                    mInfo.setText("技师：" + item.order.techName);
                    mMoney.setText(Utils.moneyToString(item.order.downPayment));
                    break;
                case AppConstants.TYPE_PAY_FOR_OTHER:
                    (itemView.findViewById(R.id.checkbox)).setVisibility(View.GONE);
                    mName.setText("朋友请客￥" + Utils.moneyToString(item.treatInfo.amount));
                    mStatus.setVisibility(View.VISIBLE);
                    break;
            }

            if (item.success) {
                if (item.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                    mStatus.setText("已核销￥" + Utils.moneyToString(item.treatInfo.useMoney));
                } else {
                    mStatus.setText("已核销");
                }
                mStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorDivide));
                mName.setTextColor(itemView.getContext().getResources().getColor(R.color.colorDivide));
                if (item.type.equals(AppConstants.TYPE_COUPON) || item.type.equals(AppConstants.TYPE_ORDER)) {
                    mType.setTextColor(itemView.getContext().getResources().getColor(R.color.colorDivide));
                    mInfo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorDivide));
                    mMoney.setTextColor(itemView.getContext().getResources().getColor(R.color.colorDivide));
                }

            } else {
                mStatus.setText(item.errorMsg);
                mStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorVerificationFailed));
                mName.setTextColor(itemView.getContext().getResources().getColor(R.color.colorText));
                if (item.type.equals(AppConstants.TYPE_COUPON) || item.type.equals(AppConstants.TYPE_ORDER)) {
                    mType.setTextColor(itemView.getContext().getResources().getColor(R.color.colorText));
                    mInfo.setTextColor(itemView.getContext().getResources().getColor(R.color.colorText));
                    mMoney.setTextColor(itemView.getContext().getResources().getColor(R.color.colorText));
                }
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

        private void bindMemberPayInfo(Trade trade) {
            mName.setText(String.format(Locale.getDefault(), "会员支付￥%s", Utils.moneyToString(trade.memberNeedPayMoney)));
            if (trade.getMemberPaidMoney() == trade.memberNeedPayMoney) {
                mStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorMemberPaySuccess));
                mStatus.setText("支付成功");
                mRetry.setVisibility(View.GONE);
            } else {
                mStatus.setTextColor(itemView.getContext().getResources().getColor(R.color.colorMemberPayFailed));
                mStatus.setText("支付失败：" + trade.memberPayError);
                mRetry.setVisibility(View.VISIBLE);
                mRetry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UiNavigation.gotoMemberPayActivity(itemView.getContext());
                    }
                });
            }
        }
    }
}
