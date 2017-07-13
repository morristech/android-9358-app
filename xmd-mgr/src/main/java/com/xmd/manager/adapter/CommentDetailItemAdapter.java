package com.xmd.manager.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.R;
import com.xmd.manager.beans.BadCommentRateListBean;
import com.xmd.manager.beans.CommentRateListBean;
import com.xmd.manager.common.ResourceUtils;

import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-4-19.
 */

public class CommentDetailItemAdapter<T> extends RecyclerView.Adapter {
    private List<T> mData;
    private Context mContext;

    private static final int TYPE_COMMENT_ITEM = 0;
    private static final int TYPE_BAD_COMMENT_ITEM = 1;

    public CommentDetailItemAdapter(Context context, List<T> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        switch (viewType) {
            case TYPE_COMMENT_ITEM:
                View commentView = LayoutInflater.from(mContext).inflate(R.layout.layout_comment_detail_item, parent, false);
                return new CommentDetailItemViewHolder(commentView);
            case TYPE_BAD_COMMENT_ITEM:
                View badCommentView = LayoutInflater.from(mContext).inflate(R.layout.layout_bad_comment_item, parent, false);
                return new BadCommentDetailItemViewHolder(badCommentView);
            default:
                return null;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if (mData.get(position) instanceof CommentRateListBean) {
            return TYPE_COMMENT_ITEM;
        } else if (mData.get(position) instanceof BadCommentRateListBean) {
            return TYPE_BAD_COMMENT_ITEM;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CommentDetailItemViewHolder) {
            CommentDetailItemViewHolder viewHolder = (CommentDetailItemViewHolder) holder;
            CommentRateListBean bean = (CommentRateListBean) mData.get(position);
            fillRate(bean.commentRate, viewHolder.tvAttitudeRate, viewHolder.ivAttitudeRate);
            viewHolder.tvRate.setText(bean.commentTagName);
        } else if (holder instanceof BadCommentDetailItemViewHolder) {
            BadCommentDetailItemViewHolder viewHolder = (BadCommentDetailItemViewHolder) holder;
            BadCommentRateListBean bean = (BadCommentRateListBean) mData.get(position);
            viewHolder.badCommentType.setText(bean.commentTagName);
            viewHolder.badCommentFitment.setText(String.valueOf(bean.commentRate / 20));
        }

    }

    private void fillRate(int rate, TextView tv, ImageView iv) {
        int result = rate / 20;
        tv.setText(String.format("%d’", result));
        switch (result) {
            case 5:
            case 4:
                tv.setTextColor(ResourceUtils.getColor(R.color.customer_comment_score_text_color));
                iv.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_manyi));
                break;
            case 3:
                tv.setTextColor(ResourceUtils.getColor(R.color.colorBody));
                iv.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_yiban));
                break;
            default:
                tv.setTextColor(ResourceUtils.getColor(R.color.colorRemark));
                iv.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_bumanyi));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class CommentDetailItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_rate)
        TextView tvRate;
        @BindView(R.id.tv_attitude_rate)
        TextView tvAttitudeRate;
        @BindView(R.id.iv_attitude_rate)
        ImageView ivAttitudeRate;

        CommentDetailItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class BadCommentDetailItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bad_comment_type)
        TextView badCommentType;
        @BindView(R.id.bad_comment_fitment)
        TextView badCommentFitment;

        BadCommentDetailItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
