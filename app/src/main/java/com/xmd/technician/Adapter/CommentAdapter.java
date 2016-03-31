package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.model.CommentInfo;
import com.xmd.technician.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 16-3-28.
 */
public class CommentAdapter extends RecyclerView.Adapter{

    List<CommentInfo> mCommentList = new ArrayList<>();
    private Context mContext;

    public CommentAdapter(Context context){
        mContext = context;
    }

    public void setData(List<CommentInfo> list){
        if(list != null){
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof CommentViewHolder){
            CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
            CommentInfo commentInfo = getItem(position);

            commentViewHolder.mName.setText(commentInfo.userInfo.name);
            commentViewHolder.mRatings.setRating(commentInfo.rate/20);
            commentViewHolder.mComment.setText(commentInfo.comment);
            commentViewHolder.mTime.setText(commentInfo.createAt);
            if(commentInfo.rewardAmount > 0){
                String s = String.format(mContext.getString(R.string.reward_amount),commentInfo.rewardAmount);
                SpannableString spanString = new SpannableString(s);
                int index = s.indexOf(":") + 1;
                ForegroundColorSpan span = new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorMain));
                spanString.setSpan(span, index, index + String.valueOf(commentInfo.rewardAmount).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                commentViewHolder.mRewardAmount.setText(spanString);
            }

            Glide.with(mContext).load(commentInfo.userInfo.imageUrl).into(commentViewHolder.mAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public CommentInfo getItem(int position){
        if(mCommentList.size() > position){
            return mCommentList.get(position);
        }

        return null;
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        @Bind(R.id.avatar) CircleImageView mAvatar;
        @Bind(R.id.name) TextView mName;
        @Bind(R.id.ratings) RatingBar mRatings;
        @Bind(R.id.comment) TextView mComment;
        @Bind(R.id.time) TextView mTime;
        @Bind(R.id.reward_amount) TextView mRewardAmount;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
