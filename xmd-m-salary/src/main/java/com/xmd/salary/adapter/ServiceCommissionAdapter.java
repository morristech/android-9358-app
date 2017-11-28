package com.xmd.salary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.salary.R;
import com.xmd.salary.bean.BellListBean;
import com.xmd.salary.bean.ServiceCellBean;
import com.xmd.salary.bean.ServiceItemBean;

import cn.zhouchaoyuan.excelpanel.BaseExcelPanelAdapter;

/**
 * Created by Lhj on 17-11-24.
 * ServiceItemBean:纵向item
 * BellListBean：横向item
 * ServiceCellBean：内容item
 */

public class ServiceCommissionAdapter extends BaseExcelPanelAdapter<BellListBean, ServiceItemBean, ServiceCellBean> {

    private Context mContext;

    public ServiceCommissionAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateCellViewHolder(ViewGroup parent, int viewType) {
        View cellLayout = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_cell_list_item, parent, false);
        ServiceCellHolder cellHolder = new ServiceCellHolder(cellLayout);
        return cellHolder;
    }

    @Override
    public void onBindCellViewHolder(RecyclerView.ViewHolder holder, int verticalPosition, int horizontalPosition) {
        ServiceCellBean cellBean = getMajorItem(verticalPosition, horizontalPosition);
        if (null == holder || !(holder instanceof ServiceCellHolder) || cellBean == null) {
            return;
        }
        ServiceCellHolder viewHolder = (ServiceCellHolder) holder;
        viewHolder.tvCellContainer.setText(cellBean.serviceCell);
        if (cellBean.line % 2 == 0) {
            viewHolder.tvCellContainer.setSelected(true);
        } else {
            viewHolder.tvCellContainer.setSelected(false);
        }
    }

    static class ServiceCellHolder extends RecyclerView.ViewHolder {
        TextView tvCellContainer;

        public ServiceCellHolder(View itemView) {
            super(itemView);
            tvCellContainer = (TextView) itemView.findViewById(R.id.tv_cell_container);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateTopViewHolder(ViewGroup parent, int viewType) {
        View rowCell = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_commission_type_item, parent, false);
        RowCellViewHolder viewHolder = new RowCellViewHolder(rowCell);
        return viewHolder;
    }

    @Override
    public void onBindTopViewHolder(RecyclerView.ViewHolder holder, int position) {
        BellListBean bellBean = getTopItem(position);
        if (null == holder || !(holder instanceof RowCellViewHolder) || bellBean == null) {
            return;
        }
        RowCellViewHolder viewHolder = (RowCellViewHolder) holder;
        viewHolder.tvServiceTypeName.setText(TextUtils.isEmpty(bellBean.name) ? "自定义" : bellBean.name);

    }

    static class RowCellViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceTypeName;

        public RowCellViewHolder(View itemView) {
            super(itemView);
            tvServiceTypeName = (TextView) itemView.findViewById(R.id.tv_service_type_name);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateLeftViewHolder(ViewGroup parent, int viewType) {
        View colView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_commission_item, parent, false);
        ColViewHolder viewHolder = new ColViewHolder(colView);
        return viewHolder;
    }

    @Override
    public void onBindLeftViewHolder(RecyclerView.ViewHolder holder, int position) {
        ServiceItemBean serviceItem = getLeftItem(position);
        if (null == holder || !(holder instanceof ColViewHolder) || serviceItem == null) {
            return;
        }
        ColViewHolder viewHolder = (ColViewHolder) holder;
        viewHolder.tvServiceName.setText(serviceItem.serviceItemName);
        ViewGroup.LayoutParams lp = viewHolder.rootView.getLayoutParams();
        viewHolder.rootView.setLayoutParams(lp);

    }

    static class ColViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;
        RelativeLayout rootView;

        public ColViewHolder(View itemView) {
            super(itemView);
            tvServiceName = (TextView) itemView.findViewById(R.id.tv_service_name);
            rootView = (RelativeLayout) itemView.findViewById(R.id.root);
        }
    }


    @Override
    public View onCreateTopLeftView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_excel_left_top, null);
    }
}

