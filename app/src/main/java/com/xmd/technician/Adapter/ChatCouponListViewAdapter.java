package com.xmd.technician.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;

import java.util.List;


/**
 * Created by Administrator on 2016/8/4.
 */
public class ChatCouponListViewAdapter extends BaseAdapter {
    private List<CouponInfo> mData;
    private Context mContext;
    private LayoutInflater layoutInflater;
    private static int DELIVERY_COUPON_TYPE = 0;
    private static int Other_COUPON_TYPE = 1;

    public  ChatCouponListViewAdapter(Context context ,List<CouponInfo> data){
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
    public int getItemViewType(int position) {
        return mData.get(position).useTypeName.equals("点钟券")?DELIVERY_COUPON_TYPE:Other_COUPON_TYPE;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
       if(getItemViewType(position)==DELIVERY_COUPON_TYPE){
           if(convertView==null){
               viewHolder = new ViewHolder();
               convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_delivery_item,null);
               viewHolder.mTvConsumeMoneyDescription = (TextView) convertView.findViewById(R.id.tv_consume_money_description);
               viewHolder.mTvCouponTitle = (TextView) convertView.findViewById(R.id.tv_coupon_title);
               viewHolder.mTvCouponReward = (TextView) convertView.findViewById(R.id.tv_coupon_reward);
               viewHolder.mCouponPeriod = (TextView) convertView.findViewById(R.id.tv_coupon_period);
               viewHolder.mCouponAmount = (TextView) convertView.findViewById(R.id.coupon_amount);
               viewHolder.mCouponType = (TextView) convertView.findViewById(R.id.coupon_type);
               viewHolder.mCouponSelected = (TextView) convertView.findViewById(R.id.coupon_select);
               convertView.setTag(viewHolder);
           }else{
               viewHolder = (ViewHolder)convertView.getTag();
           }
       }else{
           if(convertView==null){
               viewHolder = new ViewHolder();
               convertView = LayoutInflater.from(mContext).inflate(R.layout.chat_coupon_other_item,null);
               viewHolder.mTvConsumeMoneyDescription = (TextView) convertView.findViewById(R.id.tv_consume_money_description);
               viewHolder.mTvCouponTitle = (TextView) convertView.findViewById(R.id.tv_coupon_title);
               viewHolder.mTvCouponReward = (TextView) convertView.findViewById(R.id.tv_coupon_reward);
               viewHolder.mCouponPeriod = (TextView) convertView.findViewById(R.id.tv_coupon_period);
               viewHolder.mCouponAmount = (TextView) convertView.findViewById(R.id.coupon_amount);
               viewHolder.mCouponType = (TextView) convertView.findViewById(R.id.coupon_type);
               viewHolder.mCouponSelected = (TextView) convertView.findViewById(R.id.coupon_select);
               convertView.setTag(viewHolder);
           }else{
               viewHolder = (ViewHolder)convertView.getTag();
           }
       }
        if(mData.get(position).useTypeName.equals("点钟券")){
            viewHolder.mTvCouponTitle.setText("点钟券");
            viewHolder.mCouponType.setVisibility(View.GONE);
        }else{
            viewHolder.mTvCouponTitle.setText(mData.get(position).actTitle);
            viewHolder.mCouponType.setVisibility(View.VISIBLE);
        }
        viewHolder.mTvConsumeMoneyDescription.setText(mData.get(position).consumeMoneyDescription);
        viewHolder.mCouponPeriod.setText("有效时间："+mData.get(position).couponPeriod);
        if (mData.get(position).techCommission > 0||mData.get(position).techBaseCommission>0) {
            String money = Utils.getFloat2Str(String.valueOf(mData.get(position).techCommission>mData.get(position).techBaseCommission?mData.get(position).techCommission:mData.get(position).techBaseCommission));
            String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new TextAppearanceSpan(mContext,R.style.text_bold),3,text.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.mTvCouponReward.setText(spannableString);
        }else {
            viewHolder.mTvCouponReward.setVisibility(View.GONE);
        }
        if(Utils.isNotEmpty(mData.get(position).consumeMoney)){
            viewHolder.mCouponAmount.setText(String.valueOf(mData.get(position).actValue));
        }
        return convertView;
    }

    public static  class  ViewHolder{
        TextView mTvConsumeMoneyDescription;
        TextView mTvCouponTitle;
        TextView mTvCouponReward;
        TextView mCouponPeriod;
        TextView mCouponAmount;
        TextView mCouponType;
        TextView mCouponSelected;

    }
}
