package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.ExRoomInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-1.
 */

public class ExRoomAdapter extends RecyclerView.Adapter<ExRoomAdapter.ViewHolder> {
    private Context mContext;
    private List<ExRoomInfo> mData = new ArrayList<>();

    public ExRoomAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ExRoomInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ex_room, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ExRoomInfo exRoomInfo = mData.get(position);
        holder.mTypeName.setText(exRoomInfo.roomTypeName);
        if (exRoomInfo.rooms != null && !exRoomInfo.rooms.isEmpty()) {
            holder.mSubList.setVisibility(View.VISIBLE);
            RoomAdapter roomAdapter = new RoomAdapter(mContext);
            holder.mSubList.setAdapter(roomAdapter);
            holder.mSubList.setLayoutManager(new GridLayoutManager(mContext, 4));
            roomAdapter.setData(exRoomInfo.rooms);
        } else {
            holder.mSubList.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_type_name)
        TextView mTypeName;
        @BindView(R2.id.rv_room_sub_list)
        RecyclerView mSubList;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
