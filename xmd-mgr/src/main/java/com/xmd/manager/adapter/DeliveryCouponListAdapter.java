package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 15-12-11.
 */
public class DeliveryCouponListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public interface CallBackListener {
        void onSelectedItemClicked(CouponInfo couponInfo, Integer position, boolean isSelected);
    }

    private List<CouponInfo> mCouponInfoList;
    private CallBackListener mCallBackListener;
    private Context mContext;


    public DeliveryCouponListAdapter(Context context, List<CouponInfo> couponInfoList, CallBackListener onCallClickListener) {
        mContext = context;
        mCouponInfoList = couponInfoList;
        mCallBackListener = onCallClickListener;
        notifyDataSetChanged();
    }

    public void setData(List<CouponInfo> couponInfoList) {
        mCouponInfoList = couponInfoList;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.delivery_coupon_list_item, parent, false);
        return new DeliveryCouponItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof DeliveryCouponItemViewHolder) {
            final CouponInfo couponInfo = mCouponInfoList.get(position);
            DeliveryCouponItemViewHolder chatCouponViewHolder = (DeliveryCouponItemViewHolder) holder;
            ;
            chatCouponViewHolder.tvCouponTitle.setText(Utils.StrSubstring(8, couponInfo.actTitle, true));
            chatCouponViewHolder.couponType.setVisibility(View.VISIBLE);
            chatCouponViewHolder.couponType.setText(Utils.isNotEmpty(couponInfo.couponTypeName) ? String.format("(%s)", couponInfo.couponTypeName) : couponInfo.couponTypeName);
            chatCouponViewHolder.tvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
            chatCouponViewHolder.tvCouponPeriod.setText("有效时间：" + Utils.StrSubstring(18, couponInfo.couponPeriod, true));
            if (Float.parseFloat(couponInfo.commission) > 0) {
                chatCouponViewHolder.tvCouponReward.setVisibility(View.VISIBLE);
                String money = couponInfo.commission;
                String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new TextAppearanceSpan(mContext, R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                chatCouponViewHolder.tvCouponReward.setText(spannableString);
            } else {
                chatCouponViewHolder.tvCouponReward.setVisibility(View.GONE);
            }
            if (couponInfo.couponType.equals("discount")) {
                chatCouponViewHolder.imgMoneyMark.setVisibility(View.GONE);
                chatCouponViewHolder.couponEmptyView.setVisibility(View.VISIBLE);
                chatCouponViewHolder.couponAmount.setText(String.format("%1.1f折", couponInfo.actValue / 100f));
            } else if (couponInfo.couponType.equals("gift")) {
                chatCouponViewHolder.couponEmptyView.setVisibility(View.VISIBLE);
                chatCouponViewHolder.imgMoneyMark.setVisibility(View.GONE);
                chatCouponViewHolder.couponAmount.setText(TextUtils.isEmpty(couponInfo.actSubTitle) ? couponInfo.actTitle : couponInfo.actSubTitle);
            } else {
                chatCouponViewHolder.couponEmptyView.setVisibility(View.GONE);
                chatCouponViewHolder.imgMoneyMark.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(couponInfo.consumeMoneyDescription)) {
                    chatCouponViewHolder.couponAmount.setText(String.valueOf(couponInfo.actValue));
                }
            }
            if (couponInfo.isSelected == 1) {
                chatCouponViewHolder.couponSelect.setSelected(true);
            } else {
                chatCouponViewHolder.couponSelect.setSelected(false);
            }
            chatCouponViewHolder.itemView.setOnClickListener(v -> {
                if (couponInfo.isSelected == 0) {
                 //   chatCouponViewHolder.couponSelect.setSelected(true);
                    mCallBackListener.onSelectedItemClicked(couponInfo, position, true);
                } else if (couponInfo.isSelected == 1) {
                //    chatCouponViewHolder.couponSelect.setSelected(true);
                    mCallBackListener.onSelectedItemClicked(couponInfo, position, false);
                }
            });


        }
    }

    @Override
    public int getItemCount() {
        return mCouponInfoList.size();
    }

    static class DeliveryCouponItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_coupon_title)
        TextView tvCouponTitle;
        @BindView(R.id.coupon_type)
        TextView couponType;
        @BindView(R.id.tv_coupon_reward)
        TextView tvCouponReward;
        @BindView(R.id.coupon_empty_view)
        View couponEmptyView;
        @BindView(R.id.img_money_mark)
        ImageView imgMoneyMark;
        @BindView(R.id.coupon_amount)
        TextView couponAmount;
        @BindView(R.id.tv_consume_money_description)
        TextView tvConsumeMoneyDescription;
        @BindView(R.id.tv_coupon_period)
        TextView tvCouponPeriod;
        @BindView(R.id.coupon_select)
        TextView couponSelect;
        @BindView(R.id.ll_view)
        LinearLayout llView;

        DeliveryCouponItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


}
