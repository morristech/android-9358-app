package com.xmd.cashier.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.contract.VerificationContract;
import com.xmd.cashier.dal.bean.CouponInfo;
import com.xmd.cashier.dal.bean.OrderInfo;
import com.xmd.cashier.dal.bean.VerificationItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by heyangya on 16-8-24.
 */

public class VerificationListAdapter extends RecyclerView.Adapter<VerificationListAdapter.ViewHolder> {
    private VerificationContract.Presenter mPresenter;
    private List<VerificationItem> mData = new ArrayList<>();

    public VerificationListAdapter(VerificationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = null;
        switch (viewType) {
            case AppConstants.VIEW_TYPE_COUPON:
            case AppConstants.VIEW_TYPE_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_coupon_trade_list, parent, false);
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
        return mData.size();
    }

    public void setData(List<VerificationItem> data) {
        mData = data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mName;
        private TextView mMoney;
        private TextView mMoneyDesc;
        private TextView mType;
        private TextView mInfo;
        private CheckBox mCheckBox;
        private VerificationContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mMoneyDesc = (TextView) itemView.findViewById(R.id.tv_money_desc);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public ViewHolder(View itemView, VerificationContract.Presenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mMoneyDesc = (TextView) itemView.findViewById(R.id.tv_money_desc);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public void bind(final VerificationItem item) {
            switch (item.type) {
                case AppConstants.TYPE_COUPON:
                case AppConstants.TYPE_CASH_COUPON:
                case AppConstants.TYPE_DISCOUNT_COUPON:
                case AppConstants.TYPE_PAID_COUPON:
                    bindCouponInfo(item);
                    break;
                case AppConstants.TYPE_ORDER:
                    bindOrderInfo(item);
                    break;
                case AppConstants.TYPE_PAY_FOR_OTHER:
                    bindTreatInfo(item);
                    break;
            }

            if (mPresenter != null) {
                mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mCheckBox.requestFocus();
                        updateCheckBox(isChecked);
                        mPresenter.onVerificationItemChecked(item, isChecked);
                        if (AppConstants.TYPE_DISCOUNT_COUPON.equals(item.type)) {
                            // 折扣券
                            mMoney.setText(Utils.moneyToString(item.couponInfo.getReallyCouponMoney()));
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCheckBox.performClick();
                    }
                });


                mName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.type.equals(AppConstants.TYPE_PAY_FOR_OTHER)) {
                            mCheckBox.performClick();
                        } else {
                            mPresenter.onVerificationItemClicked(item);
                        }
                    }
                });
            }

            mCheckBox.setChecked(item.selected);
        }

        private void bindCouponInfo(final VerificationItem info) {
            CouponInfo couponInfo = info.couponInfo;
            mName.setText(couponInfo.actTitle);
            mName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            mName.getPaint().setAntiAlias(true);
            mType.setText(couponInfo.useTypeName);
            mInfo.setText(couponInfo.consumeMoneyDescription);
            mMoney.setText(Utils.moneyToString(couponInfo.getReallyCouponMoney()));
            mMoneyDesc.setVisibility(AppConstants.TYPE_DISCOUNT_COUPON.equals(info.type) ? View.VISIBLE : View.GONE);
        }

        private void bindOrderInfo(VerificationItem item) {
            OrderInfo order = item.order;
            mName.setText(order.customerName);
            mName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            mName.getPaint().setAntiAlias(true);
            mMoneyDesc.setVisibility(View.GONE);
            mMoney.setText(Utils.moneyToString(order.downPayment));
            mMoney.setTextColor(itemView.getContext().getResources().getColor(R.color.colorText4));
            mInfo.setText("技师：" + order.techName);
            mType.setText("付费预约");
        }

        private void bindTreatInfo(VerificationItem item) {
            mName.setText("朋友请客￥" + Utils.moneyToString(item.treatInfo.amount));
        }

        private void updateCheckBox(boolean isChecked) {
            Drawable drawable;
            if (isChecked) {
                mCheckBox.setHint("已选择");
                drawable = itemView.getContext().getResources().getDrawable(R.drawable.ic_check);
            } else {
                mCheckBox.setHint("未选择");
                drawable = itemView.getContext().getResources().getDrawable(R.drawable.ic_uncheck);
            }
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            mCheckBox.setCompoundDrawables(null, null, drawable, null);
        }
    }
}
