package com.xmd.m.comment.adapter;

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
import com.shidou.commonlibrary.util.DateUtils;
import com.xmd.app.utils.ResourceUtils;
import com.xmd.app.utils.Utils;
import com.xmd.app.widget.RoundImageView;
import com.xmd.m.R;
import com.xmd.m.R2;

import com.xmd.m.comment.bean.CommentBean;
import com.xmd.m.comment.bean.ConsumeBean;
import com.xmd.m.comment.bean.RewardBean;
import com.xmd.m.comment.bean.VisitorBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-7-1.
 */

public class ListRecycleViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callback<T> {

        void onItemClicked(T bean, String type);

        void onNegativeButtonClicked(T bean, int position);

        void onPositiveButtonClicked(T bean, int position);

        void onLoadMoreButtonClicked();

        boolean isPaged();
    }

    private static final int TYPE_COMMENT_ITEM = 0;
    private static final int TYPE_CONSUME_ITEM = 1;
    private static final int TYPE_VISITOR_ITEM = 2;
    private static final int TYPE_REWARD_ITEM = 3;
    private static final int TYPE_FOOTER = 99;

    private boolean mIsNoMore = false;
    private boolean mIsEmpty = false;
    private List<T> mData;
    private Callback mCallback;
    private Context mContext;
    private boolean isManager;
    private String mType;


    public ListRecycleViewAdapter(Context context, List<T> data, Callback callback) {
        mContext = context;
        mData = data;
        mCallback = callback;
    }

    //type
    public void setData(List<T> data, boolean isManager, String type) {
        mData = data;
        mIsEmpty = data.isEmpty();
        this.isManager = isManager;
        notifyDataSetChanged();
        this.mType = type;
    }


    public void setIsNoMore(boolean isNoMore) {
        mIsNoMore = isNoMore;
    }

    @Override
    public int getItemViewType(int position) {
        if (mCallback.isPaged() && position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else if (mData.get(position) instanceof CommentBean) {
            return TYPE_COMMENT_ITEM;
        } else if (mData.get(position) instanceof ConsumeBean) {
            return TYPE_CONSUME_ITEM;
        } else if (mData.get(position) instanceof RewardBean) {
            return TYPE_REWARD_ITEM;
        } else if (mData.get(position) instanceof VisitorBean) {
            return TYPE_VISITOR_ITEM;
        }
        return TYPE_FOOTER;
    }

    @Override
    public int getItemCount() {

        if (mCallback.isPaged()) {
            return mData.size() + 1;
        } else {
            return mData.size();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_COMMENT_ITEM:
                View commentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_list_item, parent, false);
                return new CommentViewHolder(commentView);
            case TYPE_CONSUME_ITEM:
                View consumeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_consume_list_item, parent, false);
                return new ConsumeViewHolder(consumeView);
            case TYPE_REWARD_ITEM:
                View rewardView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reward_list_item, parent, false);
                return new RewardViewHolder(rewardView);
            case TYPE_VISITOR_ITEM:
                View visitorView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_visitor_list_item, parent, false);
                return new VisitorViewHolder(visitorView);
            case TYPE_FOOTER:
                View footerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_item, parent, false);
                return new FooterViewHolder(footerView);
            default:
                View viewDefault = LayoutInflater.from(parent.getContext()).inflate(R.layout.footer_view_item, parent, false);
                return new FooterViewHolder(viewDefault);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof CommentViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof CommentBean)) {
                return;
            }
            final CommentBean commentBean = (CommentBean) obj;
            CommentViewHolder viewHolder = (CommentViewHolder) holder;
            if (!isManager && mType.equals("1")) {//差评
                String commentUserName = TextUtils.isEmpty(commentBean.userName) ? "匿名用户" : commentBean.userName;
                viewHolder.commentName.setText(String.format("%s**(匿名)", commentUserName.substring(0, 1)));
                viewHolder.commentPhone.setText("");
                Glide.with(mContext).load(R.drawable.img_default_avatar).into(viewHolder.commentHead);
            } else {
                Glide.with(mContext).load(commentBean.avatarUrl).into(viewHolder.commentHead);
                String commentUserName = TextUtils.isEmpty(commentBean.userName) ? "匿名用户" : commentBean.userName;
                viewHolder.commentName.setText(Utils.StrSubstring(8, commentUserName, true));
                viewHolder.commentPhone.setText(TextUtils.isEmpty(commentBean.phoneNum) ? "" : commentBean.phoneNum);
            }

            if (isManager) {
                viewHolder.llTechInfo.setVisibility(View.VISIBLE);
                viewHolder.commentTechName.setText(TextUtils.isEmpty(commentBean.techName) ? "会所" : Utils.briefString(commentBean.techName, 7));
                viewHolder.commentTechCode.setText(TextUtils.isEmpty(commentBean.techNo) ? "" : String.format("[%s]", commentBean.techNo));
            } else {
                viewHolder.llTechInfo.setVisibility(View.GONE);
            }
            viewHolder.commentTime.setText(DateUtils.doLong2String(commentBean.createdAt, "MM-dd HH:mm"));
            if (isManager) {
                viewHolder.llCommentHandler.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llCommentHandler.setVisibility(View.GONE);
            }
            if (commentBean.status.equals("delete")) {
                viewHolder.tvDeleteComment.setVisibility(View.GONE);
                viewHolder.imgDeleteMark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.imgDeleteMark.setVisibility(View.GONE);
                viewHolder.tvDeleteComment.setVisibility(View.VISIBLE);
            }
            if (isManager) {
                if (commentBean.returnStatus.equals("Y")) {
                    viewHolder.imgVisitMark.setVisibility(View.VISIBLE);
                    viewHolder.tvVisitComment.setText("操作");
                } else {
                    viewHolder.imgVisitMark.setVisibility(View.GONE);
                    viewHolder.tvVisitComment.setText("回访");
                }
            } else {
                viewHolder.imgVisitMark.setVisibility(View.GONE);
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
            if (commentBean.isAnonymous.equals("N") && isManager) {//匿名评价
                viewHolder.llCommentHandler.setVisibility(View.VISIBLE);
            } else {
                viewHolder.llCommentHandler.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onItemClicked(commentBean, mType);
                }
            });

            viewHolder.tvVisitComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onPositiveButtonClicked(commentBean, position);
                }
            });
            viewHolder.tvDeleteComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallback.onNegativeButtonClicked(commentBean, position);
                }
            });


            return;
        }
        if (holder instanceof ConsumeViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof ConsumeBean)) {
                return;
            }
            final ConsumeBean consumeBean = (ConsumeBean) obj;
            ConsumeViewHolder viewHolder = (ConsumeViewHolder) holder;
            viewHolder.tvConsumeTime.setText(DateUtils.doLong2String(consumeBean.createTime, "MM月dd日　HH:mm"));
            viewHolder.tvConsumeType.setText(consumeBean.businessTypeName);
            viewHolder.tvConsumeDetail.setText(String.format("%1.2f元 ", consumeBean.amount / 100f));
            return;
        }
        if (holder instanceof RewardViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof RewardBean)) {
                return;
            }
            final RewardBean rewardBean = (RewardBean) obj;
            RewardViewHolder viewHolder = (RewardViewHolder) holder;
            viewHolder.tvRewardTime.setText(DateUtils.doLong2String(rewardBean.createTime, "MM月dd日　HH:mm"));
            if (rewardBean.paidType.equals("credits")) {
                viewHolder.tvRewardType.setText("积分");
                viewHolder.tvRewardDetail.setText(String.valueOf(rewardBean.amount));
            } else {
                viewHolder.tvRewardType.setText("金钱");
                viewHolder.tvRewardDetail.setText(String.format("%1.2f元", rewardBean.amount / 100f));
            }
            if (isManager) {
                viewHolder.llRewardTech.setVisibility(View.VISIBLE);
                viewHolder.tvRewardTechName.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(rewardBean.techName)) {
                    viewHolder.tvRewardTechName.setText(rewardBean.techName);
                    if (!TextUtils.isEmpty(rewardBean.techNo)) {
                        viewHolder.tvRewardTechNo.setText(String.format("[%s]", rewardBean.techNo));
                    } else {
                        viewHolder.tvRewardTechNo.setText("");
                    }
                } else {
                    viewHolder.tvRewardTechName.setText("未归属");
                    viewHolder.tvRewardTechNo.setText("");
                }
            } else {
                viewHolder.llRewardTech.setVisibility(View.GONE);
            }

            return;
        }
        if (holder instanceof VisitorViewHolder) {
            Object obj = mData.get(position);
            if (!(obj instanceof VisitorBean)) {
                return;
            }
            final VisitorBean visitBean = (VisitorBean) obj;
            VisitorViewHolder viewHolder = (VisitorViewHolder) holder;
            viewHolder.tvVisitorTime.setText(DateUtils.doLong2String(visitBean.createTime, "MM月dd日　HH:mm"));
            viewHolder.tvVisitorType.setText(visitBean.businessTypeName);
            if (isManager) {
                viewHolder.llVisitorTech.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(visitBean.techName)) {
                    viewHolder.tvVisitorTechName.setText(visitBean.techName);
                    if (!TextUtils.isEmpty(visitBean.techNo)) {
                        viewHolder.tvVisitorTechNo.setText(String.format("[%s]", visitBean.techNo));
                    } else {
                        viewHolder.tvVisitorTechNo.setText("");
                    }
                } else {
                    viewHolder.tvVisitorTechName.setText("未归属");
                    viewHolder.tvVisitorTechNo.setText("");
                }
            } else {
                viewHolder.llVisitorTech.setVisibility(View.GONE);
            }
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


    static class CommentViewHolder extends RecyclerView.ViewHolder {

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
        LinearLayout llTechInfo;

        public CommentViewHolder(View itemView) {
            super(itemView);
            commentHead = (RoundImageView) itemView.findViewById(R.id.comment_head);
            commentName = (TextView) itemView.findViewById(R.id.comment_name);
            commentPhone = (TextView) itemView.findViewById(R.id.comment_phone);
            commentTechName = (TextView) itemView.findViewById(R.id.comment_tech_name);
            commentTechCode = (TextView) itemView.findViewById(R.id.comment_tech_code);
            commentProjectList = (RecyclerView) itemView.findViewById(R.id.comment_project_list);
            commentDetail = (TextView) itemView.findViewById(R.id.comment_detail);
            llCommentTypeTech = (LinearLayout) itemView.findViewById(R.id.ll_comment_type_tech);
            commentComplaintDetail = (TextView) itemView.findViewById(R.id.comment_complaint_detail);
            llComplaintDetail = (LinearLayout) itemView.findViewById(R.id.ll_complaint_detail);
            commentTime = (TextView) itemView.findViewById(R.id.comment_time);
            badComment = (LinearLayout) itemView.findViewById(R.id.bad_comment);
            tvDeleteComment = (TextView) itemView.findViewById(R.id.tv_delete_comment);
            tvVisitComment = (TextView) itemView.findViewById(R.id.tv_visit_comment);
            imgVisitMark = (ImageView) itemView.findViewById(R.id.img_visit_mark);
            imgDeleteMark = (ImageView) itemView.findViewById(R.id.img_delete_mark);
            llCommentHandler = (LinearLayout) itemView.findViewById(R.id.ll_comment_handler);
            llTechInfo = (LinearLayout) itemView.findViewById(R.id.ll_comment_tech_info);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.footer_textView);
        }
    }


    static class ConsumeViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_consume_time)
        TextView tvConsumeTime;
        @BindView(R2.id.tv_consume_type)
        TextView tvConsumeType;
        @BindView(R2.id.tv_consume_detail)
        TextView tvConsumeDetail;

        ConsumeViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class RewardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_reward_time)
        TextView tvRewardTime;
        @BindView(R2.id.tv_reward_type)
        TextView tvRewardType;
        @BindView(R2.id.tv_reward_tech_name)
        TextView tvRewardTechName;
        @BindView(R2.id.tv_reward_tech_no)
        TextView tvRewardTechNo;
        @BindView(R2.id.ll_reward_tech)
        LinearLayout llRewardTech;
        @BindView(R2.id.tv_reward_detail)
        TextView tvRewardDetail;

        RewardViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class VisitorViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.tv_visitor_time)
        TextView tvVisitorTime;
        @BindView(R2.id.tv_visitor_type)
        TextView tvVisitorType;
        @BindView(R2.id.tv_visitor_tech_name)
        TextView tvVisitorTechName;
        @BindView(R2.id.tv_visitor_tech_no)
        TextView tvVisitorTechNo;
        @BindView(R2.id.ll_visitor_tech)
        LinearLayout llVisitorTech;

        VisitorViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
