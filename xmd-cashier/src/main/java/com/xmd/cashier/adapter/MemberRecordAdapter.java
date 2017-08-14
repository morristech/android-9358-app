package com.xmd.cashier.adapter;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.common.Utils;
import com.xmd.cashier.dal.bean.MemberRecordInfo;
import com.xmd.cashier.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-7-15.
 * 会所会员账户记录
 */

public class MemberRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_MEMBER_RECORD_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<MemberRecordInfo> mData = new ArrayList<>();
    private Context mContext;
    private CallBack mCallBack;

    private int mFooterStatus;

    public interface CallBack {
        void onLoadMore();

        void onPrint(MemberRecordInfo info);
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public MemberRecordAdapter(Context context) {
        this.mContext = context;
    }

    public void clearData() {
        this.mData.clear();
    }

    public void setData(List<MemberRecordInfo> data) {
        this.mData.addAll(data);
    }

    public void setCallBack(CallBack callBack) {
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
            return TYPE_MEMBER_RECORD_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_MEMBER_RECORD_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_member_record, parent, false));
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
            final MemberRecordInfo info = mData.get(position);
            Glide.with(mContext).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(itemViewHolder.mAvatar);
            itemViewHolder.mName.setText(info.name);
            itemViewHolder.mLevel.setText(info.memberTypeName);
            itemViewHolder.mPhone.setText(info.telephone);
            itemViewHolder.mCardNo.setText(info.cardNo);
            itemViewHolder.mTime.setText(info.createTime);
            switch (info.tradeType) {
                case AppConstants.MEMBER_TRADE_TYPE_INCOME:
                    itemViewHolder.mAmount.setText("+ " + Utils.moneyToStringEx(info.amount));
                    itemViewHolder.mAmount.setTextColor(mContext.getResources().getColor(R.color.colorMemberPaySuccess));
                    break;
                case AppConstants.MEMBER_TRADE_TYPE_PAY:
                    itemViewHolder.mAmount.setText("- " + Utils.moneyToStringEx(info.amount));
                    itemViewHolder.mAmount.setTextColor(mContext.getResources().getColor(R.color.colorRed));
                    break;
                default:
                    itemViewHolder.mAmount.setText(Utils.moneyToStringEx(info.amount));
                    break;
            }
            itemViewHolder.mPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onPrint(info);
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
        public CircleImageView mAvatar;
        public TextView mName;
        public TextView mLevel;
        public TextView mPhone;
        public TextView mCardNo;
        public TextView mAmount;
        public TextView mTime;
        public Button mPrint;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mAvatar = (CircleImageView) itemView.findViewById(R.id.item_avatar);
            mName = (TextView) itemView.findViewById(R.id.item_name);
            mLevel = (TextView) itemView.findViewById(R.id.item_level);
            mPhone = (TextView) itemView.findViewById(R.id.item_phone);
            mCardNo = (TextView) itemView.findViewById(R.id.item_card_no);
            mAmount = (TextView) itemView.findViewById(R.id.item_amount);
            mTime = (TextView) itemView.findViewById(R.id.item_time);
            mPrint = (Button) itemView.findViewById(R.id.item_print);
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
