package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.InnerHandInfo;
import com.xmd.cashier.manager.InnerManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-8.
 * 手牌
 */

public class InnerHandAdapter extends RecyclerView.Adapter<InnerHandAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerHandInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public InnerHandAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<InnerHandInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void updateData() {
        for (InnerHandInfo handInfo : mData) {
            handInfo.selected = InnerManager.getInstance().findOrderByHand(handInfo.id);
        }
        notifyDataSetChanged();
    }

    public void setCallBack(CallBack cb) {
        mCallBack = cb;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InnerHandAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toggle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InnerHandInfo handInfo = mData.get(position);
        holder.mContentText.setText(handInfo.userIdentify);
        if (handInfo.selected) {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_select);
            holder.mSelectImage.setVisibility(View.VISIBLE);
        } else {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_unselect);
            holder.mSelectImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(handInfo, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mData != null && !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout mItemLayout;
        private TextView mContentText;
        private ImageView mSelectImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mItemLayout = (RelativeLayout) itemView.findViewById(R.id.item_layout);
            mContentText = (TextView) itemView.findViewById(R.id.item_desc);
            mSelectImage = (ImageView) itemView.findViewById(R.id.item_conner_img);
        }
    }

    public interface CallBack {
        void onItemClick(InnerHandInfo info, int position);
    }
}

