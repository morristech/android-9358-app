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
public class NormalCouponDetailActivity extends BaseActivity {

    @Bind(R.id.tv_share_text) TextView mTvShareText;
    @Bind(R.id.tv_coupon_duration) TextView mTvCouponDuration;
    @Bind(R.id.tv_commission) TextView mTvCommission;
    @Bind(R.id.tv_service_item) TextView mTvServiceItem;
    @Bind(R.id.iv_share_qr_code) ImageView mIvShareQrCode;
    @Bind(R.id.wv_act_content) WebView mWvActContent;
    @Bind(R.id.btn_share) Button mShareBtn;

    private Subscription mGetCouponInfoSubscription;
    private String mActId;
    private CouponInfoResult mCouponInfoResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_coupon_detail);

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
        setTitle(ResourceUtils.getString(R.string.normal_coupon_detail_activity_title));
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

        String serviceItem="";
        int len = mCouponInfoResult.respData.items.size();
        for (int i = 0; i < len; i++) {
            serviceItem += "," + mCouponInfoResult.respData.items.get(i).name;
        }

        if(Utils.isNotEmpty(serviceItem)) {
            serviceItem = serviceItem.substring(1);
        } else {
            serviceItem="使用不限";
        }

        if(Utils.isEmpty(couponInfo.useTimePeriod)) {
            couponInfo.useTimePeriod="使用不限";
        }

        if(Utils.isEmpty(couponInfo.actContent)) {
            couponInfo.actContent="无";
        }

        mTvShareText.setText(String.format(ResourceUtils.getString(R.string.normal_coupon_detail_activity_share_text), couponInfo.sysCommission, couponInfo.sysCommission));

        Glide.with(this).load(generateQrCodeUrl()).into(mIvShareQrCode);
        mTvCommission.setText(String.valueOf(couponInfo.actValue));

        mTvServiceItem.setText(serviceItem);
        mTvCouponDuration.setText(couponInfo.useTimePeriod);

        mWvActContent.getSettings().setJavaScriptEnabled(false);
        mWvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        mWvActContent.loadDataWithBaseURL(null, couponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);

        mShareBtn.setEnabled(true);
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
}