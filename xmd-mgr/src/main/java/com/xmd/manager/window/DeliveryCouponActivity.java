package com.xmd.manager.window;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.chat.ChatMessageManager;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.manager.Constant;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.adapter.DeliveryCouponListAdapter;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.common.CommonUtils;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponResult;
import com.xmd.manager.service.response.UserGetCouponResult;
import com.xmd.manager.widget.EmptyView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-30.
 */
public class DeliveryCouponActivity extends BaseActivity implements DeliveryCouponListAdapter.CallBackListener {
    public static final String EXTRA_CHAT_ID = "chatId";
    public static final String KEY_FROM = "from";
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.view_empty)
    EmptyView viewEmpty;
    private Customer mFromCustomer;
    private String chatId;
    private List<CouponInfo> mSelectedCouponList = new ArrayList<>();
    private List<CouponInfo> mCoupons;
    private Subscription mGetClubCouponList;
    private Subscription mUserGetCouponResult;
    private DeliveryCouponListAdapter mCouponListAdapter;
    private LinearLayoutManager mLayoutManager;
    private int remainSendCount;
    private int successCount;
    private int failedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_coupons);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mLayoutManager = new LinearLayoutManager(this);
        list.setHasFixedSize(true);
        list.setLayoutManager(mLayoutManager);
        mCoupons = new ArrayList<>();
        mFromCustomer = (Customer) getIntent().getSerializableExtra(KEY_FROM);
        chatId = getIntent().getStringExtra(EXTRA_CHAT_ID);
        if (chatId == null && mFromCustomer != null) {
            chatId = mFromCustomer.emchatId;
        }
        mGetClubCouponList = RxBus.getInstance().toObservable(ClubCouponResult.class).subscribe(this::handlerCouponResult);
        mUserGetCouponResult = RxBus.getInstance().toObservable(UserGetCouponResult.class).subscribe(this::handleUserGetCouponResult);
        viewEmpty.setStatus(EmptyView.Status.Loading);
        getData();

        mCouponListAdapter = new DeliveryCouponListAdapter(DeliveryCouponActivity.this, mCoupons, this);
        list.setAdapter(mCouponListAdapter);
    }

    private void handlerCouponResult(ClubCouponResult result) {
        if (result.statusCode == 200) {
            mCoupons.clear();
            for (int i = 0; i < result.respData.size(); i++) {
                result.respData.get(i).isSelected = 0;
                if (result.respData.get(i).actStatus.equals("online")) {
                    if (!result.respData.get(i).couponType.equals(Constant.COUPON_TYPE_PAID)) {
                        mCoupons.add(result.respData.get(i));
                    }
                }

            }
            if (mCoupons.size() > 0) {
                viewEmpty.setStatus(EmptyView.Status.Gone);
                btnSend.setVisibility(View.VISIBLE);
            } else {
                viewEmpty.setStatus(EmptyView.Status.Empty);
                btnSend.setVisibility(View.GONE);
            }
        } else {
            makeShortToast(result.msg);
        }
        mCouponListAdapter.setData(mCoupons);

    }

    @OnClick(R.id.btn_send)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                if (mSelectedCouponList.size() == 0) {
                    makeShortToast(getString(R.string.deliver_coupon_activity_select_tips));
                    return;
                }
                //先请求用户领取券
                successCount = 0;
                failedCount = 0;
                remainSendCount = mSelectedCouponList.size();
                for (CouponInfo couponInfo : mSelectedCouponList) {
                    if (!(Constant.COUPON_TYPE_PAID).equals(couponInfo.couponType)) {
                        CommonUtils.userGetCoupon(couponInfo.actId, "manager", chatId, couponInfo);
                    } else {
                        successCount++;
                        remainSendCount--;
                        ChatMessageManager.getInstance().sendCouponMessage(
                                chatId,
                                true,
                                getShareText(couponInfo),
                                couponInfo.actId, SharedPreferenceHelper.getUserInviteCode(),couponInfo.couponTypeName,couponInfo.useTimePeriod);
                    }
                }
                checkDeliverResult();
                break;
        }
    }

    public void handleUserGetCouponResult(UserGetCouponResult result) {
        remainSendCount--;
        if (result.respData != null) {
            //用户领取成功，那么发送环信消息
            CouponInfo couponInfo = result.couponInfo;
            CouponChatMessage chatMessage = CouponChatMessage.create(
                    chatId,
                    false,
                    result.respData.userActId,
                    getShareText(couponInfo),
                    SharedPreferenceHelper.getUserInviteCode(),null,null,null);
            ChatMessageManager.getInstance().sendMessage(chatMessage);
            successCount++;
        } else {
            failedCount++;
        }
        checkDeliverResult();
    }

    private String getShareText(CouponInfo couponInfo) {
        switch (couponInfo.couponType) {
            case Constant.COUPON_TYPE_PAID:
                return String.format(Locale.getDefault(), "<i>求点钟</i>立减<span>%1$d</span>元<b>%2$s</b>", couponInfo.actValue, couponInfo.couponPeriod);
            case Constant.COUPON_TYPE_DISCOUNT:
                return String.format(Locale.getDefault(), "<i>%s</i><span>%.1f</span>折<b>%s</b>", couponInfo.useTypeName, (float) couponInfo.actValue / 100, couponInfo.couponPeriod);
            case Constant.COUPON_TYPE_GIFT:
                return String.format(Locale.getDefault(), "<i>%s</i><span>%s</span><b>%s</b>", couponInfo.useTypeName, couponInfo.actSubTitle, couponInfo.couponPeriod);
            default:
                return String.format(Locale.getDefault(), "<i>%s</i><span>%d</span>元<b>%s</b>", couponInfo.useTypeName, couponInfo.actValue, couponInfo.couponPeriod);
        }
    }

    public void checkDeliverResult() {
        if (remainSendCount == 0) {
            XToast.show("发券成功" + successCount + "张" + (failedCount > 0 ? "失败" + failedCount + "张" : ""));
            finish();
            if (mFromCustomer != null) {
                EventBus.getDefault().post(new EventStartChatActivity(mFromCustomer.emchatId));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetClubCouponList, mUserGetCouponResult);
    }

    private void getData() {
        Map<String, String> params = new HashMap<>();
        params.put(RequestConstant.KEY_PAGE, "1");
        params.put(RequestConstant.KEY_PAGE_SIZE, "1000");
        MsgDispatcher.dispatchMessage(MsgDef.MSG_DEF_GET_CLUB_COUPON_LIST, params);
    }

    @Override
    public void onSelectedItemClicked(CouponInfo couponInfo, Integer position, boolean isSelected) {
        if (isSelected) {
            couponInfo.isSelected = 1;
            mCoupons.set(position, couponInfo);
            mSelectedCouponList.add(couponInfo);
        } else {
            for (int i = 0; i < mSelectedCouponList.size(); i++) {
                if (mSelectedCouponList.get(i).actId.equals(couponInfo.actId)) {
                    mSelectedCouponList.remove(mSelectedCouponList.get(i));
                    break;
                }
            }
            couponInfo.isSelected = 0;
            mCoupons.set(position, couponInfo);

        }
        mCouponListAdapter.notifyItemChanged(position);
    }
}