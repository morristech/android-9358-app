package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.CouponInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-8-8.
 */

public class VerifyDiscountAdapter extends RecyclerView.Adapter<VerifyDiscountAdapter.ViewHolder> {
    private Context mContext;
    private List<CouponInfo> mData = new ArrayList<>();

    public void setData(List<CouponInfo> infos) {
        mData.addAll(infos);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_discount, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CouponInfo info = mData.get(position);
        long discountAmount = (long) info.originAmount * (100000 - info.actAmount) / 100000;
        holder.mDiscountText.setText("优惠金额:" + Utils.moneyToStringEx((int) discountAmount) + "元(" + info.actAmount / 10000f + "折)");
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDiscountText;

        public ViewHolder(View itemView) {
            super(itemView);
            mDiscountText = (TextView) itemView.findViewById(R.id.item_discount_text);
        }
    }
}
