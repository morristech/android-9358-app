package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Administrator on 2016/8/4.
 */
public class ChatCouponAdapter extends RecyclerView.Adapter{
    private List<CouponInfo> mData;
    private Context mContext;
    private static final int DELIVERY_COUPON_TYPE = 0;
    private static final int CASH_COUPON_TYPE = 1;
    private static final int FAVORABLE_COUPON_TYPE = 2;
    private OnItemClickListener mOnItemClick;

    public ChatCouponAdapter(Context context , List<CouponInfo> data){
        this.mContext = context;
        this.mData = data;
    }
    public void setCouponInfoData(List<CouponInfo> data){
        this.mData = data;
        notifyDataSetChanged();
    }
    public  interface OnItemClickListener{
        void onItemCheck(CouponInfo info,int position,boolean idChecked);
    }
    public void setOnItemClickListener(OnItemClickListener itemClickListener){
        this.mOnItemClick = itemClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        if (((CouponInfo) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.cash_coupon))) {
            return CASH_COUPON_TYPE;
        } else if (((CouponInfo) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
            return DELIVERY_COUPON_TYPE;
        } else {
            return FAVORABLE_COUPON_TYPE;
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(DELIVERY_COUPON_TYPE==viewType){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_cash_item,parent,false);
            return new ChatCouponViewHolder(view);
        }else if(FAVORABLE_COUPON_TYPE == viewType){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_favorable_item,parent,false);
            return new ChatCouponViewHolder(view);
        }else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_delivery_item,parent,false);
            return new ChatCouponViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ChatCouponViewHolder){
            Object obj = mData.get(position);
            if (!(obj instanceof CouponInfo)) {
                return;
            }
            final CouponInfo couponInfo = (CouponInfo) obj;
            ChatCouponViewHolder chatCouponViewHolder = (ChatCouponViewHolder) holder;
            chatCouponViewHolder.mTvCouponTitle.setText(couponInfo.actTitle);
            if(couponInfo.useTypeName.equals("点钟券")){
               chatCouponViewHolder.mTvCouponTitle.setText("点钟券");
                chatCouponViewHolder.mCouponType.setVisibility(View.GONE);
            }else{
               chatCouponViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(6,couponInfo.actTitle,true));
               chatCouponViewHolder.mCouponType.setVisibility(View.VISIBLE);
            }
           chatCouponViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
           chatCouponViewHolder.mCouponPeriod.setText("有效时间："+Utils.StrSubstring(18,couponInfo.couponPeriod,true));
            if (couponInfo.commission > 0) {
                String money = Utils.getFloat2Str(String.valueOf(couponInfo.commission));
                String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new TextAppearanceSpan(mContext,R.style.text_bold),3,text.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                chatCouponViewHolder.mTvCouponReward.setText(spannableString);
            }else{
                chatCouponViewHolder.mTvCouponReward.setVisibility(View.GONE);
            }
            if(Utils.isNotEmpty(couponInfo.consumeMoney)){
                chatCouponViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
            }
            if(couponInfo.selectedStatus == 1){
                chatCouponViewHolder.mTextCheck.setSelected(false);
                chatCouponViewHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(couponInfo,position,false));
            }else{
                chatCouponViewHolder.mTextCheck.setSelected(true);
                chatCouponViewHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(couponInfo,position,true));
            }

        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class ChatCouponViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_consume_money_description) TextView mTvConsumeMoneyDescription;
        @Bind(R.id.tv_coupon_title) TextView mTvCouponTitle;
        @Bind(R.id.tv_coupon_reward) TextView mTvCouponReward;
        @Bind(R.id.tv_coupon_period) TextView mCouponPeriod;
        @Bind(R.id.coupon_amount)TextView mCouponAmount;
        @Bind(R.id.coupon_type)TextView mCouponType;
        @Bind(R.id.coupon_select)TextView mTextCheck;

        public ChatCouponViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
