package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleSummaryInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleSummaryAdapter extends RecyclerView.Adapter<SettleSummaryAdapter.ViewHolder> {
    private Context mContext;
    private List<SettleSummaryInfo> mData = new ArrayList<>();

    public SettleSummaryAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<SettleSummaryInfo> list) {
        mData.addAll(list);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_sub_summary, null, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SettleSummaryInfo info = mData.get(position);
        holder.mCashierName.setText(info.cashierName);
        holder.mOriginTotal.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.orderTotalMoney)));

        holder.mCutTotal.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.deductTotalMoney)));
        holder.mCutCouponDiscount.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.preferentialDeduct)));
        holder.mCutUserDiscount.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.manualDeduct)));
        holder.mCutMemberDiscount.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.discountDeduct)));

        holder.mRefundTotal.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.refundTotalMoney)));
        holder.mRefundBank.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.bankCardRefund)));
        holder.mRefundCash.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.moneyRefund)));
        holder.mRefundWeiXin.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.wechatRefund)));
        holder.mRefundMember.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayRefund)));

        holder.mPayTotal.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.incomeTotalMoney)));
        holder.mPayBank.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.bankCardIncome)));
        holder.mPayCash.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.moneyIncome)));
        holder.mPayWeiXin.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.wechatIncome)));
        holder.mPayMember.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayIncome)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mCashierName;
        public TextView mOriginTotal;

        public TextView mCutTotal;
        public TextView mCutCouponDiscount;
        public TextView mCutUserDiscount;
        public TextView mCutMemberDiscount;

        public TextView mRefundTotal;
        public TextView mRefundBank;
        public TextView mRefundCash;
        public TextView mRefundWeiXin;
        public TextView mRefundMember;

        public TextView mPayTotal;
        public TextView mPayBank;
        public TextView mPayCash;
        public TextView mPayWeiXin;
        public TextView mPayMember;

        public ViewHolder(View itemView) {
            super(itemView);
            mCashierName = (TextView) itemView.findViewById(R.id.tv_settle_cashier);
            mOriginTotal = (TextView) itemView.findViewById(R.id.settle_origin_total);

            mCutTotal = (TextView) itemView.findViewById(R.id.settle_cut_total);
            mCutCouponDiscount = (TextView) itemView.findViewById(R.id.settle_cut_coupon_discount);
            mCutUserDiscount = (TextView) itemView.findViewById(R.id.settle_cut_user_discount);
            mCutMemberDiscount = (TextView) itemView.findViewById(R.id.settle_cut_member_discount);

            mRefundTotal = (TextView) itemView.findViewById(R.id.settle_refund_total);
            mRefundBank = (TextView) itemView.findViewById(R.id.settle_refund_bank_card);
            mRefundCash = (TextView) itemView.findViewById(R.id.settle_refund_cash);
            mRefundWeiXin = (TextView) itemView.findViewById(R.id.settle_refund_weixin);
            mRefundMember = (TextView) itemView.findViewById(R.id.settle_refund_member);

            mPayTotal = (TextView) itemView.findViewById(R.id.settle_pay_total);
            mPayBank = (TextView) itemView.findViewById(R.id.settle_pay_bank_card);
            mPayCash = (TextView) itemView.findViewById(R.id.settle_pay_cash);
            mPayWeiXin = (TextView) itemView.findViewById(R.id.settle_pay_weixin);
            mPayMember = (TextView) itemView.findViewById(R.id.settle_pay_member);
        }
    }
}
