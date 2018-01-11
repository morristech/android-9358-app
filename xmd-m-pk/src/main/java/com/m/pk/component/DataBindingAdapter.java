package com.m.pk.component;

import android.databinding.BindingAdapter;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.m.pk.R;
import com.m.pk.bean.PKDetailListBean;
import com.m.pk.httprequest.RequestConstant;
import com.xmd.app.utils.ResourceUtils;

/**
 * Created by Lhj on 18-1-5.
 */

public class DataBindingAdapter {

    @BindingAdapter({"imageUrl"})
    public static void loadImageFromUrl(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(url)
                .placeholder(R.drawable.icon22)
                .into(view);
    }

    @BindingAdapter({"rankingImage"})
    public static void setImageSrc(ImageView imageView, PKDetailListBean bean) {
        if (bean.isTeam()) {
            switch (bean.getPosition()) {
                case 0:
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_01));
                    break;
                case 1:
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_02));
                    break;
                case 2:
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_03));
                    break;
                default:
                    imageView.setVisibility(View.GONE);
            }
        } else {
            if (bean.getPosition() == bean.getTeamSize()) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_01));
            } else if (bean.getPosition() == bean.getTeamSize() + 1) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_02));
            } else if (bean.getPosition() == bean.getTeamSize() + 2) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageDrawable(ResourceUtils.getDrawable(R.drawable.icon_nub_03));
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

    }

    @BindingAdapter({"rankingNumber"})
    public static void setRankingText(TextView rankNum, PKDetailListBean bean) {
        if (bean.isTeam()) {
            if (bean.getPosition() < 3) {
                rankNum.setVisibility(View.GONE);
            } else {
                rankNum.setVisibility(View.VISIBLE);
                rankNum.setText(String.valueOf((bean.getPosition() + 1)));
            }
        } else {
            if (bean.getPosition() >= bean.getTeamSize() + 3) {
                rankNum.setVisibility(View.VISIBLE);
                rankNum.setText(String.valueOf((bean.getPosition() - (bean.getTeamSize() - 1))));
            } else {
                rankNum.setVisibility(View.GONE);
            }
        }
    }

    @BindingAdapter({"memberNumber"})
    public static void setMemberNumber(TextView tvMember, PKDetailListBean bean) {
        if (bean.getSortType().equals(RequestConstant.KEY_SORT_BY_CUSTOMER)) {
            tvMember.setText(String.format("%s人", bean.getCustomerStat()));
        } else if (bean.getSortType().equals(RequestConstant.KEY_SORT_BY_SALE)) {
            tvMember.setText(String.format("%1.2f元", bean.getSaleStat() / 100f));
        } else if (bean.getSortType().equals(RequestConstant.KEY_SORT_BY_COUPON)) {
            tvMember.setText(String.format("%s个", bean.getCouponStat()));
        } else if (bean.getSortType().equals(RequestConstant.KEY_SORT_BY_PANIC)) {
            tvMember.setText(String.format("%s个", bean.getPaidServiceItemCount()));
        } else {
            tvMember.setText(String.format("%s个", bean.getCommentStat()));
        }
    }

}
