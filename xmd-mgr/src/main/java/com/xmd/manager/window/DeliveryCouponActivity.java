package com.xmd.manager.window;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.shidou.commonlibrary.widget.XToast;
import com.xmd.chat.MessageManager;
import com.xmd.chat.event.EventStartChatActivity;
import com.xmd.chat.message.CouponChatMessage;
import com.xmd.manager.R;
import com.xmd.manager.SharedPreferenceHelper;
import com.xmd.manager.adapter.DeliveryCouponListAdapter;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.chat.CommonUtils;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-30.
 */
public class DeliveryCouponActivity extends BaseActivity implements DeliveryCouponListAdapter.CallBackListener {
    public static final String EXTRA_CHAT_ID = "chatId";
    public static final String KEY_FROM = "from";
    @Bind(R.id.list)
    RecyclerView list;
    @Bind(R.id.btn_send)
    Button btnSend;
    @Bind(R.id.view_empty)
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
                result.respData.get(i).isSelected = 1;
                if (result.respData.get(i).actStatus.equals("online")) {
                    if (result.respData.get(i).useTypeName.equals("优惠券") || result.respData.get(i).useTypeName.equals("现金券")) {
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
                    CommonUtils.userGetCoupon(couponInfo.actId, "manager", chatId, couponInfo);
                }
                break;
        }
    }

    public void handleUserGetCouponResult(UserGetCouponResult result) {
        remainSendCount--;
        if (result.respData != null) {
            //用户领取成功，那么发送环信消息
            CouponInfo couponInfo = result.couponInfo;
            CouponChatMessage chatMessage = CouponChatMessage.create(chatId,
                    result.respData.userActId, String.format(Locale.getDefault(), "<i>%s</i><span>%d</span>元<b>%s</b>",
                            couponInfo.useTypeName, couponInfo.actValue, couponInfo.couponPeriod),
                    SharedPreferenceHelper.getUserInviteCode());
            MessageManager.getInstance().sendMessage(chatMessage);
            successCount++;
        } else {
            failedCount++;
        }
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
            for (int i = 0; i < mSelectedCouponList.size(); i++) {
                if (mSelectedCouponList.get(i).actId.equals(couponInfo.actId)) {
                    mSelectedCouponList.remove(mSelectedCouponList.get(i));
                    break;
                }
            }
            couponInfo.isSelected = 1;
            mCoupons.set(position, couponInfo);
        } else {
            couponInfo.isSelected = 2;
            mCoupons.set(position, couponInfo);
            mSelectedCouponList.add(couponInfo);
        }
        mCouponListAdapter.notifyItemChanged(position);
    }
}