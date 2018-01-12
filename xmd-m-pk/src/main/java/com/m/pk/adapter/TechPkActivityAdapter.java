package com.m.pk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.m.pk.R;
import com.m.pk.bean.ActivityRankingBean;
import com.xmd.app.utils.ResourceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lhj on 18-1-4.
 */

public class TechPkActivityAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private static final int ITEM_VIEW_TYPE_PK = 1;
    private static final int ITEM_VIEW_TYPE_BOTTOM = 2;

    private LayoutInflater mInflater;
    private List<ActivityRankingBean> mActivityList;
    private OnItemClickListener mListener;
    private Context mContext;
    private boolean hasMore;


    public void setListData(List<ActivityRankingBean> data, boolean hasMore) {
        this.mActivityList = data;
        this.hasMore = hasMore;
        notifyDataSetChanged();
    }

    public TechPkActivityAdapter(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mActivityList = new ArrayList<>();
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        if (viewType == ITEM_VIEW_TYPE_PK) {
            binding = DataBindingUtil.inflate(mInflater, R.layout.item_technician_ranking_active, parent, false);
        } else {
            binding = DataBindingUtil.inflate(mInflater, R.layout.item_list_bottom, parent, false);
        }
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        if (position == mActivityList.size()) {
            TextView tvFooter = (TextView) holder.getBinding().getRoot().getRootView().findViewById(R.id.item_footer);
            if(mActivityList.size() == 0){
                tvFooter.setVisibility(View.GONE);
            }else{
                tvFooter.setVisibility(View.VISIBLE);
            }
            if(hasMore){
                tvFooter.setText(ResourceUtils.getString(R.string.all_data_load_more));
            }else{
                tvFooter.setText(ResourceUtils.getString(R.string.all_data_load_finish));
            }
        } else {
            final ActivityRankingBean rankingBean = mActivityList.get(position);
            holder.getBinding().setVariable(com.m.pk.BR.item, rankingBean);
            holder.getBinding().executePendingBindings();
            if (rankingBean.getRankingList() != null) {
                PkTeamRankingAdapter adapter = null;
                if (TextUtils.isEmpty(rankingBean.getCategoryId())) {
                    adapter = new PkTeamRankingAdapter(mContext, rankingBean.getRankingList(), "");
                } else {
                    adapter = new PkTeamRankingAdapter(mContext, rankingBean.getRankingList(), rankingBean.getCategoryId());
                }
                ((RecyclerView) (holder.getBinding().getRoot().findViewById(R.id.teams_list))).setAdapter(adapter);
                ((RecyclerView) (holder.getBinding().getRoot().findViewById(R.id.teams_list))).setLayoutManager(new GridLayoutManager(mContext, 3));
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onActivityClick(rankingBean);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mActivityList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mActivityList.size()) {
            return ITEM_VIEW_TYPE_BOTTOM;
        } else {
            return ITEM_VIEW_TYPE_PK;
        }
    }

    public interface OnItemClickListener {
        void onActivityClick(ActivityRankingBean bean);
    }

    public void setOnItemClickedListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
