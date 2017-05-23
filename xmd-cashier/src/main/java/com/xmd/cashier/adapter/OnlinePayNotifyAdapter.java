package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OnlinePayInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-18.
 */

public class OnlinePayNotifyAdapter extends RecyclerView.Adapter<OnlinePayNotifyAdapter.ViewHolder> {
    private int tempCount;
    private List<OnlinePayInfo> mData = new ArrayList<>();
    private Context mContext;
    private OnlinePayNotifyCallBack mCallBack;

    public OnlinePayNotifyAdapter() {
        mContext = MainApplication.getInstance().getApplicationContext();
    }

    public void setData(List<OnlinePayInfo> list) {
        tempCount = list.size();
        mData.addAll(list);
    }

    public void setCallBack(OnlinePayNotifyCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void removeItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_pay_notify, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final OnlinePayInfo info = mData.get(position);
        if (tempCount == 1) {
            holder.mNotifyCount.setVisibility(View.GONE);
        } else {
            holder.mNotifyCount.setVisibility(View.VISIBLE);
            String temp = String.format("(%d/%d)", info.tempNo, tempCount);
            holder.mNotifyCount.setText(Utils.changeColor(temp, mContext.getResources().getColor(R.color.colorStatusYellow), 1, temp.length() - 1));
        }
        holder.mCustomerName.setText(info.userName);
        holder.mCustomerPhone.setText(info.telephone);

        if (TextUtils.isEmpty(info.techName)) {
            holder.mTechInfoRow.setVisibility(View.INVISIBLE);
        } else {
            holder.mTechInfoRow.setVisibility(View.VISIBLE);
            holder.mTechName.setText(info.techName);
            if (TextUtils.isEmpty(info.techNo)) {
                holder.mTechNo.setVisibility(View.INVISIBLE);
            } else {
                holder.mTechNo.setVisibility(View.VISIBLE);
                holder.mTechNo.setText(info.techNo);
            }
        }

        holder.mPayDown.setText("￥" + Utils.moneyToStringEx(info.payAmount));
        holder.mCreateTime.setText(info.createTime);
        holder.mImageOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onClose(position);
            }
        });
        holder.mPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onPass(info, position);
            }
        });
        holder.mUnpassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallBack.onUnpass(info, position);
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
        private TextView mCustomerPhone;
        private TextView mCustomerName;
        private TableRow mTechInfoRow;
        private TextView mTechName;
        private TextView mTechNo;
        private TextView mPayDown;
        private TextView mCreateTime;
        private Button mPassBtn;
        private Button mUnpassBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageOff = (ImageView) itemView.findViewById(R.id.notify_online_pay_off);
            mNotifyCount = (TextView) itemView.findViewById(R.id.notify_online_pay_count);
            mCustomerName = (TextView) itemView.findViewById(R.id.notify_online_pay_customer_name);
            mCustomerPhone = (TextView) itemView.findViewById(R.id.notify_online_pay_customer_phone);
            mTechInfoRow = (TableRow) itemView.findViewById(R.id.tr_tech_info);
            mTechName = (TextView) itemView.findViewById(R.id.notify_online_pay_tech_name);
            mTechNo = (TextView) itemView.findViewById(R.id.notify_online_pay_tech_no);
            mPayDown = (TextView) itemView.findViewById(R.id.notify_online_pay_pay_down);
            mCreateTime = (TextView) itemView.findViewById(R.id.notify_online_pay_create_time);
            mPassBtn = (Button) itemView.findViewById(R.id.notify_online_pay_pass);
            mUnpassBtn = (Button) itemView.findViewById(R.id.notify_online_pay_unpass);
        }
    }

    public interface OnlinePayNotifyCallBack {
        void onPass(OnlinePayInfo info, int position);

        void onUnpass(OnlinePayInfo info, int position);

        void onClose(int position);
    }
}
