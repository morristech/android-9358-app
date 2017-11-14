package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.InnerOrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-3.
 */

public class InnerOrderAdapter extends RecyclerView.Adapter<InnerOrderAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerOrderInfo> mData = new ArrayList<>();

    public InnerOrderAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<InnerOrderInfo> list) {
        if (list != null) {
            mData.clear();
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_order, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InnerOrderInfo info = mData.get(position);
        holder.mRoomTypeName.setText(info.roomTypeName);
        holder.mRoomNumber.setText(info.roomName);
        holder.mOrderNumber.setText(info.userIdentify);

        if (info != null && info.itemList != null && !info.itemList.isEmpty()) {
            InnerOrderDetailAdapter detailAdapter = new InnerOrderDetailAdapter();
            detailAdapter.setData(info.itemList);
            holder.mOrderItems.setLayoutManager(new LinearLayoutManager(mContext));
            holder.mOrderItems.setAdapter(detailAdapter);
        }
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mRoomTypeName;
        public TextView mRoomNumber;
        public TextView mOrderNumber;
        public RecyclerView mOrderItems;

        public ViewHolder(View itemView) {
            super(itemView);
            mRoomTypeName = (TextView) itemView.findViewById(R.id.tv_room_type_name);
            mRoomNumber = (TextView) itemView.findViewById(R.id.tv_room_number);
            mOrderNumber = (TextView) itemView.findViewById(R.id.tv_order_number);
            mOrderItems = (RecyclerView) itemView.findViewById(R.id.rv_order_items);
        }
    }
}
