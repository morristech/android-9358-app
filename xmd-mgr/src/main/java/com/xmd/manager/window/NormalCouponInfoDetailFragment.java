package com.xmd.manager.window;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Item;
import com.xmd.manager.common.ResourceUtils;
import com.xmd.manager.common.Utils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponViewResult;
import com.xmd.manager.widget.EmptyView;
import com.xmd.manager.widget.FlowLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Subscription;

/**
 * Created by Lhj on 17-11-10.
 */

public class NormalCouponInfoDetailFragment extends BaseFragment {

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
    @BindView(R.id.limit_project)
    TextView limitProject;
    @BindView(R.id.limit_project_list)
    FlowLayout limitProjectList;
    @BindView(R.id.limit_time)
    TextView limitTime;
    @BindView(R.id.tv_coupon_duration)
    TextView tvCouponDuration;
    @BindView(R.id.limit_rule)
    TextView limitRule;
    @BindView(R.id.wv_act_content)
    WebView wvActContent;
    @BindView(R.id.scroll_view)
    ScrollView scrollView;
    Unbinder unbinder;

    private Subscription mGetClubCouponViewSubscription;
    private String mCouponId;
    private List<String> limitItemList = new ArrayList<>();
    private String mShareUrl;
    private String mShareTitle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_normal_coupon_info_detail, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    protected void initView() {
        mGetClubCouponViewSubscription = RxBus.getInstance().toObservable(ClubCouponViewResult.class).subscribe(
                clubCouponViewResult -> onGetClubCouponViewResult(clubCouponViewResult)
        );
        mCouponId = getArguments().getString(CouponInfoDetailActivity.KEY_COUPON_ID);
        loadClubCoupon(mCouponId);
    }

    private void onGetClubCouponViewResult(ClubCouponViewResult result) {
        if (result.statusCode == 200) {
            emptyView.setStatus(EmptyView.Status.Gone);
            setViewData(result.respData.coupon);
            setLimitItemsViewData(result.respData.items);
            mShareUrl = result.respData.shareUrl;
            mShareTitle = result.respData.coupon.actTitle;
        } else {
            emptyView.setLoadingTip(result.msg);
            emptyView.setStatus(EmptyView.Status.Empty);
        }
    }

    private void setViewData(CouponInfo coupon) {
        tvCouponTitle.setText(Utils.StrSubstring(8, coupon.actTitle, true));
        couponType.setText(String.format("（%s）", coupon.couponTypeName));
        tvCouponReward.setVisibility(View.GONE);
        if (coupon.couponType.equals(Constant.COUPON_DISCOUNT_TYPE)) {
            imgMoneyMark.setVisibility(View.GONE);
            couponEmptyView.setVisibility(View.VISIBLE);
            couponAmount.setText(String.format("%1.1f折", coupon.actValue / 100f));
        } else if (coupon.couponType.equals(Constant.COUPON_GIFT_TYPE)) {
            couponEmptyView.setVisibility(View.VISIBLE);
            imgMoneyMark.setVisibility(View.GONE);
            couponAmount.setText(TextUtils.isEmpty(coupon.actSubTitle) ? coupon.actTitle : coupon.actSubTitle);
        } else {
            couponEmptyView.setVisibility(View.GONE);
            imgMoneyMark.setVisibility(View.VISIBLE);
            couponAmount.setText(String.valueOf(coupon.actValue));
        }
        tvConsumeMoneyDescription.setText(TextUtils.isEmpty(coupon.consumeMoneyDescription) ? "" : coupon.consumeMoneyDescription);
        tvCouponPeriod.setText("有效时间：" + Utils.StrSubstring(19, coupon.couponPeriod, true));
        btnShareCoupon.setVisibility(View.GONE);

        if (Utils.isEmpty(coupon.useTimePeriod)) {
            coupon.useTimePeriod = "使用不限";
        }

        tvCouponDuration.setText(coupon.useTimePeriod + "\n" + coupon.couponPeriod + "\n" + (coupon.userGetCount == 0 ? "不限领取数量" : String.format("每人限领%s张", String.valueOf(coupon.userGetCount))));
        wvActContent.getSettings().setJavaScriptEnabled(false);
        wvActContent.getSettings().setTextZoom(Constant.WEBVIEW_TEXT_ZOOM);
        wvActContent.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
        wvActContent.loadDataWithBaseURL(null, coupon.actContent, Constant.MIME_TYPE_HTML, Constant.DEFAULT_ENCODE, null);
    }

    private void loadClubCoupon(String actId) {
        emptyView.setStatus(EmptyView.Status.Loading);
        Map<String, String> params;
        params = new HashMap<>();
        params.put(RequestConstant.KEY_ACT_ID, actId);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_COUPON_VIEW, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (wvActContent != null && wvActContent.getParent() != null) {
            wvActContent.setVisibility(View.GONE);
            ((ViewGroup) wvActContent.getParent()).removeView(wvActContent);
            wvActContent.destroy();
            wvActContent = null;

        }
        RxBus.getInstance().unsubscribe(mGetClubCouponViewSubscription);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void initChildViews() {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = 10;
        lp.rightMargin = 10;
        lp.topMargin = Utils.dip2px(getActivity(), 12);
        for (int i = 0; i < limitItemList.size(); i++) {
            TextView view = new TextView(getActivity());
            view.setPadding(36, 5, 36, 5);
            view.setText(limitItemList.get(i));
            view.setTextColor(ResourceUtils.getColor(R.color.alert_text_color));
            view.setBackgroundDrawable(getResources().getDrawable(R.drawable.limit_project_item_bg));
            limitProjectList.addView(view, lp);
        }
    }

    public void setLimitItemsViewData(List<Item> limitItemsViewData) {
        if (limitItemList == null) {
            limitItemList = new ArrayList<>();
        }
        for (int i = 0; i < limitItemsViewData.size(); i++) {
            limitItemList.add(limitItemsViewData.get(i).name);
        }
        if (limitItemList.size() == 0) {
            limitItemList.add("使用不限");
        }

        initChildViews();
    }

    public String getShareUrl() {
        return mShareUrl;
    }

    public String getShareTitle() {
        return mShareTitle;
    }
}
