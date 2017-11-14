package com.xmd.cashier.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

public class InnerOrderTechAdapter extends RecyclerView.Adapter<InnerOrderTechAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerOrderInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public InnerOrderTechAdapter(Context context) {
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
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_order_tech, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final InnerOrderInfo info = mData.get(position);
        if (info.selected) {
            //选中
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_select);
            holder.mTimeText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_inner_time_select);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.mTimeText.setCompoundDrawables(drawable, null, null, null);
            holder.mLineView.setBackgroundResource(R.color.colorAccent);
            holder.mRoomText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mSeatText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mIdentifyText.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
            holder.mSelectImg.setImageResource(R.drawable.ic_order_select);
        } else {
            //未选中
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_select);
            holder.mTimeText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_inner_time);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            holder.mTimeText.setCompoundDrawables(drawable, null, null, null);
            holder.mLineView.setBackgroundResource(R.color.colorText2);
            holder.mRoomText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mSeatText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mIdentifyText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
            holder.mSelectImg.setImageResource(R.drawable.ic_order_unselect);
        }
        holder.mTimeText.setText(info.startTime);
        holder.mRoomText.setText(info.roomTypeName + "：" + info.roomName);
        holder.mSeatText.setText("座位号：" + info.seatId);
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
        private LinearLayout mItemLayout;
        private TextView mTimeText;
        private View mLineView;
        private TextView mRoomText;
        private TextView mSeatText;
        private TextView mIdentifyText;
        private ImageView mSelectImg;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemLayout = (LinearLayout) itemView.findViewById(R.id.layout_item_order);
            mTimeText = (TextView) itemView.findViewById(R.id.tv_item_order_time);
            mLineView = itemView.findViewById(R.id.view_divide_line);
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