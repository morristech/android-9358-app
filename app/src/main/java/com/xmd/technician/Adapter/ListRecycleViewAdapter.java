package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CreditDetailBean;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.bean.RecentlyVisitorBean;
import com.xmd.technician.chat.ChatConstant;
import com.xmd.technician.chat.ChatUser;
import com.xmd.technician.chat.CommonUtils;
import com.xmd.technician.chat.SmileUtils;
import com.xmd.technician.chat.UserUtils;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.RelativeDateFormatUtil;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
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

        void onItemClicked(T bean) throws HyphenateException;

        void onNegativeButtonClicked(T bean);

        void onPositiveButtonClicked(T bean);

        void onLoadMoreButtonClicked();

        void onSayHiButtonClicked(T bean);

        /**
         * @return whether the item is slideable
         */
        boolean isSlideable();

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
    private static final int TYPE_COUPON_INFO_ITEM_CASH = 11;
    private static final int TYPE_COUPON_INFO_ITEM_DELIVERY = 12;
    private static final int TYPE_COUPON_INFO_ITEM_FAVORABLE = 13;
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
            } else {
                itemHolder.mPaidOrderAmountContainer.setVisibility(View.INVISIBLE);
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
            if (couponInfo.useTypeName.equals("点钟券")) {
                couponListItemViewHolder.mTvCouponTitle.setText("点钟券");
                couponListItemViewHolder.mCouponType.setVisibility(View.GONE);
            } else {
                couponListItemViewHolder.mTvCouponTitle.setText(Utils.StrSubstring(8, couponInfo.actTitle, true));
                couponListItemViewHolder.mCouponType.setVisibility(View.VISIBLE);
            }
            couponListItemViewHolder.mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
            couponListItemViewHolder.mCouponPeriod.setText("有效时间：" + Utils.StrSubstring(19, couponInfo.couponPeriod, true));
            if (couponInfo.techCommission > 0) {
                String money = Utils.getFloat2Str(String.valueOf(couponInfo.techCommission));
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
//                    try {
//                        if(Utils.isNotEmpty(conversation.getLastMessage().getStringAttribute(ChatConstant.KEY_GAME_CLUB_ID))){
//                            conversationHolder.mUserManagerType.setVisibility(View.VISIBLE);
//                        }
//                    } catch (HyphenateException e) {
//                        e.printStackTrace();
//                    }
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
            if(!(obj instanceof RecentlyVisitorBean)){
                return;
            }
            final RecentlyVisitorBean recentlyVisitor = (RecentlyVisitorBean) obj;
            RecentlyVisitViewHolder viewHolder = (RecentlyVisitViewHolder) holder;

            if(Long.parseLong(recentlyVisitor.userId)>0){
                if(Utils.isNotEmpty(recentlyVisitor.userNoteName)){
                    viewHolder.mVisitorName.setText(recentlyVisitor.userNoteName);
                }else if(Utils.isNotEmpty(recentlyVisitor.userName)){
                    viewHolder.mVisitorName.setText(recentlyVisitor.userName);
                }else {
                    viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.default_user_name));
                }
            }else{
                viewHolder.mVisitorName.setText(ResourceUtils.getString(R.string.visitor_type));
            }
            if(Utils.isNotEmpty(recentlyVisitor.emchatId)){
                viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
            }else {
                viewHolder.mVisitorToChat.setVisibility(View.GONE);
            }
            if(Utils.isNotEmpty(recentlyVisitor.canSayHello)){
                viewHolder.mVisitorToChat.setVisibility(View.VISIBLE);
                if(recentlyVisitor.canSayHello.equals("0")){
                    viewHolder.mVisitorToChat.setEnabled(false);
                    viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.had_say_hi));
                    viewHolder.mVisitorToChat.setTextColor(ResourceUtils.getColor(R.color.color_white));
                }else if(recentlyVisitor.canSayHello.equals("1")){
                    viewHolder.mVisitorToChat.setEnabled(true);
                    viewHolder.mVisitorToChat.setText(ResourceUtils.getString(R.string.to_say_hi));
                }
            }else{
                viewHolder.mVisitorToChat.setVisibility(View.GONE);
            }
            if(Utils.isNotEmpty(recentlyVisitor.techName)&&Utils.isNotEmpty(recentlyVisitor.techSerialNo)){
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTechNum.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s",recentlyVisitor.techName));
                String mun = String.format("[%s]",recentlyVisitor.techSerialNo);
                viewHolder.mVisitorTechNum.setText(Utils.changeColor(mun,ResourceUtils.getColor(R.color.contact_marker),1,mun.lastIndexOf("]")));
            }else if(Utils.isNotEmpty(recentlyVisitor.techName)){
                viewHolder.mVisitorTech.setVisibility(View.VISIBLE);
                viewHolder.mVisitorTech.setText(String.format("所属技师：%s",recentlyVisitor.techName));
            }else{
                viewHolder.mVisitorTech.setVisibility(View.GONE);
                viewHolder.mVisitorTechNum.setVisibility(View.GONE);
            }

            if(null!=recentlyVisitor.customType){
                viewHolder.mVisitorType.setVisibility(View.GONE);
                viewHolder.mVisitorOtherType.setVisibility(View.GONE);
                if(recentlyVisitor.customType.equals(RequestConstant.FANS_USER)){
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                }else if(recentlyVisitor.customType.equals(RequestConstant.FANS_WX_USER)){
                    viewHolder.mVisitorOtherType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                    viewHolder.mVisitorOtherType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_fans));
                } else {
                    viewHolder.mVisitorType.setVisibility(View.VISIBLE);
                    viewHolder.mVisitorType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_weixin));
                }
            }else{
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

        @Bind(R.id.user_head_url)
        CircleImageView mUserHeadUrl;
        @Bind(R.id.username)
        TextView mUserName;
        @Bind(R.id.order_time)
        TextView mOrderTime;
        @Bind(R.id.order_amount)
        TextView mOrderAmount;
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


        public CouponListItemViewHolder(View itemView) {
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
        //     @Bind(R.id.user_manager_type)
//        TextView mUserManagerType;
//        @Bind(R.id.user_tech_type)
//        TextView mUserTechType;

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

}
