package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmd.app.utils.ResourceUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.TradeRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-11-2.
 * 内网买单
 */

public class InnerRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_INNER_RECORD_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<TradeRecordInfo> mData = new ArrayList<>();
    private Context mContext;
    private InnerRecordCallBack mCallBack;

    private int mFooterStatus;

    public interface InnerRecordCallBack {
        void onLoadMore();

        void onItemDetail(TradeRecordInfo info);

        void onItemPrintClient(TradeRecordInfo info);

        void onItemPrintClub(TradeRecordInfo info);

        void onItemPay(TradeRecordInfo info);
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public InnerRecordAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        this.mData.clear();
    }

    public void setData(List<TradeRecordInfo> data) {
        this.mData.addAll(data);
    }

    public void setCallBack(InnerRecordCallBack callBack) {
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
            return TYPE_INNER_RECORD_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_INNER_RECORD_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner_record, parent, false));
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
            final TradeRecordInfo info = mData.get(position);
            itemViewHolder.mTimeText.setText(info.createTime);
            itemViewHolder.mAmountText.setText("￥" + Utils.moneyToStringEx(info.originalAmount));
            switch (info.status) {
                case AppConstants.INNER_BATCH_STATUS_PAID:
                    itemViewHolder.mStatusText.setText("已支付");
                    itemViewHolder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                    itemViewHolder.mPayBtn.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mPaidAmountText.setVisibility(View.GONE);
                    break;
                case AppConstants.INNER_BATCH_STATUS_PASS:
                    itemViewHolder.mStatusText.setText("已确认");
                    itemViewHolder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                    itemViewHolder.mPayBtn.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.VISIBLE);
                    itemViewHolder.mPaidAmountText.setVisibility(View.GONE);
                    break;
                case AppConstants.INNER_BATCH_STATUS_UNPASS:
                    itemViewHolder.mStatusText.setText("已支付未确认");
                    itemViewHolder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                    itemViewHolder.mPayBtn.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.GONE);
                    itemViewHolder.mPaidAmountText.setVisibility(View.GONE);
                    break;
                case AppConstants.INNER_BATCH_STATUS_UNPAID:
                    itemViewHolder.mStatusText.setText("待付款");
                    itemViewHolder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorBlueDark));
                    itemViewHolder.mPayBtn.setVisibility(View.VISIBLE);
                    itemViewHolder.mPrintLayout.setVisibility(View.GONE);
                    itemViewHolder.mPaidAmountText.setVisibility(View.VISIBLE);
                    String temp = "(已付￥" + Utils.moneyToStringEx(info.paidAmount) + ")";
                    itemViewHolder.mPaidAmountText.setText(Utils.changeColor(temp, ResourceUtils.getColor(R.color.colorAccent), 3, temp.length() - 1));
                    break;
                default:
                    itemViewHolder.mStatusText.setText("状态未知");
                    itemViewHolder.mStatusText.setTextColor(ResourceUtils.getColor(R.color.colorText2));
                    itemViewHolder.mPayBtn.setVisibility(View.GONE);
                    itemViewHolder.mPrintLayout.setVisibility(View.GONE);
                    itemViewHolder.mPaidAmountText.setVisibility(View.GONE);
                    break;
            }

            if (info.details != null && !info.details.isEmpty()) {
                InnerRecordDetailAdapter detailAdapter = new InnerRecordDetailAdapter(mContext, false);
                detailAdapter.setData(info.details);
                itemViewHolder.mOrderList.setLayoutManager(new LinearLayoutManager(mContext));
                itemViewHolder.mOrderList.setAdapter(detailAdapter);
            }

            itemViewHolder.mDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemDetail(info);
                }
            });
            itemViewHolder.mPayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemPay(info);
                }
            });
            itemViewHolder.mPrintClubBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemPrintClub(info);
                }
            });
            itemViewHolder.mPrintClientBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemPrintClient(info);
                }
            });
        }
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
        public TextView mTimeText;
        public TextView mStatusText;
        public RecyclerView mOrderList;
        public TextView mAmountText;
        public TextView mPaidAmountText;
        public Button mDetailBtn;
        public LinearLayout mPrintLayout;
        public Button mPrintClientBtn;
        public Button mPrintClubBtn;
        public Button mPayBtn;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTimeText = (TextView) itemView.findViewById(R.id.tv_time);
            mStatusText = (TextView) itemView.findViewById(R.id.tv_status);
            mOrderList = (RecyclerView) itemView.findViewById(R.id.rv_inner_record_list);
            mAmountText = (TextView) itemView.findViewById(R.id.tv_amount);
            mPaidAmountText = (TextView) itemView.findViewById(R.id.tv_paid_amount);
            mDetailBtn = (Button) itemView.findViewById(R.id.btn_detail);
            mPrintLayout = (LinearLayout) itemView.findViewById(R.id.layout_print);
            mPrintClientBtn = (Button) itemView.findViewById(R.id.btn_print_client);
            mPrintClubBtn = (Button) itemView.findViewById(R.id.btn_print_club);
            mPayBtn = (Button) itemView.findViewById(R.id.btn_go_pay);
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

