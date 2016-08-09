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
    private static final int Other_COUPON_TYPE = 1;
    private OnItemClickListener mOnItemClick;

    public ChatCouponAdapter(Context context , List<CouponInfo> data,OnItemClickListener itemClickListener){
        this.mContext = context;
        this.mData = data;
        this.mOnItemClick = itemClickListener;
    }
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).useTypeName.equals("点钟券")?DELIVERY_COUPON_TYPE:Other_COUPON_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(DELIVERY_COUPON_TYPE==viewType){
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_other_item,parent,false);
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
               chatCouponViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(6,couponInfo.actTitle));
               chatCouponViewHolder.mCouponType.setVisibility(View.VISIBLE);
            }
           chatCouponViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
           chatCouponViewHolder.mCouponPeriod.setText("有效时间："+Utils.StrSubstring(18,couponInfo.couponPeriod));
            if (couponInfo.techCommission > 0||couponInfo.techBaseCommission>0) {
                String money = Utils.getFloat2Str(String.valueOf(couponInfo.techCommission>couponInfo.techBaseCommission?couponInfo.techCommission:couponInfo.techBaseCommission));
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
            chatCouponViewHolder.itemView.setOnClickListener(v -> mOnItemClick.onItemCheck(position,chatCouponViewHolder.mTextCheck));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
    public  interface OnItemClickListener{
       void onItemCheck(int position,View view);
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
