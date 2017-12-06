package com.xmd.inner.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.inner.NativeManager;
import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.RoomInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-1.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {
    private Context mContext;
    private List<RoomInfo> mData = new ArrayList<>();

    public RoomAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<RoomInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_room, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RoomInfo roomInfo = mData.get(position);
        holder.mRoomCount.setText(roomInfo.useCount + "/" + roomInfo.seatCount);
        holder.mRoomStatus.setText(roomInfo.name);
        GradientDrawable mGrad = (GradientDrawable) holder.mRoomStatus.getBackground();
        mGrad.setColor(NativeManager.getInstance().getStatusColor(roomInfo.status));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(roomInfo);
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
        @BindView(R2.id.tv_room_count)
        TextView mRoomCount;
        @BindView(R2.id.tv_room_status)
        TextView mRoomStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
