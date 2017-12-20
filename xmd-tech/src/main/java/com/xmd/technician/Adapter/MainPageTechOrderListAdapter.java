package com.xmd.technician.Adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xmd.app.widget.CircleAvatarView;
import com.xmd.technician.R;
import com.xmd.technician.bean.Order;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2016/10/26.
 */
public class MainPageTechOrderListAdapter extends RecyclerView.Adapter {
    private List<Order> mData;
    private Context mContext;
    private ItemClickedInterface mInterface;

    public interface ItemClickedInterface{
        void itemClicked(Order bean);
    }

    public MainPageTechOrderListAdapter(Context context, List<Order> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setOnItemClickedListener(ItemClickedInterface itemClickedInterface){
        this.mInterface = itemClickedInterface;
    }

    public void setData(List<Order> orders){
        this.mData = orders;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(mContext).inflate(R.layout.main_tech_order_item, parent, false);
        return new OrderViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OrderViewHolder viewHolder = (OrderViewHolder) holder;
        Order order = mData.get(position);
        if (Utils.isNotEmpty(order.customerName)) {
            viewHolder.orderName.setText(order.customerName);
        } else {
            viewHolder.orderName.setText(order.userName);
        }
        viewHolder.orderPhoneDetail.setText(order.phoneNum);
        viewHolder.orderTimeDetail.setText(order.formatAppointTime);
        if (order.orderType.equals(RequestConstant.ORDER_TYPE_APPOINT)) {
            viewHolder.orderMoney.setVisibility(View.GONE);
            viewHolder.orderMoneyDetail.setVisibility(View.GONE);
        } else {
            viewHolder.orderMoney.setVisibility(View.VISIBLE);
            viewHolder.orderMoneyDetail.setVisibility(View.VISIBLE);
            String orderMoney = order.downPayment + "元";
            viewHolder.orderMoneyDetail.setText(Utils.changeColor(orderMoney, ResourceUtils.getColor(R.color.color_main_btn), 0, orderMoney.length() - 1));
            if (order.payType == 2) {
                viewHolder.mPaidMark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mPaidMark.setVisibility(View.GONE);
            }
        }
        viewHolder.mainTechOrderSurplusTimeDetail.setText(order.remainTime + "");
        if (Utils.isEmpty(order.innerProvider)) {
            if (order.status.equals(RequestConstant.KEY_ORDER_STATUS_SUBMIT)) {
                viewHolder.mainTechOrderBtnAccept.setText(ResourceUtils.getString(R.string.accept));
                viewHolder.mainOderToHandle.setVisibility(View.VISIBLE);
                viewHolder.mainTechOrderBtnAccept.setOnClickListener(v1 -> {
                    doManageOrder(RequestConstant.KEY_ORDER_STATUS_ACCEPT, order.orderId, "");
                });
            } else if (order.status.equals(RequestConstant.KEY_ORDER_STATUS_ACCEPT)) {
                viewHolder.mainTechOrderBtnAccept.setText(ResourceUtils.getString(R.string.finish));
                viewHolder.mainOderToHandle.setVisibility(View.GONE);
                viewHolder.mainTechOrderBtnAccept.setOnClickListener(v1 -> {
                    doManageOrder(RequestConstant.KEY_ORDER_STATUS_COMPLETE, order.orderId, "");
                });
            }
        } else {
            viewHolder.mainOderToHandle.setVisibility(View.GONE);
            viewHolder.mainTechOrderBtnAccept.setText(ResourceUtils.getString(R.string.know));
            viewHolder.mainTechOrderBtnAccept.setOnClickListener(v1 -> {
                doManageOrderDisappear(order.orderId);
            });
        }
        viewHolder.mainOrderAvatar.setUserInfo(order.userId, order.headImgUrl, false);
        viewHolder.itemView.setOnClickListener(v1-> {
            if (mInterface != null){
                mInterface.itemClicked(order);
            }
        });
    }

    private void doManageOrderDisappear(String orderId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ORDER_ID, orderId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_ORDER_INNER_READ, params);
    }

    private void doManageOrder(String type, String orderId, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }


    static class OrderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.main_order_avatar)
        CircleAvatarView mainOrderAvatar;
        @BindView(R.id.order_name)
        TextView orderName;
        @BindView(R.id.order_phone)
        TextView orderPhone;
        @BindView(R.id.order_phone_detail)
        TextView orderPhoneDetail;
        @BindView(R.id.order_time)
        TextView orderTime;
        @BindView(R.id.order_time_detail)
        TextView orderTimeDetail;
        @BindView(R.id.order_money)
        TextView orderMoney;
        @BindView(R.id.order_money_detail)
        TextView orderMoneyDetail;
        @BindView(R.id.paid_mark)
        TextView mPaidMark;
        @BindView(R.id.main_tech_order_surplus_time)
        TextView mainTechOrderSurplusTime;
        @BindView(R.id.main_tech_order_surplus_time_detail)
        TextView mainTechOrderSurplusTimeDetail;
        @BindView(R.id.text_mine)
        TextView textMine;
        @BindView(R.id.main_oder_to_handle)
        RelativeLayout mainOderToHandle;
        @BindView(R.id.main_tech_order_btn_accept)
        Button mainTechOrderBtnAccept;
        @BindView(R.id.ll_order_right)
        LinearLayout llOrderRight;

        OrderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
