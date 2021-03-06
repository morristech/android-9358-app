package com.xmd.cashier.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.PayRecordInfo;
import com.xmd.cashier.dal.bean.TradeDiscountInfo;
import com.xmd.cashier.dal.bean.TradeRecordInfo;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-11.
 * 在线买单列表
 */

public class OnlinePayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONLINE_PAY_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<TradeRecordInfo> mData = new ArrayList<>();
    private Context mContext;
    private OnlinePayCallBack mCallBack;

    private int mFooterStatus;

    public interface OnlinePayCallBack {
        void onLoadMore();

        void onPrintClient(TradeRecordInfo info);

        void onPrintClub(TradeRecordInfo info);

        void onConfirm(TradeRecordInfo info, int position);

        void onException(TradeRecordInfo info, int position);

        // 查看券详情
        void onDetail(TradeDiscountInfo info);

        // 查看拆分支付详情
        void onPayDetail(List<PayRecordInfo> payRecordInfos);
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public OnlinePayAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        this.mData.clear();
    }

    public void setData(List<TradeRecordInfo> data) {
        this.mData.addAll(data);
    }

    public void setCallBack(OnlinePayCallBack callBack) {
        this.mCallBack = callBack;
    }

    public void setStatus(@Flavour int status) {
        this.mFooterStatus = status;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ONLINE_PAY_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ONLINE_PAY_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_online_pay, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_footer, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            footerHolder.mMoreMsg.setText(getDescStr(mFooterStatus));
            switch (mFooterStatus) {
                case AppConstants.FOOTER_STATUS_SUCCESS:
                case AppConstants.FOOTER_STATUS_ERROR:
                case AppConstants.FOOTER_STATUS_NO_NETWORK:
                    footerHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallBack.onLoadMore();
                        }
                    });
                    footerHolder.mMoreProgress.setVisibility(View.GONE);
                    break;
                case AppConstants.FOOTER_STATUS_LOADING:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreProgress.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.FOOTER_STATUS_NONE:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreProgress.setVisibility(View.GONE);
                    break;
                default:
                    footerHolder.itemView.setOnClickListener(null);
                    footerHolder.mMoreLayout.setVisibility(View.INVISIBLE);
                    break;
            }
        } else {
            final TradeRecordInfo info = mData.get(position);
            final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            switch (info.status) {
                case AppConstants.ONLINE_PAY_STATUS_PAID:
                    // 已支付,待确认
                    itemViewHolder.mOperateLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mPrintLayout.setVisibility(View.GONE);
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_wait);
                    itemViewHolder.mStatusText.setText(AppConstants.ONLINE_PAY_STATUS_PAID_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusYellow));
                    break;
                case AppConstants.ONLINE_PAY_STATUS_PASS:
                    // 确认
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_accept);
                    itemViewHolder.mStatusText.setText(AppConstants.ONLINE_PAY_STATUS_PASS_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGreen));
                    break;
                case AppConstants.ONLINE_PAY_STATUS_UNPASS:
                    // 请到前台
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_refuse);
                    itemViewHolder.mStatusText.setText(AppConstants.ONLINE_PAY_STATUS_UNPASS_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGray));
                    break;
                default:
                    break;
            }

            Glide.with(mContext).load(info.userAvatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(itemViewHolder.mCustomerAvatar);
            itemViewHolder.mCustomerName.setText(info.userName);
            itemViewHolder.mCustomerPhone.setText(info.telephone);

            if (TextUtils.isEmpty(info.otherTechNames)) {
                itemViewHolder.mAddTechRow.setVisibility(View.GONE);
            } else {
                itemViewHolder.mAddTechRow.setVisibility(View.VISIBLE);
                itemViewHolder.mAddTechName.setText(info.otherTechNames);
            }

            if (info.payRecordList != null && !info.payRecordList.isEmpty()) {
                itemViewHolder.mPayChannelRow.setVisibility(View.VISIBLE);
                if (info.payRecordList.size() > 1) {
                    itemViewHolder.mPayChannel.setText("拆分支付");
                    itemViewHolder.mPayChannel.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); //下划线
                    itemViewHolder.mPayChannel.setTextColor(ResourceUtils.getColor(R.color.colorAccent));
                    itemViewHolder.mPayChannel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mCallBack.onPayDetail(info.payRecordList);
                        }
                    });
                } else {
                    PayRecordInfo payRecordInfo = info.payRecordList.get(0);
                    itemViewHolder.mPayChannel.setText(payRecordInfo.payChannelName);
                    itemViewHolder.mPayChannel.getPaint().setFlags(0);
                    itemViewHolder.mPayChannel.setTextColor(ResourceUtils.getColor(R.color.colorText));
                    itemViewHolder.mPayChannel.setOnClickListener(null);
                }
                itemViewHolder.mPayChannel.getPaint().setAntiAlias(true);
            } else {
                itemViewHolder.mPayChannelRow.setVisibility(View.GONE);
            }

            if (TextUtils.isEmpty(info.techName)) {
                itemViewHolder.mTechInfoRow.setVisibility(View.GONE);
            } else {
                itemViewHolder.mTechInfoRow.setVisibility(View.VISIBLE);
                itemViewHolder.mTechName.setText(info.techName);
                if (TextUtils.isEmpty(info.techNo)) {
                    itemViewHolder.mTechNo.setVisibility(View.INVISIBLE);
                } else {
                    itemViewHolder.mTechNo.setVisibility(View.VISIBLE);
                    itemViewHolder.mTechNo.setText(String.format("[%s]", info.techNo));
                }
            }

            itemViewHolder.mCashierTime.setText(info.createTime);
            itemViewHolder.mCashierNameRow.setVisibility(View.GONE);
            itemViewHolder.mCashierMoney.setText(Utils.moneyToStringEx(info.payAmount) + "元");
            itemViewHolder.mTradeNo.setText(info.payId);

            itemViewHolder.mPrintClient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onPrintClient(info);
                }
            });

            itemViewHolder.mPrintClub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onPrintClub(info);
                }
            });

            itemViewHolder.mConfirmBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onConfirm(info, position);
                }
            });

            itemViewHolder.mExceptionBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onException(info, position);
                }
            });

            if (info.isDetail) {
                itemViewHolder.mDiscountLayout.setVisibility(View.VISIBLE);
            } else {
                itemViewHolder.mDiscountLayout.setVisibility(View.GONE);
            }

            itemViewHolder.mCashierMoneySub.setText("￥" + Utils.moneyToStringEx(info.payAmount));
            itemViewHolder.mOriginMoney.setText("￥" + Utils.moneyToStringEx(info.originalAmount));
            itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int visible = itemViewHolder.mDiscountLayout.getVisibility();
                    if (visible == View.GONE) {
                        itemViewHolder.mDiscountLayout.setVisibility(View.VISIBLE);
                        info.isDetail = true;
                    } else {
                        itemViewHolder.mDiscountLayout.setVisibility(View.GONE);
                        info.isDetail = false;
                    }
                }
            });
            OnlinePayDiscountAdapter discountAdapter = new OnlinePayDiscountAdapter();
            discountAdapter.setCallBack(new OnlinePayDiscountAdapter.CallBack() {
                @Override
                public void onItemClick(TradeDiscountInfo info) {
                    mCallBack.onDetail(info);
                }
            });
            discountAdapter.setData(info.orderDiscountList);
            itemViewHolder.mDiscountItemList.setLayoutManager(new LinearLayoutManager(mContext));
            itemViewHolder.mDiscountItemList.setAdapter(discountAdapter);
        }
    }

    public void updateItemStatus(String status, int position) {
        // 更新状态和收银员
        mData.get(position).status = status;
        mData.get(position).operatorName = AccountManager.getInstance().getUser().loginName + "(" + AccountManager.getInstance().getUser().userName + ")";
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        if (!mData.isEmpty()) {
            return mData.size() + 1;
        }
        return 0;
    }

    private String getDescStr(int status) {
        String desc = AppConstants.strSuccess;
        switch (status) {
            case AppConstants.FOOTER_STATUS_ERROR:
                desc = AppConstants.strError;
                break;
            case AppConstants.FOOTER_STATUS_NO_NETWORK:
                desc = AppConstants.strNoNetwork;
                break;
            case AppConstants.FOOTER_STATUS_NONE:
                desc = AppConstants.strNone;
                break;
            case AppConstants.FOOTER_STATUS_LOADING:
                desc = AppConstants.strLoading;
                break;
            case AppConstants.FOOTER_STATUS_SUCCESS:
            default:
                break;
        }
        return desc;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView mStatusIcon;
        public TextView mStatusText;

        public CircleImageView mCustomerAvatar;
        public TextView mCustomerName;
        public TextView mCustomerPhone;

        public TableRow mAddTechRow;
        public TextView mAddTechName;
        public TableRow mTechInfoRow;
        public TextView mTechName;
        public TextView mTechNo;

        public TableRow mCashierNameRow;
        public TextView mCashierName;
        public TextView mCashierNo;
        public TextView mCashierMoney;
        public TextView mTradeNo;
        public TextView mCashierTime;
        public TableRow mPayChannelRow;
        public TextView mPayChannel;
        public TableRow mCashierMoneyLayout;

        public LinearLayout mPrintLayout;
        public Button mPrintClient;
        public Button mPrintClub;
        public LinearLayout mOperateLayout;
        public Button mConfirmBtn;
        public Button mExceptionBtn;

        public LinearLayout mDiscountLayout;
        public TextView mOriginMoney;
        public RecyclerView mDiscountItemList;
        public TextView mCashierMoneySub;

        public TextView mDetailTextDesc;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mStatusIcon = (ImageView) itemView.findViewById(R.id.item_status_icon);
            mStatusText = (TextView) itemView.findViewById(R.id.item_status_desc);
            mCustomerAvatar = (CircleImageView) itemView.findViewById(R.id.item_customer_avatar);
            mCustomerName = (TextView) itemView.findViewById(R.id.item_customer_name);
            mCustomerPhone = (TextView) itemView.findViewById(R.id.item_customer_phone);
            mAddTechRow = (TableRow) itemView.findViewById(R.id.tr_other_tech);
            mAddTechName = (TextView) itemView.findViewById(R.id.item_add_tech);
            mTechInfoRow = (TableRow) itemView.findViewById(R.id.tr_tech_info);
            mTechName = (TextView) itemView.findViewById(R.id.item_tech_name);
            mTechNo = (TextView) itemView.findViewById(R.id.item_tech_no);
            mCashierTime = (TextView) itemView.findViewById(R.id.item_cashier_time);
            mCashierNameRow = (TableRow) itemView.findViewById(R.id.tr_cashier_name);
            mCashierName = (TextView) itemView.findViewById(R.id.item_cashier_name);
            mCashierNo = (TextView) itemView.findViewById(R.id.item_cashier_no);
            mCashierMoney = (TextView) itemView.findViewById(R.id.item_cashier_money);
            mTradeNo = (TextView) itemView.findViewById(R.id.item_trade_no);
            mPrintLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_print);
            mPrintClient = (Button) itemView.findViewById(R.id.item_print_client);
            mPrintClub = (Button) itemView.findViewById(R.id.item_print_club);
            mOperateLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_operate);
            mConfirmBtn = (Button) itemView.findViewById(R.id.item_btn_confirm);
            mExceptionBtn = (Button) itemView.findViewById(R.id.item_btn_exception);
            mPayChannel = (TextView) itemView.findViewById(R.id.item_pay_channel);
            mPayChannelRow = (TableRow) itemView.findViewById(R.id.tr_channel_layout);

            mDiscountLayout = (LinearLayout) itemView.findViewById(R.id.item_discount_layout);
            mOriginMoney = (TextView) itemView.findViewById(R.id.item_origin_money);
            mDiscountItemList = (RecyclerView) itemView.findViewById(R.id.item_discount_list);

            mCashierMoneyLayout = (TableRow) itemView.findViewById(R.id.tr_cashier_layout);
            mCashierMoneySub = (TextView) itemView.findViewById(R.id.item_cashier_money_sub);

            mDetailTextDesc = (TextView) itemView.findViewById(R.id.item_tv_detail);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mMoreLayout;
        public ProgressBar mMoreProgress;
        public TextView mMoreMsg;

        public FooterViewHolder(View itemView) {
            super(itemView);
            mMoreLayout = (LinearLayout) itemView.findViewById(R.id.ll_footer_layout);
            mMoreProgress = (ProgressBar) itemView.findViewById(R.id.pb_more_data);
            mMoreMsg = (TextView) itemView.findViewById(R.id.tv_more_data);
        }
    }
}
