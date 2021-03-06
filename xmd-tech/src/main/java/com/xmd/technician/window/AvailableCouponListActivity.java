package com.xmd.technician.window;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.app.Constants;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.event.ChatUmengStatisticsEvent;
import com.xmd.technician.Adapter.ChatCouponAdapter;
import com.xmd.technician.Constant;
import com.xmd.technician.R;
import com.xmd.technician.SharedPreferenceHelper;
import com.xmd.technician.bean.CouponInfo;
import com.xmd.technician.bean.CouponType;
import com.xmd.technician.bean.UserGetCouponResult;
import com.xmd.technician.common.ResourceUtils;
import com.xmd.technician.http.RequestConstant;
import com.xmd.technician.http.gson.CouponListResult;
import com.xmd.technician.model.LoginTechnician;
import com.xmd.technician.msgctrl.MsgDef;
import com.xmd.technician.msgctrl.MsgDispatcher;
import com.xmd.technician.msgctrl.RxBus;
import com.xmd.technician.widget.EmptyView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;


/**
 * Created by Lhj on 2016/8/3.
 */
public class AvailableCouponListActivity extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_CHAT_ID = "chatId";
    @BindView(R.id.toolbar_right_share)
    TextView toolbarRightShare;
    @BindView(R.id.view_emptyView)
    EmptyView viewEmptyView;
    @BindView(R.id.expandable_list_view)
    ExpandableListView expandableListView;

    private ChatCouponAdapter adapter;
    private List<CouponInfo> mSelectedCouponInfo;
    private List<CouponType> mCouponTypes;
    private List<CouponInfo> mPaidCoupons;
    private List<CouponInfo> mCashAndFavourables;
    private List<List<CouponInfo>> mCouponInfos;

    private Subscription mGetCouponListSubscription;

    private String chatId;
    private int remainSendCount;
    private int successCount;
    private int failedCount;
    private Subscription mUserGetCouponResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availabel_coupon_deatil);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        setTitle(ResourceUtils.getString(R.string.check_coupon));
        toolbarRightShare.setVisibility(View.VISIBLE);
        toolbarRightShare.setEnabled(false);
        toolbarRightShare.setOnClickListener(this);
        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        setBackVisible(true);
        viewEmptyView.setStatus(EmptyView.Status.Loading);
        mSelectedCouponInfo = new ArrayList<>();
        mCouponTypes = new ArrayList<>();
        mCouponInfos = new ArrayList<>();
        mPaidCoupons = new ArrayList<>();
        mCashAndFavourables = new ArrayList<>();
        adapter = new ChatCouponAdapter(this);
        adapter.setChildrenClickedInterface(new ChatCouponAdapter.OnChildrenClicked() {
            @Override
            public void onChildrenClickedListener(CouponInfo bean, int groupPosition, int childPosition, boolean isSelected) {
                if (isSelected) {
                    mSelectedCouponInfo.add(bean);
                    mCouponInfos.get(groupPosition).get(childPosition).selectedStatus = 1;
                    adapter.refreshChildData(mCouponInfos);
                } else {
                    mCouponInfos.get(groupPosition).get(childPosition).selectedStatus = 0;
                    adapter.refreshChildData(mCouponInfos);
                    mSelectedCouponInfo.remove(bean);
                }
                if (mSelectedCouponInfo.size() > 0) {
                    toolbarRightShare.setEnabled(true);
                    toolbarRightShare.setText(String.format("分享(%s)", mSelectedCouponInfo.size()));
                } else {
                    toolbarRightShare.setEnabled(false);
                    toolbarRightShare.setText("分享");
                }

            }

        });
        expandableListView.setDivider(null);
        expandableListView.setAdapter(adapter);
        mGetCouponListSubscription = RxBus.getInstance().toObservable(CouponListResult.class).subscribe(this::getCouponListResult);
        mUserGetCouponResult = RxBus.getInstance().toObservable(UserGetCouponResult.class).subscribe(this::handleUserGetCouponResult);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_COUPON_LIST);
    }

    private void getCouponListResult(CouponListResult result) {
        if (result.respData == null) {
            viewEmptyView.setStatus(EmptyView.Status.Empty);
            return;
        }
        Collections.sort(result.respData.coupons, (lhs, rhs) -> {
            if (Constant.COUPON_TYPE_PAID.equals(rhs.couponType)) {
                return 1;
            } else if (Constant.COUPON_TYPE_PAID.equals(lhs.couponType)) return -1;
            return 0;
        });
        if (result.respData.coupons != null && result.respData.coupons.size() > 0) {
            viewEmptyView.setStatus(EmptyView.Status.Gone);
            mCouponTypes.clear();
            mPaidCoupons.clear();
            mCashAndFavourables.clear();
            for (CouponInfo info : result.respData.coupons) {
                info.selectedStatus = 0;
                if (info.useTypeName.equals(ResourceUtils.getString(R.string.delivery_coupon))) {
                    mPaidCoupons.add(info);
                } else {
                    mCashAndFavourables.add(info);
                }
            }
            if (mPaidCoupons.size() > 0) {
                mCouponTypes.add(new CouponType("点钟券"));
                mCouponInfos.add(mPaidCoupons);
            }
            if (mCashAndFavourables.size() > 0) {
                mCouponTypes.add(new CouponType("优惠券"));
                mCouponInfos.add(mCashAndFavourables);
            }
            adapter.setData(mCouponTypes, mCouponInfos);
            if (mCouponInfos.size() > 0) {
                for (int i = 0; i < mCouponInfos.size(); i++) {
                    expandableListView.expandGroup(i, false);
                }

            } else {
                viewEmptyView.setStatus(EmptyView.Status.Empty);
            }
        } else {
            viewEmptyView.setStatus(EmptyView.Status.Empty);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetCouponListSubscription, mUserGetCouponResult);
    }

    public void handleUserGetCouponResult(UserGetCouponResult result) {
        remainSendCount--;
        if (result.respData != null) {
            //用户领取成功，那么发送环信消息
            ChatMessageManager.getInstance().sendCouponMessage(chatId, false, result.actId, SharedPreferenceHelper.getInviteCode(),
                    result.useTypeName, result.actTitle, result.actValue, result.couponPeriod);
            successCount++;
        } else {
            failedCount++;
        }
        checkDeliverResult();
    }

    @Override
    public void onClick(View v) {
        Map<String, String> mParams;
        mParams = new HashMap<>();
        if (v.getId() == R.id.toolbar_right_share) {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("coupon", (ArrayList<? extends Parcelable>) mSelectedCouponInfo);

            //先请求用户领取券
            successCount = 0;
            failedCount = 0;
            remainSendCount = mSelectedCouponInfo.size();
            for (CouponInfo couponInfo : mSelectedCouponInfo) {
                mParams.clear();
                if (!("paid").equals(couponInfo.couponType)) {
                    userGetCoupon(getShareText(couponInfo), couponInfo.actId,LoginTechnician.getInstance().getInviteCode(),couponInfo.couponType,couponInfo.useTypeName,
                            couponInfo.actTitle,String.valueOf(couponInfo.actValue),couponInfo.couponPeriod,couponInfo.couponPeriod);
                    mParams.put(RequestConstant.KEY_ACT_ID, couponInfo.actId);
                    mParams.put(RequestConstant.KEY_ACT_TYPE, RequestConstant.KEY_COUPON_REWARD);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SHARE_COUNT_UPDATE, mParams);

                } else {
                    successCount++;
                    remainSendCount--;
                    ChatMessageManager.getInstance().sendCouponMessage(chatId, true, couponInfo.actId, SharedPreferenceHelper.getInviteCode(),
                            couponInfo.useTypeName, couponInfo.actTitle, String.valueOf(couponInfo.actValue), couponInfo.couponPeriod);
                    mParams.put(RequestConstant.KEY_ACT_ID, couponInfo.actId);
                    mParams.put(RequestConstant.KEY_ACT_TYPE, RequestConstant.KEY_PAID_COUPON);
                    MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_TECH_SHARE_COUNT_UPDATE, mParams);
                }
            }
            checkDeliverResult();
            EventBus.getDefault().post(new ChatUmengStatisticsEvent(Constants.UMENG_STATISTICS_COUPON_SEND));

        }
    }

    private String getShareText(CouponInfo couponInfo) {
        switch (couponInfo.couponType) {
            case "paid":
                return String.format(Locale.getDefault(), "<i>求点钟</i>立减<span>%1$d</span>元<b>%2$s</b>", couponInfo.actValue, couponInfo.couponPeriod);
            case "discount":
                return String.format(Locale.getDefault(), "<i>%s</i><span>%.1f</span>折<b>%s</b>", couponInfo.useTypeName, (float) couponInfo.actValue / 100, couponInfo.couponPeriod);
            case "gift":
                return String.format(Locale.getDefault(), "<i>%s</i><span>%s</span><b>%s</b>", couponInfo.useTypeName, couponInfo.actSubTitle, couponInfo.couponPeriod);
            default:
                return String.format(Locale.getDefault(), "<i>%s</i><span>%d</span>元<b>%s</b>", couponInfo.useTypeName, couponInfo.actValue, couponInfo.couponPeriod);
        }
    }


    public void checkDeliverResult() {
        if (remainSendCount == 0) {
            XToast.show("发券成功" + successCount + "张" + (failedCount > 0 ? "失败" + failedCount + "张" : ""));
            finish();
        }
    }
    private void userGetCoupon(String content, String actId, String techCode, String couponType, String useTypeName, String actTitle,
                               String actValue, String couponPeriod, String limitTime) {
        Map<String, Object> params = new HashMap<>();
        params.put(RequestConstant.KEY_COUPON_CONTENT, content);
        params.put(RequestConstant.KEY_USER_COUPON_ACT_ID, actId);
        params.put(RequestConstant.KEY_USER_COUPON_CHANEL, "tech");
        params.put(RequestConstant.KEY_USER_COUPON_EMCHAT_ID, chatId);
        params.put(RequestConstant.KEY_USER_TECH_CODE,techCode);
        params.put(RequestConstant.KEY_COUPON_USE_TYPE_NAME, useTypeName);
        params.put(RequestConstant.KEY_COUPON_TYPE,couponType);
        params.put(RequestConstant.KEY_ACT_TITLE,actTitle);
        params.put(RequestConstant.KEY_ACT_VALUE,actValue);
        params.put(RequestConstant.KEY_COUPON_PERIOD,couponPeriod);
        params.put(RequestConstant.KEY_COUPON_LIMIT_TIME, limitTime);
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_USER_GET_COUPON, params);
    }
}
