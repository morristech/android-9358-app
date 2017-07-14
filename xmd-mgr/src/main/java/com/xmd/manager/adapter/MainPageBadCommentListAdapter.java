package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xmd_m_comment.adapter.CommentItemDetailAdapter;
import com.example.xmd_m_comment.bean.CommentBean;
import com.xmd.app.widget.RoundImageView;
import com.xmd.manager.R;

import java.util.List;


/**
 * Created by lhj on 2016/12/14.
 */

public class MainPageBadCommentListAdapter extends RecyclerView.Adapter {

    public List<CommentBean> mBadComments;
    private Context mContext;
    private OnCommentClickedListener mCommentClickedListener;

    public interface OnCommentClickedListener {
        void onCommentCallBackClick(CommentBean comment);

        void onItemClicked(CommentBean badComment);
    }


    public MainPageBadCommentListAdapter(Context context, List<CommentBean> comments) {
        mContext = context;
        this.mBadComments = comments;

    }

    public void setCommentClickedListener(OnCommentClickedListener commentClickedListener) {
        mCommentClickedListener = commentClickedListener;
    }

    public void setData(List<CommentBean> comments) {
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
        MainPageBadCommentViewHolder viewHolder = (MainPageBadCommentViewHolder) holder;
        final CommentBean commentBean = mBadComments.get(position);
        Glide.with(mContext).load(commentBean.avatarUrl).into(viewHolder.commentHead);
        viewHolder.commentName.setText(TextUtils.isEmpty(commentBean.userName) ? "匿名用户" : commentBean.userName);
        viewHolder.commentPhone.setText(TextUtils.isEmpty(commentBean.phoneNum) ? "" : commentBean.phoneNum);
        viewHolder.commentTechName.setText(TextUtils.isEmpty(commentBean.techName) ? "技师" : commentBean.techName);
        viewHolder.commentTechCode.setText(TextUtils.isEmpty(commentBean.techNo) ? "" : String.format("[%s]", commentBean.techNo));
        viewHolder.commentTime.setText(com.shidou.commonlibrary.util.DateUtils.doLong2String(commentBean.createdAt, "MM-dd HH:mm"));

        if (commentBean.returnStatus.equals("Y")) {
            viewHolder.imgVisitMark.setVisibility(View.VISIBLE);
            viewHolder.tvVisitComment.setText("操作");
        } else {
            viewHolder.imgVisitMark.setVisibility(View.GONE);
            viewHolder.tvVisitComment.setText("回访");
        }
        if (commentBean.commentType.equals("complaint")) {//投诉
            viewHolder.llComplaintDetail.setVisibility(View.VISIBLE);
            viewHolder.llCommentTypeTech.setVisibility(View.GONE);
            viewHolder.commentComplaintDetail.setText(TextUtils.isEmpty(commentBean.comment) ? "" : commentBean.comment);
        } else {//评论
            viewHolder.llCommentTypeTech.setVisibility(View.VISIBLE);
            viewHolder.llComplaintDetail.setVisibility(View.GONE);
            viewHolder.commentDetail.setText(TextUtils.isEmpty(commentBean.comment) ? "" : commentBean.comment);
            CommentItemDetailAdapter adapter = new CommentItemDetailAdapter(mContext, commentBean.commentRateList);
            viewHolder.commentProjectList.setLayoutManager(new GridLayoutManager(mContext, 2));
            viewHolder.commentProjectList.setAdapter(adapter);
        }
        if (commentBean.isAnonymous.equals("N")) {//匿名评价
         //   viewHolder.tvVisitComment.setVisibility(View.VISIBLE);
            viewHolder.llCommentHandler.setVisibility(View.VISIBLE);
        } else {
            viewHolder.llCommentHandler.setVisibility(View.GONE);
         //   viewHolder.tvVisitComment.setVisibility(View.INVISIBLE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentClickedListener.onItemClicked(commentBean);
            }
        });

        viewHolder.tvVisitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCommentClickedListener.onCommentCallBackClick(commentBean);
            }
        });


        return;
    }


    @Override
    public int getItemCount() {
        return mBadComments.size();
    }


    final static class MainPageBadCommentViewHolder extends RecyclerView.ViewHolder {
        RoundImageView commentHead;
        TextView commentName;
        TextView commentPhone;
        TextView commentTechName;
        TextView commentTechCode;
        RecyclerView commentProjectList;
        TextView commentDetail;
        LinearLayout llCommentTypeTech;
        TextView commentComplaintDetail;
        LinearLayout llComplaintDetail;
        TextView commentTime;
        LinearLayout badComment;
        TextView tvDeleteComment;
        TextView tvVisitComment;
        ImageView imgVisitMark;
        ImageView imgDeleteMark;
        LinearLayout llCommentHandler;

        MainPageBadCommentViewHolder(View view) {
            super(view);
            commentHead = (RoundImageView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_head);
            commentName = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_name);
            commentPhone = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_phone);
            commentTechName = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_tech_name);
            commentTechCode = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_tech_code);
            commentProjectList = (RecyclerView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_project_list);
            commentDetail = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_detail);
            llCommentTypeTech = (LinearLayout) itemView.findViewById(com.example.xmd_m_comment.R.id.ll_comment_type_tech);
            commentComplaintDetail = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_complaint_detail);
            llComplaintDetail = (LinearLayout) itemView.findViewById(com.example.xmd_m_comment.R.id.ll_complaint_detail);
            commentTime = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.comment_time);
            badComment = (LinearLayout) itemView.findViewById(com.example.xmd_m_comment.R.id.bad_comment);
            tvDeleteComment = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.tv_delete_comment);
            tvVisitComment = (TextView) itemView.findViewById(com.example.xmd_m_comment.R.id.tv_visit_comment);
            imgVisitMark = (ImageView) itemView.findViewById(com.example.xmd_m_comment.R.id.img_visit_mark);
            imgDeleteMark = (ImageView) itemView.findViewById(com.example.xmd_m_comment.R.id.img_delete_mark);
            llCommentHandler = (LinearLayout) itemView.findViewById(com.example.xmd_m_comment.R.id.ll_comment_handler);
        }
    }

}
