package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.common.Utils;
import com.xmd.technician.model.LoginTechnician;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-6-26.
 */

public class TechPosterDialog extends Dialog {
    @BindView(R.id.rl_view)
    RelativeLayout rlView;
    @BindView(R.id.img_poster_tech_photo)
    RoundImageView imgPosterTechPhoto;
    @BindView(R.id.tv_poster_tech_name)
    TextView tvPosterTechName;
    @BindView(R.id.tv_poster_tech_no)
    TextView tvPosterTechNo;
    @BindView(R.id.tv_poster_primary_title)
    TextView tvPosterPrimaryTitle;
    @BindView(R.id.tv_poster_minor_title)
    TextView tvPosterMinorTitle;
    @BindView(R.id.tv_poster_club_name)
    TextView tvPosterClubName;
    @BindView(R.id.img_poster_qr_code)
    ImageView imgPosterQrCode;
    @BindView(R.id.rl_poster_edit_or_save)
    RelativeLayout rlPosterEditOrSave;
    @BindView(R.id.ll_poster_edit_or_share)
    LinearLayout llPosterEditOrShare;
    @BindView(R.id.img_poster_dismiss)
    ImageView imgPosterDismiss;
    @BindView(R.id.rl_poster_save_and_share)
    RelativeLayout rlPosterSaveAndShare;
    @BindView(R.id.tv_poster_save)
    TextView tvPosterSave;
    @BindView(R.id.tv_share_poster)
    TextView tvSharePoster;

    private int style;
    private boolean isSave, isShare;
    private Context mContext;
    private PosterShareOrSaveListener posterListener;

    public TechPosterDialog(Context context, int style, boolean isSave, boolean isShare) {

        super(context, R.style.default_dialog_style);
        this.mContext = context;
        this.style = style;
        this.isSave = isSave;
        this.isShare = isShare;
    }

    public interface PosterShareOrSaveListener {
        void posterSave(View view, View dismiss);

        void posterEdit();

        void posterShare(View view, View dismiss);

    }

    public TechPosterDialog(Context context) {
        this(context, -1);

    }


    public void setPosterListener(PosterShareOrSaveListener posterListener) {
        this.posterListener = posterListener;
    }

    public TechPosterDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected TechPosterDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void setViewDate(String primaryTitle, String minorTitle, String techName, String techNo, String clubName, Bitmap image, String imageUrl, String shareUrl) {

        if (Utils.isNotEmpty(primaryTitle)) {
            if (style == Constant.TECH_POSTER_SQUARE_MODEL) {
                tvPosterPrimaryTitle.setText(String.format("<<%s>>", primaryTitle));
            } else if (style == Constant.TECH_POSTER_FLOWER_MODEL) {
                tvPosterPrimaryTitle.setText(Utils.stringFormat(primaryTitle));
            } else {
                tvPosterPrimaryTitle.setText(primaryTitle);
            }
        } else {
            tvPosterPrimaryTitle.setText("");
        }
        if (Utils.isNotEmpty(minorTitle)) {
            tvPosterMinorTitle.setText(minorTitle);
        } else {
            tvPosterMinorTitle.setText("");
        }
        if (style == Constant.TECH_POSTER_BLUE_MODEL || style == Constant.TECH_POSTER_EARNEST_MODEL) {
            if (Utils.isNotEmpty(techName)) {
                tvPosterTechName.setVisibility(View.VISIBLE);
                tvPosterTechName.setText(techName);
            } else {
                tvPosterTechName.setVisibility(View.GONE);
            }
            tvPosterTechNo.setText(Utils.isNotEmpty(techNo) ? techNo : "");
        } else {
            String techNameAndNo = "";
            if (Utils.isNotEmpty(techName)) {
                if (Utils.isNotEmpty(techNo)) {
                    techNameAndNo = String.format("%s/%s", techName, techNo);
                } else {
                    techNameAndNo = techName;
                }
            } else {
                if (Utils.isNotEmpty(techNo)) {
                    techNameAndNo = techNo;
                } else {
                    techNameAndNo = "";
                }
            }
            if (Utils.isNotEmpty(techNameAndNo)) {
                tvPosterTechName.setVisibility(View.VISIBLE);
                tvPosterTechName.setText(techNameAndNo);
            } else {
                tvPosterTechName.setVisibility(View.GONE);
            }
        }

        if (Utils.isNotEmpty(clubName)) {
            if (style == Constant.TECH_POSTER_BLUE_MODEL) {
                tvPosterClubName.setText(String.format("——— %s ———", clubName));
            } else {
                tvPosterClubName.setText(clubName);
            }
        } else {
            tvPosterClubName.setText("");
        }
        if (image != null) {
            imgPosterTechPhoto.setImageBitmap(image);
        } else {
            Glide.with(mContext).load(imageUrl).into(imgPosterTechPhoto);
        }
        if (Utils.isNotEmpty(shareUrl)) {
            Glide.with(mContext).load(shareUrl).into(imgPosterQrCode);
        } else {
            Glide.with(mContext).load(LoginTechnician.getInstance().getQrCodeUrl()).into(imgPosterQrCode);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (style) {
            case Constant.TECH_POSTER_SQUARE_MODEL:
                setContentView(R.layout.layout_tech_poster_type_square);
                break;
            case Constant.TECH_POSTER_CIRCULAR_MODEL:
                setContentView(R.layout.layout_tech_poster_type_circular);
                break;
            case Constant.TECH_POSTER_FLOWER_MODEL:
                setContentView(R.layout.layout_tech_poster_type_flower);
                break;
            case Constant.TECH_POSTER_BLUE_MODEL:
                setContentView(R.layout.layout_tech_poster_type_blue);
                break;
            case Constant.TECH_POSTER_EARNEST_MODEL:
                setContentView(R.layout.layout_tech_poster_type_earnest);
                break;
        }
        ButterKnife.bind(this);

        if (isSave) {
            if (isShare) {
                rlPosterEditOrSave.setVisibility(View.GONE);
                llPosterEditOrShare.setVisibility(View.GONE);
                rlPosterSaveAndShare.setVisibility(View.VISIBLE);
            } else {
                rlPosterEditOrSave.setVisibility(View.VISIBLE);
                llPosterEditOrShare.setVisibility(View.GONE);
                rlPosterSaveAndShare.setVisibility(View.GONE);
            }

        } else {
            rlPosterEditOrSave.setVisibility(View.GONE);
            rlPosterSaveAndShare.setVisibility(View.GONE);
            llPosterEditOrShare.setVisibility(View.VISIBLE);
        }

    }


    @OnClick({R.id.img_poster_dismiss, R.id.tv_save_poster, R.id.tv_edit_poster, R.id.ll_poster_edit, R.id.ll_poster_share, R.id.tv_poster_save, R.id.tv_share_poster})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_poster_dismiss:
                this.dismiss();
                break;
            case R.id.tv_save_poster:

                if (posterListener != null) {
                    imgPosterDismiss.setVisibility(View.GONE);
                    posterListener.posterSave(rlView, imgPosterDismiss);
                    imgPosterDismiss.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_edit_poster:
                this.dismiss();
                if (posterListener != null) {
                    posterListener.posterEdit();
                }
                break;
            case R.id.ll_poster_edit:
                this.dismiss();
                if (posterListener != null) {
                    posterListener.posterEdit();
                }
                break;
            case R.id.ll_poster_share:
                this.dismiss();
                if (posterListener != null) {
                    posterListener.posterShare(rlView, imgPosterDismiss);
                }
                break;
            case R.id.tv_poster_save:
                if (posterListener != null) {
                    imgPosterDismiss.setVisibility(View.GONE);
                    posterListener.posterSave(rlView, imgPosterDismiss);
                    imgPosterDismiss.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.tv_share_poster:
                this.dismiss();
                if (posterListener != null) {
                    posterListener.posterShare(rlView, imgPosterDismiss);
                }
                break;

        }
    }

}
