package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.BillInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 16-12-2.
 */

public class BillRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BILL_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<BillInfo> mData = new ArrayList<>();
    private Context mContext;
    private RecyclerCallBack mCallBack;

    private int mFooterStatus;

    public interface RecyclerCallBack<BillInfo> {
        void onItemClick(BillInfo info);

        void onLoadMore();
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public BillRecyclerAdapter(Context context, RecyclerCallBack callBack) {
        this.mContext = context;
        this.mCallBack = callBack;
    }

    public void setData(List<BillInfo> data) {
        this.mData.addAll(data);
    }

    public List<BillInfo> getData() {
        return mData;
    }

    public void setStatus(@Flavour int status) {
        this.mFooterStatus = status;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_BILL_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BILL_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bill_info, parent, false));
            case TYPE_FOOTER:
                return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_footer, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
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
            final BillInfo info = mData.get(position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            if (info == null) {
                return;
            }
            itemHolder.mTradeNo.setText(info.tradeNo);
            // Pos支付+会员支付
            itemHolder.mPayMoney.setText(String.format(mContext.getString(R.string.cashier_money), Utils.moneyToStringEx(info.memberPayMoney + info.posPayMoney)));
            itemHolder.mStatus.setText(Utils.getPayStatusString(info.status));
            itemHolder.mPayType.setText(Utils.getPayTypeString(info.posPayType));
            if (!TextUtils.isEmpty(info.payDate)) {
                itemHolder.mPayDate.setText(Utils.getCustomDateString(mContext, Long.parseLong(info.payDate)));
            } else {
                itemHolder.mPayDate.setText("");
            }
            itemHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClick(info);
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
        public TextView mTradeNo;
        public TextView mPayMoney;
        public TextView mStatus;
        public TextView mPayType;
        public TextView mPayDate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mTradeNo = (TextView) itemView.findViewById(R.id.tv_bill_trade_no);
            mPayMoney = (TextView) itemView.findViewById(R.id.tv_bill_money);
            mStatus = (TextView) itemView.findViewById(R.id.tv_bill_status);
            mPayType = (TextView) itemView.findViewById(R.id.tv_bill_pay_type);
            mPayDate = (TextView) itemView.findViewById(R.id.tv_bill_time);
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
