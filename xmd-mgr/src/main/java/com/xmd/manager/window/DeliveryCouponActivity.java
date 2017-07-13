package com.xmd.manager.window;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.xmd.manager.R;
import com.xmd.manager.adapter.DeliveryCouponListAdapter;
import com.xmd.manager.beans.CouponInfo;
import com.xmd.manager.beans.CouponSelectResult;
import com.xmd.manager.beans.Customer;
import com.xmd.manager.chat.EmchatUserHelper;
import com.xmd.manager.msgctrl.MsgDef;
import com.xmd.manager.msgctrl.MsgDispatcher;
import com.xmd.manager.msgctrl.RxBus;
import com.xmd.manager.service.RequestConstant;
import com.xmd.manager.service.response.ClubCouponResult;
import com.xmd.manager.widget.EmptyView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;

/**
 * Created by linms@xiaomodo.com on 16-5-30.
 */
public class DeliveryCouponActivity extends BaseActivity implements DeliveryCouponListAdapter.CallBackListener {

    public static final String KEY_FROM = "from";
    @BindView(R.id.list)
    RecyclerView list;
    @BindView(R.id.btn_send)
    Button btnSend;
    @BindView(R.id.view_empty)
    EmptyView viewEmpty;
    private Customer mFromCustomer;
    private List<CouponInfo> mSelectedCouponList = new ArrayList<>();
    private List<CouponInfo> mCoupons;
    private Subscription mGetClubCouponList;
    private DeliveryCouponListAdapter mCouponListAdapter;
    private LinearLayoutManager mLayoutManager;


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
        mGetClubCouponList = RxBus.getInstance().toObservable(ClubCouponResult.class).subscribe(result -> {
            handlerCouponResult(result);

        });
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
                if (mFromCustomer != null) {
                    EmchatUserHelper.startToChat(mFromCustomer.emchatId, mFromCustomer.userName, mFromCustomer.userHeadimgurl,
                            new CouponSelectResult(mSelectedCouponList));
                } else {
                    RxBus.getInstance().post(new CouponSelectResult(mSelectedCouponList));
                }
                finish();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getInstance().unsubscribe(mGetClubCouponList);
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