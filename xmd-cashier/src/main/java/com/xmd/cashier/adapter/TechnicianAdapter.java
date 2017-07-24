package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.TechInfo;
import com.xmd.cashier.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-15.
 */

public class TechnicianAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<TechInfo> mData = new ArrayList<>();
    private Context mContext;
    private CallBack mCallBack;

    public interface CallBack {
        void onTechItemClick(TechInfo info);
    }

    public TechnicianAdapter(Context context) {
        mContext = context;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<TechInfo> list) {
        mData.addAll(list);
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_technician, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final TechInfo info = mData.get(position);
        Glide.with(mContext).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(itemHolder.mTechAvatar);
        if (!TextUtils.isEmpty(info.techNo)) {
            itemHolder.mTechNo.setVisibility(View.VISIBLE);
            itemHolder.mTechNo.setText(info.techNo);
        } else {
            itemHolder.mTechNo.setVisibility(View.GONE);
        }
        itemHolder.mTechName.setText(info.name);
        itemHolder.mTechLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onTechItemClick(info);
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mTechLayout;
        public CircleImageView mTechAvatar;
        public TextView mTechName;
        public TextView mTechNo;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTechLayout = (LinearLayout) itemView.findViewById(R.id.item_tech_layout);
            mTechAvatar = (CircleImageView) itemView.findViewById(R.id.item_tech_avatar);
            mTechName = (TextView) itemView.findViewById(R.id.item_tech_name);
            mTechNo = (TextView) itemView.findViewById(R.id.item_tech_no);
        }
    }
}
