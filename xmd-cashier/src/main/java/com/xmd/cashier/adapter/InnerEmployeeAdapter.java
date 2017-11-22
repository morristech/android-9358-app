package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.dal.bean.InnerEmployeeInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-22.
 */

public class InnerEmployeeAdapter extends RecyclerView.Adapter<InnerEmployeeAdapter.ViewHolder> {
    private Context mContext;
    private List<InnerEmployeeInfo> mData = new ArrayList<>();

    public InnerEmployeeAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<InnerEmployeeInfo> list) {
        mData.clear();
        mData.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new InnerEmployeeAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_employee_info, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final InnerEmployeeInfo employeeInfo = mData.get(position);
        holder.mTechNo.setText("技师：" + "[" + employeeInfo.employeeNo + "]");
        holder.mTechBellType.setText(employeeInfo.bellName);
    }

    @Override
    public int getItemCount() {
        if (mData != null && !mData.isEmpty()) {
            return mData.size();
        }
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mTechNo;
        private TextView mTechBellType;

        public ViewHolder(View itemView) {
            super(itemView);
            mTechNo = (TextView) itemView.findViewById(R.id.tv_item_tech);
            mTechBellType = (TextView) itemView.findViewById(R.id.tv_item_type);
        }
    }
}
