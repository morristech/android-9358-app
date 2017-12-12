package com.xmd.manager.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.CouponBean;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lhj on 17-11-10.
 * 优惠券详情
 */

public class CouponInfoDetailActivity extends BaseActivity implements View.OnClickListener {

    public static final String KEY_COUPON_ID = "couponId";
    @BindView(R.id.ll_review_data)
    LinearLayout llReviewData;

    private String mTitle;
    private String mCouponId;
    private String mCouponType;
    private NormalCouponInfoDetailFragment normalCoupon;
    private CouponBean mCouponBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_info_detail);
        ButterKnife.bind(this);
        getIntentData();
        initView();
        initFragmentView();
    }

    public void getIntentData() {
        mCouponBean = getIntent().getParcelableExtra(OperateCouponListFragment.KEY_INTENT_COUPON_BEAN);
        if (mCouponBean == null) {
            this.finish();
            return;
        }
        mCouponId = mCouponBean.actId;
        mTitle = mCouponBean.actTitle;
        mCouponType = mCouponBean.couponType;
    }

    private void initView() {
        setTitle(TextUtils.isEmpty(mTitle) ? ResourceUtils.getString(R.string.coupon_activity_title) : mTitle + "详情");
        if (!TextUtils.isEmpty(mCouponType)) {
            setRightVisible(!mCouponType.equals(Constant.COUPON_PAID_TYPE), "分享", ResourceUtils.getDrawable(R.drawable.icon_coupon_share), this, false);
            llReviewData.setVisibility(mCouponType.equals(Constant.COUPON_PAID_TYPE) ? View.GONE : View.VISIBLE);
        }
    }

    private void initFragmentView() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (!TextUtils.isEmpty(mCouponType) && mCouponType.equals(Constant.COUPON_PAID_TYPE)) {
            PaidCouponInfoDetailFragment paidCoupon = new PaidCouponInfoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_COUPON_ID, mCouponId);
            paidCoupon.setArguments(bundle);
            ft.replace(R.id.fl_coupon_detail, paidCoupon);
        } else {
            normalCoupon = new NormalCouponInfoDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable(KEY_COUPON_ID, mCouponId);
            normalCoupon.setArguments(bundle);
            ft.replace(R.id.fl_coupon_detail, normalCoupon);
        }
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        String shareUrl = normalCoupon.getShareUrl();
        String shareTitle = normalCoupon.getShareTitle();
        if (TextUtils.isEmpty(shareUrl)) {
            XToast.show(ResourceUtils.getString(R.string.share_url_is_null));
        }
        if (TextUtils.isEmpty(shareTitle)) {
            shareTitle = ResourceUtils.getString(R.string.coupon_activity_title);
        }
        doShare(shareUrl, shareTitle);
    }

    public void doShare(String mShareUrl, String actTitle) {
        Map<String, Object> params = new HashMap<>();
        Bitmap thumbnail = ImageLoader.readBitmapFromFile(ClubData.getInstance().getClubImageLocalPath());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
        params.put(Constant.PARAM_SHARE_URL, mShareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, actTitle);
        ClubInfo clubInfo = ClubData.getInstance().getClubInfo();
        if (clubInfo != null) {
            String shareDescription = String.format(ResourceUtils.getString(R.string.share_description), clubInfo.name);
            params.put(Constant.PARAM_SHARE_DESCRIPTION, shareDescription);
        } else {
            params.put(Constant.PARAM_SHARE_DESCRIPTION, ResourceUtils.getString(R.string.share_description_without_club_name));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }

    @OnClick(R.id.btn_filter_coupon)
    public void onViewClicked() {
        Intent intent = new Intent(CouponInfoDetailActivity.this, CouponOperateDataActivity.class);
        intent.putExtra(Constant.KEY_INTENT_COUPON_BEAN, mCouponBean);
        startActivity(intent);
    }
}
