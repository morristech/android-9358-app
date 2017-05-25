package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
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
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.OrderRecordInfo;
import com.xmd.cashier.manager.AccountManager;
import com.xmd.cashier.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-11.
 * 预约订单列表
 */

public class OrderRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ONLINE_PAY_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<OrderRecordInfo> mData = new ArrayList<>();
    private Context mContext;
    private OrderRecordCallBack mCallBack;

    private int mFooterStatus;

    public interface OrderRecordCallBack {
        void onLoadMore();

        void onPrint(OrderRecordInfo info);

        void onAccept(OrderRecordInfo info, int position);

        void onReject(OrderRecordInfo info, int position);
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public OrderRecordAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        this.mData.clear();
    }

    public void setData(List<OrderRecordInfo> data) {
        this.mData.addAll(data);
    }

    public void setCallBack(OrderRecordCallBack callBack) {
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
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_record, parent, false));
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
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            final OrderRecordInfo info = mData.get(position);
            switch (info.status) {
                case AppConstants.ORDER_RECORD_STATUS_SUBMIT:
                    //待接受
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_SUBMIT_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusYellow));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_wait);
                    itemViewHolder.mOperateLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mPrintBtn.setVisibility(View.GONE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_ACCEPT:
                    //已接受
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_ACCEPT_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGreen));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_accept);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_COMPLETE:
                    //已到店
                    if (info.downPayment > 0) {
                        itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_COMPLETE_TEXT);
                    } else {
                        itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_DONE_TEXT);
                    }
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGreen));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_accept);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_REJECT:
                    //已拒绝
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_REJECT_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGray));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_refuse);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_CANCEL:
                    //已取消
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_CANCEL_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGray));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_refuse);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_FAILURE:
                    //已爽约
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_FAILURE_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGray));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_refuse);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                case AppConstants.ORDER_RECORD_STATUS_OVERTIME:
                    //已超时
                    itemViewHolder.mStatusText.setText(AppConstants.ORDER_RECORD_STATUS_OVERTIME_TEXT);
                    itemViewHolder.mStatusText.setTextColor(mContext.getResources().getColor(R.color.colorStatusGray));
                    itemViewHolder.mStatusIcon.setImageResource(R.drawable.ic_refuse);
                    itemViewHolder.mOperateLayout.setVisibility(View.GONE);
                    itemViewHolder.mPrintBtn.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }

            Glide.with(mContext).load(info.headImgUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(itemViewHolder.mCustomerAvatar);
            itemViewHolder.mCustomerName.setText(info.customerName);
            itemViewHolder.mCustomerPhone.setText(info.phoneNum);

            itemViewHolder.mTechName.setText(info.techName);
            if (TextUtils.isEmpty(info.techSerialNo)) {
                itemViewHolder.mTechNo.setVisibility(View.GONE);
            } else {
                itemViewHolder.mTechNo.setVisibility(View.VISIBLE);
                itemViewHolder.mTechNo.setText(info.techSerialNo);
            }
            itemViewHolder.mServiceName.setText(TextUtils.isEmpty(info.itemName) ? "到店选择" : info.itemName);
            itemViewHolder.mArriveTime.setText(info.appointTime);
            itemViewHolder.mAlreadyPay.setText(info.downPayment + "元");

            if (TextUtils.isEmpty(info.receiverName)) {
                itemViewHolder.mReceiveRow.setVisibility(View.GONE);
            } else {
                itemViewHolder.mReceiveRow.setVisibility(View.VISIBLE);
                itemViewHolder.mReceiveName.setText(info.receiverName);
            }

            itemViewHolder.mOrderTime.setText(info.createdAt);
            itemViewHolder.mPrintBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onPrint(info);
                }
            });

            itemViewHolder.mAcceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onAccept(info, position);
                }
            });

            itemViewHolder.mRejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onReject(info, position);
                }
            });
        }
    }

    public void updateItemStatus(String status, int position) {
        // 更新状态
        mData.get(position).status = status;
        if (AppConstants.ORDER_RECORD_STATUS_ACCEPT.equals(status)) {
            // 如果接受订单成功,暂时更新接单人信息为当前收银员
            mData.get(position).receiverName = AccountManager.getInstance().getUser().userName;
        }
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
        public TextView mStatusText;
        public ImageView mStatusIcon;

        public CircleImageView mCustomerAvatar;
        public TextView mCustomerName;
        public TextView mCustomerPhone;

        public TextView mTechName;
        public TextView mTechNo;

        public TextView mServiceName;
        public TextView mArriveTime;
        public TextView mAlreadyPay;

        public TextView mOrderTime;
        public Button mPrintBtn;
        public LinearLayout mOperateLayout;
        public Button mAcceptBtn;
        public Button mRejectBtn;

        public TableRow mReceiveRow;
        public TextView mReceiveName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mStatusText = (TextView) itemView.findViewById(R.id.item_status_desc);
            mStatusIcon = (ImageView) itemView.findViewById(R.id.item_status_icon);

            mCustomerAvatar = (CircleImageView) itemView.findViewById(R.id.item_customer_avatar);
            mCustomerName = (TextView) itemView.findViewById(R.id.item_customer_name);
            mCustomerPhone = (TextView) itemView.findViewById(R.id.item_customer_phone);

            mTechName = (TextView) itemView.findViewById(R.id.item_tech_name);
            mTechNo = (TextView) itemView.findViewById(R.id.item_tech_no);

            mServiceName = (TextView) itemView.findViewById(R.id.item_service_name);
            mArriveTime = (TextView) itemView.findViewById(R.id.item_arrive_time);
            mAlreadyPay = (TextView) itemView.findViewById(R.id.item_already_pay);

            mOrderTime = (TextView) itemView.findViewById(R.id.item_order_time);
            mPrintBtn = (Button) itemView.findViewById(R.id.item_btn_print);
            mOperateLayout = (LinearLayout) itemView.findViewById(R.id.item_layout_operate);
            mAcceptBtn = (Button) itemView.findViewById(R.id.item_btn_accept);
            mRejectBtn = (Button) itemView.findViewById(R.id.item_btn_reject);

            mReceiveRow = (TableRow) itemView.findViewById(R.id.row_receive_personal);
            mReceiveName = (TextView) itemView.findViewById(R.id.item_receive_personal);
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
