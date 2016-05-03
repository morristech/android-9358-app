package com.xmd.technician.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.chat.UserProfileProvider;
import com.xmd.technician.common.ImageLoader;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.common.ThreadManager;
import com.xmd.technician.common.Utils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CouponInfoResult;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public class PaidCouponDetailActivity extends BaseActivity {

    @Bind(R.id.tv_tips_expire) TextView mTvTipsExpire;
    @Bind(R.id.tv_tips_verified) TextView mTvTipsVerified;
    @Bind(R.id.btn_share) Button mShare;
    @Bind(R.id.tv_consume_money_description) TextView mTvConsumeMoneyDescription;
    @Bind(R.id.tv_coupon_period) TextView mTvCouponPeriod;
    @Bind(R.id.wv_act_content) WebView mWvActContent;
    @Bind(R.id.iv_share_qr_code) ImageView mIvShareQrCode;

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

    private void initView(){
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

    private void onGetCouponInfoFailed(String msg){
        makeShortToast(msg);
        finish();
    }

    private void onGetCouponInfoSucceeded(CouponInfo couponInfo){

        Glide.with(this).load(generateQrCodeUrl()).into(mIvShareQrCode);

        mTvConsumeMoneyDescription.setText(couponInfo.consumeMoneyDescription);
        mTvCouponPeriod.setText(couponInfo.couponPeriod);

        mWvActContent.getSettings().setJavaScriptEnabled(false);
        mWvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        mWvActContent.loadDataWithBaseURL(null, couponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);

        mTvTipsExpire.setText(String.format(ResourceUtils.getString(R.string.paid_coupon_detail_tips_expire), couponInfo.baseCommission));
        mTvTipsVerified.setText(String.format(ResourceUtils.getString(R.string.paid_coupon_detail_tips_verified), couponInfo.commission));
    }

    private String generateQrCodeUrl(){
        return SharedPreferenceHelper.getServerHost() + RequestConstant.URL_COUPON_SHARE_QR_CODE + "?token=" + SharedPreferenceHelper.getUserToken()
                + "&actId=" + mActId + "&sessionType=" + RequestConstant.SESSION_TYPE;
    }

    @OnClick(R.id.btn_share)
    public void doShare() {
        ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_BACKGROUND, () -> {
            String imgUrl = mCouponInfoResult.respData != null ? mCouponInfoResult.respData.imgUrl : "";
            if (Utils.isEmpty(imgUrl)) {
                imgUrl = UserProfileProvider.getInstance().getCurrentUserInfo().getAvatar();
            }

            final Bitmap thumbnail = ImageLoader.readBitmapFromImgUrl(imgUrl);
            ThreadManager.postRunnable(ThreadManager.THREAD_TYPE_MAIN, () -> {
                Map<String, Object> params = new HashMap<>();
                params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
                params.put(Constant.PARAM_SHARE_URL, mCouponInfoResult.respData.shareUrl);
                params.put(Constant.PARAM_SHARE_TITLE, mCouponInfoResult.respData.clubName + "-" + mCouponInfoResult.respData.activities.actTitle);
                params.put(Constant.PARAM_SHARE_DESCRIPTION, mCouponInfoResult.respData.activities.consumeMoneyDescription + "，超值优惠，超值享受。快来约我。");
                MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
            });
        });
    }

    @OnClick(R.id.btn_user_detail)
    public void gotoPaidCouponUserDetail() {
        Intent intent = new Intent(this, PaidCouponUserDetailActivity.class);
        intent.putExtra(Constant.PARAM_ACT_ID, mActId);
        startActivity(intent);
    }
}