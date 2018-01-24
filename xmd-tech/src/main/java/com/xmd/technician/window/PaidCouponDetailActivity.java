package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shidou.commonlibrary.widget.XToast;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.share.ShareController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public class PaidCouponDetailActivity extends BaseActivity {

    @BindView(R.id.tv_tips_verified)
    TextView mTvTipsVerified;
    @BindView(R.id.btn_share)
    Button mShare;
    @BindView(R.id.tv_consume_money_description)
    TextView mTvConsumeMoneyDescription;
    @BindView(R.id.tv_coupon_period)
    TextView mTvCouponPeriod;
    @BindView(R.id.wv_act_content)
    WebView mWvActContent;
    @BindView(R.id.iv_share_qr_code)
    ImageView mIvShareQrCode;

    private Subscription mGetCouponInfoSubscription;
    private String mActId;
    private CouponInfoResult mCouponInfoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_coupon_detail);

        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }

        mActId = intent.getStringExtra(Constant.PARAM_ACT_ID);

        mGetCouponInfoSubscription = RxBus.getInstance().toObservable(CouponInfoResult.class).subscribe(
                couponInfoResult -> handleCouponInfoResult(couponInfoResult)
        );
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCouponInfoSubscription);
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.paid_coupon_detail_activity_title));
        setBackVisible(true);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_INFO, mActId);
    }

    private void handleCouponInfoResult(CouponInfoResult result) {
        if (result.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            onGetCouponInfoFailed(result.msg);
        } else {
            mCouponInfoResult = result;
            onGetCouponInfoSucceeded(result.respData.activities);
        }
    }

    private void onGetCouponInfoFailed(String msg) {
        makeShortToast(msg);
        finish();
    }

    private void onGetCouponInfoSucceeded(CouponInfo couponInfo) {

        Glide.with(this).load(generateQrCodeUrl()).into(mIvShareQrCode);

        mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
        mTvCouponPeriod.setText(couponInfo.couponPeriod);

        mWvActContent.getSettings().setJavaScriptEnabled(false);
        mWvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        mWvActContent.loadDataWithBaseURL(null, couponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
        String verified = String.format(ResourceUtils.getString(R.string.paid_coupon_detail_tips_verified), Utils.getFloat2Str(couponInfo.commission));
        SpannableString spannableVerified = new SpannableString(verified);
        spannableVerified.setSpan(new TextAppearanceSpan(this, R.style.text_marked), 14, verified.lastIndexOf("元"), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvTipsVerified.setText(spannableVerified);
        mShare.setEnabled(true);
    }

    private String generateQrCodeUrl() {
        return SharedPreferenceHelper.getServerHost() + RequestConstant.URL_COUPON_SHARE_QR_CODE + "?token=" + SharedPreferenceHelper.getUserToken()
                + "&actId=" + mActId + "&sessionType=" + RequestConstant.SESSION_TYPE;
    }

    @OnClick(R.id.btn_share)
    public void doShare() {
        if(mCouponInfoResult == null || mCouponInfoResult.respData == null){
            XToast.show("获取券信息失败");
            return;
        }
        String imgUrl = mCouponInfoResult.respData != null ? mCouponInfoResult.respData.imgUrl : "";
        ShareController.doShare(imgUrl, mCouponInfoResult.respData.shareUrl, mCouponInfoResult.respData.clubName + "-" + mCouponInfoResult.respData.activities.actTitle,
                mCouponInfoResult.respData.activities.consumeMoneyDescription + "超值优惠，超值享受。快来约我。", RequestConstant.KEY_PAID_COUPON, mActId);
    }

    @OnClick(R.id.btn_user_detail)
    public void gotoPaidCouponUserDetail() {
        Intent intent = new Intent(this, PaidCouponUserDetailActivity.class);
        intent.putExtra(Constant.PARAM_ACT_ID, mActId);
        startActivity(intent);
    }
}