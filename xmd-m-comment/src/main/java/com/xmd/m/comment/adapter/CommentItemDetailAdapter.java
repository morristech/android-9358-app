package com.xmd.m.comment.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.m.R;
import com.xmd.m.comment.bean.CommentRateBean;

import java.util.List;

/**
 * Created by Lhj on 17-7-6.
 */

public class CommentItemDetailAdapter extends RecyclerView.Adapter {

    private List<CommentRateBean> mData;
    private Context mContext;

    public CommentItemDetailAdapter(Context context, List<CommentRateBean> data) {
        this.mContext = context;
        this.mData = data;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View commentView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_comment_item_detail, parent, false);
        return new CommentDetailItemViewHolder(commentView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        CommentDetailItemViewHolder viewHolder = (CommentDetailItemViewHolder) holder;
        CommentRateBean bean = mData.get(position);
        viewHolder.tvType.setText(bean.commentTagName);
        fillRate(bean.commentRate, viewHolder.tvCommentStar, viewHolder.imgCommentStar);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void fillRate(int rate, TextView tv, ImageView iv) {
        int result = rate / 20;
        tv.setText(String.valueOf(result));
        switch (result) {
            case 1:
                iv.setImageResource(R.drawable.ic_stars01);
                break;
            case 2:
                iv.setImageResource(R.drawable.ic_stars02);
                break;
            case 3:
                iv.setImageResource(R.drawable.ic_stars03);
                break;
            case 4:
                iv.setImageResource(R.drawable.ic_stars04);
                break;
            case 5:
                iv.setImageResource(R.drawable.ic_stars05);
                break;
        }

    }

    class CommentDetailItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvType;
        ImageView imgCommentStar;
        TextView tvCommentStar;

        public CommentDetailItemViewHolder(View itemView) {
            super(itemView);
            tvType = (TextView) itemView.findViewById(R.id.bad_comment_type);
            imgCommentStar = (ImageView) itemView.findViewById(R.id.img_comment_star);
            tvCommentStar = (TextView) itemView.findViewById(R.id.tv_comment_star);
        }
    }
}
