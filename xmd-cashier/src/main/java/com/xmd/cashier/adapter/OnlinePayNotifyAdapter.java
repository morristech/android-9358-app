package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.xmd.cashier.MainApplication;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.OnlinePayInfo;
import com.xmd.cashier.dal.bean.PayCouponInfo;

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
        switch (info.status) {
            case AppConstants.STATUS_DETAIL:
                PayCouponInfo payCouponInfo = info.payCouponInfo;
                holder.mLayoutLoading.setVisibility(View.GONE);
                holder.mLayoutNormal.setVisibility(View.GONE);
                holder.mLayoutDetail.setVisibility(View.VISIBLE);
                holder.mCouponName.setText(payCouponInfo.actTitle);
                holder.mCouponDescription.setText(payCouponInfo.consumeMoneyDescription);
                holder.mCouponNo.setText(payCouponInfo.businessNo);
                holder.mCouponCode.setText(payCouponInfo.couponNo);
                holder.mCouponTypeName.setText(payCouponInfo.couponTypeName);
                holder.mCouponUseTime.setText(payCouponInfo.couponPeriod);
                if (TextUtils.isEmpty(payCouponInfo.actContent)) {
                    holder.mCouponActContent.setVisibility(View.GONE);
                    holder.mCouponActContentNull.setVisibility(View.VISIBLE);
                } else {
                    holder.mCouponActContent.setVisibility(View.VISIBLE);
                    holder.mCouponActContentNull.setVisibility(View.GONE);
                    holder.mCouponActContent.loadDataWithBaseURL(null, payCouponInfo.actContent, "text/html", "utf-8", null);
                }
                if (payCouponInfo.items != null && !payCouponInfo.items.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (PayCouponInfo.ServiceItem item : payCouponInfo.items) {
                        builder.append(item.name).append("，");
                    }
                    builder.setLength(builder.length() - 1);
                    holder.mCouponServices.setText(builder.toString());
                } else {
                    holder.mCouponServices.setText("未指定");
                }
                holder.mBtnReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallBack.onReturn(position);
                    }
                });
                break;
            case AppConstants.STATUS_LOADING:
                holder.mLayoutLoading.setVisibility(View.VISIBLE);
                holder.mLayoutNormal.setVisibility(View.GONE);
                holder.mLayoutDetail.setVisibility(View.GONE);
                break;
            case AppConstants.STATUS_NORMAL:
            default:
                holder.mLayoutNormal.setVisibility(View.VISIBLE);
                holder.mLayoutDetail.setVisibility(View.GONE);
                holder.mLayoutLoading.setVisibility(View.GONE);
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
                if (TextUtils.isEmpty(info.tempErrMsg)) {
                    holder.mTipText.setVisibility(View.GONE);
                    holder.mTipBtn.setVisibility(View.GONE);
                    holder.mOperateLayout.setVisibility(View.VISIBLE);
                    holder.mPassBtn.setEnabled(true);
                    holder.mUnpassBtn.setEnabled(true);
                } else {
                    holder.mTipText.setVisibility(View.VISIBLE);
                    holder.mTipText.setText(info.tempErrMsg);
                    holder.mTipBtn.setVisibility(View.VISIBLE);
                    holder.mOperateLayout.setVisibility(View.GONE);
                }

                holder.mOriginMoney.setText("￥" + Utils.moneyToStringEx(info.originalAmount));
                OnlinePayDiscountAdapter discountAdapter = new OnlinePayDiscountAdapter();
                discountAdapter.setCallBack(new OnlinePayDiscountAdapter.CallBack() {
                    @Override
                    public void onItemClick(OnlinePayInfo.OnlinePayDiscountInfo info) {
                        mCallBack.onDetail(info, position);
                    }
                });
                discountAdapter.setData(info.orderDiscountList);
                holder.mDiscountList.setLayoutManager(new LinearLayoutManager(mContext));
                holder.mDiscountList.setAdapter(discountAdapter);

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
                holder.mTipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCallBack.onClose(position);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setNormalStatus(int position, String error) {
        mData.get(position).status = AppConstants.STATUS_NORMAL;
        mData.get(position).tempErrMsg = error;
        notifyItemChanged(position);
    }

    public void setLoadingStatus(int position) {
        mData.get(position).status = AppConstants.STATUS_LOADING;
        notifyItemChanged(position);
    }

    public void setNormalStatus(int position) {
        mData.get(position).status = AppConstants.STATUS_NORMAL;
        notifyItemChanged(position);
    }

    public void setDetailStatus(int position, PayCouponInfo info) {
        mData.get(position).payCouponInfo = info;
        mData.get(position).status = AppConstants.STATUS_DETAIL;
        notifyItemChanged(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 正常数据
        private LinearLayout mLayoutNormal;
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
        private LinearLayout mOperateLayout;
        private Button mTipBtn;
        private TextView mTipText;
        private TextView mOriginMoney;
        private RecyclerView mDiscountList;

        // 正在加载
        private LinearLayout mLayoutLoading;

        // 详情数据
        private LinearLayout mLayoutDetail;
        private TextView mCouponName;
        private TextView mCouponDescription;
        private TextView mCouponNo;
        private TextView mCouponCode;
        private TextView mCouponTypeName;
        private TextView mCouponServices;
        private TextView mCouponUseTime;
        private WebView mCouponActContent;
        private TextView mCouponActContentNull;
        private Button mBtnReturn;

        public ViewHolder(View itemView) {
            super(itemView);
            mLayoutNormal = (LinearLayout) itemView.findViewById(R.id.layout_online_pay_normal);
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
            mOperateLayout = (LinearLayout) itemView.findViewById(R.id.notify_online_pay_ly_operate);
            mTipBtn = (Button) itemView.findViewById(R.id.notify_online_pay_btn_tip);
            mTipText = (TextView) itemView.findViewById(R.id.notify_online_pay_tip);
            mOriginMoney = (TextView) itemView.findViewById(R.id.item_origin_money);
            mDiscountList = (RecyclerView) itemView.findViewById(R.id.item_discount_list);

            mLayoutLoading = (LinearLayout) itemView.findViewById(R.id.layout_online_pay_loading);

            mLayoutDetail = (LinearLayout) itemView.findViewById(R.id.layout_online_pay_detail);
            mCouponName = (TextView) itemView.findViewById(R.id.tv_discount_coupon_name);
            mCouponDescription = (TextView) itemView.findViewById(R.id.tv_coupon_description);
            mCouponNo = (TextView) itemView.findViewById(R.id.tv_coupon_no);
            mCouponCode = (TextView) itemView.findViewById(R.id.tv_coupon_code);
            mCouponTypeName = (TextView) itemView.findViewById(R.id.tv_coupon_type);
            mCouponServices = (TextView) itemView.findViewById(R.id.tv_coupon_service);
            mCouponUseTime = (TextView) itemView.findViewById(R.id.tv_coupon_use_time);
            mCouponActContent = (WebView) itemView.findViewById(R.id.wb_act_content);
            mCouponActContentNull = (TextView) itemView.findViewById(R.id.tv_act_content_null);
            mBtnReturn = (Button) itemView.findViewById(R.id.btn_return);
        }
    }

    public interface OnlinePayNotifyCallBack {
        void onPass(OnlinePayInfo info, int position);

        void onUnpass(OnlinePayInfo info, int position);

        void onClose(int position);

        void onReturn(int position);

        void onDetail(OnlinePayInfo.OnlinePayDiscountInfo info, int position);
    }
}
