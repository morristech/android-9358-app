package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CashierNormalInfo;
import com.xmd.manager.beans.CommissionNormalInfo;
import com.xmd.manager.common.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-24.
 * 报表按天
 */

public class ReportNormalAdapter<T> extends RecyclerView.Adapter<ReportNormalAdapter<T>.ViewHolder> {
    public static final String SCOPE_TYPE_SPA = "spa";
    public static final String SCOPE_TYPE_GOODS = "goods";
    public static final String SCOPE_TYPE_ALL = "all";

    private Context mContext;
    private List<T> mData = new ArrayList<>();
    private CallBack mCallBack;
    private String mScope;

    public ReportNormalAdapter(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    public void setScope(String scope) {
        mScope = scope;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_day, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (mData.get(position) instanceof CommissionNormalInfo) {
            CommissionNormalInfo info = (CommissionNormalInfo) mData.get(position);
            holder.mDateText.setText(info.workDate);
            holder.mAmountText.setText("+" + Utils.moneyToStringEx(info.sumCommission));
            holder.itemView.setOnClickListener(v -> mCallBack.onItemClick(info.workDate));
        } else if (mData.get(position) instanceof CashierNormalInfo) {
            CashierNormalInfo info = (CashierNormalInfo) mData.get(position);
            holder.mDateText.setText(info.date);
            switch (mScope) {
                case SCOPE_TYPE_GOODS:
                    holder.mAmountText.setText("+" + Utils.moneyToStringEx(info.goodsAmount));
                    break;
                case SCOPE_TYPE_SPA:
                    holder.mAmountText.setText("+" + Utils.moneyToStringEx(info.spaAmount));
                    break;
                case SCOPE_TYPE_ALL:
                default:
                    holder.mAmountText.setText("+" + Utils.moneyToStringEx(info.amount));
                    break;
            }
            holder.itemView.setOnClickListener(v -> mCallBack.onItemClick(info.date));
        }
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
        @BindView(R.id.tv_item_date)
        TextView mDateText;
        @BindView(R.id.tv_item_amount)
        TextView mAmountText;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CallBack {
        void onItemClick(String date);
    }
}
