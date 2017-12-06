package com.xmd.inner.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.inner.R;
import com.xmd.inner.R2;
import com.xmd.inner.bean.EmployeeInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zr on 17-12-4.
 */

public class OrderConsumeTechAdapter extends RecyclerView.Adapter<OrderConsumeTechAdapter.ViewHolder> {
    private Context mContext;
    private List<EmployeeInfo> mData = new ArrayList<>();

    public OrderConsumeTechAdapter(Context context) {
        mContext = context;
    }

    public void setData(List<EmployeeInfo> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void clearData() {
        mData.clear();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_order_consume_tech, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        EmployeeInfo employeeInfo = mData.get(position);
        holder.mTechInfo.setText("[" + employeeInfo.employeeNo + "]");
        if (!TextUtils.isEmpty(employeeInfo.bellName)) {
            holder.mTechBell.setVisibility(View.VISIBLE);
            holder.mTechBell.setText(employeeInfo.bellName);
        } else {
            holder.mTechBell.setVisibility(View.GONE);
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
        @BindView(R2.id.tv_tech_info)
        TextView mTechInfo;
        @BindView(R2.id.tv_tech_bell_type)
        TextView mTechBell;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
