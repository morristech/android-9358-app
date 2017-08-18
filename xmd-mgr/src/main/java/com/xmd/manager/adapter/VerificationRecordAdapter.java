package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.manager.R;
import com.xmd.manager.beans.VerificationDetailBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 2017/1/17.
 */

public class VerificationRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private static final int TYPE_RECORD_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_DADA_IS_EMPTY = 2;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<VerificationDetailBean> mData = null;
    private Context mContext;
    private ClickedCallback mClickedCallback;

    public interface ClickedCallback {
        void loadMore();

        void onItemClicked(VerificationDetailBean bean);
    }

    public VerificationRecordAdapter(Context context, List<VerificationDetailBean> data, ClickedCallback itemInterface) {
        mContext = context;
        mData = new ArrayList<>();
        mClickedCallback = itemInterface;
    }

    public void setData(List<VerificationDetailBean> data) {
        mData.clear();
        mData.addAll(data);
        mIsEmpty = data.isEmpty();
        notifyDataSetChanged();
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_RECORD_ITEM:
                return new VerificationRecordHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_verification_record_item, parent, false));
            case TYPE_FOOTER:
                return new ListFooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false));
            default:
                return new ListFooterHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false));

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.verification_record_list_item_loading);
            if (mIsEmpty) {
                desc = ResourceUtils.getString(R.string.verification_record_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.verification_record_list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> mClickedCallback.loadMore());
            }
            footerHolder.itemFooter.setText(desc);
        } else if (holder instanceof VerificationRecordHolder) {
            Object obj = mData.get(position);
            if (obj instanceof VerificationDetailBean) {
                bindVerificationDetailItemViewHolder(holder, obj, position);
            }

        }


    }

    private void bindVerificationDetailItemViewHolder(RecyclerView.ViewHolder holder, Object obj, Integer position) {

        VerificationRecordHolder viewHolder = (VerificationRecordHolder) holder;
        VerificationDetailBean recordBean = (VerificationDetailBean) obj;
        if (position > 0 && recordBean.currentMonth.equals(mData.get(position - 1).currentMonth)) {
            viewHolder.mRlRecordTotal.setVisibility(View.GONE);
        } else {
            viewHolder.mRlRecordTotal.setVisibility(View.VISIBLE);
            viewHolder.mRecordMonth.setText(recordBean.currentMonth.substring(0, 4) + "年" + recordBean.currentMonth.substring(5, 7) + "月");
            viewHolder.mRecordTotal.setText(String.format("核销数： %s", recordBean.currentMonthTotal));
        }
        if (recordBean.currentMonthTotal == 0) {
            viewHolder.mLlRecordDetail.setVisibility(View.GONE);
        } else {
            viewHolder.mLlRecordDetail.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(recordBean.avatarUrl).into(viewHolder.mRecordHead);
            viewHolder.mRecordUserName.setText(recordBean.userName);
            viewHolder.mRecordPhone.setText(recordBean.telephone);
            if (!TextUtils.isEmpty(recordBean.verifyTime) && recordBean.verifyTime.length() >= 16) {
                viewHolder.mRecordTime.setText(recordBean.verifyTime.substring(5, 16));
            }
            viewHolder.mRecordCouponName.setText(recordBean.description);
            viewHolder.mRecordHolder.setText(recordBean.operatorName);
            viewHolder.mRecordType.setText(recordBean.businessTypeName);
            viewHolder.itemView.setOnClickListener(v -> mClickedCallback.onItemClicked(recordBean));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (!mIsEmpty) {
            return TYPE_RECORD_ITEM;
        } else {
            return TYPE_DADA_IS_EMPTY;
        }


    }


    static class ListFooterHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class VerificationRecordHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.record_month)
        TextView mRecordMonth;
        @BindView(R.id.record_total)
        TextView mRecordTotal;
        @BindView(R.id.rl_record_total)
        RelativeLayout mRlRecordTotal;
        @BindView(R.id.record_head)
        CircleImageView mRecordHead;
        @BindView(R.id.record_user_name)
        TextView mRecordUserName;
        @BindView(R.id.record_phone)
        TextView mRecordPhone;
        @BindView(R.id.record_time)
        TextView mRecordTime;
        @BindView(R.id.record_coupon_name)
        TextView mRecordCouponName;
        @BindView(R.id.record_holder)
        TextView mRecordHolder;
        @BindView(R.id.record_type)
        TextView mRecordType;
        @BindView(R.id.ll_record_detail)
        LinearLayout mLlRecordDetail;

        public VerificationRecordHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
