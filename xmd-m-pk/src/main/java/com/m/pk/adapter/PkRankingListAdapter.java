package com.m.pk.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private int mTeamNumber;
    private String mCurrentType;
    private TeamFilterListener mFilterListener;
    private String mCurrentTeamFilter;
    private LayoutInflater mInflater;

    public PkRankingListAdapter(Context context, List<PKDetailListBean> data, String currentType) {
        this.mContext = context;
        this.mCurrentType = currentType;
        this.mData = data;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(List<PKDetailListBean> data, int teamNumber, String teamFilter) {
        this.mData = data;
        this.mTeamNumber = teamNumber;
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
       if(position == mData.size()){

       }else {
           final PKDetailListBean rankingBean = mData.get(position);
           holder.getBinding().setVariable(BR.DetailBean, rankingBean);
           holder.getBinding().executePendingBindings();
       }
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == mData.size()){
            return BOTTOM_TYPE;
        }else {
            return PK_RANKING_TYPE;
        }
    }

    public interface TeamFilterListener {
        void filterTeam(View view);
    }
}
