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

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.cashier.R;
import com.xmd.cashier.common.AppConstants;
import com.xmd.cashier.dal.bean.VerifyRecordInfo;
import com.xmd.cashier.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zr on 17-5-3.
 */

public class VerifyRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_RECORD_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private List<VerifyRecordInfo> mData = new ArrayList<>();
    private Context mContext;

    private int mFooterStatus;

    private VerifyRecordCallBack mCallBack;

    public VerifyRecordAdapter(Context context) {
        mContext = context;
    }

    public interface VerifyRecordCallBack {
        void onLoadMore();

        void onRecordClick(VerifyRecordInfo info, int position);
    }

    @IntDef({AppConstants.FOOTER_STATUS_SUCCESS, AppConstants.FOOTER_STATUS_ERROR, AppConstants.FOOTER_STATUS_NO_NETWORK, AppConstants.FOOTER_STATUS_NONE, AppConstants.FOOTER_STATUS_LOADING})
    public @interface Flavour {
    }

    public void setVerifyRecordCallBack(VerifyRecordCallBack callback) {
        mCallBack = callback;
    }

    public void clearData() {
        mData.clear();
    }

    public void setData(List<VerifyRecordInfo> data) {
        mData.addAll(data);
    }

    public void setStatus(@Flavour int status) {
        this.mFooterStatus = status;
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_RECORD_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_RECORD_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_verify_record, parent, false));
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
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            final VerifyRecordInfo info = mData.get(position);
            Glide.with(mContext).load(info.avatarUrl).dontAnimate().placeholder(R.drawable.ic_avatar).into(itemHolder.mAvatar);
            if (!TextUtils.isEmpty(info.verifyTime)) {
                itemHolder.mTime.setVisibility(View.VISIBLE);
                itemHolder.mTime.setText(DateUtils.doString2String(info.verifyTime, DateUtils.DF_DEFAULT, DateUtils.DF_JUST_DATE_TIME));
            } else {
                itemHolder.mTime.setVisibility(View.INVISIBLE);
            }
            itemHolder.mUserName.setText(TextUtils.isEmpty(info.userName) ? "匿名用户" : info.userName);
            itemHolder.mUserTelephone.setText(info.telephone);
            itemHolder.mDescription.setText(TextUtils.isEmpty(info.description) ? "无" : info.description);
            itemHolder.mOperator.setText(TextUtils.isEmpty(info.operatorName) ? "无" : info.operatorName);
            itemHolder.mTypeName.setText(info.businessTypeName);
            itemHolder.mLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallBack.onRecordClick(info, position);
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
        public LinearLayout mLayout;
        public CircleImageView mAvatar;
        public TextView mTime;
        public TextView mUserName;
        public TextView mUserTelephone;
        public TextView mDescription;
        public TextView mOperator;
        public TextView mTypeName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mLayout = (LinearLayout) itemView.findViewById(R.id.item_verify_ly);
            mAvatar = (CircleImageView) itemView.findViewById(R.id.item_verify_user_avatar);
            mTime = (TextView) itemView.findViewById(R.id.item_verify_time);
            mUserName = (TextView) itemView.findViewById(R.id.item_verify_user_name);
            mUserTelephone = (TextView) itemView.findViewById(R.id.item_verify_user_telephone);
            mDescription = (TextView) itemView.findViewById(R.id.item_verify_description);
            mOperator = (TextView) itemView.findViewById(R.id.item_verify_operator);
            mTypeName = (TextView) itemView.findViewById(R.id.item_verify_type_name);
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
