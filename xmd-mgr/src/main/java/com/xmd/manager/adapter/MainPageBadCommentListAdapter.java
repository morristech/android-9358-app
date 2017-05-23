package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.hyphenate.util.DateUtils;
import com.xmd.manager.R;
import com.xmd.manager.beans.BadComment;
import com.xmd.manager.beans.BadCommentRateListBean;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.widget.CircleImageView;

import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by lhj on 2016/12/14.
 */

public class MainPageBadCommentListAdapter extends RecyclerView.Adapter {

    public List<BadComment> mBadComments;
    private Context mContext;
    private OnCommentClickedListener mCommentClickedListener;

    public interface OnCommentClickedListener {
        void onCommentCallBackClick(BadComment comment);

        void onItemClicked(BadComment badComment);
    }


    public MainPageBadCommentListAdapter(Context context, List<BadComment> comments) {
        mContext = context;
        this.mBadComments = comments;

    }

    public void setCommentClickedListener(OnCommentClickedListener commentClickedListener) {
        mCommentClickedListener = commentClickedListener;
    }

    public void setData(List<BadComment> comments) {
        this.mBadComments = comments;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_bad_comment_item, parent, false);
        return new MainPageBadCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MainPageBadCommentViewHolder badHolder = (MainPageBadCommentViewHolder) holder;
        final BadComment badComment = mBadComments.get(position);
        badHolder.commentTime.setText(DateUtils.getTimestampString(new Date(badComment.createdAt)));
        if (Utils.isNotEmpty(badComment.phoneNum)) {
            badHolder.customerPhone.setText(badComment.phoneNum);
        }
        if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_ORDER) || badComment.commentType.equals(RequestConstant.COMMENT_TYPE_TECH)) {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_text));
            badHolder.commentTechName.setText(Utils.isEmpty(badComment.techName) ? ResourceUtils.getString(R.string.tech_text) : badComment.techName);
            if (Utils.isNotEmpty(badComment.techNo)) {
                int startIndex = 1;
                int endIndex = startIndex + badComment.techNo.length();
                Spannable spanString = Utils.changeColor("[" + badComment.techNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                badHolder.commentTechCode.setText(spanString);
                badHolder.commentTechCode.setVisibility(View.VISIBLE);
            } else {
                badHolder.commentTechCode.setVisibility(View.GONE);
            }
        } else if (badComment.commentType.equals(RequestConstant.COMMENT_TYPE_CLUB)) {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_text));
            badHolder.commentTechName.setText(ResourceUtils.getString(R.string.comment_club));
            badHolder.commentTechCode.setVisibility(View.GONE);
        } else {
            badHolder.commentType.setText(ResourceUtils.getString(R.string.comment_qr_code));
            badHolder.commentTechName.setText(Utils.isEmpty(badComment.techName) ? ResourceUtils.getString(R.string.tech_text) : badComment.techName);
            if (Utils.isNotEmpty(badComment.techNo)) {
                int startIndex = 1;
                int endIndex = startIndex + badComment.techNo.length();
                Spannable spanString = Utils.changeColor("[" + badComment.techNo + "]",
                        ResourceUtils.getColor(R.color.number_color), startIndex, endIndex);
                badHolder.commentTechCode.setText(spanString);
                badHolder.commentTechCode.setVisibility(View.VISIBLE);
            } else {
                badHolder.commentTechCode.setVisibility(View.GONE);
            }
        }
        if (badComment.commentRateList != null && badComment.commentRateList.size() > 0) {
            badHolder.mainListView.setVisibility(View.VISIBLE);
            CommentDetailItemAdapter<BadCommentRateListBean> adapter = new CommentDetailItemAdapter(mContext, badComment.commentRateList);
            badHolder.mainListView.setLayoutManager(new GridLayoutManager(mContext, 2));
            badHolder.mainListView.setAdapter(adapter);
        } else {
            badHolder.mainListView.setVisibility(View.GONE);
        }
        if (Utils.isNotEmpty(badComment.comment)) {
            badHolder.commentDetail.setText(badComment.comment);
            badHolder.commentDetail.setVisibility(View.VISIBLE);
        } else {
            badHolder.commentDetail.setVisibility(View.GONE);
        }
        badHolder.customerName.setText(Utils.StrSubstring(5, badComment.userName, true));
        if (badComment.isAnonymous.equals("Y")) {
            Glide.with(mContext).load(R.drawable.icon22).into(badHolder.commentCustomerHead);
            badHolder.commentReturnVisit.setVisibility(View.GONE);
        } else {
            Glide.with(mContext).load(badComment.avatarUrl).into(badHolder.commentCustomerHead);
            badHolder.commentReturnVisit.setVisibility(View.VISIBLE);
            badHolder.commentReturnVisit.setOnClickListener(v -> {
                mCommentClickedListener.onCommentCallBackClick(badComment);
            });
        }
        badHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentClickedListener.onItemClicked(badComment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBadComments.size();
    }


    final static class MainPageBadCommentViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.comment_customer_head)
        CircleImageView commentCustomerHead;
        @Bind(R.id.customer_name)
        TextView customerName;
        @Bind(R.id.customer_phone)
        TextView customerPhone;
        @Bind(R.id.comment_type)
        TextView commentType;
        @Bind(R.id.comment_tech_name)
        TextView commentTechName;
        @Bind(R.id.comment_tech_code)
        TextView commentTechCode;
        @Bind(R.id.comment_tech)
        LinearLayout commentTech;
        @Bind(R.id.main_list_view)
        RecyclerView mainListView;
        @Bind(R.id.comment_detail)
        TextView commentDetail;
        @Bind(R.id.comment_time)
        TextView commentTime;
        @Bind(R.id.img_btn_delete)
        ImageButton imgBtnDelete;
        @Bind(R.id.img_delete_mark)
        ImageView imgDeleteMark;
        @Bind(R.id.comment_return_visit)
        TextView commentReturnVisit;
        @Bind(R.id.bad_comment)
        LinearLayout badComment;

        MainPageBadCommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
