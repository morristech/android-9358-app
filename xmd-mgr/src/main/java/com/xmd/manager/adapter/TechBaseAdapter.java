package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.TechBaseInfo;
import com.xmd.manager.common.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-25.
 */

public class TechBaseAdapter extends RecyclerView.Adapter<TechBaseAdapter.ViewHolder> {
    private Context mContext;
    private List<TechBaseInfo> mData = new ArrayList<>();
    private int selectPosition = -1;

    public TechBaseAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<TechBaseInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_tech_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TechBaseInfo info = mData.get(position);
        holder.mTechNo.setText("[" + info.techNo + "]");
        holder.mTechName.setText(info.techName);
        if (selectPosition == position) {
            holder.itemView.setBackgroundColor(ResourceUtils.getColor(R.color.colorStoke));
        } else {
            holder.itemView.setBackgroundColor(ResourceUtils.getColor(R.color.colorWhite));
        }
        holder.itemView.setOnClickListener(v -> {
            if (selectPosition == position) {
                selectPosition = -1;
                notifyItemChanged(position);
            } else {
                int temp = selectPosition;
                selectPosition = position;
                notifyItemChanged(temp);
                notifyItemChanged(selectPosition);
            }
        });
    }

    public TechBaseInfo getSelectInfo() {
        if (selectPosition == -1) {
            return null;
        }
        return mData.get(selectPosition);
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        } else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_tech_no)
        TextView mTechNo;
        @BindView(R.id.tv_tech_nick)
        TextView mTechName;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
