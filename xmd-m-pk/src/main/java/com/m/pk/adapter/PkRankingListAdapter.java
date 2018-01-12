package com.m.pk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.m.pk.BR;
import com.m.pk.R;
import com.m.pk.bean.PKDetailListBean;

import java.util.List;

/**
 * Created by Lhj on 18-1-9.
 */

public class PkRankingListAdapter extends RecyclerView.Adapter<BindingViewHolder> {

    private static final byte PK_RANKING_TYPE = 1;
    private static final byte BOTTOM_TYPE = 3;

    private Context mContext;
    private List<PKDetailListBean> mData;
    private TeamFilterListener mFilterListener;
    private String mCurrentTeamFilter;
    private LayoutInflater mInflater;

    public PkRankingListAdapter(Context context, List<PKDetailListBean> data) {
        this.mContext = context;
        this.mData = data;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<PKDetailListBean> data, String teamFilter) {
        this.mData = data;
        this.mCurrentTeamFilter = teamFilter;
        notifyDataSetChanged();
    }

    public void setTeamFilter(TeamFilterListener listener) {
        this.mFilterListener = listener;
    }

    @Override
    public BindingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding;
        switch (viewType) {
            case PK_RANKING_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_pk_ranking_team, parent, false);
                break;
            case BOTTOM_TYPE:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_list_bottom, parent, false);
                break;
            default:
                binding = DataBindingUtil.inflate(mInflater, R.layout.item_list_bottom, parent, false);
                break;
        }
        return new BindingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(BindingViewHolder holder, int position) {
        if (position != mData.size()) {
            final PKDetailListBean rankingBean = mData.get(position);
            holder.getBinding().setVariable(BR.DetailBean, rankingBean);
            holder.getBinding().executePendingBindings();
            TextView tvSort = (TextView) holder.getBinding().getRoot().findViewById(R.id.tv_sort_team_filter);
            final LinearLayout llTeamSort = (LinearLayout) holder.getBinding().getRoot().findViewById(R.id.ll_team_filter);
            tvSort.setText(mCurrentTeamFilter);
            llTeamSort.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mFilterListener != null){
                        mFilterListener.filterTeam(llTeamSort);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mData.size()) {
            return BOTTOM_TYPE;
        } else {
            return PK_RANKING_TYPE;
        }
    }

    public interface TeamFilterListener {
        void filterTeam(View view);
    }
}
