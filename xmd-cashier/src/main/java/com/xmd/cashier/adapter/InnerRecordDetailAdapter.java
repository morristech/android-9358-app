package com.xmd.cashier.adapter;

import android.content.Context;
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
 * Created by zr on 17-11-2.
 */

public class InnerRecordDetailAdapter extends RecyclerView.Adapter<InnerRecordDetailAdapter.ViewHolder> {
    private Context mContext;
    private boolean mNotify;
    private List<InnerOrderInfo> mData = new ArrayList<>();

    public void setData(List<InnerOrderInfo> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    public InnerRecordDetailAdapter(Context context, boolean notify) {
        mContext = context;
        mNotify = notify;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mNotify) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_record_notify, parent, false));
        } else {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_record_detail, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        InnerOrderInfo info = mData.get(position);
        holder.mRoomType.setText(info.roomTypeName);
        holder.mRoomNumber.setText(info.roomName);
        holder.mOrderNumber.setText(info.userIdentify);
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mRoomType;
        public TextView mRoomNumber;
        public TextView mOrderNumber;

        public ViewHolder(View itemView) {
            super(itemView);
            mRoomType = (TextView) itemView.findViewById(R.id.tv_room_type_name);
            mRoomNumber = (TextView) itemView.findViewById(R.id.tv_room_number);
            mOrderNumber = (TextView) itemView.findViewById(R.id.tv_order_number);
        }
    }
}
