package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.CashierClubDetailInfo;
import com.xmd.manager.common.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-11-25.
 */

public class ReportTechDetailAdapter extends RecyclerView.Adapter<ReportTechDetailAdapter.ViewHolder> {
    private static final String TYPE_SPA = "spa";
    private static final String TYPE_GOODS = "goods";
    private Context mContext;
    private List<CashierClubDetailInfo.CashierTechInfo> mData = new ArrayList<>();
    private CallBack mCallBack;
    private String mType;

    public ReportTechDetailAdapter(Context context, String type) {
        mContext = context;
        mType = type;
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<CashierClubDetailInfo.CashierTechInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_report_detail_tech, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CashierClubDetailInfo.CashierTechInfo techInfo = mData.get(position);
        switch (mType) {
            case TYPE_GOODS:
                holder.mTechTitle.setText("营销人员：");
                holder.mTechInfo.setText(techInfo.techName + "[" + techInfo.techNo + "]");
                break;
            case TYPE_SPA:
                holder.mTechTitle.setText("服务技师：");
                holder.mTechInfo.setText(techInfo.techNo + "[" + techInfo.bellName + "]");
                break;
            default:
                break;
        }
        holder.mTechInfo.setTextColor(ResourceUtils.getColor(R.color.colorBlue));
        holder.mTechInfo.setOnClickListener(v -> mCallBack.onItemClick(techInfo));
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
        @BindView(R.id.tv_tech_title)
        TextView mTechTitle;
        @BindView(R.id.tv_tech_info)
        TextView mTechInfo;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface CallBack {
        void onItemClick(CashierClubDetailInfo.CashierTechInfo info);
    }
}
