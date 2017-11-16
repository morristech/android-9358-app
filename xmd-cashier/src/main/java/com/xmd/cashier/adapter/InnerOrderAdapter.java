package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
    private boolean mSelect;
    private List<InnerOrderInfo> mData = new ArrayList<>();
    private ItemCallBack mCallBack;

    public InnerOrderAdapter(Context context, boolean select) {
        mContext = context;
        mSelect = select;
    }

    public void setCallBack(ItemCallBack callback) {
        mCallBack = callback;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InnerOrderInfo info = mData.get(position);
        holder.mRoomTypeName.setText(info.roomTypeName);
        holder.mRoomNumber.setText(info.roomName);
        holder.mOrderNumber.setText(info.userIdentify);
        holder.mSelectImg.setVisibility(mSelect ? View.VISIBLE : View.GONE);
        if (mSelect) {
            holder.mSelectImg.setVisibility(View.VISIBLE);
            holder.mSelectImg.setImageResource(info.selected ? R.drawable.ic_order_select_little : R.drawable.ic_order_unselect_little);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClick(info, position);
                }
            });
        } else {
            holder.mSelectImg.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(null);
        }
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
        public ImageView mSelectImg;
        public TextView mRoomTypeName;
        public TextView mRoomNumber;
        public TextView mOrderNumber;
        public RecyclerView mOrderItems;

        public ViewHolder(View itemView) {
            super(itemView);
            mSelectImg = (ImageView) itemView.findViewById(R.id.img_select_status);
            mRoomTypeName = (TextView) itemView.findViewById(R.id.tv_room_type_name);
            mRoomNumber = (TextView) itemView.findViewById(R.id.tv_room_number);
            mOrderNumber = (TextView) itemView.findViewById(R.id.tv_order_number);
            mOrderItems = (RecyclerView) itemView.findViewById(R.id.rv_order_items);
        }
    }

    public interface ItemCallBack {
        void onItemClick(InnerOrderInfo orderInfo, int position);
    }
}
