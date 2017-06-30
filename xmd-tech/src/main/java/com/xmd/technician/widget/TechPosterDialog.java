package com.xmd.technician.widget;

import android.app.Dialog;
import android.content.Context;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-6-26.
 */

public class TechPosterDialog extends Dialog {
    @Bind(R.id.rl_view)
    RelativeLayout rlView;
    @Bind(R.id.img_poster_tech_photo)
    RoundImageView imgPosterTechPhoto;
    @Bind(R.id.tv_poster_tech_name)
    TextView tvPosterTechName;
    @Bind(R.id.tv_poster_primary_title)
    TextView tvPosterPrimaryTitle;
    @Bind(R.id.tv_poster_minor_title)
    TextView tvPosterMinorTitle;
    @Bind(R.id.tv_poster_club_name)
    TextView tvPosterClubName;
    @Bind(R.id.img_poster_qr_code)
    ImageView imgPosterQrCode;
    @Bind(R.id.rl_poster_edit_or_save)
    RelativeLayout rlPosterEditOrSave;
    @Bind(R.id.ll_poster_edit_or_share)
    LinearLayout llPosterEditOrShare;
    @Bind(R.id.img_poster_dismiss)
    ImageView imgPosterDismiss;

    private int style;
    private boolean isSave;
    private Context mContext;
    private PosterShareOrSaveListener posterListener;

    public TechPosterDialog(Context context, int style, boolean isSave) {

        super(context, R.style.default_dialog_style);
        this.mContext = context;
        this.style = style;
        this.isSave = isSave;
    }

    public interface PosterShareOrSaveListener {
        void posterSave(View view);

        void posterEdit();

        void posterShare();

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

    public void setViewDate(String primaryTitle, String minorTitle, String techName, String techNo, String clubName, String image, String imageUrl) {

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
        if (Utils.isNotEmpty(clubName)) {
            tvPosterClubName.setText(clubName);
        } else {
            tvPosterClubName.setText("");
        }

        if (Utils.isNotEmpty(image)) {
            Glide.with(mContext).load(image).into(imgPosterTechPhoto);
        } else {
            Glide.with(mContext).load(imageUrl).into(imgPosterTechPhoto);

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
        }
        ButterKnife.bind(this);
        Glide.with(mContext).load(LoginTechnician.getInstance().getQrCodeUrl()).into(imgPosterQrCode);
        if (isSave) {
            rlPosterEditOrSave.setVisibility(View.VISIBLE);
            llPosterEditOrShare.setVisibility(View.GONE);
        } else {
            rlPosterEditOrSave.setVisibility(View.GONE);
            llPosterEditOrShare.setVisibility(View.VISIBLE);
        }

    }


    @OnClick({R.id.img_poster_dismiss, R.id.tv_save_poster, R.id.tv_edit_poster, R.id.ll_poster_edit, R.id.ll_poster_share})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_poster_dismiss:
                this.dismiss();
                break;
            case R.id.tv_save_poster:

                if (posterListener != null) {
                    imgPosterDismiss.setVisibility(View.GONE);
                    posterListener.posterSave(rlView);
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
                    posterListener.posterShare();
                }
                break;

        }
    }

}
