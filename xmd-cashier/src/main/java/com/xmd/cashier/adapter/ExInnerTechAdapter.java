package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.ExInnerTechInfo;
import com.xmd.cashier.dal.bean.InnerTechInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-16.
 */

public class ExInnerTechAdapter extends RecyclerView.Adapter<ExInnerTechAdapter.ViewHolder> {
    private Context mContext;
    private List<ExInnerTechInfo> mData = new ArrayList<>();
    private ExInnerTechAdapter.CallBack mCallBack;

    public ExInnerTechAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ExInnerTechInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void setCallBack(ExInnerTechAdapter.CallBack cb) {
        mCallBack = cb;
    }

    @Override
    public ExInnerTechAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExInnerTechAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_tech_ex, parent, false));
    }

    @Override
    public void onBindViewHolder(ExInnerTechAdapter.ViewHolder holder, final int position) {
        ExInnerTechInfo exInnerTechInfo = mData.get(position);
        holder.mTypeText.setText(exInnerTechInfo.groupInfo.groupName);
        if (exInnerTechInfo.techInfos != null && !exInnerTechInfo.techInfos.isEmpty()) {
            InnerTechAdapter techAdapter = new InnerTechAdapter(mContext);
            techAdapter.setCallBack(new InnerTechAdapter.CallBack() {
                @Override
                public void onItemClick(InnerTechInfo info) {
                    mCallBack.onExItemClick(info);
                }
            });
            holder.mTechList.setAdapter(techAdapter);
            holder.mTechList.setLayoutManager(new GridLayoutManager(mContext, 4));
            techAdapter.setData(exInnerTechInfo.techInfos);
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null && !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTypeText;
        private RecyclerView mTechList;

        public ViewHolder(View itemView) {
            super(itemView);
            mTypeText = (TextView) itemView.findViewById(R.id.tv_item_tech_type);
            mTechList = (RecyclerView) itemView.findViewById(R.id.rv_ex_item_tech_list);
        }
    }

    public interface CallBack {
        void onExItemClick(InnerTechInfo info);
    }
}
