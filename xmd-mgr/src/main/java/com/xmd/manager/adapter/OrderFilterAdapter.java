package com.xmd.manager.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.OrderProjectBean;
import com.xmd.manager.beans.OrderStatusBean;
import com.xmd.manager.beans.OrderTechNoBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2016/12/5.
 */

public class OrderFilterAdapter<T> extends RecyclerView.Adapter {
    private List<T> mData;


    private static final int TYPE_ORDER_STATUS_ITEM = 0;
    private static final int TYPE_ORDER_PROJECT_ITEM = 1;
    private static final int TYPE_ORDER_TECH_ITEM = 2;

    public OrderFilterAdapter(List<T> data) {
        this.mData = data;
    }
    private OnItemClickedListener mItemClickedListener;

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public void setItemClickListener(OnItemClickedListener listener){
        this.mItemClickedListener = listener;
    }

    public interface OnItemClickedListener<T>{
        void onItemViewCLicked(T bean,int position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_ORDER_STATUS_ITEM:
                View orderStatusView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_filter_item, parent, false);
                return new OrderFilterStatusItemViewHolder(orderStatusView);
            case TYPE_ORDER_PROJECT_ITEM:
                View orderProjectView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_filter_item, parent, false);
                return new OrderFilterProjectItemViewHolder(orderProjectView);
            case TYPE_ORDER_TECH_ITEM:
                View orderTechNoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_filter_item, parent, false);
                return new OrderFilterTechNoItemViewHolder(orderTechNoView);
            default:
                return null;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof OrderTechNoBean) {
            return TYPE_ORDER_TECH_ITEM;
        } else if (mData.get(position) instanceof OrderProjectBean) {
            return TYPE_ORDER_PROJECT_ITEM;
        } else {
            return TYPE_ORDER_STATUS_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof OrderFilterStatusItemViewHolder){
            OrderFilterStatusItemViewHolder viewHolder = (OrderFilterStatusItemViewHolder) holder;
            OrderStatusBean bean = ((OrderStatusBean) mData.get(position));
            viewHolder.tvOrderFilterItem.setText(bean.orderStatusName);
            viewHolder.tvOrderFilterItem.setSelected(bean.isSelected == 1);
            viewHolder.itemView.setOnClickListener(v -> {
               if(mItemClickedListener != null){
                   mItemClickedListener.onItemViewCLicked(bean,position);
               }
            });

        }else if(holder instanceof OrderFilterProjectItemViewHolder){
            OrderFilterProjectItemViewHolder viewHolder = (OrderFilterProjectItemViewHolder) holder;
            OrderProjectBean bean = ((OrderProjectBean) mData.get(position));
            viewHolder.tvOrderFilterItem.setText(bean.name);
            viewHolder.tvOrderFilterItem.setSelected(bean.isSelect == 1);
            viewHolder.itemView.setOnClickListener(v -> {
                if(mItemClickedListener != null){
                    mItemClickedListener.onItemViewCLicked(bean,position);
                }
            });
        }else{
            if(holder instanceof OrderFilterTechNoItemViewHolder){
                OrderFilterTechNoItemViewHolder viewHolder = (OrderFilterTechNoItemViewHolder) holder;
                OrderTechNoBean bean = ((OrderTechNoBean) mData.get(position));
                viewHolder.tvOrderFilterItem.setText(bean.techNo);
                viewHolder.tvOrderFilterItem.setSelected(bean.isSelect == 1);
                viewHolder.itemView.setOnClickListener(v -> {
                    if(mItemClickedListener != null){
                        mItemClickedListener.onItemViewCLicked(bean,position);
                    }
                });
            }
        }
    }

    static class OrderFilterTechNoItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_order_filter_item)
        TextView tvOrderFilterItem;

        public OrderFilterTechNoItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderFilterStatusItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_order_filter_item)
        TextView tvOrderFilterItem;

        public OrderFilterStatusItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderFilterProjectItemViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_order_filter_item)
        TextView tvOrderFilterItem;

        public OrderFilterProjectItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



}
