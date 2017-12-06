package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.utils.Utils;
import com.xmd.inner.ConstantResource;
import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.ConsumeInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-4.
 */

public class OrderConsumeAdapter extends RecyclerView.Adapter<OrderConsumeAdapter.ViewHolder> {
    private static final int CONSUME_STATUS_FINISH = 2;
    private Context mContext;
    private List<ConsumeInfo> mData = new ArrayList<>();

    public OrderConsumeAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ConsumeInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_consume, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ConsumeInfo consumeInfo = mData.get(position);
        switch (consumeInfo.itemType) {
            case ConstantResource.BILL_SPA_TYPE:
                holder.mConsumeName.setText(consumeInfo.itemName);
                if (consumeInfo.status == CONSUME_STATUS_FINISH) {
                    holder.mConsumeStatus.setVisibility(View.VISIBLE);
                    holder.mConsumeStatus.setText("已完成");
                } else {
                    holder.mConsumeStatus.setVisibility(View.GONE);
                }
                break;
            case ConstantResource.BILL_GOODS_TYPE:
                holder.mConsumeName.setText(consumeInfo.itemName + "*" + consumeInfo.itemCount);
                holder.mConsumeStatus.setVisibility(View.GONE);
                break;
        }
        if (consumeInfo.employeeList != null && !consumeInfo.employeeList.isEmpty()) {
            holder.mConsumeTechList.setVisibility(View.VISIBLE);
            OrderConsumeTechAdapter orderConsumeTechAdapter = new OrderConsumeTechAdapter(mContext);
            holder.mConsumeTechList.setAdapter(orderConsumeTechAdapter);
            holder.mConsumeTechList.setLayoutManager(new LinearLayoutManager(mContext));
            orderConsumeTechAdapter.setData(consumeInfo.employeeList);
        } else {
            holder.mConsumeTechList.setVisibility(View.GONE);
        }
        holder.mConsumeMoney.setText("￥" + Utils.moneyToStringEx(consumeInfo.itemAmount));
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_consume_name)
        TextView mConsumeName;
        @BindView(R2.id.tv_consume_status)
        TextView mConsumeStatus;
        @BindView(R2.id.tv_consume_money)
        TextView mConsumeMoney;
        @BindView(R2.id.rv_consume_tech_list)
        RecyclerView mConsumeTechList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
