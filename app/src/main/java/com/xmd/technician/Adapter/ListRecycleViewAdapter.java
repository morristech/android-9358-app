package com.xmd.technician.Adapter;

import android.content.Context;
import android.databinding.tool.util.L;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.ApplicationBean;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CreditDetailBean;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.bean.LimitGrabBean;
import com.xmd.technician.bean.OnceCardItemBean;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.bean.PayForMeBean;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.bean.RewardBean;
import com.xmd.technician.bean.ShareCouponBean;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.Logger;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.RoundImageView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by sdcm on 15-11-24.
 */
public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {

    public interface Callback<T> {

        void onItemClicked(T bean) throws HyphenateException;

        void onNegativeButtonClicked(T bean);

        void onPositiveButtonClicked(T bean);

        void onLoadMoreButtonClicked();

        void onSayHiButtonClicked(T bean);

        void onShareClicked(T bean);

        /**
         * @return whether the item is slideable
         */
        boolean isHorizontalSliding();

        /**
         * whether is paged
         *
         * @return
         */
        boolean isPaged();
    }

    private static final int TYPE_ORDER_ITEM = 0;
    private static final int TYPE_PAID_COUPON_USER_DETAIL = 2;
    private static final int TYPE_CONVERSATION = 3;
    private static final int TYPE_CREDIT_RECORD_ITEM = 4;
    private static final int TYPE_CREDIT_APPLICATION_ITEM = 5;
    private static final int TYPE_RECENTLY_VISIT_ITEM = 6;
    private static final int TYPE_DYNAMIC_COMMENT_ITEM = 7;
    private static final int TYPE_DYNAMIC_COLLECT_ITEM = 8;
    private static final int TYPE_DYNAMIC_COUPON_ITEM = 9;
    private static final int TYPE_COUPON_INFO_ITEM_CASH = 11;
    private static final int TYPE_COUPON_INFO_ITEM_DELIVERY = 12;
    private static final int TYPE_COUPON_INFO_ITEM_FAVORABLE = 13;
    private static final int TYPE_ONCE_CARD_ITEM = 14;
    private static final int TYPE_LIMIT_GRAB_ITEM = 15;
    private static final int TYPE_REWARD_ACTIVITY_ITEM = 16;
    private static final int TYPE_CLUB_JOURNAL_ITEM = 17;
    private static final int TYPE_COUPON_PAID_ITEM = 18;
    private static final int TYPE_COUPON_CASH_ITEM = 19;
    private static final int TYPE_COUPON_FAVORABLE_ITEM = 20;
    private static final int TYPE_COUPON_PAY_FOR_ME_ITEM = 21;
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
                if (((CouponInfo) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.cash_coupon))) {
                    return TYPE_COUPON_INFO_ITEM_CASH;
                } else if (((CouponInfo) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                    return TYPE_COUPON_INFO_ITEM_DELIVERY;
                } else {
                    return TYPE_COUPON_INFO_ITEM_FAVORABLE;
                }
            } else if (mData.get(position) instanceof PaidCouponUserDetail) {
                return TYPE_PAID_COUPON_USER_DETAIL;
            } else if (mData.get(position) instanceof EMConversation) {
                return TYPE_CONVERSATION;
            } else if (mData.get(position) instanceof CreditDetailBean) {
                return TYPE_CREDIT_RECORD_ITEM;
            } else if (mData.get(position) instanceof ApplicationBean) {
                return TYPE_CREDIT_APPLICATION_ITEM;
            } else if (mData.get(position) instanceof RecentlyVisitorBean) {
                return TYPE_RECENTLY_VISIT_ITEM;
            } else if (mData.get(position) instanceof DynamicDetail) {
                if (((DynamicDetail) mData.get(position)).bizType == 1) {
                    return TYPE_DYNAMIC_COMMENT_ITEM;
                } else if (((DynamicDetail) mData.get(position)).bizType == 2 || ((DynamicDetail) mData.get(position)).bizType == 3) {
                    return TYPE_DYNAMIC_COUPON_ITEM;
                } else {
                    return TYPE_DYNAMIC_COLLECT_ITEM;
                }
            } else if (mData.get(position) instanceof OnceCardItemBean) {
                return TYPE_ONCE_CARD_ITEM;
            } else if (mData.get(position) instanceof LimitGrabBean) {
                return TYPE_LIMIT_GRAB_ITEM;
            } else if (mData.get(position) instanceof RewardBean) {
                return TYPE_REWARD_ACTIVITY_ITEM;
            } else if (mData.get(position) instanceof ClubJournalBean) {
                return TYPE_CLUB_JOURNAL_ITEM;
            } else if (mData.get(position) instanceof ShareCouponBean) {
                if (((ShareCouponBean) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.cash_coupon))) {
                    return TYPE_COUPON_CASH_ITEM;
                } else if (((ShareCouponBean) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                    return TYPE_COUPON_PAID_ITEM;
                } else {
                    return TYPE_COUPON_FAVORABLE_ITEM;
                }
            } else if (mData.get(position) instanceof PayForMeBean) {
                return TYPE_COUPON_PAY_FOR_ME_ITEM;
            } else {
                return TYPE_OTHER_ITEM;
            }
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CREDIT_RECORD_ITEM:
                View viewRecord = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_record_item, parent, false);
                return new CreditRecordViewHolder(viewRecord);
            case TYPE_ORDER_ITEM:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false);
                return new OrderListItemViewHolder(view);
            case TYPE_COUPON_INFO_ITEM_CASH:
                View viewCash = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_cush_item, parent, false);
                return new CouponListItemViewHolder((viewCash));
            case TYPE_COUPON_INFO_ITEM_DELIVERY:
                View viewDelivery = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_delivery_item, parent, false);
                return new CouponListItemViewHolder((viewDelivery));
            case TYPE_COUPON_INFO_ITEM_FAVORABLE:
                View viewFavorable = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_favorable_item, parent, false);
                return new CouponListItemViewHolder((viewFavorable));
            case TYPE_PAID_COUPON_USER_DETAIL:
                View viewDetail = LayoutInflater.from(parent.getContext()).inflate(R.layout.paid_coupon_user_detail_list_item, parent, false);
                return new PaidCouponUserDetailItemViewHolder((viewDetail));
            case TYPE_CONVERSATION:
                View viewConversion = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
                return new ConversationViewHolder(viewConversion);
            case TYPE_CREDIT_APPLICATION_ITEM:
                View viewApplication = LayoutInflater.from(parent.getContext()).inflate(R.layout.credit_application_item, parent, false);
                return new CreditApplicationViewHolder(viewApplication);
            case TYPE_RECENTLY_VISIT_ITEM:
                View viewRecentlyVisit = LayoutInflater.from(parent.getContext()).inflate(R.layout.recently_visitor_item, parent, false);
                return new RecentlyVisitViewHolder(viewRecentlyVisit);
            case TYPE_DYNAMIC_COMMENT_ITEM:
                View viewComment = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_comment_item, parent, false);
                return new DynamicItemViewHolder(viewComment);
            case TYPE_DYNAMIC_COUPON_ITEM:
                View viewCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_coupon_item, parent, false);
                return new DynamicItemViewHolder(viewCoupon);
            case TYPE_DYNAMIC_COLLECT_ITEM:
                View viewCollect = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_collect_item, parent, false);
                return new DynamicItemViewHolder(viewCollect);
            case TYPE_ONCE_CARD_ITEM:
                View viewOnceCard = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_once_card_item, parent, false);
                return new OnceCardItemViewHolder(viewOnceCard);
            case TYPE_LIMIT_GRAB_ITEM:
                View viewLimitGrab = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_limit_grab_item, parent, false);
                return new LimitGrabItemViewHolder(viewLimitGrab);
            case TYPE_REWARD_ACTIVITY_ITEM:
                View viewRewardActivity = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reward_activity_item, parent, false);
                return new RewardActivityItemViewHolder(viewRewardActivity);
            case TYPE_CLUB_JOURNAL_ITEM:
                View viewClubJournal = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_club_journal_item, parent, false);
                return new ClubJournalItemViewHolder(viewClubJournal);
            case TYPE_COUPON_PAID_ITEM:
                View viewPaidCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_delivery_item, parent, false);
                return new ShareCouponListItemViewHolder(viewPaidCoupon);
            case TYPE_COUPON_CASH_ITEM:
                View viewCashCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_cush_item, parent, false);
                return new ShareCouponListItemViewHolder(viewCashCoupon);
            case TYPE_COUPON_FAVORABLE_ITEM:
                View viewFavorableCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_favorable_item, parent, false);
                return new ShareCouponListItemViewHolder(viewFavorableCoupon);
            case TYPE_COUPON_PAY_FOR_ME_ITEM:
                View viewPayForMe = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pay_for_me_item, parent, false);
                return new PayForMeListItemViewHolder(viewPayForMe);
            default:
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
                return new ListFooterHolder(viewDefault);
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
                    v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(order.emchatId, order.userName, order.headImgUrl, "")));
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

            if (Constant.ORDER_TYPE_PAID.equals(order.orderType)) {
                itemHolder.mPaidOrderAmountContainer.setVisibility(View.VISIBLE);
                if (order.payType == 2) {
                    itemHolder.mPaidMark.setVisibility(View.VISIBLE);
                } else {
                    itemHolder.mPaidMark.setVisibility(View.GONE);
                }
            } else {
                itemHolder.mPaidOrderAmountContainer.setVisibility(View.INVISIBLE);
                itemHolder.mPaidMark.setVisibility(View.GONE);
            }

            itemHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(order);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (holder instanceof CouponListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CouponInfo)) {
                return;
            }
            final CouponInfo couponInfo = (CouponInfo) obj;
            CouponListItemViewHolder couponListItemViewHolder = (CouponListItemViewHolder) holder;
            couponListItemViewHolder.mTvCouponTitle.setText(couponInfo.actTitle);
            if (couponInfo.useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                couponListItemViewHolder.mTvCouponTitle.setText(ResourceUtils.getString(R.string.delivery_coupon));
                couponListItemViewHolder.mCouponType.setVisibility(View.GONE);
            } else {
                couponListItemViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(8, couponInfo.actTitle, true));
                couponListItemViewHolder.mCouponType.setVisibility(View.VISIBLE);
            }
            couponListItemViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
            couponListItemViewHolder.mCouponPeriod.setText("有效时间：" + Utils.StrSubstring(19, couponInfo.couponPeriod, true));
            if (couponInfo.commission > 0) {
                couponListItemViewHolder.mTvCouponReward.setVisibility(View.VISIBLE);
                String money = Utils.getFloat2Str(String.valueOf(couponInfo.commission));
                String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new TextAppearanceSpan(mContext, R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                couponListItemViewHolder.mTvCouponReward.setText(spannableString);
            } else {
                couponListItemViewHolder.mTvCouponReward.setVisibility(View.GONE);
            }

            if (Utils.isNotEmpty(couponInfo.consumeMoney)) {
                couponListItemViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
            }
            couponListItemViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(couponInfo);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            couponListItemViewHolder.mBtnShareCoupon.setOnClickListener(v -> mCallback.onShareClicked(couponInfo));
            couponListItemViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(couponInfo);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (holder instanceof ShareCouponListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ShareCouponBean)) {
                return;
            }
            final ShareCouponBean couponInfo = (ShareCouponBean) obj;
            ShareCouponListItemViewHolder couponListItemViewHolder = (ShareCouponListItemViewHolder) holder;
            couponListItemViewHolder.mTvCouponTitle.setText(couponInfo.actTitle);
            if (couponInfo.useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                couponListItemViewHolder.mTvCouponTitle.setText(ResourceUtils.getString(R.string.delivery_coupon));
                couponListItemViewHolder.mCouponType.setVisibility(View.GONE);
            } else {
                couponListItemViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(8, couponInfo.actTitle, true));
                couponListItemViewHolder.mCouponType.setVisibility(View.VISIBLE);
            }
            couponListItemViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescption);
            couponListItemViewHolder.mCouponPeriod.setText("有效时间：" + Utils.StrSubstring(19, couponInfo.couponPeriod, true));
            if (couponInfo.commission > 0) {
                couponListItemViewHolder.mTvCouponReward.setVisibility(View.VISIBLE);
                String money = Utils.getFloat2Str(String.valueOf(couponInfo.commission));
                String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), money);
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new TextAppearanceSpan(mContext, R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                couponListItemViewHolder.mTvCouponReward.setText(spannableString);
            } else {
                couponListItemViewHolder.mTvCouponReward.setVisibility(View.GONE);
            }

            if (Utils.isNotEmpty(couponInfo.consumeMoney)) {
                couponListItemViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
            }
            couponListItemViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(couponInfo);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            couponListItemViewHolder.mBtnShareCoupon.setOnClickListener(v -> mCallback.onShareClicked(couponInfo));
            return;
        }
        if (holder instanceof PaidCouponUserDetailItemViewHolder) {

            Object obj = mData.get(position);
            if (!(obj instanceof PaidCouponUserDetail)) {
                return;
            }

            final PaidCouponUserDetail paidCouponUserDetail = (PaidCouponUserDetail) obj;
            PaidCouponUserDetailItemViewHolder itemHolder = (PaidCouponUserDetailItemViewHolder) holder;

            Glide.with(mContext).load(paidCouponUserDetail.headImgUrl).into(itemHolder.mAvatar);
            itemHolder.mAvatar.setOnClickListener(
                    v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT,
                            Utils.wrapChatParams(paidCouponUserDetail.emchatId, paidCouponUserDetail.userName, paidCouponUserDetail.headImgUrl, "")));
            itemHolder.mTvCustomerName.setText(paidCouponUserDetail.userName);
            itemHolder.mTvGetDate.setText(paidCouponUserDetail.getDate);
            itemHolder.mTvTelephone.setText(paidCouponUserDetail.telephone);
            itemHolder.mTvCouponStatusDescription.setText(paidCouponUserDetail.couponStatusDescription);
            return;
        }
        if (holder instanceof ConversationViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof EMConversation)) {
                return;
            }
            final EMConversation conversation = (EMConversation) obj;
            ConversationViewHolder conversationHolder = (ConversationViewHolder) holder;

            if (conversation.getUnreadMsgCount() > 0) {
                conversationHolder.mUnread.setText(String.valueOf(conversation.getUnreadMsgCount()));
                conversationHolder.mUnread.setVisibility(View.VISIBLE);
            } else {
                conversationHolder.mUnread.setVisibility(View.INVISIBLE);
            }

            if (conversation.getAllMsgCount() != 0) {
                // 把最后一条消息的内容作为item的message内容
                EMMessage lastMessage = conversation.getLastMessage();
                String toId = lastMessage.getTo();
                Spannable span = SmileUtils.getSmiledText(mContext, CommonUtils.getMessageDigest(lastMessage, mContext));
                conversationHolder.mContent.setText(span, TextView.BufferType.EDITABLE);
                conversationHolder.mTime.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
                try {
                    if (lastMessage.direct() == EMMessage.Direct.RECEIVE) {
                        ChatUser user;
                        user = new ChatUser(conversation.getUserName());
                        user.setAvatar(lastMessage.getStringAttribute(ChatConstant.KEY_HEADER));
                        if (Utils.isNotEmpty(SharedPreferenceHelper.getUserRemark(lastMessage.getFrom()))) {
                            user.setNick(SharedPreferenceHelper.getUserRemark(lastMessage.getFrom()));
                        } else {
                            user.setNick(lastMessage.getStringAttribute(ChatConstant.KEY_NAME));
                        }
                        UserUtils.updateUser(user);
                    }
                    UserUtils.setUserAvatar(mContext, conversation.getUserName(), conversationHolder.mAvatar);
                    UserUtils.setUserNick(conversation.getUserName(), conversationHolder.mName);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
//                conversationHolder.mUserManagerType.setVisibility(View.GONE);
//                conversationHolder.mUserTechType.setVisibility(View.GONE);
                if (conversation.getLastMessage().getFrom().equals(SharedPreferenceHelper.getEmchatId())) {
                    if (SharedPreferenceHelper.getUserIsTech(toId).equals("tech")) {
                        try {
                            if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_SERIAL_NO))) {
                                String last = conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_SERIAL_NO);
                                //  conversationHolder.mUserTechType.setVisibility(View.VISIBLE);
                                //  conversationHolder.mUserTechType.setText(Utils.changeColor(last,ResourceUtils.getColor(R.color.contact_marker),1,last.length()));
                            }
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                    } else if (SharedPreferenceHelper.getUserIsTech(toId).equals("manager")) {
                        //  conversationHolder.mUserManagerType.setVisibility(View.VISIBLE);
                    }

                } else {
                    try {

                        if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_TECH_ID)))
                            ;
                        if (Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_SERIAL_NO))) {
                            String last = conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_SERIAL_NO);
                            //  conversationHolder.mUserTechType.setVisibility(View.VISIBLE);
                            // conversationHolder.mUserTechType.setText(Utils.changeColor(last,ResourceUtils.getColor(R.color.contact_marker),1,last.length()));
                        }

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }

            holder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(conversation);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (holder instanceof CreditRecordViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CreditDetailBean)) {
                return;
            }
            final CreditDetailBean creditDetailBean = (CreditDetailBean) obj;
            CreditRecordViewHolder creditRecordViewHolder = (CreditRecordViewHolder) holder;
            if (Utils.isNotEmpty(creditDetailBean.createDatetime)) {
                creditRecordViewHolder.mCreditTime.setText(creditDetailBean.createDatetime);
            }

            if (Utils.isNotEmpty(creditDetailBean.businessCategoryDesc)) {
                creditRecordViewHolder.mCreditFrom.setText(creditDetailBean.description);
            }
            if (Utils.isNotEmpty(creditDetailBean.peerAvatar)) {
                creditRecordViewHolder.mAvatar.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(creditDetailBean.peerAvatar).error(R.drawable.icon22).diskCacheStrategy(DiskCacheStrategy.ALL).into(creditRecordViewHolder.mAvatar);
            } else if (creditDetailBean.businessCategoryDesc.equals("游戏积分")) {
                creditRecordViewHolder.mAvatar.setVisibility(View.VISIBLE);
                Glide.with(mContext).load(R.drawable.icon22).into(creditRecordViewHolder.mAvatar);
            } else {
                creditRecordViewHolder.mAvatar.setVisibility(View.INVISIBLE);
            }

            if (Utils.isNotEmpty(creditDetailBean.peerName)) {
                creditRecordViewHolder.mAdverseName.setVisibility(View.VISIBLE);
                creditRecordViewHolder.mAdverseName.setText(creditDetailBean.peerName);
            } else {
                creditRecordViewHolder.mAdverseName.setVisibility(View.INVISIBLE);
            }
            if (creditDetailBean.amount > 0) {
                creditRecordViewHolder.mCreditAmount.setText(String.format("+%s", String.valueOf(creditDetailBean.amount)));
                creditRecordViewHolder.mCreditAmount.setTextColor(ResourceUtils.getColor(R.color.credit_amount_color));
            } else {
                creditRecordViewHolder.mCreditAmount.setText(String.valueOf(creditDetailBean.amount));
                creditRecordViewHolder.mCreditAmount.setTextColor(ResourceUtils.getColor(R.color.colorHead));
            }

            return;

        }
        if (holder instanceof CreditApplicationViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ApplicationBean)) {
                return;
            }
            final ApplicationBean applicationBean = (ApplicationBean) obj;
            CreditApplicationViewHolder creditApplicationViewHolder = (CreditApplicationViewHolder) holder;
            if (applicationBean.status.equals(RequestConstant.KEY_APPROVE)) {
                creditApplicationViewHolder.mCreditFrom.setText(String.format(ResourceUtils.getString(R.string.credit_exchange_accept), String.valueOf(applicationBean.amount)));
                creditApplicationViewHolder.mCreditAmount.setText("-" + String.valueOf(applicationBean.amount * applicationBean.exchangeRatio));
            } else if (applicationBean.status.equals(RequestConstant.KEY_TIMEOUT)) {
                creditApplicationViewHolder.mCreditFrom.setText(String.format(ResourceUtils.getString(R.string.credit_exchange_overtime), String.valueOf(applicationBean.amount)));
                creditApplicationViewHolder.mCreditAmount.setText("(解冻)" + String.valueOf(applicationBean.amount * applicationBean.exchangeRatio));
            } else if (applicationBean.status.equals(RequestConstant.KEY_REJECT)) {
                creditApplicationViewHolder.mCreditFrom.setText(String.format(ResourceUtils.getString(R.string.credit_exchange_reject), String.valueOf(applicationBean.amount)));
                creditApplicationViewHolder.mCreditAmount.setText("(解冻)" + String.valueOf(applicationBean.amount * applicationBean.exchangeRatio));
            } else {
                creditApplicationViewHolder.mCreditFrom.setText(String.format(ResourceUtils.getString(R.string.credit_exchange_undo), String.valueOf(applicationBean.amount)));
                creditApplicationViewHolder.mCreditAmount.setText("(冻结)" + String.valueOf(applicationBean.amount * applicationBean.exchangeRatio));
            }
            creditApplicationViewHolder.mCreditTime.setText(applicationBean.createDate);
            return;
        }
        if (holder instanceof RecentlyVisitViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof RecentlyVisitorBean)) {
                return;
            }
            final RecentlyVisitorBean recentlyVisitor = (RecentlyVisitorBean) obj;
            RecentlyVisitViewHolder viewHolder = (RecentlyVisitViewHolder) holder;

            if (Long.parseLong(recentlyVisitor.userId) > 0) {
                if (Utils.isNotEmpty(recentlyVisitor.userNoteName)) {
                    viewHolder.mVisitorName.setText(recentlyVisitor.userNoteName);
                } else if (Utils.isNotEmpty(recentlyVisitor.userName)) {
                    viewHolder.mVisitorName.setText(recentlyVisitor.userName);
                } else {
                    viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.default_user_name));
                }
            } else {
                viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.visitor_type));
            }
            if (Utils.isNotEmpty(recentlyVisitor.customerType)) {
                if (Utils.isNotEmpty(recentlyVisitor.emchatId) && !recentlyVisitor.customerType.equals(RequestConstant.TEMP_USER)) {
                    viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
                    if (Utils.isNotEmpty(recentlyVisitor.canSayHello)) {
                        viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
                        if (recentlyVisitor.canSayHello.equals("0")) {
                            viewHolder.mVisitorToChat.setEnabled(false);
                            viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.had_say_hi));
                            viewHolder.mVisitorToChat.setTextColor(ResourceUtils.getColor(R.color.color_white));
                        } else if (recentlyVisitor.canSayHello.equals("1")) {
                            viewHolder.mVisitorToChat.setEnabled(true);
                            viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.to_say_hi));
                        }
                    } else {
                        viewHolder.mVisitorToChat.setVisibility(View.GONE);
                    }
                } else {
                    viewHolder.mVisitorToChat.setVisibility(View.GONE);
                }
            } else {
                viewHolder.mVisitorToChat.setVisibility(View.GONE);
            }

            if (Utils.isNotEmpty(recentlyVisitor.techName) && Utils.isNotEmpty(recentlyVisitor.techSerialNo)) {
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTechNum.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s", recentlyVisitor.techName));
                String mun = String.format("[%s]", recentlyVisitor.techSerialNo);
                viewHolder.mVisitorTechNum.setText(Utils.changeColor(mun, ResourceUtils.getColor(R.color.contact_marker), 1, mun.lastIndexOf("]")));
            } else if (Utils.isNotEmpty(recentlyVisitor.techName)) {
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s", recentlyVisitor.techName));
            } else {
                viewHolder.mVisitorTech.setVisibility(View.GONE);
                viewHolder.mVisitorTechNum.setVisibility(View.GONE);
            }

            if (null != recentlyVisitor.customerType) {
                viewHolder.mVisitorType.setVisibility(View.GONE);
                viewHolder.mVisitorOtherType.setVisibility(View.GONE);
                if (recentlyVisitor.customerType.equals(RequestConstant.FANS_USER)) {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                } else if (recentlyVisitor.customerType.equals(RequestConstant.TEMP_USER)) {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.temporary_user));
                } else if (recentlyVisitor.customerType.equals(RequestConstant.FANS_WX_USER)) {
                    viewHolder.mVisitorOtherType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                    viewHolder.mVisitorOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                } else {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                }
            } else {
                viewHolder.mVisitorType.setVisibility(View.GONE);
                viewHolder.mVisitorOtherType.setVisibility(View.GONE);
            }
            viewHolder.mVisitorTime.setText(RelativeDateFormatUtil.format(recentlyVisitor.createdAt));
            Glide.with(mContext).load(recentlyVisitor.avatarUrl).into(viewHolder.mVisitorHead);
            viewHolder.mVisitorToChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        mCallback.onSayHiButtonClicked(recentlyVisitor);
                        viewHolder.mVisitorToChat.setEnabled(false);
                        viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.had_say_hi));
                        viewHolder.mVisitorToChat.setTextColor(ResourceUtils.getColor(R.color.color_white));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            viewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(recentlyVisitor);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });

            return;

        }

        if (holder instanceof DynamicItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof DynamicDetail)) {
                return;
            }
            final DynamicDetail dynamicDetail = (DynamicDetail) obj;
            DynamicItemViewHolder viewHolder = (DynamicItemViewHolder) holder;
            Glide.with(mContext).load(Utils.isNotEmpty(dynamicDetail.avatarUrl) ? dynamicDetail.avatarUrl : dynamicDetail.imageUrl).error(R.drawable.icon22).into(viewHolder.dynamicItemAvatar);
            viewHolder.dynamicItemName.setText(dynamicDetail.userName);
            if (Utils.isNotEmpty(dynamicDetail.userEmchatId)) {
                viewHolder.btnThanks.setVisibility(View.VISIBLE);
                viewHolder.btnThanks.setClickable(true);
                viewHolder.btnThanks.setOnClickListener(v -> mCallback.onSayHiButtonClicked(dynamicDetail));
            } else {
                viewHolder.btnThanks.setVisibility(View.GONE);
            }
            if (Utils.isNotEmpty(dynamicDetail.phoneNum) && Utils.isNotEmpty(dynamicDetail.userEmchatId)) {
                viewHolder.dynamicItemTelephone.setText(dynamicDetail.phoneNum);
            } else {
                viewHolder.dynamicItemTelephone.setText("");
            }
            viewHolder.dynamicTime.setText(com.xmd.technician.common.DateUtils.getTimestampString(new Date(dynamicDetail.createTime)));
            String textDescription = dynamicDetail.description;
            String textRemark = dynamicDetail.remark;
            int commentScore = dynamicDetail.commentScore;
            float rewardAmount = dynamicDetail.rewardAmount / 100f;

            if (dynamicDetail.bizType == 1) {

                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_comment));
                if (Utils.isNotEmpty(textDescription)) {
                    viewHolder.dynamicItemCommentDetail.setText(textDescription);
                }
                if (Utils.isNotEmpty(textRemark)) {
                    viewHolder.dynamicItemRemark.setText("#" + textRemark.replaceAll("、", " #"));
                }
                if (commentScore > 0) {
                    viewHolder.dynamicItemCommentStarts.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemCommentStarts.setText(String.valueOf(commentScore / 20f));
                } else {
                    viewHolder.dynamicItemCommentStarts.setVisibility(View.GONE);
                }

                if (rewardAmount > 0) {
                    viewHolder.dynamicItemCommentReward.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemCommentReward.setText(String.format("%1.2f", rewardAmount));
                } else {
                    viewHolder.dynamicItemCommentReward.setVisibility(View.GONE);
                }

            } else if (dynamicDetail.bizType == 2) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_collect));
                viewHolder.dynamicItemCommentDetail.setText(textDescription);
                viewHolder.dynamicItemRemark.setVisibility(View.GONE);
            } else if (dynamicDetail.bizType == 3) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_coupon));
                viewHolder.dynamicItemCommentDetail.setText(textDescription);
                if (Utils.isNotEmpty(textRemark)) {
                    viewHolder.dynamicItemRemark.setText("(" + textRemark + ")");
                } else {
                    viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                }

            } else if (dynamicDetail.bizType == 4) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_paid));
                viewHolder.dynamicItemCommentDetail.setText(textDescription);
                if (Utils.isNotEmpty(textDescription)) {
                    viewHolder.dynamicItemRemark.setText("(" + textRemark + ")");
                } else {
                    viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                }

            } else if (dynamicDetail.bizType == 5) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_to_reward));
                viewHolder.dynamicItemCommentDetail.setText(Utils.changeStringNumColor(textDescription, ResourceUtils.getColor(R.color.colorMainBtn)));
            }


            return;
        }
        if (holder instanceof OnceCardItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof OnceCardItemBean)) {
                return;
            }
            final OnceCardItemBean onceCard = (OnceCardItemBean) obj;
            OnceCardItemViewHolder cardItemViewHolder = (OnceCardItemViewHolder) holder;
            Glide.with(mContext).load(onceCard.imageUrl).into(cardItemViewHolder.mOnceCardHead);
            cardItemViewHolder.mOnceCardTitle.setText(onceCard.name);
            cardItemViewHolder.mOnceCardCredit.setText(Utils.StrSubstring(13,onceCard.comboDescription,true).trim());
            cardItemViewHolder.mOnceCardMoney.setText(onceCard.techRoyalty);
            cardItemViewHolder.mOnceCardPrice.setText(onceCard.price);
            cardItemViewHolder.mOnceCardShare.setOnClickListener(v -> mCallback.onShareClicked(onceCard));
            cardItemViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(onceCard);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            if (position == 0) {
                cardItemViewHolder.mOnceCardMarkNew.setVisibility(View.VISIBLE);
                cardItemViewHolder.mOnceCardMarkFavorable.setVisibility(View.GONE);
            } else {
                cardItemViewHolder.mOnceCardMarkNew.setVisibility(View.GONE);
                if (onceCard.isPreferential) {
                    cardItemViewHolder.mOnceCardMarkFavorable.setVisibility(View.VISIBLE);
                } else {
                    cardItemViewHolder.mOnceCardMarkFavorable.setVisibility(View.GONE);
                }
            }
            return;
        }
        if (holder instanceof LimitGrabItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof LimitGrabBean)) {
                return;
            }
            final LimitGrabBean limitGrabBean = (LimitGrabBean) obj;
            LimitGrabItemViewHolder limitGrabViewHolder = (LimitGrabItemViewHolder) holder;
            Glide.with(mContext).load(limitGrabBean.image).into(limitGrabViewHolder.mLimitGrabHead);
            limitGrabViewHolder.mLimitGrabTitle.setText(limitGrabBean.itemName);
            if (Utils.isNotEmpty(limitGrabBean.price)) {
            }
            if (Utils.isNotEmpty(limitGrabBean.amount)) {
                limitGrabViewHolder.mLimitGrabMoney.setVisibility(View.VISIBLE);
                limitGrabViewHolder.mUnit.setVisibility(View.VISIBLE);
                limitGrabViewHolder.mLimitGrabMoney.setText(limitGrabBean.amount);
                if (Utils.isNotEmpty(limitGrabBean.credits)) {
                    limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.VISIBLE);
                    String des = String.format("（或%s积分）", limitGrabBean.credits);
                    limitGrabViewHolder.mLimitGrabCredit.setText(Utils.changeColor(des, ResourceUtils.getColor(R.color.order_item_surplus_time_color), 2, des.length() - 3));
                } else {
                    limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.GONE);
                }
            } else {
                if (Utils.isNotEmpty(limitGrabBean.credits)) {
                    limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.VISIBLE);
                    String des = String.format("%s积分", limitGrabBean.credits);
                    limitGrabViewHolder.mLimitGrabCredit.setText(Utils.changeColor(des, ResourceUtils.getColor(R.color.order_item_surplus_time_color), 0, des.length() - 3));
                } else {
                    limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.GONE);
                }
            }
            if (limitGrabBean.limitedUse) {
                limitGrabViewHolder.mLimitGrabTitleMark.setVisibility(View.VISIBLE);
            } else {
                limitGrabViewHolder.mLimitGrabTitleMark.setVisibility(View.GONE);
            }

            if (Utils.isNotEmpty(limitGrabBean.price)) {
                limitGrabViewHolder.mLimitGrabDetail.setVisibility(View.VISIBLE);
                String des = String.format("原价：%s元", String.valueOf(limitGrabBean.price));
                limitGrabViewHolder.mLimitGrabDetail.setText(Utils.textStrikeThrough(des, 0, des.length()));
            } else {
                limitGrabViewHolder.mLimitGrabDetail.setVisibility(View.GONE);
            }


            limitGrabViewHolder.mLimitGrabShare.setOnClickListener(v -> mCallback.onShareClicked(limitGrabBean));
            limitGrabViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(limitGrabBean);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (holder instanceof RewardActivityItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof RewardBean)) {
                return;
            }
            final RewardBean rewardBean = (RewardBean) obj;
            RewardActivityItemViewHolder rewardHolder = (RewardActivityItemViewHolder) holder;
            if (Utils.isNotEmpty(rewardBean.image)) {
                Glide.with(mContext).load(rewardBean.image).into(rewardHolder.mRewardHead);
            } else {
                Glide.with(mContext).load(ResourceUtils.getDrawable(R.drawable.img_default_reward)).into(rewardHolder.mRewardHead);
            }
            rewardHolder.mRewardName.setText(String.format("赢取%s",rewardBean.firstPrizeName));
            String st = rewardBean.startTime.substring(2, 10).replace("-", ".");
            String et = rewardBean.endTime.substring(2, 10).replace("-", ".");
            rewardHolder.mRewardTime.setText(String.format("活动时间：%s-%s", st, et));
            rewardHolder.mRewardShare.setOnClickListener(v -> mCallback.onShareClicked(rewardBean));
            rewardHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(rewardBean);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });

            return;
        }
        if (holder instanceof ClubJournalItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ClubJournalBean)) {
                return;
            }

            final ClubJournalBean clubJournal = (ClubJournalBean) obj;
            ClubJournalItemViewHolder clubJournalHolder = (ClubJournalItemViewHolder) holder;
            if (position == 0) {
                clubJournalHolder.mJournalMark.setVisibility(View.VISIBLE);
            } else {
                clubJournalHolder.mJournalMark.setVisibility(View.GONE);
            }
            clubJournalHolder.mJournalShare.setOnClickListener(v -> mCallback.onShareClicked(clubJournal));
            clubJournalHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(clubJournal);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            Glide.with(mContext).load(clubJournal.image).into(clubJournalHolder.mJournalHead);
            clubJournalHolder.mJournalName.setText(clubJournal.title);
            clubJournalHolder.mJournalReleaseTime.setText(String.format("发布时间：%s", clubJournal.modifyDate.substring(0, 10)));
            return;
        }
        if (holder instanceof PayForMeListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof PayForMeBean)) {
                return;
            }
            final PayForMeBean payForMe = (PayForMeBean) obj;
            PayForMeListItemViewHolder payForMeViewHolder = (PayForMeListItemViewHolder) holder;
            Glide.with(mContext).load(payForMe.image).into(payForMeViewHolder.mPayForMeHead);
            payForMeViewHolder.mPayForMeTitle.setText(payForMe.actName);
            payForMeViewHolder.mPayForMeMoney.setText(String.valueOf(payForMe.unitPrice));
            if (payForMe.unitPrice == 1) {
                payForMeViewHolder.mPayForMeMark.setVisibility(View.VISIBLE);
            } else {
                payForMeViewHolder.mPayForMeMark.setVisibility(View.GONE);
            }
            if (Utils.isNotEmpty(payForMe.actPrice)) {
                payForMeViewHolder.mPayForMeDetail.setVisibility(View.VISIBLE);
                String des = String.format("原价：%s元", payForMe.actPrice);
                payForMeViewHolder.mPayForMeDetail.setText(Utils.textStrikeThrough(des, 0, des.length()));
            } else {
                payForMeViewHolder.mPayForMeDetail.setVisibility(View.GONE);
            }
            payForMeViewHolder.mPayForMeShare.setOnClickListener(v -> mCallback.onShareClicked(payForMe));
            payForMeViewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(payForMe);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
        }
        if (holder instanceof ListFooterHolder) {
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
            return;
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
        if (holder instanceof OrderListItemViewHolder) {
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
        if (holder instanceof OrderListItemViewHolder) {
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
        if (mCallback.isHorizontalSliding()) {
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

        @Bind(R.id.user_head_url)
        CircleImageView mUserHeadUrl;
        @Bind(R.id.username)
        TextView mUserName;
        @Bind(R.id.order_time)
        TextView mOrderTime;
        @Bind(R.id.order_amount)
        TextView mOrderAmount;
        @Bind(R.id.paid_mark)
        TextView mPaidMark;
        @Bind(R.id.submit_section)
        LinearLayout mSubmitSection;
        @Bind(R.id.remain_time)
        TextView mRemainTime;
        @Bind(R.id.other_status)
        TextView mOtherStatus;
        @Bind(R.id.operation)
        LinearLayout mOperation;
        @Bind(R.id.negative)
        Button mNegative;
        @Bind(R.id.positive)
        Button mPositive;
        @Bind(R.id.paid_order_container)
        View mPaidOrderAmountContainer;

        public boolean isOperationVisible;

        public OrderListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CouponListItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_consume_money_description)
        TextView mTvConsumeMoneyDescription;
        @Bind(R.id.tv_coupon_title)
        TextView mTvCouponTitle;
        @Bind(R.id.tv_coupon_reward)
        TextView mTvCouponReward;
        @Bind(R.id.tv_coupon_period)
        TextView mCouponPeriod;
        @Bind(R.id.coupon_amount)
        TextView mCouponAmount;
        @Bind(R.id.coupon_type)
        TextView mCouponType;
        @Bind(R.id.btn_share_coupon)
        TextView mBtnShareCoupon;


        public CouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ShareCouponListItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_consume_money_description)
        TextView mTvConsumeMoneyDescription;
        @Bind(R.id.tv_coupon_title)
        TextView mTvCouponTitle;
        @Bind(R.id.tv_coupon_reward)
        TextView mTvCouponReward;
        @Bind(R.id.tv_coupon_period)
        TextView mCouponPeriod;
        @Bind(R.id.coupon_amount)
        TextView mCouponAmount;
        @Bind(R.id.coupon_type)
        TextView mCouponType;
        @Bind(R.id.btn_share_coupon)
        TextView mBtnShareCoupon;


        public ShareCouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class PaidCouponUserDetailItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatar)
        CircleImageView mAvatar;
        @Bind(R.id.tv_customer_name)
        TextView mTvCustomerName;
        @Bind(R.id.tv_telephone)
        TextView mTvTelephone;
        @Bind(R.id.tv_coupon_status_description)
        TextView mTvCouponStatusDescription;
        @Bind(R.id.tv_get_date)
        TextView mTvGetDate;


        public PaidCouponUserDetailItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatar)
        ImageView mAvatar;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.content)
        TextView mContent;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.unread)
        TextView mUnread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CreditRecordViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.credit_from)
        TextView mCreditFrom;
        @Bind(R.id.avatar)
        CircleImageView mAvatar;
        @Bind(R.id.avatar_name)
        TextView mAdverseName;
        @Bind(R.id.credit_time)
        TextView mCreditTime;
        @Bind(R.id.credit_amount)
        TextView mCreditAmount;

        public CreditRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CreditApplicationViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.credit_from)
        TextView mCreditFrom;
        @Bind(R.id.avatar)
        CircleImageView mAvatar;
        @Bind(R.id.avatar_name)
        TextView mAdverseName;
        @Bind(R.id.credit_time)
        TextView mCreditTime;
        @Bind(R.id.credit_amount)
        TextView mCreditAmount;

        public CreditApplicationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class RecentlyVisitViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.visitor_head)
        CircleImageView mVisitorHead;
        @Bind(R.id.visitor_name)
        TextView mVisitorName;
        @Bind(R.id.visitor_type)
        ImageView mVisitorType;
        @Bind(R.id.visitor_other_type)
        ImageView mVisitorOtherType;
        @Bind(R.id.visitor_to_chat)
        TextView mVisitorToChat;
        @Bind(R.id.visitor_tech)
        TextView mVisitorTech;
        @Bind(R.id.visitor_tech_num)
        TextView mVisitorTechNum;
        @Bind(R.id.visitor_time)
        TextView mVisitorTime;

        public RecentlyVisitViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    static class DynamicItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.dynamic_item_avatar)
        CircleImageView dynamicItemAvatar;
        @Bind(R.id.dynamic_item_type)
        ImageView dynamicItemType;
        @Bind(R.id.dynamic_item_name)
        TextView dynamicItemName;
        @Bind(R.id.dynamic_item_telephone)
        TextView dynamicItemTelephone;
        @Bind(R.id.dynamic_item_comment_detail)
        TextView dynamicItemCommentDetail;
        @Bind(R.id.dynamic_item_remark)
        TextView dynamicItemRemark;
        @Bind(R.id.dynamic_item_comment_starts)
        TextView dynamicItemCommentStarts;
        @Bind(R.id.dynamic_item_comment_reward)
        TextView dynamicItemCommentReward;
        @Bind(R.id.dynamic_item_comment)
        RelativeLayout dynamicItemComment;
        @Bind(R.id.dynamic_time)
        TextView dynamicTime;
        @Bind(R.id.btn_thanks)
        TextView btnThanks;

        public DynamicItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OnceCardItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.once_card_head)
        RoundImageView mOnceCardHead;
        @Bind(R.id.once_card_mark_new)
        TextView mOnceCardMarkNew;
        @Bind(R.id.once_card_mark_favorable)
        TextView mOnceCardMarkFavorable;
        @Bind(R.id.once_card_title)
        TextView mOnceCardTitle;
        @Bind(R.id.once_card_credit)
        TextView mOnceCardCredit;
        @Bind(R.id.once_card_money)
        TextView mOnceCardMoney;
        @Bind(R.id.once_card_price)
        TextView mOnceCardPrice;
        @Bind(R.id.once_card_share)
        Button mOnceCardShare;

        public OnceCardItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class LimitGrabItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.limit_grab_head)
        RoundImageView mLimitGrabHead;
        @Bind(R.id.limit_grab_mark)
        TextView mLimitGrabMark;
        @Bind(R.id.limit_grab_title)
        TextView mLimitGrabTitle;
        @Bind(R.id.limit_grab_title_mark)
        TextView mLimitGrabTitleMark;
        @Bind(R.id.limit_grab_money)
        TextView mLimitGrabMoney;
        @Bind(R.id.limit_grab_credit)
        TextView mLimitGrabCredit;
        @Bind(R.id.limit_grab_detail)
        TextView mLimitGrabDetail;
        @Bind(R.id.limit_grab_share)
        Button mLimitGrabShare;
        @Bind(R.id.unit)
        TextView mUnit;

        public LimitGrabItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class RewardActivityItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.reward_head)
        ImageView mRewardHead;
        @Bind(R.id.reward_name)
        TextView mRewardName;
        @Bind(R.id.reward_time)
        TextView mRewardTime;
        @Bind(R.id.reward_share)
        Button mRewardShare;

        public RewardActivityItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ClubJournalItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.journal_head)
        CircleImageView mJournalHead;
        @Bind(R.id.journal_name)
        TextView mJournalName;
        @Bind(R.id.journal_mark)
        TextView mJournalMark;
        @Bind(R.id.journal_release_time)
        TextView mJournalReleaseTime;
        @Bind(R.id.journal_share)
        Button mJournalShare;

        public ClubJournalItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class PayForMeListItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.pay_for_me_head)
        RoundImageView mPayForMeHead;
        @Bind(R.id.pay_for_me_mark)
        TextView mPayForMeMark;
        @Bind(R.id.pay_for_me_title)
        TextView mPayForMeTitle;
        @Bind(R.id.pay_for_me_money)
        TextView mPayForMeMoney;
        @Bind(R.id.pay_for_me_detail)
        TextView mPayForMeDetail;
        @Bind(R.id.pay_for_me_share)
        Button mPayForMeShare;

        public PayForMeListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
