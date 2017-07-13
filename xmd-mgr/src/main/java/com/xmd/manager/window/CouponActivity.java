package com.xmd.manager.window;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xmd.manager.ClubData;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.auth.AuthConstants;
import com.xmd.manager.auth.AuthHelper;
import com.xmd.manager.beans.ClubInfo;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Item;
import com.xmd.manager.common.DescribeMesaageUtil;
import com.xmd.manager.common.ImageLoader;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.common.WidgetUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponViewResult;
import com.xmd.manager.service.response.UseCouponResult;
import com.xmd.manager.service.response.UserCouponViewResult;
import com.xmd.manager.widget.AlertDialogBuilder;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by sdcm on 15-12-9.
 */
public class CouponActivity extends BaseActivity {

    @BindView(R.id.coupon_empty_view)
    EmptyView mCouponEmptyView;

    @BindView(R.id.coupon_name)
    TextView mCouponName;
    @BindView(R.id.coupon_type)
    TextView mCouponType;
    @BindView(R.id.coupon_desc)
    TextView mCouponDesc;
    @BindView(R.id.activity_duration)
    TextView mActivityDuration;
    @BindView(R.id.coupon_duration)
    TextView mCouponDuration;
    @BindView(R.id.coupon_use_duration)
    TextView mCouponUseDuration;
    @BindView(R.id.coupon_status)
    TextView mCouponStatus;

    @BindView(R.id.coupon_supplement_section)
    LinearLayout mCouponSupplementSection;
    @BindView(R.id.coupon_supplement)
    WebView mCouponSupplement;
    @BindView(R.id.coupon_use_supplement)
    WebView mCouponUseSupplement;
    @BindView(R.id.coupon_commission_section)
    LinearLayout mCouponCommissionSection;
    @BindView(R.id.coupon_commission)
    TextView mCouponCommission;

    @BindView(R.id.coupon_use_data)
    Button mCouponUseData;
    @BindView(R.id.coupon_use)
    Button mCouponUse;
    @BindView(R.id.coupon_no_section)
    LinearLayout mCouponNoSection;
    @BindView(R.id.coupon_no)
    TextView mCouponNo;
    @BindView(R.id.coupon_item)
    TextView mCouponItem;
    @BindView(R.id.coupon_items_container)
    View mCouponItemsContainer;

    private CouponInfo mCouponInfo;
    private String mShareUrl;
    private int mDisplayType;

    private Subscription mUseCouponSubscription;
    private Subscription mGetUserCouponViewSubscription;
    private Subscription mGetClubCouponViewSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coupon);

        mUseCouponSubscription = RxBus.getInstance().toObservable(UseCouponResult.class).subscribe(
                useCouponResult -> onUseCouponSucceeded(useCouponResult.msg)
        );

        mGetUserCouponViewSubscription = RxBus.getInstance().toObservable(UserCouponViewResult.class).subscribe(
                userCouponViewResult -> onGetUserCouponViewResult(userCouponViewResult)
        );

        mGetClubCouponViewSubscription = RxBus.getInstance().toObservable(ClubCouponViewResult.class).subscribe(
                clubCouponViewResult -> onGetClubCouponViewResult(clubCouponViewResult)
        );

        initViews();
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mUseCouponSubscription, mGetClubCouponViewSubscription, mGetUserCouponViewSubscription);
    }

    private void initViews() {
        mCouponEmptyView.setEmptyPic(R.drawable.emtpy_coupons);
        mCouponEmptyView.setStatus(EmptyView.Status.Loading);
        mCouponSupplement.getSettings().setJavaScriptEnabled(false);
        mCouponSupplement.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        mCouponUseSupplement.getSettings().setJavaScriptEnabled(false);
        mCouponUseSupplement.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
    }

    private void loadData() {
        Intent data = getIntent();
        mDisplayType = data.getIntExtra(Constant.PARAM_COUPON_DISPLAY_TYPE, 0);
        if (Constant.COUPON_DISPLAY_TYPE_CLUB == mDisplayType) {
            String actId = data.getStringExtra(Constant.PARAM_ACT_ID);
            loadClubCoupon(actId);
        } else if (Constant.COUPON_DISPLAY_TYPE_USER == mDisplayType) {
            String suaId = data.getStringExtra(Constant.PARAM_SUA_ID);
            String couponNo = data.getStringExtra(Constant.PARAM_COUPON_NUMBER);
            loadUserCoupon(suaId, couponNo);
        }
    }

    private void loadClubCoupon(String actId) {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_ACT_ID, actId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_COUPON_VIEW, params);
    }

    private void loadUserCoupon(String suaId, String couponNo) {
        Map<String, String> params = new HashMap<>();
        if (Utils.isNotEmpty(suaId)) {
            params.put(RequestConstant.KEY_SUA_ID, suaId);
        } else {
            params.put(RequestConstant.KEY_COUPON_NO, couponNo);
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_USER_COUPON_VIEW, params);
    }

    private void initContent() {

        mCouponEmptyView.setStatus(EmptyView.Status.Gone);

        mCouponName.setText("money".equals(mCouponInfo.useType) ? mCouponInfo.actValue + "元" : mCouponInfo.actTitle);
        mCouponType.setText(mCouponInfo.useTypeName);
        mCouponDesc.setText(mCouponInfo.consumeMoneyDescription);
        mActivityDuration.setText(mCouponInfo.actPeriod);
        mCouponDuration.setText(mCouponInfo.couponPeriod);
        mCouponUseDuration.setText(DescribeMesaageUtil.getTimePeriodDes(mCouponInfo.useTimePeriod));
        //只有优惠券才显示优惠说明，现金券不显示
        if (Constant.USE_TYPE_COUPON.equals(mCouponInfo.useType) && !TextUtils.isEmpty(mCouponInfo.actDescription)) {
            mCouponSupplementSection.setVisibility(View.VISIBLE);
            mCouponSupplement.loadDataWithBaseURL(null, mCouponInfo.actDescription, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
        }

        mCouponUseSupplement.loadDataWithBaseURL(null, mCouponInfo.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
        mCouponStatus.setText(mCouponInfo.actStatusName);

        //只有展现会所券时才展现分享和使用数据等
        if (Constant.COUPON_DISPLAY_TYPE_CLUB == mDisplayType) {
            WidgetUtils.setViewVisibleOrGone(mCouponUseData, AuthHelper.checkAuthorized(AuthConstants.AUTH_CODE_COUPON_USE_DATA));
            //屏蔽会所分享点钟券
            if (!mCouponInfo.couponType.equals(Constant.COUPON_TYPE_PAID)) {
                setRightVisible(true, R.drawable.selector_share, v -> doShare());
            }

            mCouponCommissionSection.setVisibility(View.VISIBLE);
            mCouponCommission.setText(mCouponInfo.commission);

        } else {
            mCouponNoSection.setVisibility(View.VISIBLE);
            mCouponNo.setText(mCouponInfo.couponNo);
            mCouponUse.setVisibility(View.VISIBLE);
        }
    }

    public void doShare() {
        Map<String, Object> params = new HashMap<>();
        Bitmap thumbnail = ImageLoader.readBitmapFromFile(ClubData.getInstance().getClubImageLocalPath());
        params.put(Constant.PARAM_SHARE_THUMBNAIL, thumbnail);
        params.put(Constant.PARAM_SHARE_URL, mShareUrl);
        params.put(Constant.PARAM_SHARE_TITLE, mCouponInfo.actTitle);
        ClubInfo clubInfo = ClubData.getInstance().getClubInfo();
        if (clubInfo != null) {
            String shareDescription = String.format(ResourceUtils.getString(R.string.share_description), clubInfo.name);
            params.put(Constant.PARAM_SHARE_DESCRIPTION, shareDescription);
        } else {
            params.put(Constant.PARAM_SHARE_DESCRIPTION, ResourceUtils.getString(R.string.share_description_without_club_name));
        }
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_SHOW_SHARE_PLATFORM, params);
    }

    @OnClick(R.id.coupon_use_data)
    public void doCouponUseData() {
        Intent intent = new Intent(this, CouponUseDataActivity.class);
        intent.putExtra(Constant.PARAM_ACT_ID, mCouponInfo.actId);
        startActivity(intent);
    }

    @OnClick(R.id.coupon_use)
    public void doCouponUse() {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_SUA_ID, mCouponInfo.suaId);
        params.put(RequestConstant.KEY_COUPON_NO, mCouponInfo.couponNo);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_USE_COUPON, params);
    }

    private void onGetClubCouponViewResult(ClubCouponViewResult clubCouponViewResult) {
        if (clubCouponViewResult.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            mCouponEmptyView.setEmptyTip(clubCouponViewResult.msg);
            mCouponEmptyView.setStatus(EmptyView.Status.Empty);
        } else {
            mCouponInfo = clubCouponViewResult.respData.coupon;
            mShareUrl = clubCouponViewResult.respData.shareUrl;
            initContent();
            initItem(clubCouponViewResult.respData.items);
        }
    }

    private void onGetUserCouponViewResult(UserCouponViewResult userCouponViewResult) {
        if (userCouponViewResult.statusCode == RequestConstant.RESP_ERROR_CODE_FOR_LOCAL) {
            mCouponEmptyView.setEmptyTip(userCouponViewResult.msg);
            mCouponEmptyView.setStatus(EmptyView.Status.Empty);
        } else {
            mCouponInfo = userCouponViewResult.respData.userAct;
            initContent();
            initItem(userCouponViewResult.respData.items);
        }
    }

    private void initItem(List<Item> items) {
        if (items != null && !items.isEmpty()) {
            mCouponItemsContainer.setVisibility(View.VISIBLE);
            StringBuffer itemsStr = new StringBuffer(items.get(0).name);
            for (int i = 1; i < items.size(); i++) {
                itemsStr.append("，");
                itemsStr.append(items.get(i).name);
            }
            mCouponItem.setText(itemsStr);
        }

    }

    private void onUseCouponSucceeded(String message) {
        new AlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton(ResourceUtils.getString(R.string.btn_confirm), v -> setOKResultAndFinish())
                .show();
    }

    private void setOKResultAndFinish() {
        setResult(RESULT_OK);
        finish();
    }
}
