package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.HelloRecordInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ZR on 17-3-1.
 * 招呼记录
 */

public class HelloRecordAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HELLO_ITEM = 0;
    private static final int TYPE_FOOTER = 99;

    private Context mContext;
    private boolean mIsNoMore = false;
    private View.OnClickListener mFooterClickListener;
    private OnItemClickCallback mItemClickCallback;

    public interface OnItemClickCallback {
        void onItemClick(HelloRecordInfo info);
    }

    List<HelloRecordInfo> mHelloList = new ArrayList<>();

    public HelloRecordAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<HelloRecordInfo> list) {
        if (list != null) {
            mHelloList.clear();
            mHelloList.addAll(list);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> notifyDataSetChanged());
        }
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    public void setOnFooterClickListener(View.OnClickListener listener) {
        mFooterClickListener = listener;
    }

    public void setOnItemClickCallback(OnItemClickCallback callback) {
        mItemClickCallback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mHelloList.size()) {
            return TYPE_HELLO_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_HELLO_ITEM == viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_nearby_hello_item, parent, false));
        } else {
            return new FooterViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            HelloRecordInfo itemInfo = getItem(position);
            Glide.with(mContext).load(itemInfo.receiverAvatar).into(itemHolder.mAvatar);
            itemHolder.mNickName.setText(itemInfo.receiverName);
            itemHolder.mHelloDate.setText(itemInfo.sendTime);
            if (TextUtils.isEmpty(itemInfo.replyTime)) {
                itemHolder.mReplyStatus.setVisibility(View.INVISIBLE);
                itemHolder.itemView.setOnClickListener(null);
            } else {
                itemHolder.mReplyStatus.setVisibility(View.VISIBLE);
                if (mItemClickCallback != null) {
                    itemHolder.itemView.setOnClickListener(v -> mItemClickCallback.onItemClick(itemInfo));
                }
            }
        } else {
            FooterViewHolder footerHolder = (FooterViewHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mHelloList == null || mHelloList.isEmpty()) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.mFooterView.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                footerHolder.mFooterView.setOnClickListener(null);
            } else {
                footerHolder.mFooterView.setOnClickListener(v -> {
                    if (mFooterClickListener != null) {
                        mFooterClickListener.onClick(v);
                    }
                });
            }
            footerHolder.mFooterView.setText(desc);
        }
    }

    public HelloRecordInfo getItem(int position) {
        if (mHelloList.size() > position) {
            return mHelloList.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mHelloList.size() + 1;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_item_avatar)
        CircleImageView mAvatar;
        @BindView(R.id.tv_item_nickname)
        TextView mNickName;
        @BindView(R.id.tv_item_date)
        TextView mHelloDate;
        @BindView(R.id.tv_item_status)
        TextView mReplyStatus;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_footer)
        TextView mFooterView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

