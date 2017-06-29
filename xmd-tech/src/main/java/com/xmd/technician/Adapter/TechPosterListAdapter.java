package com.xmd.technician.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Lhj on 17-6-22.
 */

public class TechPosterListAdapter extends RecyclerView.Adapter {

    private List<PosterBean> mPosters;
    private Context mContext;
    private PosterCallBack mCallBack;

    private final int poster_type_flower = 1; //花型海报
    private final int poster_type_circular = 2;//圆型海报
    private final int poster_type_square = 3;//方型海报

    public TechPosterListAdapter(Context context, List<PosterBean> posters) {
        this.mContext = context;
        this.mPosters = posters;
    }

    public void setData(List<PosterBean> posters) {
        this.mPosters = posters;
        notifyDataSetChanged();
    }

    public interface PosterCallBack {
        void itemClicked(PosterBean bean);

        void deleteClicked(PosterBean bean);

        void editClicked(PosterBean bean);

        void shareClicked(PosterBean bean);
    }

    public void setPosterCallBackListener(PosterCallBack callBack) {
        this.mCallBack = callBack;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case poster_type_flower:
                View flowerType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_flower_item, parent, false);
                return new PoserViewHolder(flowerType);
            case poster_type_circular:
                View circularType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_circular_item, parent, false);
                return new PoserViewHolder(circularType);
            case poster_type_square:
                View squareType = LayoutInflater.from(mContext).inflate(R.layout.layout_tech_poster_square_item, parent, false);
                return new PoserViewHolder(squareType);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PoserViewHolder viewHolder = (PoserViewHolder) holder;
        PosterBean bean = mPosters.get(position);
        viewHolder.tvPosterClubName.setText(bean.name);
        if (Utils.isNotEmpty(bean.name)) {
            String nameMessage = "";
            if (Utils.isNotEmpty(bean.techNo)) {
                nameMessage = String.format("%s/%s", bean.name, bean.techNo);
            } else {
                nameMessage = bean.name;
            }
            viewHolder.tvPosterClubName.setVisibility(View.VISIBLE);
            viewHolder.tvPosterTechName.setText(nameMessage);
        } else {
            if (Utils.isNotEmpty(bean.techNo)) {
                viewHolder.tvPosterClubName.setVisibility(View.VISIBLE);
                viewHolder.tvPosterTechName.setText(bean.techNo);
            } else {
                viewHolder.tvPosterClubName.setVisibility(View.GONE);
                viewHolder.tvPosterClubName.setText("");
            }
        }

        if (Utils.isNotEmpty(bean.title)) {
            String title = bean.title;
            if(bean.style.equals(Constant.TECH_POSTER_TYPE_SQUARE)){
                viewHolder.tvPosterPrimaryTitle.setText(String.format("<<%s>>",title));
            }else if(bean.style.equals(Constant.TECH_POSTER_TYPE_FLOWER)){
                viewHolder.tvPosterPrimaryTitle.setText(Utils.stringFormat(title));
            }else{
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

        if (Utils.isNotEmpty(bean.clubName)) {
            viewHolder.tvPosterClubName.setText(bean.clubName);
        } else {
            viewHolder.tvPosterClubName.setText("");
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
        if (Utils.isEmpty(mPosters.get(position).style)) {
            return poster_type_flower;
        }
        if (mPosters.get(position).style.equals(Constant.TECH_POSTER_TYPE_CIRCULAR)) {
            return poster_type_circular;
        } else if (mPosters.get(position).style.equals(Constant.TECH_POSTER_TYPE_SQUARE)) {
            return poster_type_square;
        } else {
            return poster_type_flower;
        }
    }

    static class PoserViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.img_poster_tech_photo)
        RoundImageView imgPosterTechPhoto;
        @Bind(R.id.tv_poster_tech_name)
        TextView tvPosterTechName;
        @Bind(R.id.img_poster_qr_code)
        ImageView imgPosterQrCode;
        @Bind(R.id.tv_poster_primary_title)
        TextView tvPosterPrimaryTitle;
        @Bind(R.id.tv_poster_minor_title)
        TextView tvPosterMinorTitle;
        @Bind(R.id.tv_poster_club_name)
        TextView tvPosterClubName;
        @Bind(R.id.img_poster_delete)
        ImageView imgPosterDelete;
        @Bind(R.id.ll_poster_edit)
        LinearLayout llPosterEdit;
        @Bind(R.id.ll_poster_share)
        LinearLayout llPosterShare;

        PoserViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
