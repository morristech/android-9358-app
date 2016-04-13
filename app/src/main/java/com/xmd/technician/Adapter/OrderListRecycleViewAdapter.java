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
import com.xmd.technician.beans.Order;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-11-24.
 */
public class OrderListRecycleViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {

    public interface OnManageButtonClickedListener {

        void onItemClicked(Order order);

        void onNegativeButtonClicked(Order order);

        void onPositiveButtonClicked(Order order);

        void onLoadMoreButtonClicked();
    }

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;

    private List<Order> mData;
    private OnManageButtonClickedListener mOnManageButtonClickedListener;
    private Context mContext;
    private RecyclerView mRecyclerView;

    public OrderListRecycleViewAdapter(Context context, List<Order> data, OnManageButtonClickedListener onManageButtonClickedListener) {
        mContext = context;
        mData = data;
        mOnManageButtonClickedListener = onManageButtonClickedListener;
    }

    public void setData(List<Order> data) {
        mData = data;
        mIsEmpty = data.isEmpty();
        notifyDataSetChanged();
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
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_ITEM == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
            return new OrderListViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new OrderListFooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OrderListViewHolder) {
            Order order = mData.get(position);
            OrderListViewHolder itemHolder = (OrderListViewHolder) holder;

            Glide.with(mContext).load(order.headImgUrl).into(itemHolder.mUserHeadUrl);
            itemHolder.mUserName.setText(order.customerName);
            itemHolder.mOrderTime.setText(order.formatAppointTime);
            itemHolder.mOrderAmount.setText(String.format(ResourceUtils.getString(R.string.amount_unit_format), order.downPayment));

            // 1) 提交状态能够进行拒绝或者接受
            // 2) 只有普通预约的订单才能由技师来实现完成跟失效，付费预约的必须在核销或者失效的时候更改状态
            if (Constant.ORDER_STATUS_SUBMIT.equals(order.status) ||
                    (Constant.ORDER_STATUS_ACCEPT.equals(order.status) && Constant.ORDER_TYPE_APPOINT.equals(order.orderType))) {
                if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
                    itemHolder.mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_reject));
                    itemHolder.mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_accept));
                    itemHolder.mSubmitSection.setVisibility(View.VISIBLE);
                    itemHolder.mRemainTime.setText(order.remainTime);
                    itemHolder.mOtherStatus.setVisibility(View.GONE);
                } else {
                    // 只有普通预约的订单才能由技师来实现完成跟失效，付费预约的必须在核销或者失效的时候更改状态
                    itemHolder.mSubmitSection.setVisibility(View.GONE);
                    itemHolder.mOtherStatus.setVisibility(View.VISIBLE);
                    itemHolder.mOtherStatus.setText(order.statusName);
                    itemHolder.mNegative.setText(ResourceUtils.getString(R.string.order_status_operation_expire));
                    itemHolder.mPositive.setText(ResourceUtils.getString(R.string.order_status_operation_complete));
                }
                itemHolder.mNegative.setOnClickListener(v -> mOnManageButtonClickedListener.onNegativeButtonClicked(order));
                itemHolder.mPositive.setOnClickListener(v -> mOnManageButtonClickedListener.onPositiveButtonClicked(order));
                itemHolder.isOperationVisible = true;
                itemHolder.mOperation.setVisibility(View.VISIBLE);
            } else {
                itemHolder.mSubmitSection.setVisibility(View.GONE);
                itemHolder.mOtherStatus.setVisibility(View.VISIBLE);
                itemHolder.mOtherStatus.setText(order.statusName);
                itemHolder.mOperation.setVisibility(View.GONE);
                itemHolder.isOperationVisible = false;
            }

            itemHolder.itemView.setOnClickListener(v -> mOnManageButtonClickedListener.onItemClicked(order));

        } else if (holder instanceof OrderListFooterHolder) {
            OrderListFooterHolder footerHolder = (OrderListFooterHolder) holder;
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
    public int getHorizontalRange(RecyclerView.ViewHolder holder) {
        if(holder.itemView instanceof LinearLayout){
            ViewGroup viewGroup = (ViewGroup) holder.itemView;
            if(viewGroup.getChildCount() == 2){
                return viewGroup.getChildAt(1).getLayoutParams().width;
            }
        }
        return 0;
    }

    @Override
    public boolean isViewSlideable(RecyclerView.ViewHolder holder) {
        return ((OrderListViewHolder) holder).isOperationVisible;
    }

    @Override
    public RecyclerView.ViewHolder getChildViewHolder(View childView) {
        return mRecyclerView.getChildViewHolder(childView);
    }

    @Override
    public View findTargetView(float x, float y) {
        return mRecyclerView.findChildViewUnder(x, y);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new ItemSlideHelper(mContext, this));
    }

    static class OrderListFooterHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer)
        TextView itemFooter;

        public OrderListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderListViewHolder extends RecyclerView.ViewHolder {

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

        public OrderListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
