package com.m.pk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.m.pk.R;
import com.m.pk.bean.TechRankingBean;
import com.xmd.app.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 18-1-8.
 */

public class TechCommonRankingDetailAdapter extends RecyclerView.Adapter<BindingViewHolder> {


    private static final int ITEM_VIEW_TYPE_RANKING = 1;
    private static final int ITEM_VIEW_TYPE_BOTTOM = 2;

    private List<TechRankingBean> mData;
    private LayoutInflater mInflater;
    private boolean mHasMore;

    public void setRankingListDetailData(List<TechRankingBean> data, boolean hasMore) {
        this.mData = data;
        mHasMore = hasMore;
        notifyDataSetChanged();
    }

    public TechCommonRankingDetailAdapter(Context context) {
        //  mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mData = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if (viewType == ITEM_VIEW_TYPE_BOTTOM) {
            binding = DataBindingUtil.inflate(mInflater, R.layout.item_list_bottom, parent, false);
        } else {
            binding = DataBindingUtil.inflate(mInflater, R.layout.item_tech_common_ranking_list_detail, parent, false);
        }
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        if (position == mData.size()) {
            TextView tvFooter = (TextView) holder.getBinding().getRoot().getRootView().findViewById(R.id.item_footer);
            if(mHasMore){
                tvFooter.setText(ResourceUtils.getString(R.string.all_data_load_more));
            }else{
                tvFooter.setText(ResourceUtils.getString(R.string.all_data_load_finish));
            }
        } else {
            final TechRankingBean rankingBean = mData.get(position);
            holder.getBinding().setVariable(com.m.pk.BR.rankingBean, rankingBean);
            holder.getBinding().setVariable(com.m.pk.BR.detailAdapter, this);
            ImageView imgRankingNumber = (ImageView) holder.getBinding().getRoot().getRootView().findViewById(R.id.img_ranking_number);
            TextView textViewNumber = (TextView) holder.getBinding().getRoot().getRootView().findViewById(R.id.text_ranking_number);
            if (position == 0) {
                imgRankingNumber.setVisibility(View.VISIBLE);
                textViewNumber.setVisibility(View.GONE);
                imgRankingNumber.setImageResource(R.drawable.icon_nub_01);
            } else if (position == 1) {
                imgRankingNumber.setVisibility(View.VISIBLE);
                textViewNumber.setVisibility(View.GONE);
                imgRankingNumber.setImageResource(R.drawable.icon_nub_02);
            } else if (position == 2) {
                imgRankingNumber.setVisibility(View.VISIBLE);
                textViewNumber.setVisibility(View.GONE);
                imgRankingNumber.setImageResource(R.drawable.icon_nub_03);
            } else {
                imgRankingNumber.setVisibility(View.GONE);
                textViewNumber.setVisibility(View.VISIBLE);
                textViewNumber.setText(String.valueOf(position));
            }
        }
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size()) {
            return ITEM_VIEW_TYPE_BOTTOM;
        } else {
            return ITEM_VIEW_TYPE_RANKING;
        }
    }
}
