package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CommissionAmountInfo;
import com.xmd.manager.common.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-24.
 * 技师工资:会所某天所有技师提成
 */

public class TechCommissionAmountAdapter extends RecyclerView.Adapter<TechCommissionAmountAdapter.ViewHolder> {
    private Context mContext;
    private List<CommissionAmountInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public TechCommissionAmountAdapter(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<CommissionAmountInfo> list) {
        mData.clear();
        mData.addAll(list);
        sortByTotal(false);
    }

    public void clearData() {
        mData.clear();
    }

    public void sortByService(boolean sort) {
        Collections.sort(mData, (o1, o2) -> {
            if (sort) {
                // 从小到大
                return Long.valueOf(o1.serviceCommission).compareTo(Long.valueOf(o2.serviceCommission));
            } else {
                //从大到小
                return Long.valueOf(o2.serviceCommission).compareTo(Long.valueOf(o1.serviceCommission));
            }
        });
        notifyDataSetChanged();
    }

    public void sortBySale(boolean sort) {
        Collections.sort(mData, (o1, o2) -> {
            if (sort) {
                // 从小到大
                return Long.valueOf(o1.salesCommission).compareTo(Long.valueOf(o2.salesCommission));
            } else {
                //从大到小
                return Long.valueOf(o2.salesCommission).compareTo(Long.valueOf(o1.salesCommission));
            }
        });
        notifyDataSetChanged();
    }

    public void sortByTotal(boolean sort) {
        Collections.sort(mData, (o1, o2) -> {
            if (sort) {
                // 从小到大
                return Long.valueOf(o1.salesCommission + o1.serviceCommission).compareTo(Long.valueOf(o2.salesCommission + o2.serviceCommission));
            } else {
                //从大到小
                return Long.valueOf(o2.salesCommission + o2.serviceCommission).compareTo(Long.valueOf(o1.salesCommission + o1.serviceCommission));
            }
        });
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TechCommissionAmountAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_tech, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommissionAmountInfo amountInfo = mData.get(position);
        holder.mTechNo.setText(amountInfo.techNo);
        holder.mTechName.setText(amountInfo.techName);
        holder.mServiceText.setText(Utils.moneyToStringEx(amountInfo.serviceCommission));
        holder.mSaleText.setText(Utils.moneyToStringEx(amountInfo.salesCommission));
        holder.mTotalText.setText(Utils.moneyToStringEx(amountInfo.getTotalCommission()));
        holder.itemView.setOnClickListener(v -> mCallBack.onItemClick(amountInfo));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_tech_no)
        TextView mTechNo;
        @BindView(R.id.tv_tech_name)
        TextView mTechName;
        @BindView(R.id.tv_service_amount)
        TextView mServiceText;
        @BindView(R.id.tv_sale_amount)
        TextView mSaleText;
        @BindView(R.id.tv_total_amount)
        TextView mTotalText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CallBack {
        void onItemClick(CommissionAmountInfo info);
    }
}

