package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.window.PaidCouponDetailActivity;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-11-24.
 */
public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {

    public interface OnManageButtonClickedListener<T> {

        void onItemClicked(T bean);

        void onNegativeButtonClicked(T bean);

        void onPositiveButtonClicked(T bean);

        void onLoadMoreButtonClicked();
    }

    private static final int TYPE_ORDER_ITEM = 0;
    private static final int TYPE_COUPON_INFO_ITEM = 1;
    private static final int TYPE_PAID_COUPON_USER_DETAIL = 2;
    private static final int TYPE_OTHER_ITEM = 98;
    private static final int TYPE_FOOTER = 99;


    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;

    private List<T> mData;
    private OnManageButtonClickedListener mOnManageButtonClickedListener;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSlideHelper mHelper;
    private boolean mIsSlideable;

    public ListRecycleViewAdapter(Context context, List<T> data, OnManageButtonClickedListener onManageButtonClickedListener, boolean isSlideable) {
        mContext = context;
        mData = data;
        mOnManageButtonClickedListener = onManageButtonClickedListener;
        mHelper = new ItemSlideHelper(mContext, this);
        mIsSlideable = isSlideable;
    }

    public void setData(List<T> data) {
        mData = data;
        mIsEmpty = data.isEmpty();
        notifyDataSetChanged();
        mHelper.clearTargetView();
    }

    /**
     * There is data, but need to show footer "No More"
     *
     * @param isNoMore
     */
    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            if (mData.get(position) instanceof Order) {
                // Order
                return TYPE_ORDER_ITEM;
            } else if (mData.get(position) instanceof CouponInfo) {
                return TYPE_COUPON_INFO_ITEM;
            } else if (mData.get(position) instanceof PaidCouponUserDetail) {
                return TYPE_PAID_COUPON_USER_DETAIL;
            } else {
                return TYPE_OTHER_ITEM;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_ORDER_ITEM == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
            return new OrderListItemViewHolder(view);
        } else if (TYPE_COUPON_INFO_ITEM == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_item, parent, false);
            return new CouponListItemViewHolder((view));
        } else if (TYPE_PAID_COUPON_USER_DETAIL == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paid_coupon_user_detail_list_item, parent, false);
            return new PaidCouponUserDetailItemViewHolder((view));
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new ListFooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof OrderListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof Order)) {
                return;
            }

            final Order order = (Order) obj;
            OrderListItemViewHolder itemHolder = (OrderListItemViewHolder) holder;
            holder.itemView.scrollTo(0, 0);

            Glide.with(mContext).load(order.headImgUrl).into(itemHolder.mUserHeadUrl);
            itemHolder.mUserHeadUrl.setOnClickListener(v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, order.emchatId));
            itemHolder.mUserName.setText(order.customerName);
            itemHolder.mOrderTime.setText(order.formatAppointTime);
            itemHolder.mOrderAmount.setText(String.format(ResourceUtils.getString(R.string.amount_unit_format), order.downPayment));

            // 1) 提交状态能够进行拒绝或者接受
            // 2) 只有普通预约的订单才能由技师来实现完成跟失效，付费预约的必须在核销或者失效的时候更改状态
            itemHolder.mNegative.setVisibility(View.VISIBLE);
            if ((Constant.ORDER_STATUS_ACCEPT.equals(order.status) && Constant.ORDER_TYPE_PAID.equals(order.orderType))) {
                itemHolder.mSubmitSection.setVisibility(View.GONE);
                itemHolder.mOtherStatus.setVisibility(View.VISIBLE);
                itemHolder.mOtherStatus.setText(order.statusName);
                itemHolder.mOperation.setVisibility(View.GONE);
                itemHolder.isOperationVisible = false;
            } else {
                if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
                    itemHolder.mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_reject));
                    itemHolder.mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_accept));
                    itemHolder.mSubmitSection.setVisibility(View.VISIBLE);
                    itemHolder.mRemainTime.setText(order.remainTime);
                    itemHolder.mOtherStatus.setVisibility(View.GONE);
                } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
                    // 只有普通预约的订单才能由技师来实现完成跟失效，付费预约的必须在核销或者失效的时候更改状态
                    itemHolder.mSubmitSection.setVisibility(View.GONE);
                    itemHolder.mOtherStatus.setVisibility(View.VISIBLE);
                    itemHolder.mOtherStatus.setText(order.statusName);
                    itemHolder.mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_expire));
                    itemHolder.mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_complete));
                } else {
                    itemHolder.mSubmitSection.setVisibility(View.GONE);
                    itemHolder.mOtherStatus.setVisibility(View.VISIBLE);
                    itemHolder.mOtherStatus.setText(order.statusName);
                    itemHolder.mNegative.setVisibility(View.GONE);
                    itemHolder.mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_delete));
                }
                itemHolder.mNegative.setOnClickListener(v -> mOnManageButtonClickedListener.onNegativeButtonClicked(order));
                itemHolder.mPositive.setOnClickListener(v -> mOnManageButtonClickedListener.onPositiveButtonClicked(order));
                itemHolder.isOperationVisible = true;
                itemHolder.mOperation.setVisibility(View.VISIBLE);
            }

            itemHolder.itemView.setOnClickListener(v -> mOnManageButtonClickedListener.onItemClicked(order));
        } else if (holder instanceof CouponListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CouponInfo)) {
                return;
            }

            final CouponInfo couponInfo = (CouponInfo) obj;
            CouponListItemViewHolder couponListItemViewHolder = (CouponListItemViewHolder) holder;
            couponListItemViewHolder.mTvCouponTitle.setText(couponInfo.actTitle);
            couponListItemViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
            couponListItemViewHolder.mCouponPeriod.setText(couponInfo.couponPeriod);
            if (couponInfo.commission > 0) {
                couponListItemViewHolder.mTvCouponReward.setText(String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), couponInfo.commission));
            }

            couponListItemViewHolder.itemView.setOnClickListener(v -> mOnManageButtonClickedListener.onItemClicked(couponInfo));
        } else if (holder instanceof PaidCouponUserDetailItemViewHolder) {

            Object obj = mData.get(position);
            if (!(obj instanceof PaidCouponUserDetail)) {
                return;
            }

            final PaidCouponUserDetail paidCouponUserDetail = (PaidCouponUserDetail) obj;
            PaidCouponUserDetailItemViewHolder itemHolder = (PaidCouponUserDetailItemViewHolder) holder;

            Glide.with(mContext).load(paidCouponUserDetail.headImgUrl).into(itemHolder.mAvatar);
            itemHolder.mAvatar.setOnClickListener(v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, paidCouponUserDetail.emchatId));
            itemHolder.mTvCustomerName.setText(paidCouponUserDetail.userName);
            itemHolder.mTvGetDate.setText(paidCouponUserDetail.getDate);
            itemHolder.mTvTelephone.setText(paidCouponUserDetail.telephone);
            itemHolder.mTvCouponStatusDescription.setText(paidCouponUserDetail.couponStatusDescription);

        } else if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> mOnManageButtonClickedListener.onLoadMoreButtonClicked());
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getSlideOutRange(View targetView) {
        RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(targetView);
        int range = 0;
        if(holder instanceof OrderListItemViewHolder){
            OrderListItemViewHolder curHolder = (OrderListItemViewHolder) holder;
            if (curHolder.mNegative.getVisibility() == View.VISIBLE) {
                range += curHolder.mNegative.getLayoutParams().width;
            }
            if (curHolder.mPositive.getVisibility() == View.VISIBLE) {
                range += curHolder.mPositive.getLayoutParams().width;
            }
        }
        return range;
    }

    @Override
    public boolean isViewSlideable(View targetView) {
        RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(targetView);
        if(holder instanceof OrderListItemViewHolder) {
            return ((OrderListItemViewHolder) holder).isOperationVisible;
        } else {
            return false;
        }
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        if (mIsSlideable) {
            mRecyclerView.addOnItemTouchListener(mHelper);
        }
    }

    static class ListFooterHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderListItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.user_head_url)CircleImageView mUserHeadUrl;
        @Bind(R.id.username) TextView mUserName;
        @Bind(R.id.order_time) TextView mOrderTime;
        @Bind(R.id.order_amount) TextView mOrderAmount;
        @Bind(R.id.submit_section) LinearLayout mSubmitSection;
        @Bind(R.id.remain_time) TextView mRemainTime;
        @Bind(R.id.other_status) TextView mOtherStatus;
        @Bind(R.id.operation) LinearLayout mOperation;
        @Bind(R.id.negative) Button mNegative;
        @Bind(R.id.positive) Button mPositive;

        public boolean isOperationVisible;

        public OrderListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CouponListItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.tv_consume_money_description) TextView mTvConsumeMoneyDescription;
        @Bind(R.id.tv_coupon_title) TextView mTvCouponTitle;
        @Bind(R.id.tv_coupon_reward) TextView mTvCouponReward;
        @Bind(R.id.tv_coupon_period) TextView mCouponPeriod;

        public CouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class PaidCouponUserDetailItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatar) CircleImageView mAvatar;
        @Bind(R.id.tv_customer_name) TextView mTvCustomerName;
        @Bind(R.id.tv_telephone) TextView mTvTelephone;
        @Bind(R.id.tv_coupon_status_description) TextView mTvCouponStatusDescription;
        @Bind(R.id.tv_get_date) TextView mTvGetDate;


        public PaidCouponUserDetailItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
