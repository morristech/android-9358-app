package com.xmd.technician.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.Order;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.CircleImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/10/26.
 */
public class MainPageTechOrderListAdapter extends BaseAdapter {

    private List<Order> mData;
    private Context mContext;

    public MainPageTechOrderListAdapter(Context context, List<Order> data) {
        this.mContext = context;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Order order = mData.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_tech_order_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext).load(order.headImgUrl).into(viewHolder.mainOrderAvatar);
        viewHolder.orderName.setText(order.userName);
        viewHolder.orderPhoneDetail.setText(order.phoneNum);
        viewHolder.orderTimeDetail.setText(order.formatAppointTime);
        viewHolder.orderMoneyDetail.setText(order.rewardAmount + "");
        viewHolder.mainTechOrderSurplusTimeDetail.setText(order.remainTime + "");
        if (order.status.equals(RequestConstant.KEY_ORDER_STATUS_SUBMIT)) {
            viewHolder.mainTechOrderBtnAccept.setText(ResourceUtils.getString(R.string.accept));
            viewHolder.mainTechOrderBtnReject.setVisibility(View.GONE);
            viewHolder.mainHandle.setVisibility(View.VISIBLE);
            viewHolder.mainTechOrderBtnAccept.setOnClickListener(v1 -> {
                doManageOrder(RequestConstant.KEY_ORDER_STATUS_ACCEPT, order.orderId, "");
            });
        } else if (order.status.equals(RequestConstant.KEY_ORDER_STATUS_ACCEPT)) {
            viewHolder.mainTechOrderBtnAccept.setText(ResourceUtils.getString(R.string.finish));
            viewHolder.mainTechOrderBtnReject.setText(ResourceUtils.getString(R.string.order_cancel));
            viewHolder.mainTechOrderBtnReject.setVisibility(View.VISIBLE);
            viewHolder.mainHandle.setVisibility(View.GONE);
            viewHolder.mainTechOrderBtnAccept.setOnClickListener(v1 -> {
                doManageOrder(RequestConstant.KEY_ORDER_STATUS_COMPLETE, order.orderId, "");
            });
        }

        viewHolder.mainTechOrderBtnReject.setOnClickListener(v1 -> {
            doManageOrder(RequestConstant.KEY_ORDER_STATUS_FAILURE, order.orderId, "");
        });
        viewHolder.mainOrderAvatar.setOnClickListener(v -> {
            MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(order.emchatId, Utils.isEmpty(order.customerName) ? order.customerName : order.userName,
                    order.headImgUrl, ""));
        });
        return convertView;
    }

    private void doManageOrder(String type, String orderId, String reason) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PROCESS_TYPE, type);
        params.put(RequestConstant.KEY_ID, orderId);
        params.put(RequestConstant.KEY_REASON, reason);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_MANAGE_ORDER, params);
    }

    final static class ViewHolder {
        @Bind(R.id.main_order_avatar)
        CircleImageView mainOrderAvatar;
        @Bind(R.id.order_name)
        TextView orderName;
        @Bind(R.id.order_phone_detail)
        TextView orderPhoneDetail;
        @Bind(R.id.order_time_detail)
        TextView orderTimeDetail;
        @Bind(R.id.order_money_detail)
        TextView orderMoneyDetail;
        @Bind(R.id.main_tech_order_surplus_time_detail)
        TextView mainTechOrderSurplusTimeDetail;
        @Bind(R.id.main_tech_order_btn_accept)
        Button mainTechOrderBtnAccept;
        @Bind(R.id.main_tech_order_btn_reject)
        Button mainTechOrderBtnReject;
        @Bind(R.id.main_oder_to_handle)
        RelativeLayout mainHandle;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
