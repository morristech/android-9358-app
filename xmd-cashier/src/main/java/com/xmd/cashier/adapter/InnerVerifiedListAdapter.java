package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.TradeDiscountCheckInfo;
import com.xmd.cashier.dal.bean.TradeDiscountInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-12-13.
 */

public class InnerVerifiedListAdapter extends RecyclerView.Adapter<InnerVerifiedListAdapter.ViewHolder> {
    private Context mContext;
    private List<TradeDiscountInfo> mData = new ArrayList<>();

    public InnerVerifiedListAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<TradeDiscountInfo> list) {
        mData = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_discount, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TradeDiscountInfo orderDiscountInfo = mData.get(position);
        TradeDiscountCheckInfo orderDiscountCheckInfo = orderDiscountInfo.checkInfo;
        holder.mSelect.setImageResource(R.drawable.ic_discount_verified);
        holder.mName.setText(orderDiscountCheckInfo.title);
        holder.mMoney.setText(Utils.moneyToString(orderDiscountCheckInfo.amount));
        holder.mInfo.setText(orderDiscountCheckInfo.consumeDescription);
        holder.mType.setText(orderDiscountCheckInfo.typeName);
        holder.mTime.setText(orderDiscountCheckInfo.verifyCode);
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

        public ViewHolder(View itemView) {
            super(itemView);
            mSelect = (ImageView) itemView.findViewById(R.id.img_select);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMoney = (TextView) itemView.findViewById(R.id.tv_money);
            mType = (TextView) itemView.findViewById(R.id.tv_type);
            mInfo = (TextView) itemView.findViewById(R.id.tv_info);
            mTime = (TextView) itemView.findViewById(R.id.tv_time);
        }
    }

}
