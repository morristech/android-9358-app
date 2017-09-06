package com.xmd.black.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.black.R;
import com.xmd.black.bean.CustomerInfo;

import java.util.List;

/**
 * Created by Lhj on 17-7-22.
 */

public class ListRecycleViewAdapter extends RecyclerView.Adapter {

    public interface Callback {

        void onItemClicked(CustomerInfo bean, boolean isFromManager);

        boolean isPaged();
    }

    private static final int TYPE_CUSTOMER_INFO_ITEM = 0;
    private static final int TYPE_FOOTER = 99;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<CustomerInfo> mData;
    private Callback mCallback;
    private Context mContext;
    private boolean isManager;


    public ListRecycleViewAdapter(Context context, List<CustomerInfo> data, Callback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
    }

    public void setData(List<CustomerInfo> data, boolean isManager) {
        mData = data;
        mIsEmpty = data.isEmpty();
        this.isManager = isManager;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (mCallback.isPaged() && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (mData.get(position) instanceof CustomerInfo) {
            return TYPE_CUSTOMER_INFO_ITEM;
        }
        return TYPE_FOOTER;
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_CUSTOMER_INFO_ITEM:
                View customerInfoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_info_list_item, parent, false);
                return new CustomerInfoViewHolder(customerInfoView);
            case TYPE_FOOTER:
                View viewFoot = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_item, parent, false);
                return new FooterViewHolder(viewFoot);
            default:
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_item, parent, false);
                return new FooterViewHolder(viewDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CustomerInfoViewHolder) {
            final CustomerInfo customerInfo = mData.get(position);
            CustomerInfoViewHolder viewHolder = (CustomerInfoViewHolder) holder;

            if (TextUtils.isEmpty(customerInfo.userNoteName) && TextUtils.isEmpty(customerInfo.userName)) {
                viewHolder.mTvCustomerName.setText("匿名用户");
            } else {
                viewHolder.mTvCustomerName.setText(TextUtils.isEmpty(customerInfo.userNoteName) ? customerInfo.userName : customerInfo.userNoteName);
            }
            Glide.with(mContext).load(customerInfo.avatarUrl).into(viewHolder.mRoundImageView);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(customerInfo, isManager);
                }
            });
            return;
        }
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder viewHolder = (FooterViewHolder) holder;
            if (mIsEmpty) {
                viewHolder.textView.setText("----------还没有数据哦----------");
            } else if (mIsNoMore) {
                viewHolder.textView.setText(ResourceUtils.getString(R.string.all_data_load_finish));
            } else {
                viewHolder.textView.setText("----------上拉加载更多----------");
            }
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

    static class CustomerInfoViewHolder extends RecyclerView.ViewHolder {
        RoundImageView mRoundImageView;
        TextView mTvCustomerName;

        public CustomerInfoViewHolder(View itemView) {
            super(itemView);
            mRoundImageView = (RoundImageView) itemView.findViewById(R.id.customer_head);
            mTvCustomerName = (TextView) itemView.findViewById(R.id.customer_name);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.footer_textView);
        }
    }
}
