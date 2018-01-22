package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.SettleRecordInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-4-24.
 */

public class SettleRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final static int TYPE_ITEM = 0;
    private final static int TYPE_FOOTER = 1;

    private List<SettleRecordInfo> mData = new ArrayList<>();

    private Context mContext;
    private CallBack mCallBack;

    private int mStatus;

    public SettleRecordAdapter(Context context) {
        this.mContext = context;
    }

    public interface CallBack {
        void onItemClick(SettleRecordInfo info, int position);

        void onLoadMore();
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public void setCallBack(CallBack callback) {
        mCallBack = callback;
    }

    public void setData(List<SettleRecordInfo> list) {
        mData.addAll(list);
    }

    public void clearData() {
        mData.clear();
    }

    public void setStatus(@Flavour int status) {
        mStatus = status;
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
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_settle_record, parent, false));
            case TYPE_FOOTER:
                return new FooterItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout_footer, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof FooterItemHolder) {
            FooterItemHolder footerHolder = (FooterItemHolder) holder;
            footerHolder.mMoreMsg.setText(getDescStr(mStatus));
            switch (mStatus) {
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
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final SettleRecordInfo info = mData.get(position);
            itemHolder.mSettleOperator.setText(info.operatorName);
            itemHolder.mSettleTime.setText(info.createTime);
            itemHolder.mSettleAmount.setText(String.format(mContext.getResources().getString(R.string.cashier_money), Utils.moneyToStringEx(info.amount)));
            itemHolder.mRecordLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onItemClick(info, position);
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

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout mRecordLayout;
        public TextView mSettleAmount;
        public TextView mSettleOperator;
        public TextView mSettleTime;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mRecordLayout = (LinearLayout) itemView.findViewById(R.id.ll_record_layout);
            mSettleAmount = (TextView) itemView.findViewById(R.id.tv_settle_amount);
            mSettleOperator = (TextView) itemView.findViewById(R.id.tv_settle_operator);
            mSettleTime = (TextView) itemView.findViewById(R.id.tv_settle_time);
        }
    }

    public class FooterItemHolder extends RecyclerView.ViewHolder {
        public LinearLayout mMoreLayout;
        public ProgressBar mMoreProgress;
        public TextView mMoreMsg;

        public FooterItemHolder(View itemView) {
            super(itemView);
            mMoreLayout = (LinearLayout) itemView.findViewById(R.id.ll_footer_layout);
            mMoreProgress = (ProgressBar) itemView.findViewById(R.id.pb_more_data);
            mMoreMsg = (TextView) itemView.findViewById(R.id.tv_more_data);
        }
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
}
