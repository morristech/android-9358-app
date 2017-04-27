package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.R;
import com.xmd.technician.bean.PKDetailListBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.widget.RoundImageView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by sdcm on 17-3-21.
 */

public class PKRankingDetailAdapter extends RecyclerView.Adapter {

    private static final byte TEAM_TYPE = 1;
    private static final byte PERSONAL_TYPE = 2;
    private static final byte BOTTOM_TYPE = 3;

    private Context mContext;
    private List<PKDetailListBean> mData;
    private int mTeamNumber;
    private String mCurrentType;

    public PKRankingDetailAdapter(Context context, List<PKDetailListBean> data, String currentType) {
        this.mContext = context;
        this.mCurrentType = currentType;
        this.mData = data;
    }

    public void setData(List<PKDetailListBean> data, int teamNumber) {
        this.mData = data;
        this.mTeamNumber = teamNumber;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TEAM_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_detail_team_item, parent, false);
                return new PkRankingDetailViewHolder(view);
            case PERSONAL_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_pk_detail_personal_item, parent, false);
                return new PkRankingDetailViewHolder(view);
            case BOTTOM_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_main_bottom, parent, false);
                return new BottomViewHolder(view);
            default:
                view = LayoutInflater.from(mContext).inflate(R.layout.layout_main_bottom, parent, false);
                return new BottomViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof PkRankingDetailViewHolder){
            PkRankingDetailViewHolder detailViewHolder = (PkRankingDetailViewHolder) holder;
            PKDetailListBean bean = mData.get(position);

            if (position == 0 || position == mTeamNumber) {
                detailViewHolder.teamRankingTitle.setVisibility(View.VISIBLE);
            } else {
                detailViewHolder.teamRankingTitle.setVisibility(View.GONE);
            }
            if (bean.isTeam) {
                detailViewHolder.textRankingNumber.setText(String.valueOf(position + 1));
                detailViewHolder.tvRankingMemberNumber.setText(String.format("%s人", bean.memberCount));
                detailViewHolder.tvRankingName.setText(bean.teamName);
                detailViewHolder.tvRankingMember.setText(String.format("%s队员", bean.memberCount));
                if (detailViewHolder.textRankingNumber.getText().toString().equals("1")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_01).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else if (detailViewHolder.textRankingNumber.getText().toString().equals("2")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_02).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else if (detailViewHolder.textRankingNumber.getText().toString().equals("3")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_03).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else {
                    detailViewHolder.textRankingNumber.setVisibility(View.VISIBLE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.GONE);
                }
            } else {
                if (bean.leader.equals("Y")) {
                    detailViewHolder.tvCaptainMark.setVisibility(View.VISIBLE);
                } else {
                    detailViewHolder.tvCaptainMark.setVisibility(View.GONE);
                }
                detailViewHolder.tvRankingName.setText(Utils.StrSubstring(3, bean.teamMember, true));
                detailViewHolder.tvRankingMember.setText(bean.teamName);
                if (Utils.isNotEmpty(bean.serialNo)) {
                    detailViewHolder.tvRankingSerial.setVisibility(View.VISIBLE);
                    detailViewHolder.tvRankingSerial.setText(String.format("[ %s ]", bean.serialNo));
                } else {
                    detailViewHolder.tvRankingSerial.setVisibility(View.GONE);
                }
                detailViewHolder.textRankingNumber.setText(String.valueOf((position - mTeamNumber) + 1));
                if (detailViewHolder.textRankingNumber.getText().toString().equals("1")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_01).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else if (detailViewHolder.textRankingNumber.getText().toString().equals("2")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_02).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else if (detailViewHolder.textRankingNumber.getText().toString().equals("3")) {
                    Glide.with(mContext).load(R.drawable.icon_nub_03).into(detailViewHolder.imgRankingNumber);
                    detailViewHolder.textRankingNumber.setVisibility(View.GONE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.VISIBLE);
                } else {
                    detailViewHolder.textRankingNumber.setVisibility(View.VISIBLE);
                    detailViewHolder.imgRankingNumber.setVisibility(View.GONE);
                }
            }
            if (mCurrentType.equals(RequestConstant.KEY_SORT_BY_CUSTOMER)) {
                detailViewHolder.tvRankingMemberNumber.setText(String.format("%s人", bean.customerStat));
            } else if (mCurrentType.equals(RequestConstant.KEY_SORT_BY_SALE)) {
                detailViewHolder.tvRankingMemberNumber.setText(String.format("%1.2f元", bean.saleStat/100f));
            }else if(mCurrentType.equals(RequestConstant.KEY_SORT_BY_COUPON)){
                detailViewHolder.tvRankingMemberNumber.setText(String.format("%s个", bean.couponStat));
            } else {
                detailViewHolder.tvRankingMemberNumber.setText(String.format("%s个", bean.commentStat));
            }
            Glide.with(mContext).load(bean.avatarUrl).into(detailViewHolder.imgRankingHead);
        }else if(holder instanceof BottomViewHolder){

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
        } else if (mData.get(position).isTeam) {
            return TEAM_TYPE;
        } else {
            return PERSONAL_TYPE;
        }
    }

    static class PkRankingDetailViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.team_ranking_title)
        TextView teamRankingTitle;
        @Bind(R.id.img_ranking_number)
        ImageView imgRankingNumber;
        @Bind(R.id.text_ranking_number)
        TextView textRankingNumber;
        @Bind(R.id.img_ranking_head)
        RoundImageView imgRankingHead;
        @Bind(R.id.tv_captain_mark)
        TextView tvCaptainMark;
        @Bind(R.id.tv_ranking_name)
        TextView tvRankingName;
        @Bind(R.id.tv_ranking_member)
        TextView tvRankingMember;
        @Bind(R.id.tv_ranking_member_number)
        TextView tvRankingMemberNumber;
        @Bind(R.id.tv_ranking_serialNo)
        TextView tvRankingSerial;

        PkRankingDetailViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class BottomViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_bottom_text)
        TextView tvBottomText;

        BottomViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}