package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.ExInnerTechStatusInfo;
import com.xmd.cashier.dal.bean.InnerTechInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-15.
 */

public class ExInnerTechStatusAdapter extends RecyclerView.Adapter<ExInnerTechStatusAdapter.ViewHolder> {
    private Context mContext;
    private List<ExInnerTechStatusInfo> mData = new ArrayList<>();
    private ExInnerStatusCallBack mCallBack;

    public void setCallBack(ExInnerStatusCallBack callback) {
        mCallBack = callback;
    }

    public ExInnerTechStatusAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<ExInnerTechStatusInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ExInnerTechStatusAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_tech, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ExInnerTechStatusInfo techStatusInfo = mData.get(position);
        switch (techStatusInfo.status) {
            case AppConstants.TECH_STATUS_BUSY:
                holder.mStatusText.setText("忙碌");
                holder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorPink));
                holder.mStatusText.setBackgroundResource(R.drawable.bg_area_section_red);
                break;
            case AppConstants.TECH_STATUS_FREE:
                holder.mStatusText.setText("空闲");
                holder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorStatusGreen));
                holder.mStatusText.setBackgroundResource(R.drawable.bg_area_section_green);
                break;
            case AppConstants.TECH_STATUS_REST:
                holder.mStatusText.setText("休假");
                holder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText4));
                holder.mStatusText.setBackgroundResource(R.drawable.bg_area_section_gray);
                break;
            default:
                holder.mStatusText.setText("其他");
                holder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                holder.mStatusText.setBackgroundResource(R.drawable.bg_area_section_line);
                break;
        }
        if (techStatusInfo.exInfos != null && !techStatusInfo.exInfos.isEmpty()) {
            ExInnerTechAdapter exTechAdapter = new ExInnerTechAdapter(mContext);
            exTechAdapter.setCallBack(new ExInnerTechAdapter.CallBack() {
                @Override
                public void onExItemClick(InnerTechInfo info) {
                    mCallBack.onExStatusClick(info);
                }
            });
            holder.mTechInfoList.setAdapter(exTechAdapter);
            holder.mTechInfoList.setLayoutManager(new LinearLayoutManager(mContext));
            exTechAdapter.setData(techStatusInfo.exInfos);
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
        private TextView mStatusText;
        private RecyclerView mTechInfoList;

        public ViewHolder(View itemView) {
            super(itemView);
            mStatusText = (TextView) itemView.findViewById(R.id.tv_item_tech_status);
            mTechInfoList = (RecyclerView) itemView.findViewById(R.id.rv_item_tech_list);
        }
    }

    public interface ExInnerStatusCallBack {
        void onExStatusClick(InnerTechInfo info);
    }
}