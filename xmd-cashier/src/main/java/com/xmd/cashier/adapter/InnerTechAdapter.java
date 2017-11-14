package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.InnerTechInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-8.
 * 技师
 */

public class InnerTechAdapter extends RecyclerView.Adapter<InnerTechAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerTechInfo> mData = new ArrayList<>();
    private CallBack mCallBack;

    public InnerTechAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<InnerTechInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallBack(CallBack cb) {
        mCallBack = cb;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InnerTechAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toggle, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InnerTechInfo techInfo = mData.get(position);
        if (!TextUtils.isEmpty(techInfo.techNo)) {
            holder.mContentText.setText(techInfo.techNo);
        } else {
            holder.mContentText.setText(techInfo.name);
        }

        if (techInfo.selected) {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_select);
            holder.mSelectImage.setVisibility(View.VISIBLE);
        } else {
            holder.mItemLayout.setBackgroundResource(R.drawable.bg_area_unselect);
            holder.mSelectImage.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onItemClick(techInfo, position);
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
        void onItemClick(InnerTechInfo info, int position);
    }
}
