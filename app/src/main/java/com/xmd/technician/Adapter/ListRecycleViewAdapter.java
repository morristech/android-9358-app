package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.CircleImageView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 15-11-24.
 */
public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {

    public interface Callback<T> {

        void onItemClicked(T bean);

        void onNegativeButtonClicked(T bean);

        void onPositiveButtonClicked(T bean);

        void onLoadMoreButtonClicked();

        /**
         * @return whether the item is slideable
         */
        boolean isSlideable();

        /**
         * whether is paged
         * @return
         */
        boolean isPaged();
    }

    private static final int TYPE_ORDER_ITEM = 0;
    private static final int TYPE_COUPON_INFO_ITEM_CUSH = 11;
    private static final int TYPE_PAID_COUPON_USER_DETAIL = 2;
    private static final int TYPE_CONVERSATION = 3;
    private static final int TYPE_COUPON_INFO_ITEM_DELIVERY = 12;
    private static final int TYPE_COUPON_INFO_ITEM_FAVORUABLE = 13;

    private static final int TYPE_OTHER_ITEM = 98;
    private static final int TYPE_FOOTER = 99;


    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;

    private List<T> mData;
    private Callback mCallback;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSlideHelper mHelper;

    public ListRecycleViewAdapter(Context context, List<T> data, Callback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
        mHelper = new ItemSlideHelper(mContext, this);
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
        if (mCallback.isPaged() && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            if (mData.get(position) instanceof Order) {
                return TYPE_ORDER_ITEM;
            } else if (mData.get(position) instanceof CouponInfo) {
                if(((CouponInfo) mData.get(position)).useTypeName.equals("现金券")){
                    return TYPE_COUPON_INFO_ITEM_CUSH;
                }else if(((CouponInfo) mData.get(position)).useTypeName.equals("点钟券")){
                    return TYPE_COUPON_INFO_ITEM_DELIVERY;
                }else{
                    return TYPE_COUPON_INFO_ITEM_FAVORUABLE;
                }

            } else if (mData.get(position) instanceof PaidCouponUserDetail) {
                return TYPE_PAID_COUPON_USER_DETAIL;
            } else if (mData.get(position) instanceof EMConversation) {
                return TYPE_CONVERSATION;
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
        } else if (TYPE_COUPON_INFO_ITEM_CUSH == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_cush_item, parent, false);
            return new CouponListItemViewHolder((view));
        }else if(TYPE_COUPON_INFO_ITEM_DELIVERY==viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_delivery_item, parent, false);
            return new CouponListItemViewHolder((view));
        }else if(TYPE_COUPON_INFO_ITEM_FAVORUABLE ==viewType){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_favorable_item, parent, false);
            return new CouponListItemViewHolder((view));
        } else if (TYPE_PAID_COUPON_USER_DETAIL == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paid_coupon_user_detail_list_item, parent, false);
            return new PaidCouponUserDetailItemViewHolder((view));
        } else if (TYPE_CONVERSATION == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            return new ConversationViewHolder(view);
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
            itemHolder.mUserHeadUrl.setOnClickListener(
                    v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(order.emchatId, order.userName, order.headImgUrl)));
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
                    itemHolder.mRemainTime.setText(order.remainTime.contains("-") ? "0" : order.remainTime);
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
                itemHolder.mNegative.setOnClickListener(v -> mCallback.onNegativeButtonClicked(order));
                itemHolder.mPositive.setOnClickListener(v -> mCallback.onPositiveButtonClicked(order));
                itemHolder.isOperationVisible = true;
                itemHolder.mOperation.setVisibility(View.VISIBLE);
            }

            if(Constant.ORDER_TYPE_PAID.equals(order.orderType)){
                itemHolder.mPaidOrderAmountContainer.setVisibility(View.VISIBLE);
            }else {
                itemHolder.mPaidOrderAmountContainer.setVisibility(View.INVISIBLE);
            }

            itemHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(order));
        } else if (holder instanceof CouponListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CouponInfo)) {
                return;
            }

            final CouponInfo couponInfo = (CouponInfo) obj;
            CouponListItemViewHolder couponListItemViewHolder = (CouponListItemViewHolder) holder;
            couponListItemViewHolder.mTvCouponTitle.setText("money".equals(couponInfo.useType) ? TextUtils.concat(String.valueOf(couponInfo.actValue),"元",couponInfo.useTypeName) : couponInfo.actTitle);
            couponListItemViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
            couponListItemViewHolder.mCouponPeriod.setText("有效时间："+couponInfo.couponPeriod);
            if (couponInfo.techCommission > 0||couponInfo.techBaseCommission>0) {
                String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), String.valueOf(couponInfo.techCommission>couponInfo.techBaseCommission?couponInfo.techCommission:couponInfo.techBaseCommission));
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new TextAppearanceSpan(mContext,R.style.text_bold),3,text.length()-1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                couponListItemViewHolder.mTvCouponReward.setText(spannableString);
            }else {
                couponListItemViewHolder.mTvCouponReward.setVisibility(View.GONE);
            }
            if(Utils.isNotEmpty(couponInfo.consumeMoney)){
                couponListItemViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
            }
            if(Utils.isNotEmpty(couponInfo.couponTypeName)){
                couponListItemViewHolder.mCouponType.setVisibility(View.VISIBLE);
                couponListItemViewHolder.mCouponType.setText("("+couponInfo.couponTypeName+")");
            }
            couponListItemViewHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(couponInfo));
        } else if (holder instanceof PaidCouponUserDetailItemViewHolder) {

            Object obj = mData.get(position);
            if (!(obj instanceof PaidCouponUserDetail)) {
                return;
            }

            final PaidCouponUserDetail paidCouponUserDetail = (PaidCouponUserDetail) obj;
            PaidCouponUserDetailItemViewHolder itemHolder = (PaidCouponUserDetailItemViewHolder) holder;

            Glide.with(mContext).load(paidCouponUserDetail.headImgUrl).into(itemHolder.mAvatar);
            itemHolder.mAvatar.setOnClickListener(
                    v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT,
                            Utils.wrapChatParams(paidCouponUserDetail.emchatId, paidCouponUserDetail.userName, paidCouponUserDetail.headImgUrl)));
            itemHolder.mTvCustomerName.setText(paidCouponUserDetail.userName);
            itemHolder.mTvGetDate.setText(paidCouponUserDetail.getDate);
            itemHolder.mTvTelephone.setText(paidCouponUserDetail.telephone);
            itemHolder.mTvCouponStatusDescription.setText(paidCouponUserDetail.couponStatusDescription);

        } else if(holder instanceof ConversationViewHolder){

            Object obj = mData.get(position);
            if (!(obj instanceof EMConversation)) {
                return;
            }

            final EMConversation conversation = (EMConversation) obj;
            ConversationViewHolder conversationHolder = (ConversationViewHolder) holder;

            //conversationHolder.mName.setText(conversation.getUserName());
            if(conversation.getUnreadMsgCount() > 0){
                conversationHolder.mUnread.setText(String.valueOf(conversation.getUnreadMsgCount()));
                conversationHolder.mUnread.setVisibility(View.VISIBLE);
            }else {
                conversationHolder.mUnread.setVisibility(View.INVISIBLE);
            }

            if(conversation.getAllMsgCount() != 0){
                // 把最后一条消息的内容作为item的message内容
                EMMessage lastMessage = conversation.getLastMessage();
                Spannable span = SmileUtils.getSmiledText(mContext, CommonUtils.getMessageDigest(lastMessage, mContext));
                conversationHolder.mContent.setText(span, TextView.BufferType.SPANNABLE);
                conversationHolder.mTime.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                try {
                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                        ChatUser user;
                        user = new ChatUser(conversation.getUserName());
                        user.setAvatar(lastMessage.getStringAttribute(ChatConstant.KEY_HEADER));
                        user.setNick(lastMessage.getStringAttribute(ChatConstant.KEY_NAME));
                        UserUtils.updateUser(user);
                    }
                    UserUtils.setUserAvatar(mContext, conversation.getUserName(), conversationHolder.mAvatar);
                    UserUtils.setUserNick(conversation.getUserName(), conversationHolder.mName);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }catch (NullPointerException e){

                }
            }

            holder.itemView.setOnClickListener(v -> mCallback.onItemClicked(conversation));

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
                footerHolder.itemFooter.setOnClickListener(v -> mCallback.onLoadMoreButtonClicked());
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        if (mCallback.isPaged()) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }
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
        if (mCallback.isSlideable()) {
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
        @Bind(R.id.paid_order_container) View mPaidOrderAmountContainer;

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
        @Bind(R.id.coupon_amount)TextView mCouponAmount;
        @Bind(R.id.coupon_type)TextView mCouponType;


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

    static class ConversationViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.avatar) ImageView mAvatar;
        @Bind(R.id.name) TextView mName;
        @Bind(R.id.content) TextView mContent;
        @Bind(R.id.time) TextView mTime;
        @Bind(R.id.unread) TextView mUnread;
        public ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
