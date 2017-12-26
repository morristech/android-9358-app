package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.PayRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-26.
 */

public class InnerPayRecordAdapter extends RecyclerView.Adapter<InnerPayRecordAdapter.ViewHolder> {
    private Context mContext;
    private List<PayRecordInfo> mData = new ArrayList<>();

    public InnerPayRecordAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<PayRecordInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pay_record, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PayRecordInfo payRecordInfo = mData.get(position);
        if (!TextUtils.isEmpty(payRecordInfo.tradeNo)) {
            holder.mTradeNoRow.setVisibility(View.VISIBLE);
            holder.mTradeNo.setText(payRecordInfo.tradeNo);
        } else {
            holder.mTradeNoRow.setVisibility(View.GONE);
        }
        holder.mPayChannel.setText(payRecordInfo.payChannelName);
        holder.mPayAmount.setText("￥" + Utils.moneyToStringEx(payRecordInfo.amount));
        holder.mPayTime.setText(payRecordInfo.payTime);
        holder.mOperator.setText(payRecordInfo.operatorName);
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TableRow mTradeNoRow;
        private TextView mTradeNo;
        private TextView mPayChannel;
        private TextView mPayAmount;
        private TextView mPayTime;
        private TextView mOperator;

        public ViewHolder(View itemView) {
            super(itemView);
            mTradeNoRow = (TableRow) itemView.findViewById(R.id.tr_trade_no);
            mTradeNo = (TextView) itemView.findViewById(R.id.tv_trade_no);
            mPayChannel = (TextView) itemView.findViewById(R.id.tv_pay_channel);
            mPayAmount = (TextView) itemView.findViewById(R.id.tv_pay_amount);
            mPayTime = (TextView) itemView.findViewById(R.id.tv_pay_time);
            mOperator = (TextView) itemView.findViewById(R.id.tv_operator);
        }
    }
}
