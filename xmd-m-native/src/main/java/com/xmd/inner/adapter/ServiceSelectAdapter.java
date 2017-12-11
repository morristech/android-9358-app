package com.xmd.inner.adapter;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.inner.R;
import com.xmd.inner.bean.NativeCategoryBean;
import com.xmd.inner.bean.NativeServiceItemBean;
import com.xmd.inner.bean.NativeTechnician;
import com.xmd.inner.bean.NativeUserIdentifyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-12-4.
 */

public class ServiceSelectAdapter<T> extends RecyclerView.Adapter {

    public List<T> mData;
    private ItemSelectedListener mListener;
    private static final int CATEGORY_ITEM_TYPE = 1;
    private static final int SERVICE_ITEM_TYPE = 2;
    private static final int TECH_ITEM_TYPE = 3;
    private static final int USER_IDENTIFY_TYPE = 4;

    public interface ItemSelectedListener<T> {
        void itemSelected(T data, int position);
    }

    public ServiceSelectAdapter() {
        mData = new ArrayList<>();
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setItemSelectedListener(ItemSelectedListener listener) {
        this.mListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewServiceHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_service_selected_item, parent, false);
        return new ServiceSelectViewHolder(viewServiceHolder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ServiceSelectViewHolder viewHolder = (ServiceSelectViewHolder) holder;
        if (mData.get(position) instanceof NativeCategoryBean) {
            final NativeCategoryBean bean = (NativeCategoryBean) mData.get(position);
            viewHolder.tvServiceItem.setText(bean.name);
            viewHolder.tvServiceItem.setSelected(bean.isSelected);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mData.size(); i++) {
                        ((NativeCategoryBean) mData.get(i)).isSelected = false;
                    }
                    bean.isSelected = true;
                    if (mListener != null) {
                        mListener.itemSelected(bean, position);
                    }
                    notifyDataSetChanged();
                }
            });
        } else if (mData.get(position) instanceof NativeTechnician) {
            final NativeTechnician technicianBean = (NativeTechnician) mData.get(position);
            if (!TextUtils.isEmpty(technicianBean.techNo) && !TextUtils.isEmpty(technicianBean.name)) {
                viewHolder.tvServiceItem.setText(String.format("[%s] %s", technicianBean.techNo, technicianBean.name));
            } else {
                viewHolder.tvServiceItem.setText(technicianBean.name);
            }
            viewHolder.tvServiceItem.setSelected(technicianBean.isSelected);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mData.size(); i++) {
                        ((NativeTechnician) mData.get(i)).isSelected = false;
                    }
                    technicianBean.isSelected = true;
                    if (mListener != null) {
                        mListener.itemSelected(technicianBean, position);
                    }
                    notifyDataSetChanged();
                }
            });

        } else if (mData.get(position) instanceof NativeUserIdentifyBean) {
            final NativeUserIdentifyBean bean = (NativeUserIdentifyBean) mData.get(position);
            viewHolder.tvServiceItem.setText(bean.userIdentify);
            viewHolder.tvServiceItem.setSelected(bean.isSelected);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mData.size(); i++) {
                        ((NativeUserIdentifyBean) mData.get(i)).isSelected = false;
                    }
                    bean.isSelected = true;
                    if (mListener != null) {
                        mListener.itemSelected(bean, position);
                    }
                    notifyDataSetChanged();
                }
            });
        } else {
            final NativeServiceItemBean bean = (NativeServiceItemBean) mData.get(position);
            viewHolder.tvServiceItem.setText(bean.name);
            viewHolder.tvServiceItem.setSelected(bean.isSelected);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mData.size(); i++) {
                        ((NativeServiceItemBean) mData.get(i)).isSelected = false;
                    }
                    bean.isSelected = true;
                    if (mListener != null) {
                        mListener.itemSelected(bean, position);
                    }
                    notifyDataSetChanged();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof NativeCategoryBean) {
            return CATEGORY_ITEM_TYPE;
        } else if (mData.get(position) instanceof NativeTechnician) {
            return TECH_ITEM_TYPE;
        } else if (mData.get(position) instanceof NativeUserIdentifyBean) {
            return USER_IDENTIFY_TYPE;
        } else {
            return SERVICE_ITEM_TYPE;
        }

    }

    static class ServiceSelectViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceItem;

        public ServiceSelectViewHolder(View itemView) {
            super(itemView);
            tvServiceItem = (TextView) itemView.findViewById(R.id.tv_service_item);
        }
    }
}
