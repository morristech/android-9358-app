package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.CommentInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-28.
 */
public class CommentAdapter extends RecyclerView.Adapter {
    private static final int TYPE_COMMENT_ITEM = 0;
    private static final int TYPE_FOOTER = 99;

    List<CommentInfo> mCommentList = new ArrayList<>();
    private Context mContext;
    private boolean mIsNoMore = false;

    private View.OnClickListener mFooterClickListener;

    public CommentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mCommentList.size()) {
            return TYPE_COMMENT_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    public void setData(List<CommentInfo> list) {
        if (list != null) {
            mCommentList.clear();
            mCommentList.addAll(list);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    public void setOnFooterClickListener(View.OnClickListener listener) {
        mFooterClickListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (TYPE_COMMENT_ITEM == viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
            return new CommentViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_footer, parent, false);
            return new ListFooterHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentViewHolder) {
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            CommentInfo commentInfo = getItem(position);
            if (commentInfo.userInfo == null) {
                return;
            }
            commentViewHolder.mName.setText(TextUtils.isEmpty(commentInfo.userInfo.name) ? ResourceUtils.getString(R.string.default_user_name) : commentInfo.userInfo.name);
            commentViewHolder.mRatings.setRating(commentInfo.rate / 20f);
            commentViewHolder.mComment.setText(commentInfo.comment);
            commentViewHolder.mTime.setText(commentInfo.createdAt);
            if (commentInfo.rewardAmount > 0) {
                float reward = commentInfo.rewardAmount / 100f;
                String rewardDes = String.format("打赏:%1.2f元", reward);
                commentViewHolder.mRewardAmount.setText(Utils.changeColor(rewardDes, ResourceUtils.getColor(R.color.colorMainBtn), 3, rewardDes.length()));
            } else {
                commentViewHolder.mRewardAmount.setText("");
            }

            if (Utils.isNotEmpty(commentInfo.userInfo.avatarUrl)) {
                Glide.with(mContext).load(commentInfo.userInfo.avatarUrl).into(commentViewHolder.mAvatar);
            } else {
                Glide.with(mContext).load(commentInfo.userInfo.headimgurl).into(commentViewHolder.mAvatar);
            }

        } else if (holder instanceof ListFooterHolder) {
            ListFooterHolder footerHolder = (ListFooterHolder) holder;
            String desc = ResourceUtils.getString(R.string.order_list_item_loading);
            if (mCommentList == null || mCommentList.isEmpty()) {
                desc = ResourceUtils.getString(R.string.order_list_item_empty);
                footerHolder.itemFooter.setOnClickListener(null);
            } else if (mIsNoMore) {
                desc = ResourceUtils.getString(R.string.order_list_item_no_more);
                footerHolder.itemFooter.setOnClickListener(null);
            } else {
                footerHolder.itemFooter.setOnClickListener(v -> {
                    if (mFooterClickListener != null) {
                        mFooterClickListener.onClick(v);
                    }
                });
            }
            footerHolder.itemFooter.setText(desc);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size() + 1;
    }

    public CommentInfo getItem(int position) {
        if (mCommentList.size() > position) {
            return mCommentList.get(position);
        }

        return null;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.avatar)
        CircleImageView mAvatar;
        @Bind(R.id.name)
        TextView mName;
        @Bind(R.id.ratings)
        RatingBar mRatings;
        @Bind(R.id.comment)
        TextView mComment;
        @Bind(R.id.time)
        TextView mTime;
        @Bind(R.id.reward_amount)
        TextView mRewardAmount;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ListFooterHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.item_footer)
        TextView itemFooter;

        public ListFooterHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
