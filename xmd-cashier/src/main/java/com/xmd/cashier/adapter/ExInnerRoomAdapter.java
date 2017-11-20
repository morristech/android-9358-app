package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.ExInnerRoomInfo;
import com.xmd.cashier.dal.bean.InnerRoomInfo;
import com.xmd.cashier.manager.InnerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-14.
 */

public class ExInnerRoomAdapter extends RecyclerView.Adapter<ExInnerRoomAdapter.ViewHolder> {
    private Context mContext;
    private List<ExInnerRoomInfo> mData = new ArrayList<>();
    private ExCallBack mExCallBack;

    public ExInnerRoomAdapter(Context context) {
        mContext = context;
    }

    public void setExCallBack(ExCallBack callback) {
        mExCallBack = callback;
    }

    public void updateData() {
        for (ExInnerRoomInfo exRoomInfo : mData) {
            for (InnerRoomInfo roomInfo : exRoomInfo.rooms) {
                roomInfo.selected = InnerManager.getInstance().findOrderByRoom(roomInfo.id);
            }
        }
        notifyDataSetChanged();
    }

    public void setData(List<ExInnerRoomInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExInnerRoomAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_room, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ExInnerRoomInfo exRoomInfo = mData.get(position);
        holder.mTypeText.setText(exRoomInfo.roomTypeName);
        if (exRoomInfo.rooms != null && !exRoomInfo.rooms.isEmpty()) {
            InnerRoomAdapter roomAdapter = new InnerRoomAdapter(mContext);
            roomAdapter.setCallBack(new InnerRoomAdapter.CallBack() {
                @Override
                public void onItemClick(InnerRoomInfo info) {
                    mExCallBack.onSectionClick(info, position);
                }
            });
            holder.mRoomInfoList.setAdapter(roomAdapter);
            holder.mRoomInfoList.setLayoutManager(new GridLayoutManager(mContext, 4));
            roomAdapter.setData(exRoomInfo.rooms);
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null & !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTypeText;
        private RecyclerView mRoomInfoList;

        public ViewHolder(View itemView) {
            super(itemView);
            mTypeText = (TextView) itemView.findViewById(R.id.tv_item_room_type);
            mRoomInfoList = (RecyclerView) itemView.findViewById(R.id.rv_item_room_list);
        }
    }

    public interface ExCallBack {
        void onSectionClick(InnerRoomInfo roomInfo, int sectionPosition);
    }
}