package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.exceptions.HyphenateException;
import com.xmd.app.widget.CircleAvatarView;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.ActivityRankingBean;
import com.xmd.technician.bean.ApplicationBean;
import com.xmd.technician.bean.ClubJournalBean;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CreditDetailBean;
import com.xmd.technician.bean.CustomerInfo;
import com.xmd.technician.bean.DynamicDetail;
import com.xmd.technician.bean.GroupBuyBean;
import com.xmd.technician.bean.LimitGrabBean;
import com.xmd.technician.bean.Order;
import com.xmd.technician.bean.PaidCouponUserDetail;
import com.xmd.technician.bean.PayForMeBean;
import com.xmd.technician.bean.RewardBean;
import com.xmd.technician.bean.ShareCouponBean;
import com.xmd.technician.bean.TechRankingBean;
import com.xmd.technician.common.DateUtils;
import com.xmd.technician.common.ItemSlideHelper;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.widget.BlockChildLinearLayout;
import com.xmd.technician.widget.CircleImageView;
import com.xmd.technician.widget.RoundImageView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Lhj on 15-11-24.
 */
public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ItemSlideHelper.Callback {


    private static final int TYPE_ORDER_ITEM = 0;
    private static final int TYPE_PAID_COUPON_USER_DETAIL = 2;
    private static final int TYPE_CONVERSATION = 3;
    private static final int TYPE_CREDIT_RECORD_ITEM = 4;
    private static final int TYPE_CREDIT_APPLICATION_ITEM = 5;
    private static final int TYPE_DYNAMIC_COMMENT_ITEM = 7;
    private static final int TYPE_DYNAMIC_COLLECT_ITEM = 8;
    private static final int TYPE_DYNAMIC_COUPON_ITEM = 9;
    private static final int TYPE_COUPON_INFO_ITEM_DELIVERY = 12;
    private static final int TYPE_COUPON_INFO_ITEM_FAVORABLE = 13;
    private static final int TYPE_LIMIT_GRAB_ITEM = 15;
    private static final int TYPE_REWARD_ACTIVITY_ITEM = 16;
    private static final int TYPE_CLUB_JOURNAL_ITEM = 17;
    private static final int TYPE_COUPON_PAID_ITEM = 18; //点钟券
    private static final int TYPE_COUPON_FAVORABLE_ITEM = 20;//优惠券
    private static final int TYPE_COUPON_PAY_FOR_ME_ITEM = 22;
    private static final int TYPE_TECH_PK_ACTIVITY_ITEM = 23;
    private static final int TYPE_TECH_PERSONAL_RANKING = 24;
    private static final int TYPE_TECH_BLACKLIST = 25;
    private static final int TYPE_GROUP_BUY_ITEM = 26;
    //   private static final int TYPE_INVITATION_REWARD_ACTIVITY_ITEM = 26;
    private static final int TYPE_FOOTER = 99;
    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<T> mData;
    private Callback mCallback;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSlideHelper mHelper;
    private String mDataLoadCompleteDes;

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

    public void SetDataLoadCompleteDes(String string) {
        this.mDataLoadCompleteDes = string;
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
                if (((CouponInfo) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
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
            } else if (mData.get(position) instanceof DynamicDetail) {
                if (((DynamicDetail) mData.get(position)).bizType == 1) {
                    return TYPE_DYNAMIC_COMMENT_ITEM;
                } else if (((DynamicDetail) mData.get(position)).bizType == 2 || ((DynamicDetail) mData.get(position)).bizType == 3) {
                    return TYPE_DYNAMIC_COUPON_ITEM;
                } else {
                    return TYPE_DYNAMIC_COLLECT_ITEM;
                }
            } else if (mData.get(position) instanceof LimitGrabBean) {
                return TYPE_LIMIT_GRAB_ITEM;
            } else if (mData.get(position) instanceof RewardBean) {
                return TYPE_REWARD_ACTIVITY_ITEM;
            } else if (mData.get(position) instanceof ClubJournalBean) {
                return TYPE_CLUB_JOURNAL_ITEM;
            } else if (mData.get(position) instanceof ShareCouponBean) {
                if (((ShareCouponBean) mData.get(position)).useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                    return TYPE_COUPON_PAID_ITEM;
                } else {
                    return TYPE_COUPON_FAVORABLE_ITEM;
                }
            } else if (mData.get(position) instanceof PayForMeBean) {
                return TYPE_COUPON_PAY_FOR_ME_ITEM;
            } else if (mData.get(position) instanceof ActivityRankingBean) {
                return TYPE_TECH_PK_ACTIVITY_ITEM;
            } else if (mData.get(position) instanceof TechRankingBean) {
                return TYPE_TECH_PERSONAL_RANKING;
            } else if (mData.get(position) instanceof CustomerInfo) {
                return TYPE_TECH_BLACKLIST;
            } else if (mData.get(position) instanceof GroupBuyBean) {
                return TYPE_GROUP_BUY_ITEM;
            } else {
                return TYPE_FOOTER;
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
            case TYPE_DYNAMIC_COMMENT_ITEM:
                View viewComment = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_comment_item, parent, false);
                return new DynamicItemViewHolder(viewComment);
            case TYPE_DYNAMIC_COUPON_ITEM:
                View viewCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_coupon_item, parent, false);
                return new DynamicItemViewHolder(viewCoupon);
            case TYPE_DYNAMIC_COLLECT_ITEM:
                View viewCollect = LayoutInflater.from(parent.getContext()).inflate(R.layout.dynamic_collect_item, parent, false);
                return new DynamicItemViewHolder(viewCollect);
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
            case TYPE_COUPON_FAVORABLE_ITEM:
                View viewFavorableCoupon = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_favorable_item, parent, false);
                return new ShareCouponListItemViewHolder(viewFavorableCoupon);
            case TYPE_COUPON_PAY_FOR_ME_ITEM:
                View viewPayForMe = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_pay_for_me_item, parent, false);
                return new PayForMeListItemViewHolder(viewPayForMe);
            case TYPE_TECH_PK_ACTIVITY_ITEM:
                View viewPKActivity = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_technician_pk_ranking, parent, false);
                return new PKActivityRankingListItemViewHolder(viewPKActivity);
            case TYPE_TECH_PERSONAL_RANKING:
                View viewRanking = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tech_personal_ranking_item, parent, false);
                return new TechPersonalRankingListItemViewHolder(viewRanking);
            case TYPE_TECH_BLACKLIST:
                View emchatBlasklist = LayoutInflater.from(parent.getContext()).inflate(R.layout.emchat_blacklist_item, parent, false);
                return new EmchatBlacklistListItemViewHolder(emchatBlasklist);
            case TYPE_GROUP_BUY_ITEM:
                View groupBuy = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group_buy, parent, false);
                return new GroupBuyListViewHolder(groupBuy);
//            case TYPE_INVITATION_REWARD_ACTIVITY_ITEM:
//                View invitationRewardActivity = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_invitation_reward_activity_item, parent, false);
//                return new InvitationRewardActivityItemViewHolder(invitationRewardActivity);
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
                    v -> MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_START_CHAT, Utils.wrapChatParams(order.emchatId, order.userName, order.headImgUrl, "customer")));
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
                couponListItemViewHolder.mCouponType.setText(Utils.isNotEmpty(couponInfo.couponTypeName) ? String.format("(%s)", couponInfo.couponTypeName) : couponInfo.couponTypeName);
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
            if (couponInfo.couponType.equals("discount")) {
                couponListItemViewHolder.imgMoneyMark.setVisibility(View.GONE);
                couponListItemViewHolder.emptyView.setVisibility(View.VISIBLE);
                couponListItemViewHolder.mCouponAmount.setText(String.format("%1.1f折", couponInfo.actValue / 100f));
            } else if (couponInfo.couponType.equals("gift")) {
                couponListItemViewHolder.emptyView.setVisibility(View.VISIBLE);
                couponListItemViewHolder.imgMoneyMark.setVisibility(View.GONE);
                couponListItemViewHolder.mCouponAmount.setText(TextUtils.isEmpty(couponInfo.actSubTitle) ? couponInfo.actTitle : couponInfo.actSubTitle);
            } else {
                couponListItemViewHolder.emptyView.setVisibility(View.GONE);
                couponListItemViewHolder.imgMoneyMark.setVisibility(View.VISIBLE);
                if (Utils.isNotEmpty(couponInfo.consumeMoney)) {
                    couponListItemViewHolder.mCouponAmount.setText(String.valueOf(couponInfo.actValue));
                }
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
                            Utils.wrapChatParams(paidCouponUserDetail.emchatId, paidCouponUserDetail.userName, paidCouponUserDetail.headImgUrl, "customer")));
            itemHolder.mTvCustomerName.setText(Utils.StrSubstring(8, paidCouponUserDetail.userName, true));
            itemHolder.mTvGetDate.setText(paidCouponUserDetail.getDate);
            itemHolder.mTvTelephone.setText(paidCouponUserDetail.telephone);
            itemHolder.mTvCouponStatusDescription.setText(paidCouponUserDetail.couponStatusDescription);
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
                creditRecordViewHolder.mCreditFrom.setText(creditDetailBean.businessCategoryDesc);
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
                creditRecordViewHolder.mCreditAmount.setTextColor(ResourceUtils.getColor(R.color.color_main_head));
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
        if (holder instanceof DynamicItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof DynamicDetail)) {
                return;
            }
            final DynamicDetail dynamicDetail = (DynamicDetail) obj;
            DynamicItemViewHolder viewHolder = (DynamicItemViewHolder) holder;

            if (Utils.isNotEmpty(dynamicDetail.userName) && dynamicDetail.userName.endsWith("**(匿名)")) {
                viewHolder.dynamicItemAvatar.setImageResource(R.drawable.img_default_avatar);
            } else {
                viewHolder.dynamicItemAvatar.setUserInfo(dynamicDetail.userId, Utils.isNotEmpty(dynamicDetail.avatarUrl) ? dynamicDetail.avatarUrl : dynamicDetail.imageUrl, false);
            }
            viewHolder.dynamicItemName.setText(TextUtils.isEmpty(dynamicDetail.userName) ? "匿名用户" : Utils.StrSubstring(6, dynamicDetail.userName, true));
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
            viewHolder.dynamicTime.setText(DateUtils.getTimestampString(new Date(dynamicDetail.createTime)));
            String textDescription = dynamicDetail.description;
            String textRemark = dynamicDetail.remark;
            int commentScore = dynamicDetail.commentScore;
            float rewardAmount = dynamicDetail.rewardAmount / 100f;

            if (dynamicDetail.bizType == 1) {

                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_comment));
                if (Utils.isNotEmpty(textDescription)) {
                    viewHolder.dynamicItemCommentDetail.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemCommentDetail.setText(textDescription);
                } else {
                    viewHolder.dynamicItemCommentDetail.setVisibility(View.INVISIBLE);
                    viewHolder.dynamicItemCommentDetail.setText("");
                }
                if (Utils.isNotEmpty(textRemark)) {
                    viewHolder.dynamicItemRemark.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemRemark.setText("#" + textRemark.replaceAll("、", " #"));
                } else {
                    viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                    viewHolder.dynamicItemRemark.setText("");
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
                viewHolder.dynamicItemCommentStarts.setVisibility(View.GONE);
                viewHolder.dynamicItemCommentReward.setVisibility(View.GONE);
            } else if (dynamicDetail.bizType == 3) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_coupon));
                viewHolder.dynamicItemCommentDetail.setText(textDescription);
                viewHolder.dynamicItemCommentStarts.setVisibility(View.GONE);
                viewHolder.dynamicItemCommentReward.setVisibility(View.GONE);
                if (Utils.isNotEmpty(textRemark)) {
                    viewHolder.dynamicItemRemark.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemRemark.setText("(" + textRemark + ")");
                } else {
                    viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                }

            } else if (dynamicDetail.bizType == 4) {
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_paid));
                viewHolder.dynamicItemCommentDetail.setText(textDescription);
                viewHolder.dynamicItemCommentStarts.setVisibility(View.GONE);
                viewHolder.dynamicItemCommentReward.setVisibility(View.GONE);
                if (Utils.isNotEmpty(textDescription)) {
                    viewHolder.dynamicItemRemark.setVisibility(View.VISIBLE);
                    viewHolder.dynamicItemRemark.setText("(" + textRemark + ")");
                } else {
                    viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                }

            } else if (dynamicDetail.bizType == 5) {
                viewHolder.dynamicItemCommentStarts.setVisibility(View.GONE);
                viewHolder.dynamicItemCommentReward.setVisibility(View.GONE);
                viewHolder.dynamicItemRemark.setVisibility(View.GONE);
                viewHolder.dynamicItemType.setImageDrawable(ResourceUtils.getDrawable(R.drawable.img_to_reward));
                viewHolder.dynamicItemCommentDetail.setText(Utils.changeStringNumColor(textDescription, ResourceUtils.getColor(R.color.color_main_btn)));
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
            limitGrabViewHolder.mLimitGrabMoney.setText(limitGrabBean.amount);
            if (Integer.parseInt(limitGrabBean.credits) > 0) {
                limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.VISIBLE);
                String des = String.format("（或%s积分）", limitGrabBean.credits);
                limitGrabViewHolder.mLimitGrabCredit.setText(Utils.changeColor(des, ResourceUtils.getColor(R.color.order_item_surplus_time_color), 2, des.length() - 3));
            } else {
                limitGrabViewHolder.mLimitGrabCredit.setVisibility(View.GONE);
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
            limitGrabViewHolder.mShowCode.setOnClickListener(v -> mCallback.onPositiveButtonClicked(limitGrabBean));
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
            rewardHolder.mRewardName.setText(String.format("赢取%s", rewardBean.firstPrizeName));
            if (Utils.isNotEmpty(rewardBean.startTime) && Utils.isNotEmpty(rewardBean.endTime)) {
                rewardHolder.mRewardTime.setText(String.format("活动时间：%s-%s", rewardBean.startTime, rewardBean.endTime));
            }
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

//        if (holder instanceof InvitationRewardActivityItemViewHolder) {
//            Object obj = mData.get(position);
//            if (!(obj instanceof RewardBean)) {
//                return;
//            }
//            final InvitationRewardBean rewardBean = (InvitationRewardBean) obj;
//            InvitationRewardActivityItemViewHolder rewardHolder = (InvitationRewardActivityItemViewHolder) holder;
//            if (Utils.isNotEmpty(rewardBean.image)) {
//                Glide.with(mContext).load(rewardBean.image).into(rewardHolder.mRewardHead);
//            } else {
//                Glide.with(mContext).load(ResourceUtils.getDrawable(R.drawable.img_default_reward)).into(rewardHolder.mRewardHead);
//            }
//            rewardHolder.mRewardName.setText(String.format("赢取%s", rewardBean.firstPrizeName));
//            if (Utils.isNotEmpty(rewardBean.startTime) && Utils.isNotEmpty(rewardBean.endTime)) {
//                rewardHolder.mRewardTime.setText(String.format("活动时间：%s-%s", rewardBean.startTime, rewardBean.endTime));
//            }
//            rewardHolder.mRewardShare.setOnClickListener(v -> mCallback.onShareClicked(rewardBean));
//            rewardHolder.itemView.setOnClickListener(v -> {
//                try {
//                    mCallback.onItemClicked(rewardBean);
//                } catch (HyphenateException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            return;
//        }
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
            clubJournalHolder.mTvJournalNo.setText(String.format("No %s", String.valueOf(clubJournal.sequenceNo)));
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
            payForMeViewHolder.mShowCode.setOnClickListener(v -> mCallback.onPositiveButtonClicked(payForMe));
            return;
        }
        if (holder instanceof PKActivityRankingListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ActivityRankingBean)) {
                return;
            }
            final ActivityRankingBean activityBean = (ActivityRankingBean) obj;
            PKActivityRankingListItemViewHolder rankingViewHolder = (PKActivityRankingListItemViewHolder) holder;
            rankingViewHolder.pkActiveName.setText(activityBean.activityName);
            rankingViewHolder.pkActiveStatus.setText(activityBean.statusName);
            if (activityBean.status.equals("4")) {
                Glide.with(mContext).load(R.drawable.icon_underway).into(rankingViewHolder.imgPkActiveStatus);
                rankingViewHolder.pkActiveStatus.setTextColor(ResourceUtils.getColor(R.color.underway_color));
            } else {
                Glide.with(mContext).load(R.drawable.icon_completed).into(rankingViewHolder.imgPkActiveStatus);
                rankingViewHolder.pkActiveStatus.setTextColor(ResourceUtils.getColor(R.color.color_main_remark));
            }
            rankingViewHolder.pkActiveTime.setText(activityBean.startDate + "至" + activityBean.endDate);
            if (activityBean.rankingList != null) {
                PKRankingAdapter adapter = null;
                if (Utils.isEmpty(activityBean.categoryId)) {
                    adapter = new PKRankingAdapter(mContext, activityBean.rankingList, "");
                } else {
                    adapter = new PKRankingAdapter(mContext, activityBean.rankingList, activityBean.categoryId);
                }
                rankingViewHolder.teamList.setLayoutManager(new GridLayoutManager(mContext, 3));
                rankingViewHolder.teamList.setAdapter(adapter);
            }
            rankingViewHolder.layoutTechnicianRanking.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(activityBean);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
            return;
        }
        if (holder instanceof TechPersonalRankingListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof TechRankingBean)) {
                return;
            }
            final TechRankingBean techBean = (TechRankingBean) obj;
            TechPersonalRankingListItemViewHolder techRankingViewHolder = (TechPersonalRankingListItemViewHolder) holder;
            if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_USER)) {
                techRankingViewHolder.tvRankingMemberNumber.setText(String.format("%s 人", techBean.counts));
            } else if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_PAID)) {
                techRankingViewHolder.tvRankingMemberNumber.setText(String.format("%s 个", techBean.counts));
            } else {
                techRankingViewHolder.tvRankingMemberNumber.setText(String.format("%s 个", techBean.counts));
            }
            if (position == 0) {
                techRankingViewHolder.rankingTitle.setVisibility(View.VISIBLE);
                if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_USER)) {
                    techRankingViewHolder.sortType.setText("注册用户");
                } else if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_PAID)) {
                    techRankingViewHolder.sortType.setText("点钟券");
                } else {
                    techRankingViewHolder.sortType.setText("好评数");
                }

            } else {
                techRankingViewHolder.rankingTitle.setVisibility(View.GONE);
            }
            if (position == 0) {
                Glide.with(mContext).load(R.drawable.icon_nub_01).into(techRankingViewHolder.imgRankingNumber);
                techRankingViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                techRankingViewHolder.textRankingNumber.setVisibility(View.GONE);
            } else if (position == 1) {
                Glide.with(mContext).load(R.drawable.icon_nub_02).into(techRankingViewHolder.imgRankingNumber);
                techRankingViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                techRankingViewHolder.textRankingNumber.setVisibility(View.GONE);
            } else if (position == 2) {
                Glide.with(mContext).load(R.drawable.icon_nub_03).into(techRankingViewHolder.imgRankingNumber);
                techRankingViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                techRankingViewHolder.textRankingNumber.setVisibility(View.GONE);
            } else {
                techRankingViewHolder.textRankingNumber.setText(String.valueOf(position + 1));
                techRankingViewHolder.imgRankingNumber.setVisibility(View.GONE);
                techRankingViewHolder.textRankingNumber.setVisibility(View.VISIBLE);
            }
            if (Utils.isNotEmpty(techBean.name)) {
                techRankingViewHolder.tvTechName.setText(techBean.name);
            } else {
                techRankingViewHolder.tvTechName.setText("技师");
            }
            if (Utils.isNotEmpty(techBean.serialNo)) {
                String techNO = String.format("[%s]", techBean.serialNo);
                techRankingViewHolder.tvTechSerialNo.setText(Utils.changeColor(techNO, ResourceUtils.getColor(R.color.contact_marker), 1, techNO.length() - 1));
                techRankingViewHolder.tvTechSerialNo.setVisibility(View.VISIBLE);
            } else {
                techRankingViewHolder.tvTechSerialNo.setVisibility(View.GONE);
            }
            Glide.with(mContext).load(techBean.avatarUrl).into(techRankingViewHolder.imgTechHead);

        }
        if (holder instanceof EmchatBlacklistListItemViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CustomerInfo)) {
                return;
            }
            final CustomerInfo customerInfo = (CustomerInfo) obj;
            EmchatBlacklistListItemViewHolder viewHolder = (EmchatBlacklistListItemViewHolder) holder;

            if (Long.parseLong(customerInfo.userId) > 0) {
                if (Utils.isNotEmpty(customerInfo.userNoteName)) {
                    viewHolder.tvCustomerName.setText(customerInfo.userNoteName);
                } else if (Utils.isNotEmpty(customerInfo.userName)) {
                    viewHolder.tvCustomerName.setText(customerInfo.userName);
                } else {
                    viewHolder.tvCustomerName.setText(ResourceUtils.getString(R.string.default_user_name));
                }
            } else {
                viewHolder.tvCustomerName.setText(ResourceUtils.getString(R.string.visitor_type));
            }
            Glide.with(mContext).load(customerInfo.avatarUrl).into(viewHolder.imgCustomerHead);

            viewHolder.itemView.setOnClickListener(v -> {
                try {
                    mCallback.onItemClicked(customerInfo);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            });
        }

        if(holder instanceof GroupBuyListViewHolder){
            Object obj = mData.get(position);
            if(!(obj instanceof GroupBuyBean)){
                return;
            }
            final GroupBuyBean groupBuyBean = (GroupBuyBean) obj;
            GroupBuyListViewHolder viewHolder = (GroupBuyListViewHolder) holder;
            Glide.with(mContext).load(groupBuyBean.itemImageUrl).into(viewHolder.groupBuyHead);
            viewHolder.groupBuyBigTitle.setText(Utils.StrSubstring(9,groupBuyBean.name,true));
            viewHolder.groupBuySmallTitle.setText(String.format("（%s人团）",String.valueOf(groupBuyBean.personalCount)));
            viewHolder.groupBuyMoney.setText(String.format("%1.2f",groupBuyBean.price / 100f));
            String des = String.format("原价：%1.2f元",  groupBuyBean.itemPrice /100f);
            viewHolder.groupBuyDetail.setText(Utils.textStrikeThrough(des, 0, des.length()));
            viewHolder.groupBuyUseTime.setText(groupBuyBean.activityPeriod);
            viewHolder.groupBuyShare.setOnClickListener(v -> mCallback.onShareClicked(groupBuyBean));
            viewHolder.llShowCode.setOnClickListener(v -> mCallback.onPositiveButtonClicked(groupBuyBean));
        }

        if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = "";
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                if (Utils.isNotEmpty(mDataLoadCompleteDes)) {
                    desc = mDataLoadCompleteDes;
                } else {
                    desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                }
                footerHolder.itemFooter.setOnClickListener(null);
            } else {

                desc = ResourceUtils.getString(R.string.order_list_item_loading);
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

    public interface Callback<T> {

        void onItemClicked(T bean) throws HyphenateException;

        void onNegativeButtonClicked(T bean);

        void onPositiveButtonClicked(T bean);

        void onLoadMoreButtonClicked();

        void onSayHiButtonClicked(T bean);

        void onShareClicked(T bean);

        void onLongClicked(T bean);

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

    static class ListFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class OrderListItemViewHolder extends RecyclerView.ViewHolder {

        public boolean isOperationVisible;
        @BindView(R.id.user_head_url)
        CircleImageView mUserHeadUrl;
        @BindView(R.id.username)
        TextView mUserName;
        @BindView(R.id.order_time)
        TextView mOrderTime;
        @BindView(R.id.order_amount)
        TextView mOrderAmount;
        @BindView(R.id.paid_mark)
        TextView mPaidMark;
        @BindView(R.id.submit_section)
        LinearLayout mSubmitSection;
        @BindView(R.id.remain_time)
        TextView mRemainTime;
        @BindView(R.id.other_status)
        TextView mOtherStatus;
        @BindView(R.id.operation)
        LinearLayout mOperation;
        @BindView(R.id.negative)
        Button mNegative;
        @BindView(R.id.positive)
        Button mPositive;
        @BindView(R.id.paid_order_container)
        View mPaidOrderAmountContainer;

        public OrderListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CouponListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_consume_money_description)
        TextView mTvConsumeMoneyDescription;
        @BindView(R.id.tv_coupon_title)
        TextView mTvCouponTitle;
        @BindView(R.id.tv_coupon_reward)
        TextView mTvCouponReward;
        @BindView(R.id.tv_coupon_period)
        TextView mCouponPeriod;
        @BindView(R.id.coupon_amount)
        TextView mCouponAmount;
        @BindView(R.id.coupon_type)
        TextView mCouponType;
        @BindView(R.id.btn_share_coupon)
        TextView mBtnShareCoupon;


        public CouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ShareCouponListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_consume_money_description)
        TextView mTvConsumeMoneyDescription;
        @BindView(R.id.tv_coupon_title)
        TextView mTvCouponTitle;
        @BindView(R.id.tv_coupon_reward)
        TextView mTvCouponReward;
        @BindView(R.id.tv_coupon_period)
        TextView mCouponPeriod;
        @BindView(R.id.coupon_amount)
        TextView mCouponAmount;
        @BindView(R.id.coupon_type)
        TextView mCouponType;
        @BindView(R.id.btn_share_coupon)
        TextView mBtnShareCoupon;
        @BindView(R.id.img_money_mark)
        ImageView imgMoneyMark;
        @BindView(R.id.coupon_empty_view)
        View emptyView;


        public ShareCouponListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class PaidCouponUserDetailItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        CircleImageView mAvatar;
        @BindView(R.id.tv_customer_name)
        TextView mTvCustomerName;
        @BindView(R.id.tv_telephone)
        TextView mTvTelephone;
        @BindView(R.id.tv_coupon_status_description)
        TextView mTvCouponStatusDescription;
        @BindView(R.id.tv_get_date)
        TextView mTvGetDate;


        public PaidCouponUserDetailItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ConversationViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        CircleAvatarView mAvatar;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.content)
        TextView mContent;
        @BindView(R.id.time)
        TextView mTime;
        @BindView(R.id.unread)
        TextView mUnread;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CreditRecordViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.credit_from)
        TextView mCreditFrom;
        @BindView(R.id.avatar)
        CircleImageView mAvatar;
        @BindView(R.id.avatar_name)
        TextView mAdverseName;
        @BindView(R.id.credit_time)
        TextView mCreditTime;
        @BindView(R.id.credit_amount)
        TextView mCreditAmount;

        public CreditRecordViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CreditApplicationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.credit_from)
        TextView mCreditFrom;
        @BindView(R.id.avatar)
        CircleImageView mAvatar;
        @BindView(R.id.avatar_name)
        TextView mAdverseName;
        @BindView(R.id.credit_time)
        TextView mCreditTime;
        @BindView(R.id.credit_amount)
        TextView mCreditAmount;

        public CreditApplicationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class DynamicItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.dynamic_item_avatar)
        CircleAvatarView dynamicItemAvatar;
        @BindView(R.id.dynamic_item_type)
        ImageView dynamicItemType;
        @BindView(R.id.dynamic_item_name)
        TextView dynamicItemName;
        @BindView(R.id.dynamic_item_telephone)
        TextView dynamicItemTelephone;
        @BindView(R.id.dynamic_item_comment_detail)
        TextView dynamicItemCommentDetail;
        @BindView(R.id.dynamic_item_remark)
        TextView dynamicItemRemark;
        @BindView(R.id.dynamic_item_comment_starts)
        TextView dynamicItemCommentStarts;
        @BindView(R.id.dynamic_item_comment_reward)
        TextView dynamicItemCommentReward;
        @BindView(R.id.dynamic_item_comment)
        RelativeLayout dynamicItemComment;
        @BindView(R.id.dynamic_time)
        TextView dynamicTime;
        @BindView(R.id.btn_thanks)
        TextView btnThanks;

        public DynamicItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class LimitGrabItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.limit_grab_head)
        RoundImageView mLimitGrabHead;
        @BindView(R.id.limit_grab_mark)
        TextView mLimitGrabMark;
        @BindView(R.id.limit_grab_title)
        TextView mLimitGrabTitle;
        @BindView(R.id.limit_grab_title_mark)
        TextView mLimitGrabTitleMark;
        @BindView(R.id.limit_grab_money)
        TextView mLimitGrabMoney;
        @BindView(R.id.limit_grab_credit)
        TextView mLimitGrabCredit;
        @BindView(R.id.limit_grab_detail)
        TextView mLimitGrabDetail;
        @BindView(R.id.limit_grab_share)
        Button mLimitGrabShare;
        @BindView(R.id.unit)
        TextView mUnit;
        @BindView(R.id.ll_show_code)
        LinearLayout mShowCode;

        public LimitGrabItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class RewardActivityItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.reward_head)
        ImageView mRewardHead;
        @BindView(R.id.reward_name)
        TextView mRewardName;
        @BindView(R.id.reward_time)
        TextView mRewardTime;
        @BindView(R.id.reward_share)
        Button mRewardShare;

        public RewardActivityItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class InvitationRewardActivityItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.invitation_reward_head)
        ImageView invitationRewardHead;
        @BindView(R.id.invitation_reward_name)
        TextView invitationRewardName;
        @BindView(R.id.invitation_reward_time)
        TextView invitationRewardTime;
        @BindView(R.id.invitation_reward_share)
        Button invitationRewardShare;

        public InvitationRewardActivityItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ClubJournalItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.journal_head)
        CircleImageView mJournalHead;
        @BindView(R.id.journal_name)
        TextView mJournalName;
        @BindView(R.id.journal_mark)
        TextView mJournalMark;
        @BindView(R.id.journal_release_time)
        TextView mJournalReleaseTime;
        @BindView(R.id.journal_share)
        Button mJournalShare;
        @BindView(R.id.tv_journal_no)
        TextView mTvJournalNo;

        public ClubJournalItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class PayForMeListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pay_for_me_head)
        RoundImageView mPayForMeHead;
        @BindView(R.id.pay_for_me_mark)
        TextView mPayForMeMark;
        @BindView(R.id.pay_for_me_title)
        TextView mPayForMeTitle;
        @BindView(R.id.pay_for_me_money)
        TextView mPayForMeMoney;
        @BindView(R.id.pay_for_me_detail)
        TextView mPayForMeDetail;
        @BindView(R.id.pay_for_me_share)
        Button mPayForMeShare;
        @BindView(R.id.ll_show_code)
        LinearLayout mShowCode;

        public PayForMeListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class PKActivityRankingListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.pk_active_name)
        TextView pkActiveName;
        @BindView(R.id.img_pk_active_status)
        ImageView imgPkActiveStatus;
        @BindView(R.id.pk_active_status)
        TextView pkActiveStatus;
        @BindView(R.id.team_list)
        RecyclerView teamList;
        @BindView(R.id.pk_active_time)
        TextView pkActiveTime;
        @BindView(R.id.layout_technician_ranking)
        BlockChildLinearLayout layoutTechnicianRanking;
        @BindView(R.id.ll_tech_ranking)
        LinearLayout llTechTanking;

        PKActivityRankingListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class TechPersonalRankingListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sort_type)
        TextView sortType;
        @BindView(R.id.ranking_title)
        LinearLayout rankingTitle;
        @BindView(R.id.img_ranking_number)
        ImageView imgRankingNumber;
        @BindView(R.id.text_ranking_number)
        TextView textRankingNumber;
        @BindView(R.id.img_tech_head)
        RoundImageView imgTechHead;
        @BindView(R.id.tv_tech_name)
        TextView tvTechName;
        @BindView(R.id.tv_tech_serialNo)
        TextView tvTechSerialNo;
        @BindView(R.id.tv_ranking_member_number)
        TextView tvRankingMemberNumber;

        TechPersonalRankingListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class EmchatBlacklistListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.customer_head)
        CircleImageView imgCustomerHead;
        @BindView(R.id.customer_name)
        TextView tvCustomerName;

        EmchatBlacklistListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }


    static class GroupBuyListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_buy_head)
        RoundImageView groupBuyHead;
        @BindView(R.id.group_buy_big_title)
        TextView groupBuyBigTitle;
        @BindView(R.id.group_buy_small_title)
        TextView groupBuySmallTitle;
        @BindView(R.id.group_buy_money)
        TextView groupBuyMoney;
        @BindView(R.id.group_buy_detail)
        TextView groupBuyDetail;
        @BindView(R.id.group_buy_use_time)
        TextView groupBuyUseTime;
        @BindView(R.id.group_buy_share)
        Button groupBuyShare;
        @BindView(R.id.ll_show_code)
        LinearLayout llShowCode;

        GroupBuyListViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
