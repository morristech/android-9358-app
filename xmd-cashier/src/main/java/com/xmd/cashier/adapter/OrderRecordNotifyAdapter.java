package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OrderRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-18.
 */

public class OrderRecordNotifyAdapter extends RecyclerView.Adapter<OrderRecordNotifyAdapter.ViewHolder> {
    private int mTempCount;
    private List<OrderRecordInfo> mData = new ArrayList<>();
    private Context mContext;
    private OrderRecordNotifyCallBack mCallBack;

    public OrderRecordNotifyAdapter() {
        mContext = MainApplication.getInstance().getApplicationContext();
    }

    public void setData(List<OrderRecordInfo> list) {
        mTempCount = list.size();
        this.mData.addAll(list);
    }

    public void setCallBack(OrderRecordNotifyCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_record_notify, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OrderRecordInfo info = mData.get(position);
        if (mTempCount == 1) {
            holder.mNotifyCount.setVisibility(View.GONE);
        } else {
            holder.mNotifyCount.setVisibility(View.VISIBLE);
            String temp = String.format("(%d/%d)", info.tempNo, mTempCount);
            holder.mNotifyCount.setText(Utils.changeColor(temp, mContext.getResources().getColor(R.color.colorStatusYellow), 1, temp.length() - 1));
        }
        holder.mArriveTime.setText(info.appointTime);
        holder.mCustomerName.setText(info.customerName);
        holder.mCustomerPhone.setText(info.phoneNum);
        holder.mTechName.setText(info.techName);
        holder.mTechNo.setText(info.techSerialNo);
        holder.mServiceItem.setText(TextUtils.isEmpty(info.itemName) ? "到店选择" : info.itemName);
        holder.mPayDown.setText("￥" + info.downPayment);
        holder.mImageOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onClose(position);
            }
        });
        holder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onAccept(info, position);
            }
        });
        holder.mRejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onReject(info, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageOff;
        private TextView mNotifyCount;
        private TextView mArriveTime;
        private TextView mCustomerPhone;
        private TextView mCustomerName;
        private TextView mTechName;
        private TextView mTechNo;
        private TextView mServiceItem;
        private TextView mPayDown;
        private Button mAcceptBtn;
        private Button mRejectBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageOff = (ImageView) itemView.findViewById(R.id.notify_order_record_off);
            mNotifyCount = (TextView) itemView.findViewById(R.id.notify_order_record_count);
            mArriveTime = (TextView) itemView.findViewById(R.id.notify_order_arrive_time);
            mCustomerName = (TextView) itemView.findViewById(R.id.notify_order_customer_name);
            mCustomerPhone = (TextView) itemView.findViewById(R.id.notify_order_customer_phone);
            mTechName = (TextView) itemView.findViewById(R.id.notify_order_tech_name);
            mTechNo = (TextView) itemView.findViewById(R.id.notify_order_tech_no);
            mServiceItem = (TextView) itemView.findViewById(R.id.notify_order_service_item);
            mPayDown = (TextView) itemView.findViewById(R.id.notify_order_pay_down);
            mAcceptBtn = (Button) itemView.findViewById(R.id.notify_order_accept);
            mRejectBtn = (Button) itemView.findViewById(R.id.notify_order_reject);
        }
    }

    public interface OrderRecordNotifyCallBack {
        void onAccept(OrderRecordInfo info, int position);

        void onReject(OrderRecordInfo info, int position);

        void onClose(int position);
    }
}
