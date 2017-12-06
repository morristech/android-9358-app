package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.app.utils.Utils;
import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.OrderInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-4.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    public static final String TYPE_MULTI = "multi";
    public static final String TYPE_SINGLE = "single";

    private String mType;
    private Context mContext;
    private List<OrderInfo> mData = new ArrayList<>();

    public OrderAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<OrderInfo> list) {
        mType = TYPE_MULTI;
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setData(OrderInfo orderInfo) {
        mType = TYPE_SINGLE;
        mData.add(orderInfo);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        OrderInfo orderInfo = mData.get(position);
        holder.mConsumeIdentify.setText(orderInfo.userIdentify);
        holder.mConsumeAmount.setText("￥" + Utils.moneyToStringEx(orderInfo.amount));
        if (orderInfo.itemList != null && !orderInfo.itemList.isEmpty()) {
            holder.mConsumeItems.setVisibility(View.VISIBLE);
            OrderConsumeAdapter orderConsumeAdapter = new OrderConsumeAdapter(mContext);
            holder.mConsumeItems.setAdapter(orderConsumeAdapter);
            holder.mConsumeItems.setLayoutManager(new LinearLayoutManager(mContext));
            orderConsumeAdapter.setData(orderInfo.itemList);
        } else {
            holder.mConsumeItems.setVisibility(View.GONE);
        }
        switch (mType) {
            case TYPE_MULTI:
                holder.mSummaryLayout.setVisibility(View.VISIBLE);
                holder.mConsumeItems.setVisibility(View.GONE);
                break;
            case TYPE_SINGLE:
                holder.mSummaryLayout.setVisibility(View.GONE);
                holder.mConsumeItems.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
        holder.mSummaryLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mConsumeItems.setVisibility((holder.mConsumeItems.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE);

            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.layout_consume_summary)
        RelativeLayout mSummaryLayout;
        @BindView(R2.id.tv_consume_identify)
        TextView mConsumeIdentify;
        @BindView(R2.id.tv_consume_amount)
        TextView mConsumeAmount;
        @BindView(R2.id.rv_consume_items)
        RecyclerView mConsumeItems;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
