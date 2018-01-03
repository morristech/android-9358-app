package com.xmd.manager.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.util.DateUtils;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.ActivityRankingBean;
import com.xmd.manager.beans.BadComment;
import com.xmd.manager.beans.BadCommentRateListBean;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.CommentDetailBean;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.CouponRecordBean;
import com.xmd.manager.beans.CouponStatisticsBean;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.beans.GroupBean;
import com.xmd.manager.beans.GroupMessage;
import com.xmd.manager.beans.MarketingIncomeBean;
import com.xmd.manager.beans.OnlinePayBean;
import com.xmd.manager.beans.OperateReportBean;
import com.xmd.manager.beans.Order;
import com.xmd.manager.beans.RegisterInfo;
import com.xmd.manager.beans.StaffDataBean;
import com.xmd.manager.beans.TechBadComment;
import com.xmd.manager.beans.TechRankingBean;
import com.xmd.manager.beans.VisitInfo;
import com.xmd.manager.common.DescribeMesaageUtil;
import com.xmd.manager.common.ItemSlideHelper;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.WidgetUtils;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.BlockChildLinearLayout;
import com.xmd.manager.widget.CircleImageView;
import com.xmd.permission.BusinessPermissionManager;
import com.xmd.permission.PermissionConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
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
         * whether is paged
         *
         * @return
         */
        boolean isPaged();

        /**
         * @return whether the item is slideable
         */
        void onSlideDeleteItem(T bean);

        /**
         * longClickedToDelete
         */
        void onLongClicked(T bean);

        boolean isSlideable();

        /**
         * Whether to display data statistics
         *
         * @return
         */
        boolean showStatData();
    }

    private static final int TYPE_ORDER_ITEM = 0;
    private static final int TYPE_CUSTOMER_ITEM = 1;
    private static final int TYPE_CUSTOMER_ITEM_HEADER = 3;
    private static final int TYPE_CONVERSATION_ITEM = 4;
    private static final int TYPE_COUPON_ITEM = 6;
    private static final int TYPE_CLUB_ITEM = 8;
    private static final int TYPE_VISIT_ITEM = 9;
    private static final int TYPE_GROUP_MESSAGE_ITEM = 10;
    private static final int TYPE_BAD_COMMENT_ITEM = 11;
    private static final int TYPE_BAD_COMMENT_TECH_ITEM = 12;
    private static final int TYPE_ClUB_GROUP_ITEM = 13;
    private static final int TYPE_REGISTER_ITEM = 14;
    private static final int TYPE_USER_COMMENT_ITEM = 15;
    private static final int TYPE_ONLINE_PAY_ITEM = 16;
    private static final int TYPE_MARKETING_INCOME_ITEM = 17;
    private static final int TYPE_TECH_PK_ACTIVITY_ITEM = 18;
    private static final int TYPE_TECH_PERSONAL_RANKING = 19;
    private static final int TYPE_STAFF_DATA_ITEM = 20;
    private static final int TYPE_COUPON_OPERATE_ITEM = 21;
    private static final int TYPE_OPERATE_REPORT_ITEM = 22;
    private static final int TYPE_COUPON_RECORD_ITEM = 23;
    private static final int TYPE_NORMAL_COUPON_LIST_ITEM = 24;
    private static final int TYPE_PAID_COUPON_LIST_ITEM = 25;
    private static final int TYPE_FOOTER = 99;
    private static final int TYPE_DADA_IS_EMPTY = 100;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;

    private List<T> mData;
    private Callback mCallback;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemSlideHelper mHelper;
    private String mBottomMessage;

    public ListRecycleViewAdapter(Context context, List<T> data, Callback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
        mHelper = new ItemSlideHelper(mContext, this);
    }

    public ListRecycleViewAdapter(Context context, List<T> data) {
        mContext = context;
        mData = data;
    }

    public void setData(List<T> data) {
        mData = data;
        mIsEmpty = data.isEmpty();
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    public void setBottomAlterTextMessage(String alterText) {
        this.mBottomMessage = alterText;
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
        } else if (!mIsEmpty) {
            if (mData.get(position) instanceof Order) {
                return TYPE_ORDER_ITEM;
            } else if (mData.get(position) instanceof Customer) {
                return TYPE_CUSTOMER_ITEM;
            } else if (mData.get(position) instanceof EMConversation) {
                return TYPE_CONVERSATION_ITEM;
            } else if (mData.get(position) instanceof CouponInfo) {
                return TYPE_COUPON_ITEM;
            } else if (mData.get(position) instanceof ClubInfo) {
                return TYPE_CLUB_ITEM;
            } else if (mData.get(position) instanceof VisitInfo) {
                return TYPE_VISIT_ITEM;
            } else if (mData.get(position) instanceof GroupMessage) {
                return TYPE_GROUP_MESSAGE_ITEM;
            } else if (mData.get(position) instanceof BadComment) {
                return TYPE_BAD_COMMENT_ITEM;
            } else if (mData.get(position) instanceof TechBadComment) {
                return TYPE_BAD_COMMENT_TECH_ITEM;
            } else if (mData.get(position) instanceof GroupBean) {
                return TYPE_ClUB_GROUP_ITEM;
            } else if (mData.get(position) instanceof RegisterInfo) {
                return TYPE_REGISTER_ITEM;
            } else if (mData.get(position) instanceof OnlinePayBean) {
                return TYPE_ONLINE_PAY_ITEM;
            } else if (mData.get(position) instanceof MarketingIncomeBean) {
                return TYPE_MARKETING_INCOME_ITEM;
            } else if (mData.get(position) instanceof CommentDetailBean) {
                return TYPE_USER_COMMENT_ITEM;
            } else if (mData.get(position) instanceof ActivityRankingBean) {
                return TYPE_TECH_PK_ACTIVITY_ITEM;
            } else if (mData.get(position) instanceof TechRankingBean) {
                return TYPE_TECH_PERSONAL_RANKING;
            } else if (mData.get(position) instanceof StaffDataBean) {
                return TYPE_STAFF_DATA_ITEM;
            } else if (mData.get(position) instanceof CouponStatisticsBean) {
                return TYPE_COUPON_OPERATE_ITEM;
            } else if (mData.get(position) instanceof OperateReportBean) {
                return TYPE_OPERATE_REPORT_ITEM;
            } else if (mData.get(position) instanceof CouponRecordBean) {
                return TYPE_COUPON_RECORD_ITEM;
            } else if (mData.get(position) instanceof CouponBean) {
                if (((CouponBean) mData.get(position)).couponType.equals(Constant.COUPON_PAID_TYPE)) {
                    return TYPE_PAID_COUPON_LIST_ITEM;
                } else {
                    return TYPE_NORMAL_COUPON_LIST_ITEM;
                }

            }
        }
        return TYPE_DADA_IS_EMPTY;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CONVERSATION_ITEM:
                return new ConversationListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.conversation_list_item, parent, false));
            case TYPE_ORDER_ITEM:
                return new OrderListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, parent, false));
            case TYPE_CUSTOMER_ITEM:
                return new CustomerListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item, parent, false));
            case TYPE_CUSTOMER_ITEM_HEADER:
                return new CustomerHeaderListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_list_item_header, parent, false));
            case TYPE_FOOTER:
                return new ListFooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false));
            case TYPE_COUPON_ITEM:
                return new CouponItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_item, parent, false));
            case TYPE_CLUB_ITEM:
                return new ClubListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.club_list_item, parent, false));
            case TYPE_VISIT_ITEM:
                return new VisitListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.visit_list_item, parent, false));
            case TYPE_GROUP_MESSAGE_ITEM:
                return new GroupMessageItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_message_list_item_detail, parent, false));
            case TYPE_BAD_COMMENT_ITEM:
                return new BadCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bad_comment_list_item, parent, false));
            case TYPE_BAD_COMMENT_TECH_ITEM:
                return new TechBadCommentItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.bad_tech_comment_item, parent, false));
            case TYPE_ClUB_GROUP_ITEM:
                return new ClubGroupItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item, parent, false));
            case TYPE_REGISTER_ITEM:
                return new RegisterListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_register_list_item, parent, false));
            case TYPE_USER_COMMENT_ITEM:
                return new UserCommentListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_comment_list_item, parent, false));
            case TYPE_ONLINE_PAY_ITEM:
                return new OnlinePayListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_online_pay_list_item, parent, false));
            case TYPE_MARKETING_INCOME_ITEM:
                return new MarketingIncomeItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_marketing_income_item, parent, false));
            case TYPE_TECH_PK_ACTIVITY_ITEM:
                View viewPKActivity = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_technician_pk_ranking, parent, false);
                return new PKActivityRankingListItemViewHolder(viewPKActivity);
            case TYPE_TECH_PERSONAL_RANKING:
                View viewRanking = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tech_personal_ranking_item, parent, false);
                return new TechPersonalRankingListItemViewHolder(viewRanking);
            case TYPE_STAFF_DATA_ITEM:
                View staffData = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_staff_data_item, parent, false);
                return new StaffDataListItemViewHolder(staffData);
            case TYPE_COUPON_OPERATE_ITEM:
                View couponOperateView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_coupon_operate_item, parent, false);
                return new CouponOperateListItemViewHolder(couponOperateView);
            case TYPE_OPERATE_REPORT_ITEM:
                View operateReportView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_operate_item, parent, false);
                return new OperateReportListItemViewHolder(operateReportView);
            case TYPE_COUPON_RECORD_ITEM:
                View couponRecordItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_coupon_record_item, parent, false);
                return new CouponRecordListItemViewHolder(couponRecordItem);
            case TYPE_NORMAL_COUPON_LIST_ITEM:
                View normalCouponItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_coupon_item, parent, false);
                return new CouponListItemViewHolder(normalCouponItem);
            case TYPE_PAID_COUPON_LIST_ITEM:
                View paidCouponItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_list_paid_item, parent, false);
                return new CouponListItemViewHolder(paidCouponItem);
            default:
                return null;
        }

    }

    @Override
    public int getSlideOutRange(View targetView) {
        RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(targetView);
        int range = 0;
        if (holder instanceof ConversationListItemViewHolder) {
            ConversationListItemViewHolder conHolder = (ConversationListItemViewHolder) holder;
          /*  if (conHolder.delete.getVisibility() == View.VISIBLE) {
                range += conHolder.delete.getLayoutParams().width;
            }*/

        }
        return range;
    }

    @Override
    public boolean isViewSlideable(View targetView) {
        RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(targetView);
        if (holder instanceof ConversationListItemViewHolder) {
            return ((ConversationListItemViewHolder) holder).isOperationVisible;
        }
        return false;
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

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = TextUtils.isEmpty(mBottomMessage) ? ResourceUtils.getString(R.string.order_list_item_no_more) : mBottomMessage;
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> mCallback.onLoadMoreButtonClicked());
            }
            footerHolder.itemFooter.setText(desc);
        } else if (!mIsEmpty) {
            Object obj = mData.get(position);

            if (holder instanceof OrderListItemViewHolder) {
                if (!(obj instanceof Order)) {
                    return;
                }

                bindOrderListItemViewHolder(holder, obj);

            } else if (holder instanceof CustomerListItemViewHolder) {
                if (!(obj instanceof Customer)) {
                    return;
                }
                bindCustomerListItemViewHolder(holder, obj);

            } else if (holder instanceof CustomerHeaderListItemViewHolder) {
                if (!(obj instanceof Customer)) {
                    return;
                }
                bindCustomerHeaderListItemViewHolder(holder, obj);

            } else if (holder instanceof CouponItemViewHolder) {

                if (!(obj instanceof CouponInfo)) {
                    return;
                }
                bindCouponItemViewHolder(holder, obj);

            } else if (holder instanceof ClubListItemViewHolder) {
                if (obj instanceof ClubInfo) {
                    bindClubItemViewHolder(holder, obj);
                }
            } else if (holder instanceof VisitListItemViewHolder) {
                bindVisitItemViewHolder(holder, obj);
            } else if (holder instanceof GroupMessageItemViewHolder) {
                bindGroupMessageItemViewHolder(holder, obj);
            } else if (holder instanceof BadCommentItemViewHolder) {
                bindBadCommentItemViewHolder(holder, obj);
            } else if (holder instanceof TechBadCommentItemViewHolder) {
                bindTechBadCommentItemViewHolder(holder, obj);
            } else if (holder instanceof ClubGroupItemViewHolder) {
                bindClubGroupItemViewHolder(holder, obj);
            } else if (holder instanceof RegisterListItemViewHolder) {
                bindRegisterItemViewHolder(holder, obj);
            } else if (holder instanceof UserCommentListItemViewHolder) {
                bindUserCommentListItemViewHolder(holder, obj);
            } else if (holder instanceof OnlinePayListItemViewHolder) {
                bindOnlinePayListItemViewHolder(holder, obj, position);
            } else if (holder instanceof MarketingIncomeItemViewHolder) {
                bindMarketingIncomeViewHolder(holder, obj);
            } else if (holder instanceof PKActivityRankingListItemViewHolder) {
                bindPkActivityListItemViewHolder(holder, obj);
            } else if (holder instanceof TechPersonalRankingListItemViewHolder) {
                bindTechPersonalRankingViewHolder(holder, obj, position);
            } else if (holder instanceof StaffDataListItemViewHolder) {
                bindStaffDataRankingViewHolder(holder, obj, position);
            } else if (holder instanceof CouponOperateListItemViewHolder) {
                bindCouponOperateItemViewHolder(holder, obj);
            } else if (holder instanceof OperateReportListItemViewHolder) {
                bindOperateReportListViewHolder(holder, obj);
            } else if (holder instanceof CouponRecordListItemViewHolder) {
                bindCouponRecordListViewHolder(holder, obj);
            } else if (holder instanceof CouponListItemViewHolder) {
                bindCouponListItemViewHolder(holder, obj);
            }
        }

    }


    /**
     * 订单
     *
     * @param holder
     * @param obj
     */
    private void bindOrderListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final Order order = (Order) obj;
        boolean isPaidOrder = Constant.ORDER_TYPE_PAID.equals(order.orderType);
        OrderListItemViewHolder itemHolder = (OrderListItemViewHolder) holder;
        Glide.with(mContext).load(order.headImgUrl).error(R.drawable.icon22).into(itemHolder.avatar);
        itemHolder.customerName.setText(Utils.briefString(order.customerName, 7));
        itemHolder.customerPhone.setText(order.phoneNum);
        itemHolder.bookTime.setText(order.createdAt);
        itemHolder.techName.setText(Utils.isEmpty(order.techName) ? ResourceUtils.getString(R.string.order_list_item_not_decided) : Utils.briefString(order.techName, 5));
        if (Utils.isEmpty(order.techName)) {
            itemHolder.techSerial.setVisibility(View.GONE);
        } else {
            itemHolder.techSerial.setVisibility(View.VISIBLE);
            if (Utils.isNotEmpty(order.techSerialNo)) {
                int startIndex = 1;
                int endIndex = startIndex + order.techSerialNo.length();
                Spannable spanString = Utils.changeColor("[" + order.techSerialNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                itemHolder.techSerial.setText(spanString);
            } else {
                itemHolder.techSerial.setText("");
            }
        }

        itemHolder.orderState.setText(isPaidOrder ? Constant.PAID_ORDER_LABELS.get(order.status) : Constant.ORDER_LABELS.get(order.status));
        itemHolder.itemName.setText(Utils.isEmpty(order.itemName) ? ResourceUtils.getString(R.string.order_list_item_not_decided) : order.itemName);
        itemHolder.itemTime.setText(order.appointTime);

        if (isPaidOrder) {
            itemHolder.downPayment.setVisibility(View.VISIBLE);
            itemHolder.downPayment.setText(String.format(ResourceUtils.getString(R.string.paid_order_list_order_down_payment), order.downPayment));
            if (order.payType == 2) {
                itemHolder.paidMark.setVisibility(View.VISIBLE);
            } else {
                itemHolder.paidMark.setVisibility(View.GONE);
            }
        } else {
            itemHolder.downPayment.setVisibility(View.INVISIBLE);
            itemHolder.paidMark.setVisibility(View.GONE);
        }

        WidgetUtils.setViewVisibleOrGone(itemHolder.operation, BusinessPermissionManager.getInstance().containPermission(PermissionConstants.MG_ORDER_OPERATE));
        if (!WidgetUtils.isVisible(itemHolder.operation)) {
            return;
        }
        // 只有当其有权限操作的时候，才需要继续以下的判断操作。
        WidgetUtils.setViewVisibleOrGone(itemHolder.operationForSubmit, false);
        WidgetUtils.setViewVisibleOrGone(itemHolder.operationForAccept, false);
        WidgetUtils.setViewVisibleOrGone(itemHolder.operationForPaidOrderAccept, false);

        if (Constant.ORDER_STATUS_SUBMIT.equals(order.status)) {
            WidgetUtils.setViewVisibleOrGone(itemHolder.operationForSubmit, true);
            if (mCallback != null) {
                itemHolder.accept.setOnClickListener(v -> mCallback.onPositiveButtonClicked(order));
                itemHolder.reject.setOnClickListener(v -> mCallback.onNegativeButtonClicked(order));
            }
        } else if (Constant.ORDER_STATUS_ACCEPT.equals(order.status)) {
            // Paid Order
            if (isPaidOrder) {
                WidgetUtils.setViewVisibleOrGone(itemHolder.operationForPaidOrderAccept, true);
                itemHolder.expire.setEnabled(order.isExpire);
                itemHolder.verified.setEnabled(order.isExpire);
                if (order.isExpire) {
                    if (mCallback != null) {
                        itemHolder.expire.setOnClickListener(v -> mCallback.onNegativeButtonClicked(order));
                        itemHolder.verified.setOnClickListener(v -> mCallback.onPositiveButtonClicked(order));
                    }
                }

            } else {
                // Appointed Order
                WidgetUtils.setViewVisibleOrGone(itemHolder.operationForAccept, true);
                if (mCallback != null) {
                    itemHolder.failure.setOnClickListener(v -> mCallback.onNegativeButtonClicked(order));
                    itemHolder.complete.setOnClickListener(v -> mCallback.onPositiveButtonClicked(order));
                }
            }
        } else {
            WidgetUtils.setViewVisibleOrGone(itemHolder.operationForAccept, false);
        }
    }

    /**
     * 客情管理
     *
     * @param holder
     * @param obj
     */
    private void bindCustomerListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final Customer customer = (Customer) obj;
        if (TextUtils.isEmpty(customer.userName)) {
            customer.userName = "匿名用户";
        }
        CustomerListItemViewHolder viewHolder = (CustomerListItemViewHolder) holder;
        viewHolder.mTvCustomerName.setText(Utils.briefString(customer.userName, 8));
        viewHolder.mTvTelephone.setText(customer.phoneNum);
        Spannable spannable = new SpannableString(String.format(ResourceUtils.getString(R.string.bad_comment_unit_format), customer.badCommentCount));
        spannable.setSpan(new StyleSpan(Typeface.BOLD), 3, String.valueOf(customer.badCommentCount).length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.mTvBadCommentsCount.setText(spannable);

        if (Constant.CUSTOMER_TYPE_WEIXIN.equals(customer.userType)) {
            viewHolder.mTvCustomerType.setText("微信");
            viewHolder.mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_weixin));
            viewHolder.mTvCustomerType.setBackgroundResource(R.drawable.customer_type_weixin);
        } else if (Constant.CUSTOMER_TYPE_TEMP.equals(customer.userType) || Constant.CUSTOMER_TYPE_TEMP_TECH.equals(customer.userType)) {
            viewHolder.mTvCustomerType.setText("领券");
            viewHolder.mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_temp));
            viewHolder.mTvCustomerType.setBackgroundResource(R.drawable.customer_type_temp);
        } else {
            viewHolder.mTvCustomerType.setText("粉丝");
            viewHolder.mTvCustomerType.setTextColor(ResourceUtils.getColor(R.color.customer_type_label_user));
            viewHolder.mTvCustomerType.setBackgroundResource(R.drawable.customer_type_user);
        }
        viewHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(customer));
    }

    /**
     * 客情管理头部
     *
     * @param holder
     * @param obj
     */
    private void bindCustomerHeaderListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final Customer customer = (Customer) obj;
        CustomerHeaderListItemViewHolder viewHolder = (CustomerHeaderListItemViewHolder) holder;
        if (Constant.CUSTOMER_TYPE_HEADER_TECHNICIAN == customer.type) {
            viewHolder.mTvHeader.setText(customer.techName);
        } else {
            viewHolder.mTvHeader.setText(Constant.ACTIVE_DEGREES.get(customer.active));
        }
        viewHolder.mTvGroupCount.setText(String.format(ResourceUtils.getString(R.string.person_unit_format), customer.groupCount));
    }

    private void bindUserCommentListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final CommentDetailBean commentBean = (CommentDetailBean) obj;
        UserCommentListItemViewHolder viewHolder = (UserCommentListItemViewHolder) holder;
        Glide.with(mContext).load(commentBean.obj.techAvatarUrl).into(viewHolder.techAvatar);
        if (Utils.isNotEmpty(commentBean.obj.techName)) {
            viewHolder.tvTechName.setText(commentBean.obj.techName);
        } else {
            if (Utils.isNotEmpty(commentBean.commentRateList.get(0).techName)) {
                viewHolder.tvTechName.setText(commentBean.commentRateList.get(0).techName);
            } else {
                viewHolder.tvTechName.setText("");
            }

        }
        if (Utils.isNotEmpty(commentBean.obj.techSerialNo)) {
            Spannable spanString = Utils.changeColor("[ " + commentBean.obj.techSerialNo + " ]",
                    ResourceUtils.getColor(R.color.customer_comment_reward_text_color), 2, 2 + String.valueOf(commentBean.obj.techSerialNo).length());
            viewHolder.tvTechNo.setText(spanString);
        } else {
            viewHolder.tvTechNo.setText("");
        }

        viewHolder.tvScore.setText(String.format("%1.1f", commentBean.obj.rate / 20f));
        if (commentBean.obj.rewardAmount > 0) {
            float reward = commentBean.obj.rewardAmount / 100f;
            String spanString = String.format("打赏：%1.2f元", reward);
            viewHolder.tvReward.setText(Utils.changeColor(spanString, ResourceUtils.getColor(R.color.customer_comment_reward_text_color), 3, spanString.length() - 1));
        } else {
            viewHolder.tvReward.setText("");
        }
        if (TextUtils.isEmpty(commentBean.obj.comment)) {
            viewHolder.tvComment.setVisibility(View.GONE);
            viewHolder.impressionLine.setVisibility(View.GONE);
        } else {
            viewHolder.impressionLine.setVisibility(View.VISIBLE);
            viewHolder.tvComment.setVisibility(View.VISIBLE);
            viewHolder.tvComment.setText(commentBean.obj.comment);
        }
        if (commentBean.commentRateList != null && commentBean.commentRateList.size() > 0) {
            viewHolder.commentRateList.setVisibility(View.VISIBLE);
            CommentDetailItemAdapter adapter = new CommentDetailItemAdapter(mContext, commentBean.commentRateList);
            viewHolder.commentRateList.setLayoutManager(new GridLayoutManager(mContext, 4));
            viewHolder.commentRateList.setAdapter(adapter);
        } else {
            viewHolder.commentRateList.setVisibility(View.GONE);
        }
        fillImpression(mContext, commentBean.obj.impression, viewHolder.impressionContainer);
        viewHolder.tvTime.setText(commentBean.obj.createdAt);
        viewHolder.btnDelete.setOnClickListener(v -> mCallback.onNegativeButtonClicked(commentBean));
    }

    private void fillImpression(Context context, String impression, GridView gv) {
        if (TextUtils.isEmpty(impression)) {
            gv.setVisibility(View.GONE);
            return;
        }

        gv.setVisibility(View.VISIBLE);
        List<Map<String, Object>> list = new ArrayList<>();
        String[] impres = null;
        if (impression.contains(",")) {
            impres = impression.split(",");
        } else {
            impres = impression.split("、");
        }

        for (int i = 0; i < impres.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("icon", impres[i]);
            list.add(item);
        }
        gv.setAdapter(new SimpleAdapter(context, list, R.layout.impression_item_view, new String[]{"icon"}, new int[]{R.id.tv_impression}));
    }


    /**
     * 优惠券
     *
     * @param holder
     * @param obj
     */
    private void bindCouponItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final CouponInfo couponInfo = (CouponInfo) obj;
        CouponItemViewHolder itemHolder = (CouponItemViewHolder) holder;
        itemHolder.mCouponName.setText(couponInfo.actTitle);
        itemHolder.mCouponType.setText(couponInfo.useTypeName);
        itemHolder.mCouponDesc.setText(couponInfo.consumeMoneyDescription);
        itemHolder.mActivityDuration.setText(couponInfo.actPeriod);
        itemHolder.mCouponDuration.setText(couponInfo.couponPeriod);
        itemHolder.mCouponUseDuration.setText(DescribeMesaageUtil.getTimePeriodDes(couponInfo.useTimePeriod));
        itemHolder.mCouponStatus.setText(couponInfo.actStatusName);
        itemHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(couponInfo));
    }

    private void bindClubItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        ClubListItemViewHolder clubHolder = (ClubListItemViewHolder) holder;
        ClubInfo clubInfo = (ClubInfo) obj;
        Glide.with(mContext).load(clubInfo.imageUrl).error(R.drawable.img_club_logo).into(clubHolder.mClubAvatar);
        clubHolder.mClubName.setText(clubInfo.clubName);
        clubHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(clubInfo));
    }

    private void bindVisitItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        VisitListItemViewHolder visitHolder = (VisitListItemViewHolder) holder;
        VisitInfo visitInfo = (VisitInfo) obj;
        if (Utils.isNotEmpty(visitInfo.userName)) {
            visitHolder.mVisitName.setText(visitInfo.userName);
        } else {
            visitHolder.mVisitName.setText(ResourceUtils.getString(R.string.visit_default_name));
        }
        if (Utils.isNotEmpty(visitInfo.userPhoneNum)) {
            visitHolder.mVisitPhone.setText(visitInfo.userPhoneNum);
        } else {
            visitHolder.mVisitPhone.setText("");
        }
        visitHolder.mVisitTime.setText(visitInfo.createdAt);
        if (visitInfo.source.equals(RequestConstant.KEY_LINK)) {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_link));
        } else if (visitInfo.source.equals(RequestConstant.KEY_WIFI)) {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_wifi));
        } else if (visitInfo.source.equals(RequestConstant.KEY_CLUB_QR_CODE)) {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_club_qr_code));
        } else if (visitInfo.source.equals(RequestConstant.KEY_TECH_QR_CODE)) {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_tech_qr_code));
        } else if (visitInfo.source.equals(RequestConstant.KEY_9358)) {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_9358));
        } else {
            visitHolder.mVisitFrom.setText(ResourceUtils.getString(R.string.from_link));
        }
    }

    private void bindRegisterItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        RegisterListItemViewHolder visitHolder = (RegisterListItemViewHolder) holder;
        RegisterInfo user = (RegisterInfo) obj;

        if (Utils.isNotEmpty(user.userName)) {
            visitHolder.mRegisterName.setText(user.userName);
        } else {
            visitHolder.mRegisterName.setText(ResourceUtils.getString(R.string.visit_default_name));
        }
        if (Utils.isNotEmpty(user.phoneNum)) {
            visitHolder.mRegisterPhone.setText(user.phoneNum);
        } else {
            visitHolder.mRegisterPhone.setText("");
        }
        if (Utils.isNotEmpty(user.techName)) {
            visitHolder.mRegisterTech.setText(user.techName);
            if (Utils.isNotEmpty(user.techSerialNo)) {
                int startIndex = 1;
                int endIndex = startIndex + user.techSerialNo.length();
                Spannable spanString = Utils.changeColor("[" + user.techSerialNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                visitHolder.mRegisterTechNo.setText(spanString);

                visitHolder.mRegisterTechNo.setVisibility(View.VISIBLE);
            } else {
                visitHolder.mRegisterTechNo.setText("");
            }

        } else {
            visitHolder.mRegisterTech.setText(ResourceUtils.getString(R.string.tech_is_null));
            visitHolder.mRegisterTechNo.setText("");
        }
        visitHolder.mRegisterTime.setText(user.createTime);
        Glide.with(mContext).load(user.userHeadimgurl).into(visitHolder.mRegisterHead);
    }

    private void bindGroupMessageItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        GroupMessageItemViewHolder groupHolder = (GroupMessageItemViewHolder) holder;
        GroupMessage groupMessage = (GroupMessage) obj;

        groupHolder.messageSummaryContainer.setVisibility(View.VISIBLE);
        groupHolder.messageDetailContainer.setVisibility(View.GONE);

        //消息内容
        if (Utils.isNotEmpty(groupMessage.message)) {
            groupHolder.contentInfoSummary.setText(groupMessage.message);
            groupHolder.contentInfoDetail.setText(groupMessage.message);
        } else {
            groupHolder.contentInfoSummary.setText("");
            groupHolder.contentInfoDetail.setText("");
        }

        //消息图片
        if (Utils.isNotEmpty(groupMessage.imageUrl)) {
            groupHolder.contentImage.setVisibility(View.VISIBLE);
            groupHolder.contentTitleSummary.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(groupMessage.imageUrl).into(groupHolder.contentImage);
        } else {
            groupHolder.contentImage.setVisibility(View.GONE);
            groupHolder.contentTitleSummary.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(groupMessage.imageUrl) || Utils.isNotEmpty(groupMessage.message)) {
            groupHolder.msgContentTip.setVisibility(View.VISIBLE);
            groupHolder.msgDetailContainer.setVisibility(View.VISIBLE);
            groupHolder.msgSummaryContainer.setVisibility(View.VISIBLE);
        } else {
            groupHolder.msgContentTip.setVisibility(View.GONE);
            groupHolder.msgDetailContainer.setVisibility(View.GONE);
            groupHolder.msgSummaryContainer.setVisibility(View.GONE);
        }

        //消息中的优惠活动
        if (Utils.isNotEmpty(groupMessage.actName)) {
            groupHolder.activitySummaryContainer.setVisibility(View.VISIBLE);
            groupHolder.activityDetailContainer.setVisibility(View.VISIBLE);
            groupHolder.activityDetailTip.setVisibility(View.VISIBLE);

            groupHolder.activityInfoSummary.setText(groupMessage.actName);
            groupHolder.activityInfoDetail.setText(groupMessage.actName);

            if (Utils.isNotEmpty(groupMessage.msgType)) {
                groupHolder.activityTitleSummary.setVisibility(View.VISIBLE);
                groupHolder.activityTitleDetail.setVisibility(View.VISIBLE);

                groupHolder.activityTitleSummary.setText(Constant.MESSAGE_ACTIVITY_LABELS.get(groupMessage.msgType));
                groupHolder.activityTitleDetail.setText(Constant.MESSAGE_ACTIVITY_LABELS.get(groupMessage.msgType));
            } else {
                groupHolder.activityTitleSummary.setVisibility(View.GONE);
                groupHolder.activityTitleDetail.setVisibility(View.GONE);
            }

        } else {
            groupHolder.activitySummaryContainer.setVisibility(View.GONE);
            groupHolder.activityDetailContainer.setVisibility(View.GONE);
            groupHolder.activityDetailTip.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(String.valueOf(groupMessage.sendCount))) {
            groupHolder.customerCountSummary.setText(String.valueOf(groupMessage.sendCount) + "人");
        }

        //消息发送的用户群
        if (groupMessage.groupType != null && Utils.isNotEmpty(Constant.USE_GROUP_LABELS.get(groupMessage.groupType))) {
            groupHolder.typeTitleSummary.setVisibility(View.VISIBLE);
            groupHolder.typeTitleDetail.setVisibility(View.VISIBLE);
            groupHolder.typeTitleSummary.setText(Constant.USE_GROUP_LABELS.get(groupMessage.groupType));
            groupHolder.typeTitleDetail.setText(Constant.USE_GROUP_LABELS.get(groupMessage.groupType));
        } else {
            groupHolder.typeTitleSummary.setVisibility(View.GONE);
            groupHolder.typeTitleDetail.setVisibility(View.GONE);
        }

        if (Utils.isNotEmpty(groupMessage.subGroupLabels)) {
            String s = String.format("%s，共%d人", groupMessage.subGroupLabels, groupMessage.sendCount);
            groupHolder.customerTypeDetail.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), groupMessage.subGroupLabels.length() + 2, s.length() - 1));
        } else {
            String s = String.format("共%d人", groupMessage.sendCount);
            groupHolder.customerTypeDetail.setText(Utils.changeColor(s, ResourceUtils.getColor(R.color.colorMain), 1, s.length() - 1));
        }

        //消息发送时间
        if (Utils.isNotEmpty(groupMessage.sendDate)) {
            groupHolder.sendTimeSummary.setText(groupMessage.sendDate);
            groupHolder.sendTimeDetail.setText(groupMessage.sendDate);
        }

        //消息操作人
        if (Utils.isNotEmpty(groupMessage.operatorName)) {
            groupHolder.sendOperatorSummary.setText(groupMessage.operatorName);
            groupHolder.sendOperatorDetail.setText(groupMessage.operatorName);
        }

        //效果分析数据
        groupHolder.sendCount.setText(String.valueOf(groupMessage.sendCount));
        groupHolder.viewCount.setText(String.valueOf(groupMessage.viewCount));

        if (Constant.MSG_TYPE_JOURNAL.equals(groupMessage.msgType)) {
            groupHolder.joinCount.setText("-");
        } else {
            groupHolder.joinCount.setText(String.valueOf(groupMessage.joinCount));
        }

        if (Constant.MSG_TYPE_JOURNAL.equals(groupMessage.msgType) || Constant.MSG_TYPE_LUCKY_WHEEL.equals(groupMessage.msgType)) {
            groupHolder.shopCount.setText("-");
        } else {
            groupHolder.shopCount.setText(String.valueOf(groupMessage.verificationCount));
        }

        if (mCallback.showStatData()) {
            groupHolder.statHintText.setVisibility(View.VISIBLE);
            groupHolder.statViewContainer.setVisibility(View.VISIBLE);
        } else {
            groupHolder.statHintText.setVisibility(View.GONE);
            groupHolder.statViewContainer.setVisibility(View.GONE);
        }

        //展开收起文字加下划线
        groupHolder.expandButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        groupHolder.putWayButton.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        groupHolder.itemView.setOnClickListener(v -> {
            if (groupHolder.messageSummaryContainer.getVisibility() == View.VISIBLE) {
                groupHolder.messageSummaryContainer.setVisibility(View.GONE);
                groupHolder.messageDetailContainer.setVisibility(View.VISIBLE);
            } else {
                groupHolder.messageSummaryContainer.setVisibility(View.VISIBLE);
                groupHolder.messageDetailContainer.setVisibility(View.GONE);
            }
        });
    }

    private void bindBadCommentItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        BadCommentItemViewHolder badHolder = (BadCommentItemViewHolder) holder;
        BadComment badComment = (BadComment) obj;
        badHolder.commentTime.setText(DateUtils.getTimestampString(new Date(badComment.createdAt)));
        if (Utils.isNotEmpty(badComment.phoneNum)) {
            badHolder.customerPhone.setText(badComment.phoneNum);
        } else {
            badHolder.customerPhone.setText("");
        }
        if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_ORDER) || badComment.commentType.equals(RequestConstant.COMMENT_TYPE_TECH)) {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_text));
            badHolder.commentTechName.setText(Utils.isEmpty(badComment.techName) ? ResourceUtils.getString(R.string.tech_text) : badComment.techName);
            if (Utils.isNotEmpty(badComment.techNo)) {
                int startIndex = 1;
                int endIndex = startIndex + badComment.techNo.length();
                Spannable spanString = Utils.changeColor("[" + badComment.techNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                badHolder.commentTechCode.setText(spanString);
                badHolder.commentTechCode.setVisibility(View.VISIBLE);
            } else {
                badHolder.commentTechCode.setVisibility(View.GONE);
            }
        } else if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_CLUB)) {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_text));
            badHolder.commentTechName.setText(ResourceUtils.getString(R.string.comment_club));
            badHolder.commentTechCode.setVisibility(View.GONE);
        } else {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_qr_code));
            badHolder.commentTechName.setText(Utils.isEmpty(badComment.techName) ? ResourceUtils.getString(R.string.tech_text) : badComment.techName);
            if (Utils.isNotEmpty(badComment.techNo)) {
                int startIndex = 1;
                int endIndex = startIndex + badComment.techNo.length();
                Spannable spanString = Utils.changeColor("[" + badComment.techNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                badHolder.commentTechCode.setText(spanString);
                badHolder.commentTechCode.setVisibility(View.VISIBLE);
            } else {
                badHolder.commentTechCode.setVisibility(View.GONE);
            }
        }
        if (badComment.commentRateList != null && badComment.commentRateList.size() > 0) {
            badHolder.commentProjectList.setVisibility(View.VISIBLE);
            CommentDetailItemAdapter<BadCommentRateListBean> adapter = new CommentDetailItemAdapter(mContext, badComment.commentRateList);
            badHolder.commentProjectList.setLayoutManager(new GridLayoutManager(mContext, 2));
            badHolder.commentProjectList.setAdapter(adapter);
        } else {
            badHolder.commentProjectList.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(badComment.comment)) {
            badHolder.commentDetail.setText(badComment.comment);
            badHolder.commentDetail.setVisibility(View.VISIBLE);
        } else {
            badHolder.commentDetail.setVisibility(View.INVISIBLE);
        }
        badHolder.customerName.setText(badComment.userName);
        if (badComment.isAnonymous.equals("Y")) {
            Glide.with(mContext).load(R.drawable.icon22).into(badHolder.commentCustomerHead);
            badHolder.commentReturnVisit.setVisibility(View.GONE);
        } else {
            Glide.with(mContext).load(badComment.avatarUrl).into(badHolder.commentCustomerHead);
            badHolder.commentReturnVisit.setVisibility(View.VISIBLE);
            if (badComment.returnStatus.equals("Y")) {
                badHolder.commentReturnVisit.setText(ResourceUtils.getString(R.string.operation));
                badHolder.commentReturnVisit.setEnabled(true);
            } else {
                badHolder.commentReturnVisit.setText(ResourceUtils.getString(R.string.return_visit));
                badHolder.commentReturnVisit.setEnabled(true);
            }
        }
        if (badComment.status.equals(RequestConstant.DELETE_COMMENT)) {
            badHolder.imgBtnDelete.setVisibility(View.GONE);
            badHolder.imgDeleteMark.setVisibility(View.VISIBLE);
        } else {
            badHolder.imgBtnDelete.setVisibility(View.VISIBLE);
            badHolder.imgDeleteMark.setVisibility(View.GONE);
        }
        badHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onItemClicked(badComment);
            }
        });
        badHolder.commentReturnVisit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onPositiveButtonClicked(badComment);
            }
        });
        badHolder.imgBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onNegativeButtonClicked(badComment);
            }
        });
    }

    private void bindTechBadCommentItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        TechBadCommentItemViewHolder techHolder = (TechBadCommentItemViewHolder) holder;
        TechBadComment techComment = (TechBadComment) obj;
        techHolder.badCommentTechRank.setText(String.valueOf(holder.getLayoutPosition() + 1));
        Glide.with(mContext).load(techComment.avatarUrl).into(techHolder.badCommentTechHead);
        techHolder.badCommentTechName.setText(Utils.StrSubstring(6, techComment.name, true));
        if (Utils.isNotEmpty(techComment.techNo)) {
            String techNo = String.format("[%s]", techComment.techNo);
            techHolder.badCommentTechSerial.setText(Utils.changeColor(techNo, ResourceUtils.getColor(R.color.btn_background), 1, techNo.length() - 1));
            techHolder.badCommentTechSerial.setVisibility(View.VISIBLE);
        } else {
            techHolder.badCommentTechSerial.setVisibility(View.GONE);
        }
        techHolder.badCommentTotal.setText(String.valueOf(techComment.badCommentCount));
        techHolder.badCommentRatio.setText(techComment.badCommentRate + "%");

    }

    private void bindClubGroupItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        ClubGroupItemViewHolder groupListHolder = (ClubGroupItemViewHolder) holder;
        GroupBean groupBean = (GroupBean) obj;
        groupListHolder.groupName.setText(Utils.StrSubstring(11, groupBean.name, true));
        groupListHolder.groupMemberAmount.setText(String.format("（%s）", String.valueOf(groupBean.totalCount)));
        if (Utils.isNotEmpty(groupBean.userNames)) {
            groupListHolder.groupMember.setText(groupBean.userNames);
        } else {
            groupListHolder.groupMember.setText(ResourceUtils.getString(R.string.group_member_is_null));
        }
        groupListHolder.itemView.setOnClickListener(v -> {
            mCallback.onItemClicked(groupBean);
        });

    }

    private void bindOnlinePayListItemViewHolder(RecyclerView.ViewHolder holder, Object obj, int position) {
        OnlinePayListItemViewHolder viewHolder = (OnlinePayListItemViewHolder) holder;
        OnlinePayBean onLinePay = (OnlinePayBean) obj;
        Glide.with(mContext).load(onLinePay.avatarUrl).into(viewHolder.customerHead);
        if (Utils.isNotEmpty(onLinePay.userName)) {
            viewHolder.tvCustomerName.setText(Utils.StrSubstring(8, onLinePay.userName, true));
        } else {
            viewHolder.tvCustomerName.setText("匿名用户");
        }
        if (Utils.isNotEmpty(onLinePay.telephone)) {
            viewHolder.tvCustomerPhone.setText(onLinePay.telephone);
        } else {
            viewHolder.tvCustomerPhone.setText("");
        }
        if (Utils.isNotEmpty(onLinePay.description)) {
            viewHolder.expandableTextView.setText(onLinePay.description);
        } else {
            viewHolder.expandableTextView.setText(ResourceUtils.getString(R.string.text_default));
        }
        if (Utils.isNotEmpty(onLinePay.techName)) {
            if (Utils.isNotEmpty(onLinePay.techNo)) {
                viewHolder.customerPayTechName.setText(String.format("%s[%s]", onLinePay.techName, onLinePay.techNo));
            } else {
                viewHolder.customerPayTechName.setText(onLinePay.techName);
            }
        } else {
            viewHolder.customerPayTechName.setText(ResourceUtils.getString(R.string.text_default));
        }
        if (Utils.isNotEmpty(onLinePay.otherTechNames)) {
            viewHolder.customerPayCombineTechName.setText(onLinePay.otherTechNames);
        } else {
            viewHolder.customerPayCombineTechName.setText(ResourceUtils.getString(R.string.text_default));
        }
        viewHolder.customerPayMoney.setText(String.format("%1.2f", onLinePay.payAmount / 100f));
        viewHolder.customerPayTime.setText(onLinePay.createTime);
        viewHolder.tvOnlinePayTime.setText(onLinePay.modifyTime);
        viewHolder.tvOnlinePayHandler.setText(onLinePay.operatorName);
        if (onLinePay.status.equals("paid")) {
            viewHolder.tvOnlinePayStatus.setText("待处理");
            viewHolder.tvOnlinePayStatus.setTextColor(Color.parseColor("#999999"));
        } else if (onLinePay.status.equals("pass")) {
            viewHolder.tvOnlinePayStatus.setText("已确认");
            viewHolder.tvOnlinePayStatus.setTextColor(Color.parseColor("#66cc66"));
        } else {
            viewHolder.tvOnlinePayStatus.setText("到前台");
            viewHolder.tvOnlinePayStatus.setTextColor(Color.parseColor("#ff6666"));
        }

    }

    private void bindMarketingIncomeViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        MarketingIncomeItemViewHolder viewHolder = (MarketingIncomeItemViewHolder) holder;
        MarketingIncomeBean bean = (MarketingIncomeBean) obj;
        if (bean.showItemCard) {
            viewHolder.llOnceCardIncomeView.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llOnceCardIncomeView.setVisibility(View.GONE);
        }

        if (bean.showPageCard) {
            viewHolder.tvMixedPackageIncome.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvMixedPackageIncome.setVisibility(View.GONE);
        }

        viewHolder.tvIncomeTime.setText(bean.dealDate);//changeSize
        String total = String.format("%1.2f", bean.totalAmount / 100f);
        Spannable ss = new SpannableString(total);
        ss.setSpan(new TextAppearanceSpan(mContext, R.style.text_float), total.length() - 2, total.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        viewHolder.tvIncomeTotalMoney.setText(ss);
        viewHolder.tvOnceCardIncome.setText(String.format("%1.2f", bean.itemCardAmount / 100f));
        viewHolder.tvGrabIncome.setText(String.format("%1.2f", bean.paidServiceItemAmount / 100f));
        viewHolder.tvPaidIncome.setText(String.format("%1.2f", bean.paidCouponAmount / 100f));
        viewHolder.tvPayForMeIncome.setText(String.format("%1.2f", bean.oneYuanAmount / 100f));
        viewHolder.tvMixedPackageIncome.setText(String.format("%1.2f", bean.packageCardAmount / 100f));
    }

    private void bindPkActivityListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final ActivityRankingBean activityBean = (ActivityRankingBean) obj;
        PKActivityRankingListItemViewHolder rankingViewHolder = (PKActivityRankingListItemViewHolder) holder;
        rankingViewHolder.pkActiveName.setText(activityBean.activityName);
        rankingViewHolder.pkActiveStatus.setText(activityBean.statusName);
        if (activityBean.status.equals("4")) {
            Glide.with(mContext).load(R.drawable.icon_underway).into(rankingViewHolder.imgPkActiveStatus);
            rankingViewHolder.pkActiveStatus.setTextColor(ResourceUtils.getColor(R.color.underway_color));
        } else {
            Glide.with(mContext).load(R.drawable.icon_completed).into(rankingViewHolder.imgPkActiveStatus);
            rankingViewHolder.pkActiveStatus.setTextColor(ResourceUtils.getColor(R.color.colorRemark));
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
            mCallback.onItemClicked(activityBean);
        });
        return;
    }

    private void bindTechPersonalRankingViewHolder(RecyclerView.ViewHolder holder, Object obj, int position) {
        final TechRankingBean techBean = (TechRankingBean) obj;
        TechPersonalRankingListItemViewHolder techRankingViewHolder = (TechPersonalRankingListItemViewHolder) holder;
        if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_USER)) {
            techRankingViewHolder.tvRankingMemberNumber.setText(String.format("%s 人", techBean.counts));
        } else if (techBean.type.equals(RequestConstant.KEY_TECH_SORT_BY_PAID)) {
            techRankingViewHolder.tvRankingMemberNumber.setText(String.format("%s 张", techBean.counts));
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

    private void bindStaffDataRankingViewHolder(RecyclerView.ViewHolder holder, Object obj, int position) {

        final StaffDataBean staffBean = (StaffDataBean) obj;
        StaffDataListItemViewHolder staffDataViewHolder = (StaffDataListItemViewHolder) holder;

        String techName = TextUtils.isEmpty(staffBean.name) ? "技师" : staffBean.name;
        staffDataViewHolder.tvStaffName.setText(Utils.StrSubstring(4, techName, true));
        Glide.with(mContext).load(staffBean.avatarUrl).error(R.drawable.icon22).into(staffDataViewHolder.imgStaffHead);
        if (Utils.isNotEmpty(staffBean.techNo)) {
            String techNO = String.format("[%s]", staffBean.techNo);
            staffDataViewHolder.tvStaffSerialNo.setText(Utils.changeColor(techNO, ResourceUtils.getColor(R.color.contact_marker), 1, techNO.length() - 1));
            staffDataViewHolder.tvStaffSerialNo.setVisibility(View.VISIBLE);
        } else {
            staffDataViewHolder.tvStaffSerialNo.setVisibility(View.GONE);
        }
        if (position == 0) {
            Glide.with(mContext).load(R.drawable.icon_nub_01).into(staffDataViewHolder.imgStaffNumber);
            staffDataViewHolder.imgStaffNumber.setVisibility(View.VISIBLE);
            staffDataViewHolder.textStaffNumber.setVisibility(View.GONE);
        } else if (position == 1) {
            Glide.with(mContext).load(R.drawable.icon_nub_02).into(staffDataViewHolder.imgStaffNumber);
            staffDataViewHolder.imgStaffNumber.setVisibility(View.VISIBLE);
            staffDataViewHolder.textStaffNumber.setVisibility(View.GONE);
        } else if (position == 2) {
            Glide.with(mContext).load(R.drawable.icon_nub_03).into(staffDataViewHolder.imgStaffNumber);
            staffDataViewHolder.imgStaffNumber.setVisibility(View.VISIBLE);
            staffDataViewHolder.textStaffNumber.setVisibility(View.GONE);
        } else {
            staffDataViewHolder.textStaffNumber.setText(String.valueOf(position + 1));
            staffDataViewHolder.imgStaffNumber.setVisibility(View.GONE);
            staffDataViewHolder.textStaffNumber.setVisibility(View.VISIBLE);
        }

        staffDataViewHolder.tvStaffOrder.setText(String.valueOf(staffBean.totalOrderCount));
        staffDataViewHolder.tvCompleteOrderCount.setText(String.valueOf(staffBean.completeOrderCount));
        if (position != 0 && position == (getItemCount() - 1)) {
            staffDataViewHolder.tvBottomText.setVisibility(View.VISIBLE);
        } else {
            staffDataViewHolder.tvBottomText.setVisibility(View.GONE);
        }
    }

    private void bindCouponOperateItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final CouponStatisticsBean operateBean = (CouponStatisticsBean) obj;
        CouponOperateListItemViewHolder viewHolder = (CouponOperateListItemViewHolder) holder;
        viewHolder.tvCouponOperateDate.setText(operateBean.reportDate);
        viewHolder.tvCouponOperateReceive.setText(operateBean.getTotal);
        viewHolder.tvCouponOperateOverTime.setText(operateBean.expireTotal);
        viewHolder.tvCouponOperateVerification.setText(operateBean.haveUseTotal);
        viewHolder.tvCouponOperateReceive.setOnClickListener(v -> mCallback.onPositiveButtonClicked(operateBean));
        viewHolder.tvCouponOperateVerification.setOnClickListener(v -> mCallback.onNegativeButtonClicked(operateBean));
    }

    private void bindOperateReportListViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final OperateReportBean operateBean = (OperateReportBean) obj;
        OperateReportListItemViewHolder viewHolder = (OperateReportListItemViewHolder) holder;
        viewHolder.imgOperateNewRemark.setVisibility(operateBean.read == 0 ? View.VISIBLE : View.GONE);
        viewHolder.tvReportName.setText(operateBean.name);
        viewHolder.tvReportDelete.setVisibility(View.GONE);
        if (operateBean.type.equals("custom")) {
            viewHolder.tvReportDelete.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvReportDelete.setVisibility(View.GONE);
        }
        viewHolder.tvReportShare.setOnClickListener(v -> mCallback.onPositiveButtonClicked(operateBean));
        viewHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(operateBean));
        viewHolder.tvReportDelete.setOnClickListener(v -> mCallback.onNegativeButtonClicked(operateBean));
    }

    private void bindCouponRecordListViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final CouponRecordBean recordBean = (CouponRecordBean) obj;
        CouponRecordListItemViewHolder viewHolder = (CouponRecordListItemViewHolder) holder;
        Glide.with(mContext).load(recordBean.userHeadImage).error(R.drawable.icon22).into(viewHolder.imgRecordUserHead);
        viewHolder.tvRecordUserName.setText(TextUtils.isEmpty(recordBean.userName) ? "匿名用户" : recordBean.userName);
        viewHolder.tvRecordUserPhone.setText(TextUtils.isEmpty(recordBean.userPhoneNum) ? "" : recordBean.userPhoneNum);
        viewHolder.tvRecordCouponStatus.setText(TextUtils.isEmpty(recordBean.statusName) ? "" : recordBean.statusName);
        if (!TextUtils.isEmpty(recordBean.statusName) && recordBean.statusName.equals("已过期")) {
            viewHolder.tvRecordCouponStatus.setEnabled(false);
        } else {
            viewHolder.tvRecordCouponStatus.setEnabled(true);
        }
        viewHolder.tvCouponName.setText(TextUtils.isEmpty(recordBean.title) ? "优惠券" : recordBean.title);
        viewHolder.tvCouponNo.setText(String.format("[ %s ]", recordBean.businessNo));
        viewHolder.tvCouponVerificationNum.setText(recordBean.couponNo);
        viewHolder.tvCouponGetTime.setText(TextUtils.isEmpty(recordBean.getTime) ? "-" : recordBean.getTime);
        viewHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(recordBean));
    }

    private void bindCouponListItemViewHolder(RecyclerView.ViewHolder holder, Object obj) {
        final CouponBean couponBean = (CouponBean) obj;
        CouponListItemViewHolder viewHolder = (CouponListItemViewHolder) holder;
        if (couponBean.couponType.equals(Constant.COUPON_PAID_TYPE)) {
            viewHolder.tvCouponTitle.setText(couponBean.couponTypeName);
            viewHolder.couponType.setText("");
        } else {
            viewHolder.tvCouponTitle.setText(Utils.StrSubstring(8, couponBean.actTitle, true));
            viewHolder.couponType.setText(String.format("（%s）", couponBean.couponTypeName));
        }
        if (couponBean.commissionAmount > 0) {
            viewHolder.tvCouponReward.setVisibility(View.VISIBLE);
            String text = String.format("提成 %1.1f元", couponBean.commissionAmount / 100f);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new TextAppearanceSpan(mContext, R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            viewHolder.tvCouponReward.setText(spannableString);
        } else {
            viewHolder.tvCouponReward.setVisibility(View.GONE);
        }

        if (couponBean.couponType.equals(Constant.COUPON_DISCOUNT_TYPE)) { //折扣券
            viewHolder.imgMoneyMark.setVisibility(View.GONE);
            viewHolder.couponEmptyView.setVisibility(View.VISIBLE);
            viewHolder.couponAmount.setText(String.format("%s 折", couponBean.amount));
            viewHolder.tvConsumeMoneyDescription.setText(TextUtils.isEmpty(couponBean.consumeMoneyDescription) ? "" : couponBean.consumeMoneyDescription);
        } else if (couponBean.couponType.equals(Constant.COUPON_GIFT_TYPE)) {//礼品券
            viewHolder.couponEmptyView.setVisibility(View.VISIBLE);
            viewHolder.imgMoneyMark.setVisibility(View.GONE);
            viewHolder.couponAmount.setText(TextUtils.isEmpty(couponBean.actSubTitle) ? couponBean.actId : couponBean.actSubTitle);
            viewHolder.tvConsumeMoneyDescription.setText("");
        } else {
            viewHolder.couponEmptyView.setVisibility(View.GONE);
            viewHolder.imgMoneyMark.setVisibility(View.VISIBLE);
            viewHolder.couponAmount.setText(couponBean.amount);
            viewHolder.tvConsumeMoneyDescription.setText(TextUtils.isEmpty(couponBean.consumeMoneyDescription) ? "" : couponBean.consumeMoneyDescription);
        }

        viewHolder.tvCouponPeriod.setText(Utils.StrSubstring(19, couponBean.couponPeriod, true));
        viewHolder.btnShareCoupon.setVisibility(couponBean.online.equals(Constant.COUPON_ONLINE_TRUE) && !couponBean.couponType.equals(Constant.COUPON_PAID_TYPE) ? View.VISIBLE : View.GONE);
        viewHolder.itemView.setOnClickListener(v -> mCallback.onItemClicked(couponBean));
        viewHolder.btnShareCoupon.setOnClickListener(v -> mCallback.onPositiveButtonClicked(couponBean));
    }


    @Override
    public int getItemCount() {
        if (mCallback.isPaged()) {
            return mData.size() + 1;
        } else if (!mIsEmpty) {
            return mData.size();
        }
        return 0;
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

        @BindView(R.id.avatar)
        CircleImageView avatar;
        @BindView(R.id.custom_name)
        TextView customerName;
        @BindView(R.id.custom_phone)
        TextView customerPhone;
        @BindView(R.id.book_time)
        TextView bookTime;
        @BindView(R.id.tech_name)
        TextView techName;
        @BindView(R.id.tech_serial)
        TextView techSerial;
        @BindView(R.id.order_state)
        TextView orderState;
        @BindView(R.id.item_name)
        TextView itemName;
        @BindView(R.id.item_time)
        TextView itemTime;
        @BindView(R.id.paid_mark)
        TextView paidMark;
        @BindView(R.id.down_payment)
        TextView downPayment;
        @BindView(R.id.operation)
        FrameLayout operation;
        @BindView(R.id.operation_for_submit)
        RelativeLayout operationForSubmit;
        @BindView(R.id.operation_for_accept)
        RelativeLayout operationForAccept;
        @BindView(R.id.operation_for_paid_order_accept)
        RelativeLayout operationForPaidOrderAccept;
        @BindView(R.id.reject)
        Button reject;
        @BindView(R.id.accept)
        Button accept;
        @BindView(R.id.failure)
        Button failure;
        @BindView(R.id.complete)
        Button complete;
        @BindView(R.id.verified)
        Button verified;
        @BindView(R.id.expire)
        Button expire;


        public OrderListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CustomerListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_customer_name)
        TextView mTvCustomerName;
        @BindView(R.id.tv_bad_comments_count)
        TextView mTvBadCommentsCount;
        @BindView(R.id.tv_telephone)
        TextView mTvTelephone;
        @BindView(R.id.tv_customer_type)
        TextView mTvCustomerType;

        public CustomerListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class CustomerHeaderListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_header)
        TextView mTvHeader;
        @BindView(R.id.tv_group_count)
        TextView mTvGroupCount;

        public CustomerHeaderListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ConversationListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.avatar)
        CircleImageView mAvatar;
        @BindView(R.id.name)
        TextView mName;
        @BindView(R.id.content)
        TextView mContent;
        @BindView(R.id.time)
        TextView mTime;
        @BindView(R.id.unread)
        TextView mUnread;

        public boolean isOperationVisible;

        public ConversationListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    static class CouponItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.coupon_name)
        TextView mCouponName;
        @BindView(R.id.coupon_type)
        TextView mCouponType;
        @BindView(R.id.coupon_desc)
        TextView mCouponDesc;
        @BindView(R.id.activity_duration)
        TextView mActivityDuration;
        @BindView(R.id.coupon_duration)
        TextView mCouponDuration;
        @BindView(R.id.coupon_use_duration)
        TextView mCouponUseDuration;
        @BindView(R.id.coupon_status)
        TextView mCouponStatus;
        @BindView(R.id.activity_limit)
        TextView mActivityLimit;

        CouponItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    static class ClubListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.club_name)
        TextView mClubName;
        @BindView(R.id.club_avatar)
        CircleImageView mClubAvatar;

        public ClubListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class VisitListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name)
        TextView mVisitName;
        @BindView(R.id.user_phone)
        TextView mVisitPhone;
        @BindView(R.id.user_time)
        TextView mVisitTime;
        @BindView(R.id.user_from)
        TextView mVisitFrom;

        public VisitListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class RegisterListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_name)
        TextView mRegisterName;
        @BindView(R.id.user_phone)
        TextView mRegisterPhone;
        @BindView(R.id.user_time)
        TextView mRegisterTime;
        @BindView(R.id.user_tech)
        TextView mRegisterTech;
        @BindView(R.id.user_tech_no)
        TextView mRegisterTechNo;
        @BindView(R.id.user_head)
        CircleImageView mRegisterHead;
        @BindView(R.id.layout_user_tech)
        LinearLayout mLayoutUserTech;

        public RegisterListItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class GroupMessageItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.group_message_customer_type)
        TextView typeTitleSummary;
        @BindView(R.id.group_detail_customer_type)
        TextView typeTitleDetail;
        @BindView(R.id.group_message_customer_count)
        TextView customerCountSummary;
        @BindView(R.id.group_detail_type_detail)
        TextView customerTypeDetail;

        @BindView(R.id.group_message_activity_container)
        LinearLayout activitySummaryContainer;
        @BindView(R.id.detail_activity_container)
        LinearLayout activityDetailContainer;
        @BindView(R.id.group_message_activity_title)
        TextView activityTitleSummary;
        @BindView(R.id.group_detail_activity_title)
        TextView activityTitleDetail;
        @BindView(R.id.group_message_activity_info)
        TextView activityInfoSummary;
        @BindView(R.id.group_detail_activity_info)
        TextView activityInfoDetail;
        @BindView(R.id.activity_detail_tip)
        TextView activityDetailTip;

        @BindView(R.id.group_message_content)
        TextView contentTitleSummary;
        @BindView(R.id.group_detail_message_content)
        TextView contentInfoSummary;
        @BindView(R.id.group_message_content_detail)
        TextView contentInfoDetail;
        @BindView(R.id.group_detail_message_image)
        ImageView contentImage;

        @BindView(R.id.group_send_time)
        TextView sendTimeSummary;
        @BindView(R.id.group_detail_send_time)
        TextView sendTimeDetail;

        @BindView(R.id.group_send_operator)
        TextView sendOperatorSummary;
        @BindView(R.id.group_detail_send_operator)
        TextView sendOperatorDetail;

        @BindView(R.id.group_message_summary)
        TextView putWayButton;
        @BindView(R.id.group_message_detail)
        TextView expandButton;

        @BindView(R.id.message_summary)
        LinearLayout messageSummaryContainer;
        @BindView(R.id.message_detail)
        LinearLayout messageDetailContainer;

        @BindView(R.id.effect_analysis_hint)
        TextView statHintText;
        @BindView(R.id.effect_analysis_container)
        LinearLayout statViewContainer;
        @BindView(R.id.send_count_text)
        TextView sendCount;
        @BindView(R.id.view_count_text)
        TextView viewCount;
        @BindView(R.id.join_count_text)
        TextView joinCount;
        @BindView(R.id.shop_count_text)
        TextView shopCount;

        @BindView(R.id.message_content_tip)
        TextView msgContentTip;
        @BindView(R.id.message_content_detail_container)
        LinearLayout msgDetailContainer;
        @BindView(R.id.group_message_content_container)
        LinearLayout msgSummaryContainer;

        public GroupMessageItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class BadCommentItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_customer_head)
        CircleImageView commentCustomerHead;
        @BindView(R.id.customer_name)
        TextView customerName;
        @BindView(R.id.customer_phone)
        TextView customerPhone;
        @BindView(R.id.comment_type)
        TextView commentType;
        @BindView(R.id.comment_tech_name)
        TextView commentTechName;
        @BindView(R.id.comment_tech_code)
        TextView commentTechCode;
        @BindView(R.id.comment_tech)
        LinearLayout commentTech;
        @BindView(R.id.comment_project_list)
        RecyclerView commentProjectList;
        @BindView(R.id.comment_detail)
        TextView commentDetail;
        @BindView(R.id.comment_time)
        TextView commentTime;
        @BindView(R.id.img_btn_delete)
        ImageButton imgBtnDelete;
        @BindView(R.id.img_delete_mark)
        ImageView imgDeleteMark;
        @BindView(R.id.comment_return_visit)
        Button commentReturnVisit;
        @BindView(R.id.bad_comment)
        LinearLayout badComment;
        @BindView(R.id.ll_comment)
        LinearLayout llComment;

        public BadCommentItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class TechBadCommentItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bad_comment_tech_rank)
        TextView badCommentTechRank;
        @BindView(R.id.bad_comment_tech_head)
        CircleImageView badCommentTechHead;
        @BindView(R.id.bad_comment_tech_name)
        TextView badCommentTechName;
        @BindView(R.id.bad_comment_tech_serial)
        TextView badCommentTechSerial;
        @BindView(R.id.bad_comment_total)
        TextView badCommentTotal;
        @BindView(R.id.bad_comment_ratio)
        TextView badCommentRatio;

        public TechBadCommentItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ClubGroupItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.group_name)
        TextView groupName;
        @BindView(R.id.group_member_amount)
        TextView groupMemberAmount;
        @BindView(R.id.group_member)
        TextView groupMember;

        public ClubGroupItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class UserCommentListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tech_avatar)
        CircleImageView techAvatar;
        @BindView(R.id.tv_tech_name)
        TextView tvTechName;
        @BindView(R.id.tv_tech_no)
        TextView tvTechNo;
        @BindView(R.id.tv_score)
        TextView tvScore;
        @BindView(R.id.tv_reward)
        TextView tvReward;
        @BindView(R.id.comment_rate_list)
        RecyclerView commentRateList;
        @BindView(R.id.impression_container)
        GridView impressionContainer;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.tv_time)
        TextView tvTime;
        @BindView(R.id.btn_delete)
        ImageView btnDelete;
        @BindView(R.id.impression_line)
        View impressionLine;

        UserCommentListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OnlinePayListItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.customer_head)
        CircleImageView customerHead;
        @BindView(R.id.tv_customer_name)
        TextView tvCustomerName;
        @BindView(R.id.tv_customer_phone)
        TextView tvCustomerPhone;
        @BindView(R.id.tv_online_pay_status)
        TextView tvOnlinePayStatus;
        @BindView(R.id.customer_pay_money)
        TextView customerPayMoney;
        @BindView(R.id.customer_pay_time)
        TextView customerPayTime;
        @BindView(R.id.customer_pay_tech_name)
        TextView customerPayTechName;
        @BindView(R.id.expand_text_view)
        ExpandableTextView expandableTextView;
        @BindView(R.id.tv_online_pay_time)
        TextView tvOnlinePayTime;
        @BindView(R.id.tv_online_pay_handler)
        TextView tvOnlinePayHandler;
        @BindView(R.id.customer_pay_combine_tech_name)
        TextView customerPayCombineTechName;

        OnlinePayListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class MarketingIncomeItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_income_time)
        TextView tvIncomeTime;
        @BindView(R.id.tv_income_total_money)
        TextView tvIncomeTotalMoney;
        @BindView(R.id.tv_once_card_income)
        TextView tvOnceCardIncome;
        @BindView(R.id.ll_once_card_income_view)
        LinearLayout llOnceCardIncomeView;
        @BindView(R.id.tv_grab_income)
        TextView tvGrabIncome;
        @BindView(R.id.tv_pay_for_me_income)
        TextView tvPayForMeIncome;
        @BindView(R.id.tv_paid_income)
        TextView tvPaidIncome;
        @BindView(R.id.tv_mixed_package_income)
        TextView tvMixedPackageIncome;

        MarketingIncomeItemViewHolder(View view) {
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
        CircleImageView imgTechHead;
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

    static class StaffDataListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_staff_number)
        ImageView imgStaffNumber;
        @BindView(R.id.text_staff_number)
        TextView textStaffNumber;
        @BindView(R.id.img_staff_head)
        CircleImageView imgStaffHead;
        @BindView(R.id.tv_staff_name)
        TextView tvStaffName;
        @BindView(R.id.tv_staff_serialNo)
        TextView tvStaffSerialNo;
        @BindView(R.id.tv_staff_order)
        TextView tvStaffOrder;
        @BindView(R.id.tv_staff_commit_number)
        TextView tvCompleteOrderCount;
        @BindView(R.id.tv_bottom_text)
        TextView tvBottomText;

        StaffDataListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class CouponOperateListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_coupon_operate_date)
        TextView tvCouponOperateDate;
        @BindView(R.id.tv_coupon_operate_receive)
        TextView tvCouponOperateReceive;
        @BindView(R.id.tv_coupon_operate_verification)
        TextView tvCouponOperateVerification;
        @BindView(R.id.tv_coupon_operate_over_time)
        TextView tvCouponOperateOverTime;

        CouponOperateListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class OperateReportListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_report_name)
        TextView tvReportName;
        @BindView(R.id.tv_report_share)
        TextView tvReportShare;
        @BindView(R.id.tv_report_delete)
        TextView tvReportDelete;
        @BindView(R.id.img_operate_new_remark)
        ImageView imgOperateNewRemark;

        OperateReportListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class CouponRecordListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_record_user_head)
        CircleImageView imgRecordUserHead;
        @BindView(R.id.tv_record_user_name)
        TextView tvRecordUserName;
        @BindView(R.id.tv_record_user_phone)
        TextView tvRecordUserPhone;
        @BindView(R.id.tv_record_coupon_status)
        TextView tvRecordCouponStatus;
        @BindView(R.id.tv_coupon_name)
        TextView tvCouponName;
        @BindView(R.id.tv_coupon_no)
        TextView tvCouponNo;
        @BindView(R.id.tv_coupon_verification_num)
        TextView tvCouponVerificationNum;
        @BindView(R.id.tv_coupon_get_time)
        TextView tvCouponGetTime;

        CouponRecordListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class CouponListItemViewHolder extends RecyclerView.ViewHolder {
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
        @BindView(R.id.btn_share_coupon)
        TextView btnShareCoupon;

        CouponListItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}