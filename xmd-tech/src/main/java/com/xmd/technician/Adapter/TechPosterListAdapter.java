package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.bean.PosterBean;
import com.xmd.technician.common.Utils;
import com.xmd.technician.widget.RoundImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-6-22.
 */

public class TechPosterListAdapter extends RecyclerView.Adapter {

    private final int POSTER_TYPE_FLOWER = 1; //花型海报
    private final int POSTER_TYPE_CIRCULAR = 2;//圆型海报
    private final int POSTER_TYPE_SQUARE = 3;//方型海报
    private final int POSTER_TYPE_BLUE = 4; // 蓝色
    private final int POSTER_TYPE_EARNEST = 5; //真认真
    private List<PosterBean> mPosters;
    private Context mContext;
    private PosterCallBack mCallBack;

    public TechPosterListAdapter(Context context, List<PosterBean> posters) {
        this.mContext = context;
        this.mPosters = posters;
    }

    public void setData(List<PosterBean> posters) {
        this.mPosters = posters;
        notifyDataSetChanged();
    }

    public void setPosterCallBackListener(PosterCallBack callBack) {
        this.mCallBack = callBack;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case POSTER_TYPE_FLOWER:
                View flowerType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_flower_item, parent, false);
                return new PoserViewHolder(flowerType);
            case POSTER_TYPE_CIRCULAR:
                View circularType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_circular_item, parent, false);
                return new PoserViewHolder(circularType);
            case POSTER_TYPE_SQUARE:
                View squareType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_square_item, parent, false);
                return new PoserViewHolder(squareType);
            case POSTER_TYPE_BLUE:
                View blueType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_blue_item, parent, false);
                return new PoserViewHolder(blueType);
            case POSTER_TYPE_EARNEST:
                View earnestType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_earnest_item, parent, false);
                return new PoserViewHolder(earnestType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PoserViewHolder viewHolder = (PoserViewHolder) holder;
        PosterBean bean = mPosters.get(position);
        viewHolder.tvPosterClubName.setText(bean.name);
        if (Utils.isNotEmpty(bean.name)) {
            if (bean.style.equals(Constant.TECH_POSTER_TYPE_BLUE) || bean.style.equals(Constant.TECH_POSTER_TYPE_EARNEST)) {
                viewHolder.tvPosterTechName.setText(bean.name);
                viewHolder.tvPosterTechName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechNo.setText(TextUtils.isEmpty(bean.techNo) ? "" : bean.techNo);
            } else {
                String nameMessage = "";
                if (Utils.isNotEmpty(bean.techNo)) {
                    nameMessage = String.format("%s/%s", bean.name, bean.techNo);
                } else {
                    nameMessage = bean.name;
                }
                viewHolder.tvPosterClubName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechName.setText(nameMessage);
            }
        } else {
            if (Utils.isNotEmpty(bean.techNo)) {
                viewHolder.tvPosterClubName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechName.setText(bean.techNo);
            } else {
                viewHolder.tvPosterClubName.setVisibility(View.GONE);
                viewHolder.tvPosterTechName.setVisibility(View.GONE);
            }
        }

        if (Utils.isNotEmpty(bean.title)) {
            String title = bean.title;
            if (bean.style.equals(Constant.TECH_POSTER_TYPE_SQUARE)) {
                viewHolder.tvPosterPrimaryTitle.setText(String.format("<<%s>>", title));
            } else if (bean.style.equals(Constant.TECH_POSTER_TYPE_FLOWER)) {
                viewHolder.tvPosterPrimaryTitle.setText(Utils.stringFormat(title));
            } else {
                viewHolder.tvPosterPrimaryTitle.setText(title);
            }
        } else {
            viewHolder.tvPosterPrimaryTitle.setText("");
        }

        if (Utils.isNotEmpty(bean.subTitle)) {
            viewHolder.tvPosterMinorTitle.setText(bean.subTitle);
        } else {
            viewHolder.tvPosterMinorTitle.setText("");
        }

        if (bean.style.equals(Constant.TECH_POSTER_TYPE_BLUE)) {
            viewHolder.tvPosterClubName.setText(Utils.isNotEmpty(bean.clubName) ? String.format("--%s--", bean.clubName) : "");
        } else {
            viewHolder.tvPosterClubName.setText(TextUtils.isEmpty(bean.clubName) ? "" : bean.clubName);
        }

        Glide.with(mContext).load(bean.imageUrl).into(viewHolder.imgPosterTechPhoto);
        Glide.with(mContext).load(bean.qrCodeUrl).into(viewHolder.imgPosterQrCode);

        if (mCallBack != null) {
            viewHolder.itemView.setOnClickListener(v -> mCallBack.itemClicked(bean));
            viewHolder.imgPosterDelete.setOnClickListener(v -> mCallBack.deleteClicked(bean));
            viewHolder.llPosterEdit.setOnClickListener(v -> mCallBack.editClicked(bean));
            viewHolder.llPosterShare.setOnClickListener(v -> mCallBack.shareClicked(bean));
        }
    }

    @Override
    public int getItemCount() {
        return mPosters.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mPosters == null || mPosters.size() == 0) {
            return POSTER_TYPE_FLOWER;
        }
        String style = mPosters.get(position).style;
        switch (style) {
            case Constant.TECH_POSTER_TYPE_CIRCULAR:
                return POSTER_TYPE_CIRCULAR;
            case Constant.TECH_POSTER_TYPE_SQUARE:
                return POSTER_TYPE_SQUARE;
            case Constant.TECH_POSTER_TYPE_BLUE:
                return POSTER_TYPE_BLUE;
            case Constant.TECH_POSTER_TYPE_EARNEST:
                return POSTER_TYPE_EARNEST;
            default:
                return POSTER_TYPE_FLOWER;
        }
    }

    public interface PosterCallBack {
        void itemClicked(PosterBean bean);

        void deleteClicked(PosterBean bean);

        void editClicked(PosterBean bean);

        void shareClicked(PosterBean bean);
    }

    static class PoserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_poster_tech_photo)
        RoundImageView imgPosterTechPhoto;
        @BindView(R.id.tv_poster_tech_name)
        TextView tvPosterTechName;
        @BindView(R.id.img_poster_qr_code)
        ImageView imgPosterQrCode;
        @BindView(R.id.tv_poster_primary_title)
        TextView tvPosterPrimaryTitle;
        @BindView(R.id.tv_poster_minor_title)
        TextView tvPosterMinorTitle;
        @BindView(R.id.tv_poster_club_name)
        TextView tvPosterClubName;
        @BindView(R.id.img_poster_delete)
        ImageView imgPosterDelete;
        @BindView(R.id.ll_poster_edit)
        LinearLayout llPosterEdit;
        @BindView(R.id.ll_poster_share)
        LinearLayout llPosterShare;
        @BindView(R.id.tv_poster_tech_no)
        TextView tvPosterTechNo;

        PoserViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
