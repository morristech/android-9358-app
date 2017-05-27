package com.xmd.cashier.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
    public static final int VIEW_TYPE_COUPON = 1;
    public static final int VIEW_TYPE_ORDER = 2;
    public static final int VIEW_TYPE_TREAT = 3;

    public VerificationListAdapter(VerificationContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = null;
        switch (viewType) {
            case VIEW_TYPE_COUPON:
            case VIEW_TYPE_ORDER:
                view = LayoutInflater.from(context).inflate(R.layout.item_coupon_trade_list, parent, false);
                break;
            case VIEW_TYPE_TREAT:
                view = LayoutInflater.from(context).inflate(R.layout.item_treat_status, parent, false);
                break;
        }
        return new ViewHolder(view, mPresenter);
    }

    @Override
    public int getItemViewType(int position) {
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
        private TextView mType;
        private TextView mInfo;
        private CheckBox mCheckBox;
        private VerificationContract.Presenter mPresenter;

        public ViewHolder(View itemView) {
            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public ViewHolder(View itemView, VerificationContract.Presenter presenter) {
            super(itemView);
            mPresenter = presenter;
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox);
        }

        public void bind(final VerificationItem item) {
            switch (item.type) {
                case AppConstants.TYPE_COUPON:
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
        }

        private void bindOrderInfo(VerificationItem item) {
            OrderInfo order = item.order;
            mName.setText(order.customerName);
            mName.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            mName.getPaint().setAntiAlias(true);
            mMoney.setText(Utils.moneyToString(order.downPayment));
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

        public void hideCheckBox() {
            mCheckBox.setVisibility(View.INVISIBLE);
        }
    }
}
