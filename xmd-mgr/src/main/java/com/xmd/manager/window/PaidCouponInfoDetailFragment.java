package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponViewResult;
import com.xmd.manager.widget.EmptyView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-11-10.
 */

public class PaidCouponInfoDetailFragment extends BaseFragment {

    @BindView(R.id.empty_view)
    EmptyView emptyView;
    @BindView(R.id.tv_coupon_title)
    TextView tvCouponTitle;
    @BindView(R.id.coupon_type)
    TextView couponType;
    @BindView(R.id.tv_coupon_reward)
    TextView tvCouponReward;
    @BindView(R.id.coupon_empty_view)
    View couponEmptyView;
    @BindView(R.id.img_money_mark)
    ImageView imgMoneyMark;
    @BindView(R.id.coupon_amount)
    TextView couponAmount;
    @BindView(R.id.tv_consume_money_description)
    TextView tvConsumeMoneyDescription;
    @BindView(R.id.tv_coupon_period)
    TextView tvCouponPeriod;
    @BindView(R.id.btn_share_coupon)
    TextView btnShareCoupon;
    @BindView(R.id.limit_time)
    TextView limitTime;
    @BindView(R.id.wv_act_content)
    WebView wvActContent;
    Unbinder unbinder;
    @BindView(R.id.tv_coupon_period_time)
    TextView couponPeriodTime;

    private Subscription mGetPaidCouponViewSubscription;
    private String paidCouponId;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paid_coupon_info_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView() {
        mGetPaidCouponViewSubscription = RxBus.getInstance().toObservable(ClubCouponViewResult.class).subscribe(
                clubCouponViewResult -> onGetClubCouponViewResult(clubCouponViewResult)
        );
        paidCouponId = getArguments().getString(CouponInfoDetailActivity.KEY_COUPON_ID);

        loadClubCoupon(paidCouponId);

    }

    private void onGetClubCouponViewResult(ClubCouponViewResult result) {
        if (result.statusCode == 200) {
            emptyView.setStatus(EmptyView.Status.Gone);
            setViewData(result.respData.coupon);
        } else {
            emptyView.setLoadingTip(result.msg);
            emptyView.setStatus(EmptyView.Status.Empty);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        RxBus.getInstance().unsubscribe(mGetPaidCouponViewSubscription);
        unbinder.unbind();
        if (wvActContent != null && wvActContent.getParent() != null) {
            wvActContent.setVisibility(View.GONE);
            ((ViewGroup) wvActContent.getParent()).removeView(wvActContent);
            wvActContent.destroy();
            wvActContent = null;
        }
    }

    private void loadClubCoupon(String actId) {
        emptyView.setStatus(EmptyView.Status.Loading);
        Map<String, String> params;
        params = new HashMap<>();
        params.put(RequestConstant.KEY_ACT_ID, actId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_COUPON_VIEW, params);
    }

    public void setViewData(CouponInfo coupon) {
        tvCouponTitle.setText(coupon.couponTypeName);
        couponType.setText("");
        if (!coupon.techCommission.equals("0.0")) {
            tvCouponReward.setVisibility(View.VISIBLE);
            String text = String.format(ResourceUtils.getString(R.string.coupon_fragment_coupon_reward), coupon.techCommission);
            SpannableString spannableString = new SpannableString(text);
            spannableString.setSpan(new TextAppearanceSpan(getActivity(), R.style.text_bold), 3, text.length() - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvCouponReward.setText(spannableString);
        } else {
            tvCouponReward.setVisibility(View.GONE);
        }
        couponEmptyView.setVisibility(View.GONE);
        imgMoneyMark.setVisibility(View.VISIBLE);
        couponAmount.setText(String.valueOf(coupon.actValue));
        tvConsumeMoneyDescription.setText(TextUtils.isEmpty(coupon.consumeMoneyDescription) ? "" : coupon.consumeMoneyDescription);
        tvCouponPeriod.setText(Utils.StrSubstring(19, coupon.couponPeriod, true));
        btnShareCoupon.setVisibility(coupon.actStatus.equals(Constant.COUPON_ONLINE_TYPE) && !coupon.couponType.equals(Constant.COUPON_PAID_TYPE) ? View.VISIBLE : View.GONE);
        couponPeriodTime.setText(coupon.useTimePeriod);
        wvActContent.getSettings().setJavaScriptEnabled(false);
        wvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        wvActContent.loadDataWithBaseURL(null, coupon.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
    }
}
