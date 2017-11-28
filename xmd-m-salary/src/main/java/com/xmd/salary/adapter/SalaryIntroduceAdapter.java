package com.xmd.salary.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.salary.R;
import com.xmd.salary.bean.CardItemBean;
import com.xmd.salary.bean.CommodityItemBean;
import com.xmd.salary.bean.OrderItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 17-11-22.
 */

public class SalaryIntroduceAdapter<T> extends RecyclerView.Adapter {

    private static final int COMMODITIES_TYPE = 0;
    private static final int CARD_TYPE = 1;
    private static final int ORDER_TYPE = 2;


    private List<T> mData;
    private Context mContext;

    public SalaryIntroduceAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<T> dataList) {
        mData = dataList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case COMMODITIES_TYPE:
                View commoditiesType = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_commodities_item, parent, false);
                return new CommoditiesViewHolder(commoditiesType);
            case CARD_TYPE:
                View cardType = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_card_item, parent, false);
                return new CardViewHolder(cardType);
            case ORDER_TYPE:
                View orderType = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_item, parent, false);
                return new OrderViewHolder(orderType);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommoditiesViewHolder) {
            CommodityItemBean bean = (CommodityItemBean) mData.get(position);
            CommoditiesViewHolder viewHolder = (CommoditiesViewHolder) holder;
            viewHolder.tvCommoditiesType.setText(bean.commodityName);
            viewHolder.tvCommoditiesCommission.setText(String.valueOf(String.format("%1.1f", bean.commodityCommission / 100f)));
            if (position % 2 == 0) {
                viewHolder.llCommoditiesView.setSelected(true);
            } else {
                viewHolder.llCommoditiesView.setSelected(false);
            }
        } else if (holder instanceof CardViewHolder) {
            CardItemBean bean = (CardItemBean) mData.get(position);
            CardViewHolder viewHolder = (CardViewHolder) holder;
            viewHolder.tvCardTypeName.setText(bean.cardName);
            viewHolder.tvCardTypeDetail.setText("充" + bean.cardPay);
            viewHolder.tvCardDetailName.setText(bean.carItem);
            viewHolder.tvCommoditiesCommission.setText(String.valueOf(String.format("%1.1f", bean.techCommission / 100f)));
            if (position % 2 == 0) {
                viewHolder.llCardView.setSelected(true);
            } else {
                viewHolder.llCardView.setSelected(false);
            }
        } else if (holder instanceof OrderViewHolder) {
            OrderViewHolder viewHolder = (OrderViewHolder) holder;
            OrderItemBean bean = (OrderItemBean) mData.get(position);
            viewHolder.tvOrderType.setText(bean.orderTypeName);
            viewHolder.tvOrderCommission.setText(String.valueOf(bean.orderCommission));
            if (position % 2 == 0) {
                viewHolder.llOrderView.setSelected(true);
            } else {
                viewHolder.llOrderView.setSelected(false);
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof CommodityItemBean) {
            return COMMODITIES_TYPE;
        } else if (mData.get(position) instanceof CardItemBean) {
            return CARD_TYPE;
        } else {
            return ORDER_TYPE;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class CommoditiesViewHolder extends RecyclerView.ViewHolder {
        TextView tvCommoditiesType;
        TextView tvCommoditiesCommission;
        LinearLayout llCommoditiesView;

        CommoditiesViewHolder(View view) {
            super(view);
            tvCommoditiesType = (TextView) view.findViewById(R.id.tv_commodities_type);
            tvCommoditiesCommission = (TextView) view.findViewById(R.id.tv_commodities_commission);
            llCommoditiesView = (LinearLayout) view.findViewById(R.id.ll_commodities_view);
        }
    }

    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView tvCardTypeName;
        TextView tvCardTypeDetail;
        TextView tvCardDetailName;
        TextView tvCardItemDetail;
        TextView tvCommoditiesCommission;
        LinearLayout llCardView;

        CardViewHolder(View view) {
            super(view);
            tvCardTypeName = (TextView) view.findViewById(R.id.tv_card_type_name);
            tvCardTypeDetail = (TextView) view.findViewById(R.id.tv_card_type_detail);
            tvCardDetailName = (TextView) view.findViewById(R.id.tv_card_detail_name);
            tvCardItemDetail = (TextView) view.findViewById(R.id.tv_card_item_detail);
            tvCommoditiesCommission = (TextView) view.findViewById(R.id.tv_commodities_commission);
            llCardView = (LinearLayout) view.findViewById(R.id.ll_card_view);

        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderType;
        TextView tvOrderCommission;
        LinearLayout llOrderView;

        OrderViewHolder(View view) {
            super(view);
            tvOrderType = (TextView) view.findViewById(R.id.tv_order_type);
            tvOrderCommission = (TextView) view.findViewById(R.id.tv_order_commission);
            llOrderView = (LinearLayout) view.findViewById(R.id.ll_order_view);
        }
    }


    static class ServiceItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvServiceName;

        public ServiceItemViewHolder(View itemView) {
            super(itemView);
            tvServiceName = (TextView) itemView.findViewById(R.id.tv_service_name);
        }
    }

}
