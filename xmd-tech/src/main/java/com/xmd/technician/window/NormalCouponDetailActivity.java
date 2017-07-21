package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
import com.xmd.technician.widget.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-4-29.
 */
public class NormalCouponDetailActivity extends BaseActivity {

    @BindView(R.id.tv_share_text)
    TextView mTvShareText;
    @BindView(R.id.tv_coupon_duration)
    TextView mTvCouponDuration;
    @BindView(R.id.tv_commission)
    TextView mTvCommission;
    @BindView(R.id.iv_share_qr_code)
    ImageView mIvShareQrCode;
    @BindView(R.id.wv_act_content)
    WebView mWvActContent;
    @BindView(R.id.btn_share)
    Button mShareBtn;
    @BindView(R.id.money_sign)
    ImageView mMoneySign;
    private Subscription mGetCouponInfoSubscription;
    private String mActId;
    private CouponInfoResult mCouponInfoResult;
    private FlowLayout mFlowLayout;
    private ScrollView mScrollView;

    private List<String> limitList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_coupon_detail);
        mFlowLayout = (FlowLayout) findViewById(R.id.limit_project_list);
        mScrollView = (ScrollView) findViewById(R.id.scroll_view);
        mScrollView.setFillViewport(true);


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

    private void onGetCouponInfoFailed(String msg) {
        makeShortToast(msg);
        finish();
    }

    private void onGetCouponInfoSucceeded(CouponInfo couponInfo) {


        int len = mCouponInfoResult.respData.items.size();
        for (int i = 0; i < len; i++) {
            limitList.add(mCouponInfoResult.respData.items.get(i).name);
        }
        if (limitList.size() == 0) {
            limitList.add("使用不限");
        }
        initChildViews();
        if (Utils.isEmpty(couponInfo.useTimePeriod)) {
            couponInfo.useTimePeriod = "使用不限";
        }

        if (Utils.isEmpty(couponInfo.actContent)) {
            couponInfo.actContent = "无";
        }
        String shareText = String.format(ResourceUtils.getString(R.string.normal_coupon_detail_activity_share_text), couponInfo.commission);
        SpannableString spannableString = new SpannableString(shareText);
        spannableString.setSpan(new TextAppearanceSpan(this, R.style.text_marked), 16, shareText.lastIndexOf("元"), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        mTvShareText.setText(spannableString);

        Glide.with(this).load(generateQrCodeUrl()).into(mIvShareQrCode);
        if (couponInfo.couponType.equals("discount")) {
            mMoneySign.setVisibility(View.GONE);
            mTvCommission.setText(String.format("%1.1f折", couponInfo.actValue / 100f));
        } else if (couponInfo.couponType.equals("gift")) {
            mMoneySign.setVisibility(View.GONE);
            mTvCommission.setText(Utils.isNotEmpty(couponInfo.actSubTitle) ? couponInfo.actSubTitle : couponInfo.actTitle);
        } else {
            mMoneySign.setVisibility(View.VISIBLE);
            mTvCommission.setText(String.valueOf(couponInfo.actValue));
        }

        mTvCouponDuration.setText(couponInfo.useTimePeriod);

        mWvActContent.getSettings().setJavaScriptEnabled(false);
        mWvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        mWvActContent.loadDataWithBaseURL(null, couponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);

        mShareBtn.setEnabled(true);
    }

    private String generateQrCodeUrl() {
        return SharedPreferenceHelper.getServerHost() + RequestConstant.URL_COUPON_SHARE_QR_CODE + "?token=" + SharedPreferenceHelper.getUserToken()
                + "&actId=" + mActId + "&sessionType=" + RequestConstant.SESSION_TYPE;
    }

    @OnClick(R.id.btn_share)
    public void doShare() {
        String imgUrl = mCouponInfoResult.respData != null ? mCouponInfoResult.respData.imgUrl : "";
        ShareController.doShare(imgUrl, mCouponInfoResult.respData.shareUrl, mCouponInfoResult.respData.clubName + "-" + mCouponInfoResult.respData.activities.actTitle,
                mCouponInfoResult.respData.activities.consumeMoneyDescription + "，超值优惠，超值享受。快来约我。", Constant.SHARE_COUPON, mActId);
    }

    private void initChildViews() {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = Utils.dip2px(NormalCouponDetailActivity.this, 12);
        //  lp.bottomMargin =40;
        for (int i = 0; i < limitList.size(); i++) {
            TextView view = new TextView(this);
            view.setPadding(36, 5, 36, 5);
            view.setText(limitList.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.limit_project_item_bg));
            mFlowLayout.addView(view, lp);
        }
    }
}
