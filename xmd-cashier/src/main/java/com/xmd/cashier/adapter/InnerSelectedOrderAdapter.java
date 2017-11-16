package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.InnerOrderInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-7.
 */

public class InnerSelectedOrderAdapter extends RecyclerView.Adapter<InnerSelectedOrderAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerOrderInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public InnerSelectedOrderAdapter(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBack cb) {
        mCallBack = cb;
    }

    public void setData(List<InnerOrderInfo> list) {
        if (list != null) {
            mData.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_selected_order, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InnerOrderInfo info = mData.get(position);
        if (info.selected) {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_select);
            holder.mRoomText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mSeatText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mIdentifyText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mSelectImg.setImageResource(R.drawable.ic_order_select);
        } else {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_unselect);
            holder.mRoomText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mSeatText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mIdentifyText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mSelectImg.setImageResource(R.drawable.ic_order_unselect);
        }
        holder.mRoomText.setText(info.roomTypeName + "：" + info.roomName);
        holder.mSeatText.setText("座位号：" + info.seatName);
        holder.mIdentifyText.setText("客单号：" + info.userIdentify);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(info, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mItemLayout;
        public TextView mRoomText;
        public TextView mSeatText;
        public TextView mIdentifyText;
        public ImageView mSelectImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemLayout = (LinearLayout) itemView.findViewById(R.id.layout_item_order);
            mRoomText = (TextView) itemView.findViewById(R.id.tv_item_order_room);
            mSeatText = (TextView) itemView.findViewById(R.id.tv_item_order_seat);
            mIdentifyText = (TextView) itemView.findViewById(R.id.tv_item_order_identify);
            mSelectImg = (ImageView) itemView.findViewById(R.id.img_item_select);
        }
    }

    public interface CallBack {
        void onItemClick(InnerOrderInfo info, int position);
    }
}