package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.RankingListBean;
import com.xmd.technician.widget.CircleImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-4-22.
 */

public class PKRankingAdapter extends RecyclerView.Adapter {

    private static final byte REGISTER_TYPE = 1;
    private static final byte SALE_TYPE = 2;
    private static final byte SERVICE_TYPE = 3;
    private static final byte PAID_TYPE = 4;

    private Context mContext;
    private List<RankingListBean> mData;

    public PKRankingAdapter(Context context, List<RankingListBean> data) {
        this.mContext = context;
        this.mData = data;
        notifyDataSetChanged();
    }


    @Override
    public int getItemViewType(int position) {
        RankingListBean bean = mData.get(position);
        if (bean.categoryId.equals(Constant.KEY_CATEGORY_CUSTOMER_TYPE)) {
            return REGISTER_TYPE;
        } else if (bean.categoryId.equals(Constant.KEY_CATEGORY_SAIL_TYPE)) {
            return SALE_TYPE;
        }else if(bean.categoryId.equals(Constant.KEY_CATEGORY_PAID_TYPE)){
            return PAID_TYPE;
        } else {
            return SERVICE_TYPE;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case REGISTER_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_ranking_register_item, parent, false);
                break;
            case SALE_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_ranking_sale_item, parent, false);
                break;
            case SERVICE_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_ranking_service_item, parent, false);
                break;
            case PAID_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_ranking_paid_item, parent, false);
                break;
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_ranking_register_item, parent, false);
                break;
        }
        return new PKRankingTeamItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PKRankingTeamItemViewHolder teamViewHolder = (PKRankingTeamItemViewHolder) holder;
        RankingListBean bean = mData.get(position);
        teamViewHolder.tvPkActiveTeamName.setText(bean.name);
        Glide.with(mContext).load(bean.avatarUrl).into(teamViewHolder.pkActiveUserHead);
        if (bean.categoryId.equals(Constant.KEY_CATEGORY_CUSTOMER_TYPE)) {
            teamViewHolder.tvPkActiveTeamMember.setText(String.format("%s人", bean.statValue));
        } else if (bean.categoryId.equals(Constant.KEY_CATEGORY_SAIL_TYPE)) {
            teamViewHolder.tvPkActiveTeamMember.setText(String.format("%1.2f元", bean.statValue / 100f));
        } else {
            teamViewHolder.tvPkActiveTeamMember.setText(String.format("%s个", bean.statValue));
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    static class PKRankingTeamItemViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.pk_active_user_head)
        CircleImageView pkActiveUserHead;
        @Bind(R.id.tv_pk_active_team_name)
        TextView tvPkActiveTeamName;
        @Bind(R.id.tv_pk_active_team_member)
        TextView tvPkActiveTeamMember;
        @Bind(R.id.ll_star_user)
        LinearLayout llStarUser;

        PKRankingTeamItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
